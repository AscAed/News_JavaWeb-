import axios from 'axios'
import type {
  ApiResponse,
  Headline,
  HeadlineCreateDTO,
  HeadlineQueryDTO,
  HeadlineUpdateDTO,
  PageResult,
} from '@/types/headline'

// 创建axios实例
const request = axios.create({
  baseURL: '/api/v1',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
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
  },
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
  },
)

// 新闻相关API
export const getHeadlines = async (
  params: HeadlineQueryDTO,
): Promise<ApiResponse<PageResult<Headline>>> => {
  const backendParams: any = {...params}
  if (params.type) {
    backendParams.typeId = params.type
    delete backendParams.type
  }
  const res: ApiResponse<any> = await request.get('/headlines', {params: backendParams})
  if (res.data && res.data.items) {
    res.data.items = res.data.items.map((item: any) => ({
      ...item,
      hid: item.id || item.hid,
      coverImage: item.cover_image || item.coverImage,
      type: item.type_id || item.type,
      typeName: item.type_name || item.typeName,
      pageViews: item.page_views || item.pageViews,
      likeCount: item.like_count || item.likeCount,
      commentCount: item.comment_count || item.commentCount,
      publisher: item.author_id || item.publisher,
      isTop: item.is_top === 1 || item.is_top === true || item.isTop === true,
      publishedTime: item.published_time || item.publishedTime,
      createdTime: item.created_time || item.createdTime,
      updatedTime: item.updated_time || item.updatedTime,
      pastHours: item.past_hours || item.pastHours
    }))
  }
  return res
}

export const getHeadlineById = async (hid: number): Promise<ApiResponse<Headline>> => {
  const res: ApiResponse<any> = await request.get(`/headlines/${hid}`)
  if (res.data) {
    const item = res.data
    res.data = {
      ...item,
      hid: item.id || item.hid,
      coverImage: item.cover_image || item.coverImage,
      type: item.type_id || item.type,
      typeName: item.type_name || item.typeName,
      pageViews: item.page_views || item.pageViews,
      likeCount: item.like_count || item.likeCount,
      commentCount: item.comment_count || item.commentCount,
      publisher: item.author_id || item.publisher,
      isTop: item.is_top === 1 || item.is_top === true || item.isTop === true,
      publishedTime: item.published_time || item.publishedTime,
      createdTime: item.created_time || item.createdTime,
      updatedTime: item.updated_time || item.updatedTime,
      pastHours: item.past_hours || item.pastHours
    }
  }
  return res
}

export const createHeadline = (data: HeadlineCreateDTO): Promise<ApiResponse<string>> => {
  return request.post('/headlines', data)
}

export const updateHeadline = (
  hid: number,
  data: HeadlineUpdateDTO,
): Promise<ApiResponse<string>> => {
  return request.put(`/headlines/${hid}`, data)
}

export const deleteHeadline = (hid: number): Promise<ApiResponse<string>> => {
  return request.delete(`/headlines/${hid}`)
}

// TODO: Backend does not support these specific endpoints yet. Logic might move to updateHeadline.
// export const publishHeadline = (hid: number): Promise<ApiResponse<string>> => {
//   return request.put(`/headline/${hid}/publish`)
// }

// export const offlineHeadline = (hid: number): Promise<ApiResponse<string>> => {
//   return request.put(`/headline/${hid}/offline`)
// }

export const getNewsTypes = (): Promise<ApiResponse<any[]>> => {
  return request.get('/categories')
}
