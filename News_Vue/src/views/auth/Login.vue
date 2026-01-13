<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'
import { Phone, Lock } from '@element-plus/icons-vue'
import type { LoginForm } from '@/types'

const router = useRouter()
const userStore = useUserStore()

// 表单数据
const loginForm = reactive<LoginForm>({
  phone: '',
  password: ''
})

// 表单验证规则
const rules = {
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 16, message: '密码长度为6-16位', trigger: 'blur' }
  ]
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
      password: loginForm.password
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
  <div class="min-h-screen flex items-center justify-center bg-gray-50 py-12 px-4 sm:px-6 lg:px-8">
    <div class="max-w-md w-full space-y-8">
      <div>
        <h2 class="mt-6 text-center text-3xl font-extrabold text-gray-900">
          登录到新闻头条
        </h2>
        <p class="mt-2 text-center text-sm text-gray-600">
          或
          <router-link to="/register" class="font-medium text-indigo-600 hover:text-indigo-500">
            注册新账号
          </router-link>
        </p>
      </div>
      
      <div class="mt-8">
        <div class="bg-white py-8 px-6 shadow rounded-lg">
          <el-form
            ref="formRef"
            :model="loginForm"
            :rules="rules"
            class="space-y-6"
            @submit.prevent="handleLogin"
          >
            <div>
              <el-form-item label="手机号" prop="phone">
                <el-input
                  v-model="loginForm.phone"
                  placeholder="请输入手机号"
                  :prefix-icon="Phone"
                  size="large"
                />
              </el-form-item>
            </div>

            <div>
              <el-form-item label="密码" prop="password">
                <el-input
                  v-model="loginForm.password"
                  type="password"
                  placeholder="请输入密码"
                  :prefix-icon="Lock"
                  size="large"
                  show-password
                  @keyup.enter="handleLogin"
                />
              </el-form-item>
            </div>

            <div>
              <el-button
                type="primary"
                size="large"
                :loading="loading"
                @click="handleLogin"
                class="w-full"
              >
                {{ loading ? '登录中...' : '登录' }}
              </el-button>
            </div>
          </el-form>
        </div>
      </div>
    </div>
  </div>
</template>
