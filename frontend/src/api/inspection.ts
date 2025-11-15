import request from '@/utils/request'

// 卫生检查接口定义
export interface Inspection {
  id: number
  roomId: number
  roomNo: string
  buildingId: number
  buildingName: string
  inspectionDate: string
  score: number
  level: string
  levelText: string
  inspectorId: number | null
  inspectorName: string
  remarks: string | null
  issues: string | null
  imageList: string[]
  createTime: string
  updateTime: string
}

// 卫生检查表单接口
export interface InspectionForm {
  id?: number
  roomId: number
  inspectionDate: string
  score: number
  level: string
  remarks: string
  issues: string
  images: string[]
}

// 卫生检查查询参数
export interface InspectionParams {
  current: number
  size: number
  roomId?: number
  roomNo?: string
  buildingId?: number
  startDate?: string
  endDate?: string
  level?: string
}

/**
 * 分页查询卫生检查记录
 */
export function getInspectionPage(params: InspectionParams) {
  return request({
    url: '/inspection/page',
    method: 'post',
    data: params
  })
}

/**
 * 根据ID查询检查详情
 */
export function getInspectionById(id: number) {
  return request({
    url: `/inspection/${id}`,
    method: 'get'
  })
}

/**
 * 新增卫生检查记录
 */
export function addInspection(data: InspectionForm) {
  return request({
    url: '/inspection',
    method: 'post',
    data
  })
}

/**
 * 更新卫生检查记录
 */
export function updateInspection(data: InspectionForm) {
  return request({
    url: '/inspection',
    method: 'put',
    data
  })
}

/**
 * 删除卫生检查记录
 */
export function deleteInspection(id: number) {
  return request({
    url: `/inspection/${id}`,
    method: 'delete'
  })
}

/**
 * 批量删除检查记录
 */
export function batchDeleteInspection(ids: number[]) {
  return request({
    url: '/inspection/batch',
    method: 'delete',
    data: ids
  })
}

/**
 * 获取宿舍选项列表
 */
export function getDormitoryOptions() {
  return request({
    url: '/inspection/dormitories/options',
    method: 'get'
  })
}

/**
 * 获取检查统计信息
 */
export function getInspectionStatistics(startDate?: string, endDate?: string) {
  return request({
    url: '/inspection/statistics',
    method: 'get',
    params: { startDate, endDate }
  })
}

/**
 * 获取不合格宿舍列表
 */
export function getFailedInspections(days?: number) {
  return request({
    url: '/inspection/failed',
    method: 'get',
    params: { days }
  })
}

/**
 * 获取宿舍检查历史
 */
export function getInspectionHistory(roomId: number, limit?: number) {
  return request({
    url: `/inspection/history/${roomId}`,
    method: 'get',
    params: { limit }
  })
}

/**
 * 获取检查等级选项
 */
export function getInspectionLevelOptions() {
  return [
    { label: '优秀', value: 'excellent', type: 'success' },
    { label: '良好', value: 'good', type: 'primary' },
    { label: '合格', value: 'pass', type: 'warning' },
    { label: '不合格', value: 'fail', type: 'danger' }
  ]
}

/**
 * 根据分数获取等级
 */
export function getLevelByScore(score: number): string {
  if (score >= 90) return 'excellent'
  if (score >= 80) return 'good'
  if (score >= 60) return 'pass'
  return 'fail'
}

/**
 * 根据等级获取文本
 */
export function getLevelText(level: string): string {
  const levelMap: Record<string, string> = {
    excellent: '优秀',
    good: '良好',
    pass: '合格',
    fail: '不合格'
  }
  return levelMap[level] || '未知'
}

/**
 * 根据等级获取标签类型
 */
export function getLevelType(level: string): string {
  const typeMap: Record<string, string> = {
    excellent: 'success',
    good: 'primary',
    pass: 'warning',
    fail: 'danger'
  }
  return typeMap[level] || 'info'
}