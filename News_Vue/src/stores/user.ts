import {defineStore} from 'pinia'
import {computed, ref} from 'vue'
import {ElMessage} from 'element-plus'
import {getUserInfo, login, logout, register as registerApi} from '@/api/modules/auth'
import type {LoginForm, RegisterForm, User} from '@/types'

export const useUserStore = defineStore('user', () => {
  // 状态
  const token = ref<string>(localStorage.getItem('token') || '')
  const refreshToken = ref<string>(localStorage.getItem('refreshToken') || '')
  const userInfo = ref<User | null>(null)
  
  // 初始化用户信息（同步尝试）
  try {
    const savedUserInfo = localStorage.getItem('userInfo')
    if (savedUserInfo) {
      userInfo.value = JSON.parse(savedUserInfo)
    }
  } catch (e) {
    console.error('Failed to parse userInfo from localStorage:', e)
  }

  const isLoggedIn = computed(() => !!token.value)

  // 注册
  const userRegister = async (registerForm: RegisterForm) => {
    try {
      const response = await registerApi(registerForm)
      ElMessage.success('注册成功')
      return true
    } catch (error) {
      console.error('注册失败:', error)
      throw error
    }
  }

  // 登录
  const userLogin = async (loginForm: LoginForm) => {
    try {
      const response = await login(loginForm)
      const { data: { token: newToken, refreshToken: newRefreshToken, user } } = response

      // 保存token和用户信息
      token.value = newToken
      refreshToken.value = newRefreshToken || ''
      userInfo.value = user

      localStorage.setItem('token', newToken)
      if (newRefreshToken) {
        localStorage.setItem('refreshToken', newRefreshToken)
      }
      localStorage.setItem('userInfo', JSON.stringify(user))

      ElMessage.success('登录成功')
      return true
    } catch (error) {
      console.error('登录失败:', error)
      return false
    }
  }

  // 登出
  const userLogout = async () => {
    try {
      await logout(refreshToken.value)
    } catch (error) {
      console.error('登出请求失败:', error)
    } finally {
      // 清除本地数据
      token.value = ''
      refreshToken.value = ''
      userInfo.value = null
      localStorage.removeItem('token')
      localStorage.removeItem('refreshToken')
      localStorage.removeItem('userInfo')

      ElMessage.success('已退出登录')
    }
  }

  // 获取用户信息
  const fetchUserInfo = async () => {
    try {
      const response = await getUserInfo()
      userInfo.value = response.data
      localStorage.setItem('userInfo', JSON.stringify(response.data))
    } catch (error: any) {
      console.error('获取用户信息失败:', error)
      // 注意：不再主动清除token，除非是401错误（由request.ts处理）
      // 这里的错误可能是网络波动或非授权类错误，保留token以便重试
    }
  }

  // 初始化用户信息（保留供App.vue调用，作为保险）
  const initUserInfo = () => {
    const savedUserInfo = localStorage.getItem('userInfo')
    if (savedUserInfo) {
      try {
        userInfo.value = JSON.parse(savedUserInfo)
      } catch (error) {
        console.error('解析用户信息失败:', error)
        localStorage.removeItem('userInfo')
      }
    }
  }

  // 更新用户信息
  const updateUserInfo = (newUserInfo: Partial<User>) => {
    if (userInfo.value) {
      userInfo.value = { ...userInfo.value, ...newUserInfo }
      localStorage.setItem('userInfo', JSON.stringify(userInfo.value))
    }
  }

  return {
    // 状态
    token,
    refreshToken,
    userInfo,
    isLoggedIn,

    // 方法
    register: userRegister,
    login: userLogin,
    logout: userLogout,
    fetchUserInfo,
    initUserInfo,
    updateUserInfo,
  }
})
