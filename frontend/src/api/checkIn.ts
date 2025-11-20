import request from '@/utils/request'

// 入住分配分页查询参数
export interface CheckInParams {
  studentId?: string
  studentName?: string
  studentNo?: string
  dormitoryId?: string
  roomNo?: string
  bedNo?: string
  status?: number
  checkInStartDate?: string
  checkInEndDate?: string
  applyStartDate?: string
  applyEndDate?: string
  current?: number
  size?: number
}

// 入住申请表单数据
export interface CheckInForm {
  id?: string
  studentId: string
  dormitoryId?: string
  bedId?: string
  bedNo?: string
  buildingName?: string
  roomNo?: string
  checkInDate: string
  expectedCheckoutDate?: string
  status?: number
  applicationReason?: string
  approvalRemark?: string
  checkoutReason?: string
  remark?: string
}

// 入住分配信息
export interface CheckIn {
  id: string
  studentId: string
  studentNo: string
  studentName: string
  studentGender: number
  studentGenderText: string
  college: string
  major: string
  className: string
  dormitoryId: string
  buildingName: string
  roomNo: string
  bedId: string
  bedNo: string
  checkInDate: string
  expectedCheckoutDate?: string
  actualCheckoutDate?: string
  status: number
  statusText: string
  applicationReason?: string
  approver?: string
  approvalTime?: string
  approvalRemark?: string
  checkoutReason?: string
  remark?: string
  createTime: string
  updateTime: string
  createBy?: string
  updateBy?: string
}

/**
 * 分页查询入住分配列表
 */
export function getCheckInPage(params: CheckInParams): Promise<{
  records: CheckIn[]
  total: number
  current: number
  size: number
}> {
  return request.post('/check-in/page', params)
}

/**
 * 根据ID获取入住分配详情
 */
export function getCheckInById(id: string): Promise<CheckIn> {
  return request.get(`/check-in/${id}`)
}

/**
 * 提交入住申请
 */
export function submitApplication(data: CheckInForm): Promise<void> {
  return request.post('/check-in/application', data)
}

/**
 * 审批入住申请
 */
export interface CheckInApprovalDTO {
  id: string | number
  status: number
  approvalRemark?: string
}

export function approveApplication(data: CheckInApprovalDTO): Promise<void> {
  return request.post('/check-in/approve', data)
}

/**
 * 分配宿舍
 */
export function assignDormitory(
  id: string,
  dormitoryId: string,
  bedId: string,
  bedNo: string
): Promise<void> {
  return request.put(`/check-in/${id}/assign-dormitory`, null, {
    params: { dormitoryId, bedId, bedNo }
  })
}

/**
 * 退宿处理
 */
export function checkout(id: string, checkoutReason?: string): Promise<void> {
  return request.put(`/check-in/${id}/checkout`, null, {
    params: { checkoutReason }
  })
}

/**
 * 取消入住申请
 */
export function cancelApplication(id: string, reason: string): Promise<void> {
  return request.put(`/check-in/${id}/cancel`, null, {
    params: { reason }
  })
}

/**
 * 批量审批入住申请
 */
export interface BatchCheckInApprovalDTO {
  ids: (string | number)[]
  status: number
  approvalRemark?: string
}

export function batchApprove(data: BatchCheckInApprovalDTO): Promise<{
  totalCount: number
  successCount: number
  failCount: number
  errorMessages: string[]
}> {
  return request.post('/check-in/batch-approve', data)
}

/**
 * 获取入住统计信息
 */
export function getCheckInStatistics(): Promise<{
  totalCount: number
  applyingCount: number
  checkedInCount: number
  checkedOutCount: number
  rejectedCount: number
}> {
  return request.get('/check-in/statistics')
}

/**
 * 根据学生ID获取入住记录
 */
export function getCheckInByStudentId(studentId: string): Promise<CheckIn> {
  return request.get(`/check-in/student/${studentId}`)
}

/**
 * 根据宿舍ID获取入住学生列表
 */
export function getCheckInsByDormitoryId(dormitoryId: string): Promise<CheckIn[]> {
  return request.get(`/check-in/dormitory/${dormitoryId}`)
}

/**
 * 根据床位ID获取入住记录
 */
export function getCheckInByBedId(bedId: string): Promise<CheckIn> {
  return request.get(`/check-in/bed/${bedId}`)
}

// 可申请学生查询参数
export interface AvailableStudentsParams {
  pageIndex?: number
  pageSize?: number
  keyword?: string
}

// 可申请学生响应
export interface AvailableStudentsResponse {
  records: Array<{
    id: string
    studentNo: string
    name: string
    gender: number
    college: string
    major: string
    className: string
  }>
  total: number
  current: number
  size: number
}

/**
 * 获取可申请入住的学生列表（在校生且未入住）
 */
export function getAvailableStudents(params?: AvailableStudentsParams): Promise<AvailableStudentsResponse> {
  return request.post('/check-in/available-students', params || {})
}

/**
 * 获取可用床位列表（有空闲床位的宿舍）
 */
export function getAvailableBeds(keyword: string = ''): Promise<Array<{
  dormitoryId: string
  buildingNo: string
  buildingName: string
  roomNo: string
  floorNumber: number
  bedList: Array<{
    bedId: string  // 床位ID，用于提交表单
    bedNo: string  // 床位号，用于显示
    status: number
    description?: string
    isOccupied?: boolean
    statusText?: string
  }>
}>> {
  return request.get('/check-in/available-beds', {
    params: { keyword }
  })
}