// API配置文件
export const API_BASE_URL = '/api/v1'

// 请求配置
export const REQUEST_CONFIG = {
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
}

// 响应状态码
export const HTTP_STATUS = {
  OK: 200,
  CREATED: 201,
  NO_CONTENT: 204,
  BAD_REQUEST: 400,
  UNAUTHORIZED: 401,
  FORBIDDEN: 403,
  NOT_FOUND: 404,
  INTERNAL_SERVER_ERROR: 500,
}
