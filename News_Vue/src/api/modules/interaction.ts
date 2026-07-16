import request from '../request'
import type { ApiResponse, Comment } from '@/types/headline'
import type { ApiResponse } from '@/types/headline'

// 获取评论列表
export const getComments = (params: {
  headlineId: number
  page?: number
  pageSize?: number
  sortBy?: string
  sortOrder?: string
  status?: number
}): Promise<ApiResponse<{ items: Comment[]; total: number }>> => {
  return request.get(`/headlines/${params.headlineId}/comments`, {
    params: {
      page: params.page,
      page_size: params.pageSize,
      sort_by: params.sortBy,
      sort_order: params.sortOrder,
      status: params.status,
    },
  })
}

// 发表评论
export const addComment = (data: {
  headlineId: number
  content: string
  parentId?: string
}): Promise<ApiResponse<Comment>> => {
  return request.post('/comments', data)
}

// 点赞评论
export const likeComment = (
  commentId: string,
  action: 'like' | 'unlike' = 'like',
): Promise<ApiResponse<any>> => {
  return request.post(`/comments/${commentId}/like`, { action })
}

// 删除评论
export const deleteComment = (commentId: string): Promise<ApiResponse<void>> => {
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
export const getFavorites = (params: { page?: number; size?: number }) => {
  return request.get('/favorites', { params })
}
