import request from '@/utils/request'
import type { PageResponse } from '@/utils/request'

// 访客登记接口定义
export interface Visitor {
  id: number
  visitorName: string
  visitorPhone: string
  visitorIdCard: string | null
  visitStudentName: string
  visitStudentNo: string | null
  roomId: number
  roomNo: string
  buildingId: number
  buildingName: string
  visitPurpose: string | null
  expectedVisitTime: string
  actualArrivalTime: string | null
  leaveTime: string | null
  status: number
  statusText: string
  remarks: string | null
  registrarId: number | null
  registrarName: string | null
  createTime: string
  updateTime: string
}

// 访客登记表单接口
export interface VisitorForm {
  id?: number
  visitorName: string
  visitorPhone: string
  visitorIdCard: string
  visitStudentName: string
  visitStudentNo: string
  roomId: number
  visitPurpose: string
  expectedVisitTime: string
  actualArrivalTime: string
  leaveTime: string
  status: number
  remarks: string
}

// 访客登记查询参数
export interface VisitorParams {
  current: number
  size: number
  visitorName?: string
  visitorPhone?: string
  visitStudentName?: string
  visitStudentNo?: string
  roomId?: string
  roomNo?: string
  buildingId?: number
  startDate?: string
  endDate?: string
  status?: number
}

/**
 * 分页查询访客登记记录
 */
export function getVisitorPage(params: VisitorParams): Promise<PageResponse<Visitor>> {
  return request({
    url: '/visitor/page',
    method: 'post',
    data: params
  })
}

/**
 * 根据ID查询访客登记详情
 */
export function getVisitorById(id: number): Promise<Visitor> {
  return request({
    url: `/visitor/${id}`,
    method: 'get'
  })
}

/**
 * 新增访客登记记录
 */
export function addVisitor(data: VisitorForm) {
  return request({
    url: '/visitor/add',
    method: 'post',
    data
  })
}

/**
 * 更新访客登记记录
 */
export function updateVisitor(data: VisitorForm) {
  return request({
    url: '/visitor',
    method: 'put',
    data
  })
}

/**
 * 删除访客登记记录
 */
export function deleteVisitor(id: number) {
  return request({
    url: `/visitor/${id}`,
    method: 'delete'
  })
}

/**
 * 批量删除访客登记记录
 */
export function batchDeleteVisitor(ids: number[]) {
  return request({
    url: '/visitor/batch',
    method: 'delete',
    data: ids
  })
}

/**
 * 访客签到
 */
export function checkIn(id: number) {
  return request({
    url: `/visitor/${id}/check-in`,
    method: 'post'
  })
}

/**
 * 访客签退
 */
export function checkOut(id: number) {
  return request({
    url: `/visitor/${id}/check-out`,
    method: 'post'
  })
}

/**
 * 取消访客访问
 */
export function cancelVisit(id: number, reason?: string) {
  return request({
    url: `/visitor/${id}/cancel`,
    method: 'post',
    params: { reason }
  })
}

/**
 * 宿舍搜索接口参数
 */
export interface DormitorySearchParams {
  keyword?: string
  current?: number
  size?: number
  buildingNo?: string
  status?: number
}

/**
 * 获取宿舍选项列表
 */
export function getDormitoryOptions() {
  return request({
    url: '/visitor/dormitories/options',
    method: 'get'
  })
}

/**
 * 搜索宿舍信息
 */
export function searchDormitories(params: DormitorySearchParams) {
  return request({
    url: '/visitor/dormitories/search',
    method: 'post',
    data: params
  })
}

/**
 * 搜索学生信息接口参数
 */
export interface StudentSearchParams {
  keyword?: string
  current?: number
  size?: number
  buildingId?: number
}

/**
 * 搜索学生信息
 */
export function searchStudents(params: StudentSearchParams) {
  return request({
    url: '/visitor/students/search',
    method: 'post',
    data: params
  })
}

/**
 * 获取访客统计信息
 */
export function getVisitorStatistics(startDate?: string, endDate?: string) {
  return request({
    url: '/visitor/statistics',
    method: 'get',
    params: { startDate, endDate }
  })
}

/**
 * 获取访客状态选项
 */
export function getVisitorStatusOptions() {
  return [
    { label: '待访问', value: 0, type: 'info' },
    { label: '访问中', value: 1, type: 'success' },
    { label: '已完成', value: 2, type: 'primary' },
    { label: '已取消', value: 3, type: 'danger' }
  ]
}

/**
 * 根据状态获取文本
 */
export function getStatusText(status: number): string {
  const statusMap: Record<number, string> = {
    0: '待访问',
    1: '访问中',
    2: '已完成',
    3: '已取消'
  }
  return statusMap[status] || '未知状态'
}

/**
 * 根据状态获取标签类型
 */
export function getStatusType(status: number): string {
  const typeMap: Record<number, string> = {
    0: 'info',
    1: 'success',
    2: 'primary',
    3: 'danger'
  }
  return typeMap[status] || 'info'
}