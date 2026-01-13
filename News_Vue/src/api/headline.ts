import axios from 'axios'
import type { Headline, HeadlineCreateDTO, HeadlineUpdateDTO, HeadlineQueryDTO, PageResult, ApiResponse } from '@/types/headline'

// 创建axios实例
const request = axios.create({
  baseURL: 'http://localhost:8080/api/v1',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器 - 添加token
request.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// 响应拦截器 - 处理错误
request.interceptors.response.use(
  (response) => {
    return response.data
  },
  (error) => {
    if (error.response?.status === 401) {
      // token过期，跳转到登录页
      localStorage.removeItem('token')
      window.location.href = '/login'
    }
    return Promise.reject(error)
  }
)

// 新闻相关API
export const getHeadlines = (params: HeadlineQueryDTO): Promise<ApiResponse<PageResult<Headline>>> => {
  return request.post('/headline/findNewsPage', params)
}

export const getHeadlineById = (hid: number): Promise<ApiResponse<Headline>> => {
  return request.get(`/headline/show/${hid}`)
}

export const createHeadline = (data: HeadlineCreateDTO): Promise<ApiResponse<string>> => {
  return request.post('/headline/publish', data)
}

export const updateHeadline = (hid: number, data: HeadlineUpdateDTO): Promise<ApiResponse<string>> => {
  return request.put(`/headline/${hid}`, data)
}

export const deleteHeadline = (hid: number): Promise<ApiResponse<string>> => {
  return request.delete(`/headline/${hid}`)
}

export const publishHeadline = (hid: number): Promise<ApiResponse<string>> => {
  return request.put(`/headline/${hid}/publish`)
}

export const offlineHeadline = (hid: number): Promise<ApiResponse<string>> => {
  return request.put(`/headline/${hid}/offline`)
}

export const getNewsTypes = (): Promise<ApiResponse<any[]>> => {
  return request.get('/categories')
}
