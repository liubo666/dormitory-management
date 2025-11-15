<template>
  <div class="fee-management">
    <div class="page-header">
      <div class="page-title">
        <h2>
          <el-icon><Money /></el-icon>
          费用管理
        </h2>
        <p>管理宿舍费用信息，包括住宿费、水电费、网费等</p>
      </div>
      <div class="page-actions">
        <el-button type="primary" @click="handleAdd" size="large">
          <el-icon><Plus /></el-icon>
          新增费用
        </el-button>
      </div>
    </div>

    <!-- 统计信息 -->
    <div class="statistics-section">
      <el-card shadow="never">
        <div class="statistics-cards">
          <div class="stat-card">
            <div class="stat-icon total">
              <el-icon><DataAnalysis /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ statistics.totalCount }}</div>
              <div class="stat-label">总费用数</div>
            </div>
          </div>
          <div class="stat-card">
            <div class="stat-icon amount">
              <el-icon><Money /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-value">¥{{ statistics.totalAmount?.toFixed(2) || '0.00' }}</div>
              <div class="stat-label">总费用金额</div>
            </div>
          </div>
          <div class="stat-card">
            <div class="stat-icon paid">
              <el-icon><Select /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ statistics.fullyPaidCount }}</div>
              <div class="stat-label">已支付</div>
            </div>
          </div>
          <div class="stat-card">
            <div class="stat-icon unpaid">
              <el-icon><CloseBold /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ statistics.unpaidCount }}</div>
              <div class="stat-label">未支付</div>
            </div>
          </div>
        </div>
      </el-card>
    </div>

    <div class="content-container">
      <div class="search-section">
        <el-card shadow="never">
          <el-form :model="searchForm" inline class="search-form">
            <el-form-item label="费用名称">
              <el-input
                v-model="searchForm.feeName"
                placeholder="请输入费用名称"
                clearable
                style="width: 200px"
              />
            </el-form-item>
            <el-form-item label="费用类型">
              <el-select
                v-model="searchForm.feeType"
                placeholder="请选择费用类型"
                clearable
                style="width: 150px"
              >
                <el-option
                  v-for="option in FEE_TYPE_OPTIONS"
                  :key="option.value"
                  :label="option.label"
                  :value="option.value"
                />
              </el-select>
            </el-form-item>
            <el-form-item label="学生姓名">
              <el-input
                v-model="searchForm.studentName"
                placeholder="请输入学生姓名"
                clearable
                style="width: 150px"
              />
            </el-form-item>
            <el-form-item label="宿舍号">
              <el-input
                v-model="searchForm.roomNo"
                placeholder="请输入宿舍号"
                clearable
                style="width: 150px"
              />
            </el-form-item>
            <el-form-item label="支付状态">
              <el-select
                v-model="searchForm.paymentStatus"
                placeholder="请选择支付状态"
                clearable
                style="width: 130px"
              >
                <el-option
                  v-for="option in PAYMENT_STATUS_OPTIONS"
                  :key="option.value"
                  :label="option.label"
                  :value="option.value"
                />
              </el-select>
            </el-form-item>
            <el-form-item label="日期范围">
              <el-date-picker
                v-model="dateRange"
                type="daterange"
                range-separator="至"
                start-placeholder="开始日期"
                end-placeholder="结束日期"
                format="YYYY-MM-DD"
                value-format="YYYY-MM-DD"
                style="width: 240px"
              />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleSearch">
                <el-icon><Search /></el-icon>
                搜索
              </el-button>
              <el-button @click="handleReset">
                <el-icon><RefreshRight /></el-icon>
                重置
              </el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </div>

      <div class="table-section">
        <el-table
          :data="tableData"
          v-loading="loading"
          stripe
          border
          style="width: 100%"
        >
          <el-table-column prop="feeName" label="费用名称" width="120" show-overflow-tooltip />
          <el-table-column prop="feeTypeName" label="费用类型" width="100">
            <template #default="{ row }">
              <el-tag :type="getFeeTypeTagType(row.feeType)">
                {{ row.feeTypeName }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="studentName" label="学生姓名" width="100" />
          <el-table-column prop="studentNo" label="学号" width="120" />
          <el-table-column prop="roomNo" label="宿舍号" width="100" />
          <el-table-column prop="amount" label="费用金额" width="120" align="right">
            <template #default="{ row }">
              <span class="amount-text">¥{{ row.amount?.toFixed(2) || '0.00' }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="paidAmount" label="已付金额" width="120" align="right">
            <template #default="{ row }">
              <span class="paid-amount">¥{{ row.paidAmount?.toFixed(2) || '0.00' }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="unpaidAmount" label="未付金额" width="120" align="right">
            <template #default="{ row }">
              <span class="unpaid-amount">¥{{ row.unpaidAmount?.toFixed(2) || '0.00' }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="paymentStatusName" label="支付状态" width="100" align="center">
            <template #default="{ row }">
              <el-tag :type="getPaymentStatusTagType(row.paymentStatus)">
                {{ row.paymentStatusName }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="dueDate" label="截止日期" width="120" />
          <el-table-column prop="createTime" label="创建时间" width="160" />
          <el-table-column label="操作" width="200" fixed="right">
            <template #default="{ row }">
              <el-button-group>
                <el-button size="small" @click="handleView(row)">
                  <el-icon><View /></el-icon>
                </el-button>
                <el-button size="small" type="primary" @click="handleEdit(row)">
                  <el-icon><Edit /></el-icon>
                </el-button>
                <el-button
                  size="small"
                  type="success"
                  @click="handlePay(row)"
                  v-if="row.paymentStatus !== 2"
                >
                  <el-icon><Money /></el-icon>
                </el-button>
                <el-button size="small" type="danger" @click="handleDelete(row)">
                  <el-icon><Delete /></el-icon>
                </el-button>
              </el-button-group>
            </template>
          </el-table-column>
        </el-table>

        <div class="table-footer">
          <div class="pagination-section">
            <el-pagination
              v-model:current-page="pagination.current"
              v-model:page-size="pagination.size"
              :page-sizes="[10, 20, 50, 100]"
              :total="pagination.total"
              layout="total, sizes, prev, pager, next, jumper"
              @size-change="handleSizeChange"
              @current-change="handlePageChange"
            />
          </div>
        </div>
      </div>
    </div>

    <!-- 费用表单对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      @close="handleDialogClose"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="formRules"
        label-width="100px"
      >
        <el-form-item label="费用类型" prop="feeType">
          <el-select
            v-model="form.feeType"
            placeholder="请选择费用类型"
            style="width: 100%"
          >
            <el-option
              v-for="option in FEE_TYPE_OPTIONS"
              :key="option.value"
              :label="option.label"
              :value="option.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="费用名称" prop="feeName">
          <el-input v-model="form.feeName" placeholder="请输入费用名称" />
        </el-form-item>
        <el-form-item label="费用描述">
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="2"
            placeholder="请输入费用描述"
          />
        </el-form-item>
        <el-form-item label="费用金额" prop="amount">
          <el-input-number
            v-model="form.amount"
            :min="0"
            :precision="2"
            style="width: 100%"
            placeholder="请输入费用金额"
          />
        </el-form-item>
        <el-form-item label="计费周期" prop="billingCycle">
          <el-select
            v-model="form.billingCycle"
            placeholder="请选择计费周期"
            style="width: 100%"
          >
            <el-option
              v-for="option in BILLING_CYCLE_OPTIONS"
              :key="option.value"
              :label="option.label"
              :value="option.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="学生信息">
          <el-select
            v-model="form.studentId"
            placeholder="请选择学生"
            filterable
            remote
            reserve-keyword
            :remote-method="handleStudentSearch"
            :loading="studentLoading"
            style="width: 100%"
            @change="handleStudentChange"
            @focus="handleStudentSearch('')"
            clearable
          >
            <el-option
              v-for="student in studentOptions"
              :key="student.value"
              :label="student.label"
              :value="student.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="宿舍信息">
          <el-select
            v-model="form.roomId"
            placeholder="请选择宿舍"
            filterable
            remote
            reserve-keyword
            :remote-method="handleDormitorySearch"
            :loading="dormitoryLoading"
            style="width: 100%"
            @change="handleDormitoryChange"
            @focus="handleDormitorySearch('')"
            clearable
          >
            <el-option
              v-for="dormitory in dormitoryOptions"
              :key="dormitory.value"
              :label="dormitory.label"
              :value="dormitory.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="费用期间">
          <el-date-picker
            v-model="feeDateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="截止日期">
          <el-date-picker
            v-model="form.dueDate"
            type="date"
            placeholder="请选择截止日期"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="备注">
          <el-input
            v-model="form.remark"
            type="textarea"
            :rows="2"
            placeholder="请输入备注"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit" :loading="submitting">
            确定
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox, FormInstance, FormRules } from 'element-plus'
import {
  Money,
  Plus,
  Search,
  RefreshRight,
  View,
  Delete,
  Edit,
  DataAnalysis,
  Select,
  CloseBold
} from '@element-plus/icons-vue'
import {
  getFeePage,
  getFeeById,
  createFee,
  updateFee,
  deleteFee,
  markAsPaid,
  getFeeStatistics,
  type FeePageParams,
  type FeeItem,
  type FeeForm,
  type FeeStatistics,
  FEE_TYPE_OPTIONS,
  BILLING_CYCLE_OPTIONS,
  PAYMENT_STATUS_OPTIONS
} from '@/api/fee'
import { searchStudents as apiSearchStudents, searchDormitories as apiSearchDormitories, type StudentSearchParams, type DormitorySearchParams } from '@/api/visitor'

const loading = ref(false)
const submitting = ref(false)
const tableData = ref<FeeItem[]>([])
const statistics = ref<FeeStatistics>({
  totalCount: 0,
  totalAmount: 0,
  totalPaidAmount: 0,
  totalUnpaidAmount: 0,
  unpaidCount: 0,
  partiallyPaidCount: 0,
  fullyPaidCount: 0
})

const searchForm = reactive<FeePageParams>({
  current: 1,
  size: 10
})

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const dateRange = ref<[string, string] | null>(null)

// 学生和宿舍搜索相关
const studentLoading = ref(false)
const dormitoryLoading = ref(false)
const studentOptions = ref<Array<{label: string, value: string}>>([])
const dormitoryOptions = ref<Array<{label: string, value: string}>>([])

// 表单相关
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref<FormInstance>()
const isEdit = ref(false)
const currentEditId = ref('')

const form = reactive<FeeForm>({
  feeType: undefined,
  feeName: '',
  description: '',
  amount: undefined,
  billingCycle: undefined,
  studentName: '',
  studentNo: '',
  roomId: '',
  roomNo: '',
  buildingId: '',
  buildingName: '',
  startTime: '',
  endTime: '',
  dueDate: '',
  remark: ''
})

const feeDateRange = ref<[string, string] | null>(null)

// 表单验证规则
const formRules: FormRules = {
  feeType: [
    { required: true, message: '请选择费用类型', trigger: 'change' }
  ],
  feeName: [
    { required: true, message: '请输入费用名称', trigger: 'blur' },
    { min: 2, max: 50, message: '费用名称长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  amount: [
    { required: true, message: '请输入费用金额', trigger: 'blur' },
    { type: 'number', min: 0, message: '费用金额必须大于等于0', trigger: 'blur' }
  ],
  billingCycle: [
    { required: true, message: '请选择计费周期', trigger: 'change' }
  ]
}

const getFeeTypeTagType = (feeType: number) => {
  switch (feeType) {
    case 1: return 'warning'  // 住宿费
    case 2: return 'primary'  // 水电费
    case 3: return 'success'  // 网费
    case 4: return 'info'     // 其他费用
    default: return 'info'
  }
}

const getPaymentStatusTagType = (paymentStatus: number) => {
  switch (paymentStatus) {
    case 0: return 'danger'   // 未支付
    case 1: return 'warning'  // 部分支付
    case 2: return 'success'  // 已支付
    default: return 'info'
  }
}

const loadData = async () => {
  loading.value = true
  try {
    const params = {
      ...searchForm,
      startDate: dateRange.value?.[0],
      endDate: dateRange.value?.[1],
      current: pagination.current,
      size: pagination.size
    }

    const response = await getFeePage(params)
    console.log('费用管理 - API返回数据:', response)

    if (response && response.records) {
      tableData.value = response.records
      pagination.total = response.total || 0
    } else if (Array.isArray(response)) {
      tableData.value = response
      pagination.total = response.length
    } else {
      tableData.value = []
      pagination.total = 0
    }
  } catch (error) {
    console.error('获取费用列表失败:', error)
    ElMessage.error('获取费用列表失败')
    tableData.value = []
    pagination.total = 0
  } finally {
    loading.value = false
  }
}

const loadStatistics = async () => {
  try {
    const params = {
      ...searchForm,
      startDate: dateRange.value?.[0],
      endDate: dateRange.value?.[1]
    }
    const response = await getFeeStatistics(params)
    if (response) {
      statistics.value = response
    }
  } catch (error) {
    console.error('获取统计信息失败:', error)
  }
}

const handleSearch = () => {
  pagination.current = 1
  loadData()
  loadStatistics()
}

const handleReset = () => {
  Object.keys(searchForm).forEach(key => {
    if (key !== 'current' && key !== 'size') {
      (searchForm as any)[key] = undefined
    }
  })
  dateRange.value = null
  handleSearch()
}

const handlePageChange = (page: number) => {
  pagination.current = page
  loadData()
}

const handleSizeChange = (size: number) => {
  pagination.size = size
  pagination.current = 1
  loadData()
}


const handleAdd = () => {
  isEdit.value = false
  dialogTitle.value = '新增费用'
  resetForm()
  dialogVisible.value = true
}

const handleEdit = async (row: FeeItem) => {
  isEdit.value = true
  dialogTitle.value = '编辑费用'
  currentEditId.value = row.id

  try {
    loading.value = true
    // 调用getFeeById获取完整的费用信息
    const feeDetails = await getFeeById(row.id)

    console.log('费用管理 - 获取到的费用详情:', feeDetails)

    if (feeDetails) {
      // 手动映射每个字段，确保数据类型正确
      form.feeType = feeDetails.feeType
      form.feeName = feeDetails.feeName || ''
      form.description = feeDetails.description || ''
      form.amount = feeDetails.amount
      form.billingCycle = feeDetails.billingCycle
      form.studentId = feeDetails.studentId
      form.studentName = feeDetails.studentName || ''
      form.studentNo = feeDetails.studentNo || ''
      form.roomId = feeDetails.roomId
      form.roomNo = feeDetails.roomNo || ''
      form.buildingId = feeDetails.roomId // 使用roomId作为buildingId
      form.buildingName = feeDetails.buildingName || ''
      form.startTime = feeDetails.startTime || ''
      form.endTime = feeDetails.endTime || ''
      form.dueDate = feeDetails.dueDate || ''
      form.remark = feeDetails.remark || ''

      // 设置日期范围
      if (feeDetails.startTime && feeDetails.endTime) {
        feeDateRange.value = [feeDetails.startTime, feeDetails.endTime]
        console.log('设置日期范围:', feeDateRange.value)
      }

      // 设置学生选项
      if (feeDetails.studentId && feeDetails.studentName && feeDetails.studentNo) {
        studentOptions.value = [{
          label: `${feeDetails.studentNo} - ${feeDetails.studentName}`,
          value: feeDetails.studentId
        }]
        console.log('设置学生选项:', studentOptions.value)
      }

      // 设置宿舍选项
      if (feeDetails.roomId && (feeDetails.buildingName || feeDetails.roomNo)) {
        dormitoryOptions.value = [{
          label: `${feeDetails.buildingName || ''} ${feeDetails.roomNo || ''}`.trim(),
          value: feeDetails.roomId
        }]
        console.log('设置宿舍选项:', dormitoryOptions.value)
      }

      console.log('最终表单数据:', form)
    }

    dialogVisible.value = true
  } catch (error) {
    console.error('获取费用详情失败:', error)
    ElMessage.error('获取费用详情失败')
  } finally {
    loading.value = false
  }
}

const handleView = (row: FeeItem) => {
  const details = `
费用名称: ${row.feeName}
费用类型: ${row.feeTypeName}
学生姓名: ${row.studentName || '-'}
学号: ${row.studentNo || '-'}
宿舍号: ${row.roomNo || '-'}
费用金额: ¥${row.amount?.toFixed(2) || '0.00'}
已付金额: ¥${row.paidAmount?.toFixed(2) || '0.00'}
未付金额: ¥${row.unpaidAmount?.toFixed(2) || '0.00'}
支付状态: ${row.paymentStatusName}
截止日期: ${row.dueDate || '-'}
创建时间: ${row.createTime}
  `.trim()

  ElMessage.info(details)
}

const handlePay = async (row: FeeItem) => {
  try {
    await ElMessageBox.confirm(
      `确认要标记费用"${row.feeName}"为已支付吗？金额：¥${row.amount?.toFixed(2) || '0.00'}`,
      '确认支付',
      {
        confirmButtonText: '确认',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    const result = await markAsPaid(row.id)
    if (result) {
      ElMessage.success('支付成功')
      loadData()
      loadStatistics()
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('支付失败:', error)
      ElMessage.error('支付失败')
    }
  }
}

const handleDelete = async (row: FeeItem) => {
  try {
    await ElMessageBox.confirm(
      `确认要删除费用"${row.feeName}"吗？金额：¥${row.amount?.toFixed(2) || '0.00'}`,
      '确认删除',
      {
        confirmButtonText: '确认',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    const result = await deleteFee(row.id)
    if (result) {
      ElMessage.success('删除成功')
      loadData()
      loadStatistics()
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
      ElMessage.error('删除失败')
    }
  }
}


const resetForm = () => {
  Object.keys(form).forEach(key => {
    (form as any)[key] = ''
  })
  form.feeType = undefined
  form.billingCycle = undefined
  form.amount = undefined
  feeDateRange.value = null
  currentEditId.value = ''

  // 清空搜索选项
  studentOptions.value = []
  dormitoryOptions.value = []
}

const handleDialogClose = () => {
  resetForm()
  formRef.value?.resetFields()
}

// 搜索学生
const handleStudentSearch = async (query: string) => {
  studentLoading.value = true
  try {
    const response = await apiSearchStudents({
      keyword: query,
      current: 1,
      size: 20
    })

    console.log('费用管理 - 学生搜索结果:', response)

    if (response && response.records) {
      studentOptions.value = response.records.map((student: any) => ({
        label: `${student.studentNo} - ${student.name}`,
        value: student.id
      }))
    } else if (Array.isArray(response)) {
      studentOptions.value = response.map((student: any) => ({
        label: `${student.studentNo} - ${student.name}`,
        value: student.id
      }))
    } else {
      studentOptions.value = []
    }
  } catch (error) {
    console.error('搜索学生失败:', error)
    studentOptions.value = []
  } finally {
    studentLoading.value = false
  }
}

// 搜索宿舍
const handleDormitorySearch = async (query: string) => {
  dormitoryLoading.value = true
  try {
    const response = await apiSearchDormitories({
      keyword: query,
      current: 1,
      size: 20
    })

    console.log('费用管理 - 宿舍搜索结果:', response)

    if (response && response.records) {
      dormitoryOptions.value = response.records.map((dormitory: any) => ({
        label: `${dormitory.buildingName} ${dormitory.roomNo}`,
        value: dormitory.id
      }))
    } else if (Array.isArray(response)) {
      dormitoryOptions.value = response.map((dormitory: any) => ({
        label: `${dormitory.buildingName} ${dormitory.roomNo}`,
        value: dormitory.id
      }))
    } else {
      dormitoryOptions.value = []
    }
  } catch (error) {
    console.error('搜索宿舍失败:', error)
    dormitoryOptions.value = []
  } finally {
    dormitoryLoading.value = false
  }
}

// 处理学生选择变化
const handleStudentChange = (studentId: string) => {
  if (!studentId) {
    form.studentName = ''
    form.studentNo = ''
    return
  }

  // 从选项中找到选中的学生信息
  const selectedStudent = studentOptions.value.find(option => option.value === studentId)
  if (selectedStudent) {
    // 解析标签获取学号和姓名
    const match = selectedStudent.label.match(/^(.+?) - (.+)$/)
    if (match) {
      form.studentNo = match[1]
      form.studentName = match[2]
    }
  }
}

// 处理宿舍选择变化
const handleDormitoryChange = (roomId: string) => {
  if (!roomId) {
    form.roomNo = ''
    form.buildingName = ''
    form.buildingId = ''
    return
  }

  // 从选项中找到选中的宿舍信息
  const selectedDormitory = dormitoryOptions.value.find(option => option.value === roomId)
  if (selectedDormitory) {
    // 解析标签获取楼栋名称和宿舍号
    const match = selectedDormitory.label.match(/^(.+?) (.+)$/)
    if (match) {
      form.buildingName = match[1]
      form.roomNo = match[2]
    }
    form.buildingId = roomId
  }
}

const handleSubmit = async () => {
  if (!formRef.value) return

  try {
    await formRef.value.validate()
    submitting.value = true

    const submitData = {
      ...form,
      startTime: feeDateRange.value?.[0],
      endTime: feeDateRange.value?.[1]
    }

    let result
    if (isEdit.value) {
      result = await updateFee(currentEditId.value, submitData)
    } else {
      result = await createFee(submitData)
    }

    if (result) {
      ElMessage.success(isEdit.value ? '更新成功' : '创建成功')
      dialogVisible.value = false
      loadData()
      loadStatistics()
    }
  } catch (error) {
    console.error('提交失败:', error)
    ElMessage.error(isEdit.value ? '更新失败' : '创建失败')
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  loadData()
  loadStatistics()
})
</script>

<style scoped>
.fee-management {
  padding: 24px;
  min-height: calc(100vh - 84px);
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 24px;
  background: white;
  padding: 24px;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
}

.page-title h2 {
  display: flex;
  align-items: center;
  margin: 0 0 8px 0;
  font-size: 24px;
  font-weight: 600;
  color: #1e40af;
}

.page-title h2 .el-icon {
  margin-right: 12px;
  font-size: 28px;
}

.page-title p {
  margin: 0;
  color: #64748b;
  font-size: 14px;
}

.statistics-section {
  margin-bottom: 24px;
}

.statistics-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
  padding: 20px;
}

.stat-card {
  display: flex;
  align-items: center;
  padding: 16px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 12px;
  color: white;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
}

.stat-icon {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 10px;
  margin-right: 12px;
  font-size: 20px;
}

.stat-icon.total {
  background: rgba(255, 255, 255, 0.2);
}

.stat-icon.amount {
  background: rgba(255, 255, 255, 0.2);
}

.stat-icon.paid {
  background: rgba(255, 255, 255, 0.2);
}

.stat-icon.unpaid {
  background: rgba(255, 255, 255, 0.2);
}

.stat-content {
  flex: 1;
}

.stat-value {
  font-size: 22px;
  font-weight: 700;
  margin-bottom: 3px;
  line-height: 1;
}

.stat-label {
  font-size: 12px;
  opacity: 0.9;
  font-weight: 500;
}

.content-container {
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
  overflow: hidden;
}

.search-section {
  padding: 20px;
  border-bottom: 1px solid #f1f5f9;
}

.search-form {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
}

.table-section {
  padding: 20px;
}

.table-footer {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #f1f5f9;
}

.pagination-section {
  display: flex;
  justify-content: center;
}

.amount-text {
  color: #f56c6c;
  font-weight: 600;
}

.paid-amount {
  color: #67c23a;
  font-weight: 600;
}

.unpaid-amount {
  color: #e6a23c;
  font-weight: 600;
}

:deep(.el-table) {
  --el-table-border-color: #f1f5f9;
  --el-table-header-bg-color: #f8fafc;
}

:deep(.el-table th) {
  font-weight: 600;
  color: #374151;
  background-color: #f8fafc;
}

:deep(.el-table .el-button-group .el-button) {
  padding: 4px 8px;
}

:deep(.el-pagination) {
  --el-pagination-button-bg-color: #ffffff;
}

:deep(.el-dialog__header) {
  padding: 20px 24px 16px;
  border-bottom: 1px solid #f1f5f9;
}

:deep(.el-dialog__body) {
  padding: 24px;
}

:deep(.el-dialog__footer) {
  padding: 16px 24px 20px;
  border-top: 1px solid #f1f5f9;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

@media (max-width: 768px) {
  .fee-management {
    padding: 16px;
  }

  .page-header {
    flex-direction: column;
    gap: 16px;
  }

  .page-actions {
    width: 100%;
  }

  .page-actions .el-button {
    width: 100%;
  }

  .statistics-cards {
    grid-template-columns: 1fr;
    padding: 16px;
  }

  .search-form {
    flex-direction: column;
  }

  .search-form .el-form-item {
    width: 100%;
  }

  .search-form .el-input,
  .search-form .el-select,
  .search-form .el-date-picker {
    width: 100%;
  }

  .pagination-section {
    width: 100%;
  }

  :deep(.el-dialog) {
    width: 90vw !important;
    margin: 5vh auto;
  }

  :deep(.el-form-item) {
    margin-bottom: 18px;
  }
}

@media (max-width: 1024px) {
  .statistics-cards {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>