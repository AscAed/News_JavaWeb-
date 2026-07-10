import axios from 'axios'
import {ElMessage} from 'element-plus'
import {API_BASE_URL, REQUEST_CONFIG} from './config'

let isRefreshing = false
let retryQueue: Function[] = []

const handleLogoutLocal = () => {
  localStorage.removeItem('token')
  localStorage.removeItem('refreshToken')
  localStorage.removeItem('userInfo')

  import('@/stores/user').then(({useUserStore}) => {
    const userStore = useUserStore()
    userStore.token = ''
    userStore.refreshToken = ''
    userStore.userInfo = null
  })

  import('@/router').then(({default: router}) => {
    if (
      router.currentRoute.value.meta?.requiresAuth ||
      router.currentRoute.value.meta?.requiresAdmin
    ) {
      router.push('/login')
    }
  })
}

// 创建axios实例
const request = axios.create({
  baseURL: API_BASE_URL,
  ...REQUEST_CONFIG,
})

// 请求拦截器
request.interceptors.request.use(
  (config) => {
    // 添加JWT token
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  },
)

// 响应拦截器
request.interceptors.response.use(
  (response) => {
    const { code, message, data } = response.data

    // 请求成功
    if (code === 200) {
      return response.data
    }

    // 请求失败
    ElMessage.error(message || '请求失败')
    return Promise.reject(new Error(message || '请求失败'))
  },
  (error) => {
    // 处理HTTP错误状态
    if (error.response) {
      const { status, data, config } = error.response

      switch (status) {
        case 401:
          // 判断接口是否已经是刷新Token的接口，防止死循环
          if (config.url && config.url.includes('/auth/refresh')) {
            ElMessage.error('登录已过期，请重新登录')
            handleLogoutLocal()
            return Promise.reject(error)
          }

          if (!isRefreshing) {
            isRefreshing = true
            const refreshTokenStr = localStorage.getItem('refreshToken')
            if (!refreshTokenStr) {
              ElMessage.error('登录已过期，请重新登录')
              handleLogoutLocal()
              return Promise.reject(error)
            }

            // 动态引入避免循环依赖
            return import('@/api/modules/auth').then(({ refreshToken }) => {
              return refreshToken(refreshTokenStr)
            }).then((res: any) => {
              const newTokens = res.data
              localStorage.setItem('token', newTokens.token)
              if (newTokens.refreshToken) {
                localStorage.setItem('refreshToken', newTokens.refreshToken)
              }
              import('@/stores/user').then(({useUserStore}) => {
                const store = useUserStore()
                store.token = newTokens.token
                if (newTokens.refreshToken) store.refreshToken = newTokens.refreshToken
              })

              config.headers.Authorization = `Bearer ${newTokens.token}`
              // 执行队列中排队的请求
              retryQueue.forEach((cb) => cb(newTokens.token))
              retryQueue = []
              
              // 重新发起当前失败的请求并返回
              return request(config)
            }).catch((refreshErr) => {
              ElMessage.error('登录已过期，请重新登录')
              retryQueue = []
              handleLogoutLocal()
              return Promise.reject(refreshErr)
            }).finally(() => {
              isRefreshing = false
            })
          } else {
            // 正在刷新时，将新请求挂起，放入重试队列
            return new Promise((resolve) => {
              retryQueue.push((token: string) => {
                config.headers.Authorization = `Bearer ${token}`
                resolve(request(config))
              })
            })
          }
        case 403:
          ElMessage.error('没有权限访问该资源')
          break
        case 404:
          ElMessage.error('请求的资源不存在')
          break
        case 500:
          ElMessage.error('服务器内部错误')
          break
        default:
          ElMessage.error(data?.message || '网络错误')
      }
    } else if (error.request) {
      ElMessage.error('网络连接失败')
    } else {
      ElMessage.error('请求配置错误')
    }

    return Promise.reject(error)
  },
)

export default request
