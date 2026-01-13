import request from '../request'

// 用户登录
export const login = (data: { phone: string; password: string }) => {
  return request.post('/auth/login', data)
}

// 用户注册
export const register = (data: { 
  username: string
  phone: string
  password: string
  email?: string 
}) => {
  return request.post('/auth/register', data)
}

// 刷新token
export const refreshToken = () => {
  return request.post('/auth/refresh')
}

// 用户登出
export const logout = () => {
  return request.post('/auth/logout')
}

// 获取用户信息
export const getUserInfo = () => {
  return request.get('/users/info')
}

// 更新用户信息
export const updateUserInfo = (data: any) => {
  return request.put('/users/info', data)
}

// 修改密码
export const changePassword = (data: { 
  oldPassword: string
  newPassword: string 
}) => {
  return request.put('/users/password', data)
}
