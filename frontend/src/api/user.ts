import request from '@/utils/request'

// 登录请求参数
export interface LoginParams {
  username: string
  password: string
}

// 登录响应数据
export interface LoginResponse {
  token: string
  refreshToken: string
}

// 用户信息
export interface UserInfo {
  id: string
  username: string
  name: string
  gender: number
  phone: string
  email: string
  role: string
  avatar: string
  previewUrl: string // 预览头像链接
  status: number
  createTime?: string
  updateTime?: string
}

/**
 * 用户登录
 */
export function login(data: LoginParams): Promise<LoginResponse> {
  return request.post('/user/login', data)
}

/**
 * 用户登出
 */
export function logout(): Promise<void> {
  return request.post('/user/logout')
}

/**
 * 获取当前用户信息
 */
export function getCurrentUserInfo(): Promise<UserInfo> {
  return request.get('/user/info')
}

/**
 * 刷新访问Token
 */
export function refreshToken(refreshTokenValue: string): Promise<LoginResponse> {
  return request.post('/user/refresh', null, {
    params: { refreshToken: refreshTokenValue }
  })
}

// 忘记密码请求参数
export interface ForgotPasswordParams {
  username: string
  email: string
}

/**
 * 发送忘记密码邮件
 */
export function sendResetPasswordEmail(data: ForgotPasswordParams): Promise<void> {
  return request.post('/user/forgot-password', data)
}

// 重置密码请求参数
export interface ResetPasswordParams {
  token: string
  password: string
}

/**
 * 重置密码
 */
export function resetPassword(data: ResetPasswordParams): Promise<void> {
  return request.post('/user/reset-password', data)
}

/**
 * 更新个人信息
 */
export function updateUserInfo(data: Partial<UserInfo>): Promise<UserInfo> {
  return request.post('/user/updateInfo', data)
}


/**
 * 修改密码
 */
export interface ChangePasswordParams {
  oldPassword: string
  newPassword: string
  confirmPassword: string
}

export function changePassword(data: ChangePasswordParams): Promise<void> {
  return request.post('/user/change-password', data)
}