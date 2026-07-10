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
  email: string
  code: string
}) => {
  return request.post('/auth/register', data)
}

// 发送验证码
export const sendCode = (email: string, captchaToken: string) => {
  return request.post('/auth/send-code', { email, captchaToken })
}

// 获取滑块验证码
export const getCaptcha = () => {
  return request.get('/auth/captcha/generate')
}

// 验证滑块位置
export const verifyCaptcha = (data: { captchaKey: string; sliderX: number }) => {
  return request.post('/auth/captcha/verify', data)
}


// 刷新token
export const refreshToken = (refreshTokenStr: string) => {
  return request.post('/auth/refresh', { refreshToken: refreshTokenStr })
}

// 用户登出
export const logout = (refreshTokenStr?: string) => {
  return request.post('/auth/logout', refreshTokenStr ? { refreshToken: refreshTokenStr } : undefined)
}

// 获取用户信息
export const getUserInfo = () => {
  return request.get('/users/profile')
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
