import request from '../request'

// 获取评论列表
export const getComments = (params: {
  newsId: number
  page?: number
  size?: number
}) => {
  return request.get('/comments', { params })
}

// 发表评论
export const addComment = (data: {
  newsId: number
  content: string
  parentId?: number
}) => {
  return request.post('/comments', data)
}

// 点赞评论
export const likeComment = (commentId: number) => {
  return request.post(`/comments/${commentId}/like`)
}

// 删除评论
export const deleteComment = (commentId: number) => {
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
