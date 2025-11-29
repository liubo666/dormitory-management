import request from '@/utils/request'

// 报修信息接口定义
export interface Repair {
  id: number
  roomId: number
  roomNo: string
  buildingName: string
  studentId: number
  studentName: string
  title: string
  description: string
  type: string
  typeText: string
  priority: number
  priorityText: string
  status: number
  statusText: string
  reportTime: string
  handleTime: string | null
  completeTime: string | null
  handlerId: number | null
  handlerName: string | null
  handleRemark: string | null
  imageList: string[]
  contactPhone: string | null
  createTime: string
  updateTime: string
}

// 报修表单接口
export interface RepairForm {
  roomId: number
  studentId: number
  title: string
  description: string
  type: string
  priority: number
  contactPhone: string
  images: string[]
}

// 报修查询参数
export interface RepairParams {
  current: number
  size: number
  roomNo?: string
  roomId?: number
  buildingId?: number
  studentId?: number
  studentName?: string
  type?: string
  status?: number
  priority?: number
  startDate?: string
  endDate?: string
}

// 报修处理表单
export interface RepairHandleForm {
  id: number
  status: number
  handleRemark: string
}

/**
 * 分页查询报修列表
 */
export function getRepairPage(params: RepairParams) {
  return request({
    url: '/repair/page',
    method: 'post',
    data: params
  })
}

/**
 * 根据ID查询报修详情
 */
export function getRepairById(id: number): Promise<Repair> {
  return request({
    url: `/repair/${id}`,
    method: 'get'
  })
}

/**
 * 新增报修
 */
export function addRepair(data: RepairForm) {
  return request({
    url: '/repair',
    method: 'post',
    data
  })
}

/**
 * 处理报修
 */
export function handleRepair(data: RepairHandleForm) {
  return request({
    url: '/repair/handle',
    method: 'post',
    data
  })
}

/**
 * 删除报修
 */
export function deleteRepair(id: number) {
  return request({
    url: `/repair/${id}`,
    method: 'delete'
  })
}

/**
 * 获取宿舍选项列表
 */
export function getDormitoryOptions() {
  return request({
    url: '/repair/dormitories/options',
    method: 'get'
  })
}

/**
 * 获取学生选项列表
 */
export function getStudentOptions(keyword?: string) {
  return request({
    url: '/repair/students/options',
    method: 'get',
    params: { keyword }
  })
}

/**
 * 获取报修类型选项
 */
export function getRepairTypeOptions() {
  return [
    { label: '水电维修', value: 'water' },
    { label: '电路维修', value: 'electric' },
    { label: '门窗维修', value: 'door' },
    { label: '家具维修', value: 'furniture' },
    { label: '其他', value: 'other' }
  ]
}

/**
 * 获取报修状态选项
 */
export function getRepairStatusOptions() {
  return [
    { label: '待处理', value: 1, type: 'warning' },
    { label: '处理中', value: 2, type: 'primary' },
    { label: '已完成', value: 3, type: 'success' },
    { label: '已取消', value: 4, type: 'info' }
  ]
}

/**
 * 获取报修优先级选项
 */
export function getRepairPriorityOptions() {
  return [
    { label: '低', value: 1, type: 'info' },
    { label: '中', value: 2, type: 'warning' },
    { label: '高', value: 3, type: 'danger' }
  ]
}