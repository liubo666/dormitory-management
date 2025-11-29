import request from '@/utils/request'
import type { PageResponse } from '@/utils/request'

export interface RegistrationApplicationDTO {
  username: string
  password: string
  confirmPassword: string
  realName: string
  gender: number
  phone: string
  email: string
  adminEmployeeNo: string
}

export interface RegistrationApplicationVO {
  id: number
  applicationNo: string
  username: string
  realName: string
  gender: number
  phone: string
  email: string
  adminEmployeeNo: string
  status: number
  statusText: string
  approvedByName?: string
  approvedTime?: string
  rejectionReason?: string
  createTime: string
}

// 提交注册申请
export const submitRegistration = (data: RegistrationApplicationDTO) => {
  return request.post('/registration/apply', data)
}

// 验证管理员工号
export const validateAdminEmployeeNo = (employeeNo: string): Promise<boolean> => {
  return request.get(`/registration/validate-admin/${employeeNo}`)
}

// 分页查询注册申请（管理员）
export const getRegistrationApplications = (params: {
  current: number
  size: number
  status?: number
}): Promise<PageResponse<RegistrationApplicationVO>> => {
  return request.get('/registration/admin/applications', { params })
}

// 根据审批token获取申请详情
export const getApplicationByToken = (token: string): Promise<RegistrationApplicationVO> => {
  return request.get(`/registration/application/${token}`)
}

// 审批通过（管理员）
export const approveApplication = (token: string) => {
  return request.post(`/registration/admin/approve/${token}`)
}

// 审批驳回（管理员）
export const rejectApplication = (token: string, reason: string) => {
  return request.post(`/registration/admin/reject/${token}`, null, {
    params: { reason }
  })
}