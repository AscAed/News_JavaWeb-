// 用户相关类型
export interface User {
  id: number
  username: string
  phone: string
  email?: string
  avatarUrl?: string
  status: number
  lastLoginTime?: string
  createdTime: string
  updatedTime: string
}

export interface LoginForm {
  phone: string
  password: string
}

export interface RegisterForm {
  username: string
  phone: string
  password: string
  email?: string
}

// 新闻相关类型
export interface News {
  hid: number | string
  title: string
  summary?: string
  content?: string
  coverImageUrl?: string
  type: number | string
  typeName?: string
  publisher: number | string
  author?: string
  source?: string
  sourceUrl?: string
  tags?: string
  pageViews: number
  likeCount: number
  commentCount: number
  favoriteCount: number
  isTop: boolean
  isHot: boolean
  status: number
  publishedTime?: string
  createdTime: string
  updatedTime: string
}

export interface NewsType {
  tid: number
  tname: string
  description?: string
  iconUrl?: string
  sortOrder: number
  status: number
  createdTime: string
  updatedTime: string
}

export interface NewsPublishForm {
  title: string
  content: string
  summary?: string
  type: number
  tags?: string
  coverImage?: string
}

// 评论相关类型
export interface Comment {
  cid: number
  hid: number
  userId: number
  parentId: number
  content: string
  likeCount: number
  status: number
  createdTime: string
  updatedTime: string
  user?: {
    id: number
    username: string
    avatarUrl?: string
  }
  replies?: Comment[]
}

export interface CommentForm {
  newsId: number
  content: string
  parentId?: number
}

// 收藏相关类型
export interface Favorite {
  id: number
  hid: number
  userId: number
  favoriteTime: string
  news?: News
}

// API响应类型
export interface ApiResponse<T = any> {
  code: number
  message: string
  data: T
  timestamp?: string
}

export interface PageResult<T = any> {
  records: T[]
  total: number
  size: number
  current: number
  pages: number
}

// 文件上传类型
export interface UploadResult {
  fileId: string
  fileName: string
  originalName: string
  fileSize: number
  fileType: string
  mimeType: string
  accessUrl: string
  thumbnailUrl?: string
}
