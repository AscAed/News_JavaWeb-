<template>
  <div class="auth-container register-page">
    <div class="auth-card">
      <div class="back-action">
        <el-button class="back-btn" link @click="router.push('/')">
          <el-icon>
            <ArrowLeft/>
          </el-icon>
          返回首页
        </el-button>
      </div>
      <div class="auth-header">
        <div class="brand-logo-container" style="justify-content: center; margin-bottom: 16px;">
          <div class="brand-icon-box">易</div>
          <h1 class="brand-text" style="font-size: 24px;">闻趣事</h1>
        </div>
        <h2 class="auth-title">创建账号</h2>
        <p class="auth-subtitle">
          欢迎加入易闻趣事
        </p>
      </div>

      <el-form
        ref="registerFormRef"
        :model="registerForm"
        :rules="registerRules"
        class="auth-form"
        label-position="top"
        @submit.prevent="handleRegister"
      >
        <el-form-item prop="username">
          <el-input
            v-model="registerForm.username"
            class="google-input"
            maxlength="20"
            placeholder="用户名"
            size="large"
          />
        </el-form-item>

        <el-form-item prop="phone">
          <el-input
            v-model="registerForm.phone"
            class="google-input"
            maxlength="11"
            placeholder="手机号"
            size="large"
          />
        </el-form-item>

        <el-form-item prop="email">
          <el-input
            v-model="registerForm.email"
            class="google-input"
            placeholder="邮箱"
            size="large"
          />
        </el-form-item>

        <el-form-item prop="code">
          <div class="code-container">
            <el-input
              v-model="registerForm.code"
              class="google-input"
              maxlength="6"
              placeholder="6位验证码"
              size="large"
            />
            <el-button
              type="primary"
              :disabled="codeLoading || countdown > 0"
              @click="handleSendCode"
              class="send-btn"
              size="large"
            >
              {{ countdown > 0 ? `${countdown}s 后重发` : '获取验证码' }}
            </el-button>
          </div>
        </el-form-item>

        <el-form-item prop="password">
          <el-input
            v-model="registerForm.password"
            type="password"
            class="google-input"
            show-password
            placeholder="密码"
            size="large"
          />
        </el-form-item>

        <el-form-item prop="confirmPassword">
          <el-input
            v-model="registerForm.confirmPassword"
            type="password"
            class="google-input"
            show-password
            placeholder="确认密码"
            size="large"
            @keyup.enter="handleRegister"
          />
        </el-form-item>

        <el-form-item class="terms-item">
          <el-checkbox v-model="agreeTerms">
            我已阅读并同意
            <a class="auth-link-inline" href="#" @click.prevent="showTerms">用户协议</a>
            和
            <a class="auth-link-inline" href="#" @click.prevent="showPrivacy">隐私政策</a>
          </el-checkbox>
        </el-form-item>

        <div class="auth-actions">
          <router-link class="auth-link" to="/login">登录现有账号</router-link>
          <el-button
            type="primary"
            :loading="loading"
            :disabled="!agreeTerms"
            @click="handleRegister"
            class="auth-submit-btn"
            size="large"
          >
            下一步
          </el-button>
        </div>
      </el-form>
    </div>
  </div>
  <!-- 滑块验证码组件 -->
  <SliderCaptcha 
    v-model="captchaVisible"
    @success="onCaptchaSuccess"
  />
</template>

<script setup lang="ts">
import {onUnmounted, reactive, ref} from 'vue'
import {useRouter} from 'vue-router'
import {ElMessage, ElMessageBox, type FormInstance, type FormRules} from 'element-plus'
import {useUserStore} from '@/stores/user'
import {sendCode} from '@/api/modules/auth'
import SliderCaptcha from '@/components/SliderCaptcha.vue'

const router = useRouter()
const userStore = useUserStore()

// 响应式数据
const registerFormRef = ref<FormInstance>()
const loading = ref(false)
const codeLoading = ref(false)
const countdown = ref(0)
const agreeTerms = ref(false)
const captchaVisible = ref(false)
const captchaToken = ref('')
let timer: any = null

const registerForm = reactive({
  username: '',
  phone: '',
  email: '',
  password: '',
  confirmPassword: '',
  code: '',
})

const validateConfirmPassword = (rule: any, value: string, callback: any) => {
  if (value !== registerForm.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const registerRules: FormRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 2, max: 20, message: '用户名长度应为2-20位', trigger: 'blur' },
    {
      pattern: /^[a-zA-Z0-9_\u4e00-\u9fa5]+$/,
      message: '用户名只能包含字母、数字、下划线和中文',
      trigger: 'blur',
    },
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    {pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur'},
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    {type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur'},
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 16, message: '密码长度应为6-16位', trigger: 'blur' },
    {
      pattern: /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)[a-zA-Z\d]{6,16}$/,
      message: '密码必须包含大小写字母和数字',
      trigger: 'blur',
    },
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    {validator: validateConfirmPassword, trigger: 'blur'},
  ],
  code: [
    { required: true, message: '请输入验证码', trigger: 'blur' },
    { len: 6, message: '验证码必须为6位', trigger: 'blur' },
  ],
}

