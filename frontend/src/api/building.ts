import request from '@/utils/request'

// 宿舍楼请求参数
export interface BuildingParams {
  buildingNo?: string
  buildingName?: string
  status?: number
  current?: number
  size?: number
}

// 宿舍楼表单数据
export interface BuildingForm {
  id?: string
  buildingNo: string
  buildingName: string
  floorCount: number
  description?: string
  status: number
}

// 宿舍楼信息
export interface Building {
  id: string
  buildingNo: string
  buildingName: string
  floorCount: number
  description?: string
  status: number
  statusText: string
  createTime: string
  updateTime: string
  createBy?: string
  updateBy?: string
}

/**
 * 分页查询宿舍楼列表
 */
export function getBuildingPage(params: BuildingParams): Promise<{
  records: Building[]
  total: number
  current: number
  size: number
}> {
  return request.post('/building/page', params)
}

/**
 * 根据ID获取宿舍楼详情
 */
export function getBuildingById(id: string): Promise<Building> {
  return request.get(`/building/${id}`)
}

/**
 * 新增宿舍楼
 */
export function createBuilding(data: BuildingForm): Promise<void> {
   return request.post('/building', data)
}

/**
 * 更新宿舍楼DTO
 */
export interface BuildingUpdateDTO {
  id: string
  buildingNo?: string
  buildingName?: string
  floorCount?: number
  description?: string
  status?: number
}

/**
 * 更新宿舍楼
 */
export function updateBuilding(data: BuildingUpdateDTO): Promise<void> {
   return request.post('/building/update', data)
}

/**
 * 删除宿舍楼
 */
export function deleteBuilding(id: string): Promise<void> {
   return request.delete(`/building/${id}`)
}

/**
 * 修改宿舍楼状态
 */
export function updateBuildingStatus(id: string, status: number): Promise<void> {
   return request.get(`/building/status`, { params: { id, status } })
}