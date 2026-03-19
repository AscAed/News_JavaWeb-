// 新闻头条相关类型定义

export interface Headline {
  hid: number
  title: string
  summary: string
  content?: string
  coverImage?: string
  tags?: string | string[]
  type: number
  typeName?: string
  pageViews: number
  pastHours: number
  publisher?: number
  author?: string
  status: number
  isTop: boolean
  publishedTime?: string
  createdTime: string
  updatedTime: string
  likeCount?: number
  commentCount?: number
  favoriteCount?: number
}

export interface HeadlineCreateDTO {
  title: string
  content: string
  summary?: string
  coverImage?: string
  type: number
  tags?: string[]
}

export interface HeadlineUpdateDTO {
  title?: string
  content?: string
  summary?: string
  coverImage?: string
  type?: number
  tags?: string[]
  status?: number
}

export interface HeadlineQueryDTO {
  keywords?: string
  type?: number
  status?: number
  page?: number
  pageSize?: number
  sortBy?: string
  sortOrder?: 'asc' | 'desc'
}

export interface PageResult<T> {
  total: number
  page: number
  pageSize: number
  totalPages: number
  items: T[]
}

export interface ApiResponse<T = any> {
  code: number
  message: string
  data: T
  timestamp?: string
}

// 评论相关类型定义
export interface CommentAuthor {
  username: string
  avatarUrl?: string
}

export interface Comment {
  id: string
  headline_id: number
  parent_id: string | null
  author: CommentAuthor
  content: string
  like_count: number
  reply_count: number
  status: number
  created_time: string
  updated_time: string
  replies?: Comment[]
}
