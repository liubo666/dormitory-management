import request from '@/utils/request'

// 学生请求参数
export interface StudentParams {
  studentNo?: string
  name?: string
  gender?: number
  college?: string
  major?: string
  className?: string
  grade?: string
  dormitoryId?: string
  status?: number
  current?: number
  size?: number
}

// 学生表单数据
export interface StudentForm {
  id?: string
  studentNo: string
  name: string
  gender: number
  birthDate: string
  idCard?: string
  phone?: string
  email?: string
  college: string
  major: string
  className?: string
  grade?: string
  enrollmentDate?: string
  graduationDate?: string
  dormitoryId?: string
  bedNo?: string
  status: number
  homeAddress?: string
  emergencyContact?: string
  emergencyPhone?: string
  remark?: string
}

// 学生信息
export interface Student {
  id: string
  studentNo: string
  name: string
  gender: number
  genderText: string
  birthDate: string
  age: number
  idCard?: string
  phone?: string
  email?: string
  college: string
  major: string
  className?: string
  grade?: string
  enrollmentDate?: string
  graduationDate?: string
  dormitoryId?: string
  buildingName?: string
  roomNo?: string
  bedNo?: string
  status: number
  statusText: string
  homeAddress?: string
  emergencyContact?: string
  emergencyPhone?: string
  remark?: string
  createTime: string
  updateTime: string
  createBy?: string
  updateBy?: string
  checkInStatus :number
}

// 宿舍分配数据
export interface DormitoryAssignment {
  studentId: string
  dormitoryId: string
  bedNo: string
  reason?: string
}

// 宿舍调换数据
export interface DormitoryChange {
  newDormitoryId: string
  newBedNo: string
  reason?: string
}

/**
 * 分页查询学生列表
 */
export function getStudentPage(params: StudentParams): Promise<{
  records: Student[]
  total: number
  current: number
  size: number
}> {
  return request.post('/student/page', params)
}

/**
 * 根据ID获取学生详情
 */
export function getStudentById(id: string): Promise<Student> {
  return request.get(`/student/${id}`)
}

/**
 * 根据学号获取学生信息
 */
export function getStudentByStudentNo(studentNo: string): Promise<Student> {
  return request.get(`/student/studentNo/${studentNo}`)
}

/**
 * 新增学生
 */
export function createStudent(data: StudentForm): Promise<void> {
  return request.post('/student', data)
}

/**
 * 更新学生信息DTO
 */
export interface StudentUpdateDTO {
  id: string
  studentNo?: string
  name?: string
  gender?: number
  birthDate?: string
  idCard?: string
  phone?: string
  email?: string
  college?: string
  major?: string
  className?: string
  grade?: string
  enrollmentDate?: string
  graduationDate?: string
  dormitoryId?: string
  bedNo?: string
  status?: number
  homeAddress?: string
  emergencyContact?: string
  emergencyPhone?: string
  remark?: string
}

/**
 * 更新学生信息
 */
export function updateStudent(data: StudentUpdateDTO): Promise<void> {
  return request.post('/student/update', data)
}

/**
 * 删除学生
 */
export function deleteStudent(id: string): Promise<void> {
  return request.delete(`/student/${id}`)
}

/**
 * 修改学生状态DTO
 */
export interface StudentStatusUpdateDTO {
  id: string
  status: number
}

/**
 * 修改学生状态
 */
export function updateStudentStatus(data: StudentStatusUpdateDTO): Promise<void> {
  return request.post('/student/status', data)
}

/**
 * 分配宿舍
 */
export function assignDormitory(studentId: string, data: DormitoryAssignment): Promise<void> {
  return request.post(`/student/${studentId}/assign-dormitory`, data)
}

/**
 * 调换宿舍DTO
 */
export interface ChangeDormitoryDTO {
  studentId: string
  newDormitoryId: string
  newBedNo: string
  reason?: string
}

/**
 * 调换宿舍
 */
export function changeDormitory(data: ChangeDormitoryDTO): Promise<void> {
  return request.post('/student/change-dormitory', data)
}

/**
 * 学生退宿DTO
 */
export interface StudentCheckoutDTO {
  studentId: string
  reason?: string
}

/**
 * 退宿处理
 */
export function checkoutDormitory(data: StudentCheckoutDTO): Promise<void> {
  return request.post('/student/checkout', data)
}

/**
 * 根据宿舍ID获取学生列表
 */
export function getStudentsByDormitory(dormitoryId: string): Promise<Student[]> {
  return request.get(`/student/dormitory/${dormitoryId}`)
}

/**
 * 获取学生统计信息
 */
export function getStudentStatistics(): Promise<{
  totalCount: number
  dormitoryCount: number
  statusStats: Record<number, number>
  genderStats: Record<number, number>
}> {
  return request.get('/student/statistics')
}

/**
 * 批量导入学生
 */
export function batchImportStudents(students: StudentForm[]): Promise<{
  totalCount: number
  successCount: number
  failCount: number
  errorMessages: string[]
}> {
  return request.post('/student/batch-import', students)
}

/**
 * 获取可用宿舍列表
 */
export function getAvailableDormitories(): Promise<Array<{
  id: string
  buildingName: string
  roomNo: string
  availableBeds: number
  bedList: Array<{
    bedNo: string
    isOccupied: boolean
  }>
}>> {
  return request.get('/student/available-dormitories')
}