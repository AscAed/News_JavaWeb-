import request from '../request'
import type { ApiResponse } from '@/types/headline'

// 获取评论列表
export const getComments = (headlineId: number, params: {
  page?: number
  page_size?: number
}): Promise<ApiResponse<any>> => {
  return request.get(`/headlines/${headlineId}/comments`, { params })
}

// 发表评论
export const addComment = (data: {
  headlineId: number
  content: string
  parentId?: string
}): Promise<ApiResponse<any>> => {
  return request.post('/comments', data)
}

// 点赞评论
export const likeComment = (commentId: string, action: 'like' | 'unlike' = 'like'): Promise<ApiResponse<any>> => {
  return request.post(`/comments/${commentId}/like`, { action })
}

// 删除评论
export const deleteComment = (commentId: string): Promise<ApiResponse<any>> => {
  return request.delete(`/comments/${commentId}`)
}

// 收藏新闻
export const favoriteNews = (newsId: number) => {
  return request.post('/favorites', { newsId })
}

// 取消收藏
export const unfavoriteNews = (newsId: number) => {
  return request.delete(`/favorites/${newsId}`)
}

// 获取收藏列表
export const getFavorites = (params: {
  page?: number
  size?: number
}) => {
  return request.get('/favorites', { params })
}