// 方法
const handleRegister = async () => {
  if (!registerFormRef.value) return

  try {
    await registerFormRef.value.validate()

    if (!agreeTerms.value) {
      ElMessage.warning('请先同意用户协议和隐私政策')
      return
    }

    loading.value = true

    // 调用注册API
    await userStore.register({
      username: registerForm.username,
      phone: registerForm.phone,
      email: registerForm.email,
      password: registerForm.password,
      code: registerForm.code,
    })

    ElMessage.success('注册成功，请登录')

    // 跳转到登录页
    router.push('/login')
  } catch (error: any) {
    console.error('注册失败:', error)
    ElMessage.error(error.message || '注册失败')
  } finally {
    loading.value = false
  }
}

const showTerms = () => {
  ElMessageBox.alert('这里是用户协议的内容...', '用户协议', {
    confirmButtonText: '确定',
    type: 'info',
  })
}

const showPrivacy = () => {
  ElMessageBox.alert('这里是隐私政策的内容...', '隐私政策', {
    confirmButtonText: '确定',
    type: 'info',
  })
}

// 邮件验证码处理
const handleSendCode = async () => {
  if (!registerForm.email) {
    ElMessage.warning('请先输入邮箱')
    return
  }

  // 校验邮箱格式
  const emailPattern = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/
  if (!emailPattern.test(registerForm.email)) {
    ElMessage.warning('请输入正确的邮箱格式')
    return
  }

  // 弹出人机验证
  captchaVisible.value = true
}

// 验证成功后的回调
const onCaptchaSuccess = (token: string) => {
  captchaToken.value = token
  executeSendCode()
}

// 真正执行发送验证码
const executeSendCode = async () => {
  try {
    codeLoading.value = true
    await sendCode(registerForm.email, captchaToken.value)
    ElMessage.success('验证码已发送')

    // 启动倒计时
    countdown.value = 60
    timer = setInterval(() => {
      if (countdown.value > 0) {
        countdown.value--
      } else {
        if (timer) clearInterval(timer)
      }
    }, 1000)
  } catch (error: any) {
    console.error('发送验证码失败:', error)
    // 如果是后端验证码校验失败
    if (error.response?.data?.message?.includes('人机验证')) {
      captchaToken.value = ''
    }
  } finally {
    codeLoading.value = false
  }
}

onUnmounted(() => {
  if (timer) clearInterval(timer)
})
</script>

<style scoped>
.auth-container {
  min-height: calc(100vh - 72px - 140px);
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: var(--bg-secondary);
  padding: var(--spacing-xl) var(--spacing-md);
}

.auth-card {
  width: 100%;
  max-width: 448px;
  background-color: var(--bg-primary);
  border-radius: 8px;
  border: 1px solid var(--border-primary);
  padding: 48px 40px 36px;
  box-sizing: border-box;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  transition: background-color 0.3s ease, border-color 0.3s ease;
}

@media (max-width: 600px) {
  .auth-card {
    border: none;
    box-shadow: none;
    background-color: transparent;
    padding: 24px 20px;
  }
}

.back-action {
  margin-bottom: 16px;
  margin-top: -16px;
  margin-left: -16px;
}

.back-btn {
  color: var(--text-secondary);
  font-size: 14px;
}

.auth-header {
  text-align: center;
  margin-bottom: 32px;
}

.logo-title {
  color: var(--primary-color);
  font-family: var(--font-family-heading);
  font-size: 24px;
  font-weight: 500;
  margin-bottom: 16px;
}

.auth-title {
  font-family: var(--font-family-sans);
  font-size: 24px;
  font-weight: 400;
  color: var(--text-primary);
  margin-bottom: 8px;
}

.auth-subtitle {
  font-size: 16px;
  font-weight: 400;
  color: var(--text-primary);
  margin-bottom: 8px;
}

.auth-form {
  margin-top: var(--spacing-md);
}

:deep(.google-input .el-input__wrapper) {
  border-radius: 4px;
  box-shadow: inset 0 0 0 1px var(--border-secondary) !important;
  padding: 8px 15px;
  background-color: transparent;
}

:deep(.google-input .el-input__wrapper.is-focus) {
  box-shadow: inset 0 0 0 2px var(--primary-color) !important;
}

:deep(.google-input .el-input__inner) {
  height: 38px;
  font-size: 16px;
  color: var(--text-primary);
}

.terms-item {
  margin-bottom: 16px;
}

:deep(.el-checkbox__label) {
  font-size: 14px;
  color: var(--text-secondary);
  white-space: normal;
}

.auth-link-inline {
  color: var(--primary-accent);
  text-decoration: none;
}

.auth-link-inline:hover {
  text-decoration: underline;
}

.auth-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 40px;
}

.auth-submit-btn {
  height: 36px;
  padding: 0 24px;
  font-size: 14px;
  font-weight: 500;
  border-radius: 4px;
  background-color: var(--primary-accent);
}

.auth-link {
  color: var(--primary-accent);
  font-weight: 500;
  text-decoration: none;
  font-size: 14px;
  padding: 8px;
  margin-left: -8px;
  border-radius: 4px;
  transition: background-color 0.2s;
}

.auth-link:hover {
  background-color: var(--bg-tertiary);
}

.code-container {
  display: flex;
  gap: 12px;
  width: 100%;
}

.send-btn {
  width: 120px;
  flex-shrink: 0;
  background-color: var(--primary-accent);
  color: white;
  border: none;
}

.send-btn:disabled {
  background-color: var(--border-primary);
  color: var(--text-secondary);
}
</style>
