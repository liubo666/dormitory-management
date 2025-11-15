import request from '@/utils/request'

// 统计信息接口
export interface OverallStatistics {
  studentStatistics: StudentStatistics
  dormitoryStatistics: DormitoryStatistics
  feeStatistics: FeeStatistics
  visitorStatistics: VisitorStatistics
  statisticsTime: string
}

// 学生统计信息
export interface StudentStatistics {
  totalStudents: number
  checkedInStudents: number
  pendingStudents: number
  applyingStudents: number
  checkedOutStudents: number
  rejectedStudents: number
  suspendedStudents: number
  graduatedStudents: number
  checkInRate: number
  gradeDistribution: GradeDistribution
}

// 年级分布
export interface GradeDistribution {
  grade2021: number
  grade2022: number
  grade2023: number
  grade2024: number
  otherGrades: number
}

// 宿舍统计信息
export interface DormitoryStatistics {
  totalRooms: number
  occupiedRooms: number
  availableRooms: number
  occupancyRate: number
  totalBeds: number
  occupiedBeds: number
  availableBeds: number
  bedOccupancyRate: number
  buildingDistribution: BuildingDistribution
}

// 楼栋分布
export interface BuildingDistribution {
  building1Rooms: number
  building2Rooms: number
  building3Rooms: number
  building4Rooms: number
  otherBuildingsRooms: number
  building1OccupancyRate: number
  building2OccupancyRate: number
  building3OccupancyRate: number
  building4OccupancyRate: number
  otherBuildingsOccupancyRate: number
}

// 费用统计信息
export interface FeeStatistics {
  totalFees: number
  totalAmount: number
  paidAmount: number
  unpaidAmount: number
  collectionRate: number
  unpaidCount: number
  partiallyPaidCount: number
  fullyPaidCount: number
  feeTypeDistribution: FeeTypeDistribution
  monthlyAmount: number
  yearlyAmount: number
}

// 费用类型分布
export interface FeeTypeDistribution {
  accommodationAmount: number
  utilitiesAmount: number
  internetAmount: number
  otherAmount: number
}

// 访客统计信息
export interface VisitorStatistics {
  totalVisitors: number
  todayVisitors: number
  weeklyVisitors: number
  monthlyVisitors: number
  pendingVisitors: number
  visitingVisitors: number
  completedVisitors: number
  cancelledVisitors: number
  statusDistribution: VisitorStatusDistribution
  dailyTrend: DailyVisitorTrend
}

// 访客状态分布
export interface VisitorStatusDistribution {
  pendingCount: number
  visitingCount: number
  completedCount: number
  cancelledCount: number
}

// 每日访客趋势
export interface DailyVisitorTrend {
  dailyCounts: DailyVisitorCount[]
}

// 每日访客数
export interface DailyVisitorCount {
  date: string
  count: number
}

// 获取综合统计信息
export const getOverallStatistics = () => {
  return request({
    url: '/api/statistics/overall',
    method: 'get'
  })
}

// 获取学生统计信息
export const getStudentStatistics = () => {
  return request({
    url: '/api/statistics/student',
    method: 'get'
  })
}

// 获取宿舍统计信息
export const getDormitoryStatistics = () => {
  return request({
    url: '/api/statistics/dormitory',
    method: 'get'
  })
}

// 获取费用统计信息
export const getFeeStatistics = () => {
  return request({
    url: '/api/statistics/fee',
    method: 'get'
  })
}

// 获取访客统计信息
export const getVisitorStatistics = () => {
  return request({
    url: '/api/statistics/visitor',
    method: 'get'
  })
}