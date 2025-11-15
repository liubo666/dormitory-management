import request from '@/utils/request'

// 费用类型选项
export const FEE_TYPE_OPTIONS = [
  { value: 1, label: '住宿费' },
  { value: 2, label: '水电费' },
  { value: 3, label: '网费' },
  { value: 4, label: '其他费用' }
]

// 计费周期选项
export const BILLING_CYCLE_OPTIONS = [
  { value: 1, label: '按月' },
  { value: 2, label: '按学期' },
  { value: 3, label: '按年' },
  { value: 4, label: '一次性' }
]

// 支付状态选项
export const PAYMENT_STATUS_OPTIONS = [
  { value: 0, label: '未支付', type: 'danger' },
  { value: 1, label: '部分支付', type: 'warning' },
  { value: 2, label: '已支付', type: 'success' }
]

// 支付方式选项
export const PAYMENT_METHOD_OPTIONS = [
  { value: 1, label: '现金' },
  { value: 2, label: '银行转账' },
  { value: 3, label: '微信' },
  { value: 4, label: '支付宝' },
  { value: 5, label: '校园卡' }
]

// 费用查询参数接口
export interface FeePageParams {
  current?: number
  size?: number
  feeType?: number
  feeName?: string
  studentId?: number
  studentName?: string
  studentNo?: string
  roomId?: number
  roomNo?: string
  buildingId?: number
  buildingName?: string
  paymentStatus?: number
  collectorId?: number
  collectorName?: string
  startDate?: string
  endDate?: string
}

// 费用数据接口
export interface FeeItem {
  id: string
  feeType: number
  feeTypeName: string
  feeName: string
  description?: string
  amount: number
  billingCycle: number
  billingCycleName: string
  studentId?: string
  studentName?: string
  studentNo?: string
  roomId?: string
  buildingNo?: string
  buildingName?: string
  roomNo?: string
  startTime?: string
  endTime?: string
  paymentStatus: number
  paymentStatusName: string
  paidAmount: number
  unpaidAmount: number
  paymentTime?: string
  paymentMethod?: number
  collectorId?: string
  collectorName?: string
  dueDate?: string
  invoiceNo?: string
  remark?: string
  createTime: string
  updateTime: string
  createBy?: string
  updateBy?: string
}

// 费用表单数据接口
export interface FeeForm {
  feeType?: number
  feeName?: string
  description?: string
  amount?: number
  billingCycle?: number
  studentId?: string
  studentName?: string
  studentNo?: string
  roomId?: string
  roomNo?: string
  buildingId?: string
  buildingName?: string
  startTime?: string
  endTime?: string
  dueDate?: string
  remark?: string
}

// 费用统计接口
export interface FeeStatistics {
  totalCount: number
  totalAmount: number
  totalPaidAmount: number
  totalUnpaidAmount: number
  unpaidCount: number
  partiallyPaidCount: number
  fullyPaidCount: number
}

// 分页查询费用列表
export const getFeePage = (params: FeePageParams) => {
  return request({
    url: '/api/fees/page',
    method: 'post',
    data: params
  })
}

// 根据ID获取费用详情
export const getFeeById = (id: string) => {
  return request({
    url: `/api/fees/${id}`,
    method: 'get'
  })
}

// 创建费用
export const createFee = (data: FeeForm) => {
  return request({
    url: '/api/fees/add',
    method: 'post',
    data
  })
}

// 更新费用
export const updateFee = (id: string, data: FeeForm) => {
  return request({
    url: `/api/fees/update/${id}`,
    method: 'put',
    data
  })
}

// 删除费用
export const deleteFee = (id: string) => {
  return request({
    url: `/api/fees/${id}`,
    method: 'delete'
  })
}

// 批量删除费用
export const batchDeleteFees = (ids: string[]) => {
  return request({
    url: '/api/fees/batch',
    method: 'delete',
    data: ids
  })
}

// 更新支付状态
export const updatePaymentStatus = (id: string, paymentStatus: number, paidAmount?: number) => {
  return request({
    url: `/api/fees/${id}/payment-status`,
    method: 'put',
    params: {
      paymentStatus,
      paidAmount
    }
  })
}

// 标记为已支付
export const markAsPaid = (id: string) => {
  return request({
    url: `/api/fees/${id}/paid`,
    method: 'put'
  })
}

// 部分支付
export const partialPayment = (id: string, paymentAmount: number) => {
  return request({
    url: `/api/fees/${id}/partial-payment`,
    method: 'put',
    params: {
      paymentAmount
    }
  })
}

// 获取费用统计信息
export const getFeeStatistics = (params: FeePageParams) => {
  return request({
    url: '/api/fees/statistics',
    method: 'post',
    data: params
  })
}