import request from '../request'

// 获取新闻列表
export const getNewsList = (params: {
  page?: number
  size?: number
  type?: number
  keyword?: string
}) => {
  return request.get('/headlines', { params })
}

// 获取新闻详情
export const getNewsDetail = (hid: number) => {
  return request.get(`/headlines/${hid}`)
}

// 发布新闻
export const publishNews = (data: {
  title: string
  content: string
  summary?: string
  type: number
  tags?: string
  coverImage?: string
}) => {
  return request.post('/headlines', data)
}

// 更新新闻
export const updateNews = (hid: number, data: any) => {
  return request.put(`/headlines/${hid}`, data)
}

// 删除新闻
export const deleteNews = (hid: number) => {
  return request.delete(`/headlines/${hid}`)
}

// 获取新闻分类
export const getNewsTypes = () => {
  return request.get('/news-types')
}

// 上传文件
export const uploadFile = (file: File) => {
  const formData = new FormData()
  formData.append('file', file)
  
  return request.post('/common/upload', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}
