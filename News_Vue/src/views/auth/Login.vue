<script setup lang="ts">
import {reactive, ref} from 'vue'
import {useRouter} from 'vue-router'
import {useUserStore} from '@/stores/user'
import type {LoginForm} from '@/types'

const router = useRouter()
const userStore = useUserStore()

// 表单数据
const loginForm = reactive<LoginForm>({
  phone: '',
  password: '',
})

// 表单验证规则
const rules = {
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    {pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur'},
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    {min: 6, max: 16, message: '密码长度为6-16位', trigger: 'blur'},
  ],
}

// 响应式数据
const loading = ref(false)
const formRef = ref()

// 方法
const handleLogin = async () => {
  if (!formRef.value) return

  try {
    await formRef.value.validate()
    loading.value = true

    // 调用登录API
    const success = await userStore.login({
      phone: loginForm.phone,
      password: loginForm.password,
    })
    if (success) {
      router.push('/')
    }
  } catch (error) {
    console.error('登录失败:', error)
  } finally {
    loading.value = false
  }
}

const goToRegister = () => {
  router.push('/register')
}
</script>

<template>
  <div class="auth-container login-page">
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
        <h2 class="auth-title">登录</h2>
        <p class="auth-subtitle">
          使用您的易闻趣事账号继续
        </p>
      </div>

      <el-form
        ref="formRef"
        :model="loginForm"
        :rules="rules"
        class="auth-form"
        label-position="top"
        @submit.prevent="handleLogin"
      >
        <el-form-item prop="phone">
          <el-input
            v-model="loginForm.phone"
            class="google-input"
            placeholder="手机号"
            size="large"
          />
        </el-form-item>

        <el-form-item prop="password">
          <el-input
            v-model="loginForm.password"
            class="google-input"
            placeholder="密码"
            show-password
            size="large"
            type="password"
            @keyup.enter="handleLogin"
          />
        </el-form-item>

        <div class="auth-actions">
          <router-link class="auth-link" to="/register">创建账号</router-link>
          <el-button
            :loading="loading"
            class="auth-submit-btn"
            size="large"
            type="primary"
            @click="handleLogin"
          >
            {{ loading ? '请稍候...' : '下一步' }}
          </el-button>
        </div>
      </el-form>
    </div>
  </div>
</template>

<style scoped>
.auth-container {
  min-height: calc(100vh - 72px - 140px); /* Adjust based on header/footer height */
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
</style>
