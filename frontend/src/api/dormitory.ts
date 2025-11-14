import { request } from '@/utils/request'

// 宿舍请求参数
export interface DormitoryParams {
  buildingId?: string
  roomNo?: string
  floorNumber?: number
  bedCount?: number
  occupiedBeds?: number
  status?: number
  current?: number
  size?: number
}

// 床位信息DTO（匹配后端BedInfoDTO）
export interface BedInfoDTO {
    bedNo: string
    status: number
    description: string
    isOccupied: boolean
    statusText: string
}

// 宿舍表单数据（匹配后端DormitoryDTO）
export interface DormitoryForm {
  id?: string | number
  buildingNo: string
  buildingName: string
  roomNo: string
  floorNumber: number
  bedInfos: BedInfoDTO[]
  description?: string
}

// 宿舍信息
export interface Dormitory {
  id: string
  buildingName: string
  buildingNo: string
  roomNo: string
  floorNumber: number
  totalBeds: number
  occupiedBeds: number
  availableBeds: number
  description: string
  status: number
  statusText: string
  createTime: string
  updateTime: string
  createBy: string
  updateBy: string
  bedInfos: BedInfoDTO[]
}

/**
 * 分页查询宿舍列表
 */
export function getDormitoryPage(params: DormitoryParams): Promise<{
  records: Dormitory[]
  total: number
  current: number
  size: number
}> {
  return request.post('/dormitory/page', params)
}

/**
 * 根据ID获取宿舍详情
 */
export function getDormitoryById(id: string): Promise<Dormitory> {
  return request.get(`/dormitory/${id}`)
}

/**
 * 根据楼栋ID获取宿舍列表
 */
export function getDormitoriesByBuilding(buildingId: string): Promise<Dormitory[]> {
  return request.get(`/dormitory/building/${buildingId}`)
}

/**
 * 获取可用宿舍列表
 */
export function getAvailableDormitories(buildingId?: string): Promise<Dormitory[]> {
  const params = buildingId ? { buildingId } : {}
  return request.get('/dormitory/available', { params })
}

/**
 * 新增宿舍
 */
export function createDormitory(data: DormitoryForm): Promise<void> {
  return request.post('/dormitory', data)
}

/**
 * 更新宿舍
 */
export function updateDormitory(data: DormitoryForm): Promise<void> {
  return request.put(`/dormitory/${data.id}`, data)
}

/**
 * 删除宿舍
 */
export function deleteDormitory(id: string): Promise<void> {
  return request.delete(`/dormitory/${id}`)
}

/**
 * 修改宿舍状态
 */
export function updateDormitoryStatus(id: string, status: number): Promise<void> {
  return request.get('/dormitory/status', { params: { id, status } })
}

/**
 * 获取宿舍床位信息
 */
export function getDormitoryBeds(id: string): Promise<{
  bedCount: number
  occupiedBeds: number
  availableBeds: number
  bedList: Array<{
    bedNo: string
    isOccupied: boolean
    studentName?: string
    studentNo?: string
  }>
}> {
  return request.get(`/dormitory/${id}/beds`)
}