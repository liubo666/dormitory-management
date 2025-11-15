<template>
  <div class="checkin-management">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="page-title">
        <h2>
          <el-icon><House /></el-icon>
          入住分配管理
        </h2>
        <p>管理学生入住申请、审批、分配和退宿等全流程</p>
      </div>
      <div class="page-actions">
        <el-button type="primary" @click="openApplicationDialog">
          <el-icon><Plus /></el-icon>
          入住申请
        </el-button>
        <el-button @click="loadStatistics">
          <el-icon><Refresh /></el-icon>
          刷新统计
        </el-button>
      </div>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-container">
      <el-row :gutter="20">
        <el-col :span="6">
          <el-card class="stats-card">
            <div class="stats-content">
              <div class="stats-icon applying">
                <el-icon><Document /></el-icon>
              </div>
              <div class="stats-info">
                <div class="stats-number">{{ statistics.applyingCount }}</div>
                <div class="stats-label">申请中</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stats-card">
            <div class="stats-content">
              <div class="stats-icon checked-in">
                <el-icon><UserFilled /></el-icon>
              </div>
              <div class="stats-info">
                <div class="stats-number">{{ statistics.checkedInCount }}</div>
                <div class="stats-label">已入住</div>
              </div>
            </div>
          </el-card>
        </el-col>
<!--        <el-col :span="6">-->
<!--          <el-card class="stats-card">-->
<!--            <div class="stats-content">-->
<!--              <div class="stats-icon checked-out">-->
<!--                <el-icon><CircleClose /></el-icon>-->
<!--              </div>-->
<!--              <div class="stats-info">-->
<!--                <div class="stats-number">{{ statistics.checkedOutCount }}</div>-->
<!--                <div class="stats-label">已退宿</div>-->
<!--              </div>-->
<!--            </div>-->
<!--          </el-card>-->
<!--        </el-col>-->
        <el-col :span="6">
          <el-card class="stats-card">
            <div class="stats-content">
              <div class="stats-icon rejected">
                <el-icon><Close /></el-icon>
              </div>
              <div class="stats-info">
                <div class="stats-number">{{ statistics.rejectedCount }}</div>
                <div class="stats-label">已拒绝</div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 搜索表单 -->
    <div class="search-container">
      <el-card shadow="never">
        <el-form :model="searchForm" inline class="search-form">
          <el-form-item label="学生姓名">
            <el-input
              v-model="searchForm.studentName"
              placeholder="请输入学生姓名"
              clearable
              style="width: 200px"
            />
          </el-form-item>
          <el-form-item label="学号">
            <el-input
              v-model="searchForm.studentNo"
              placeholder="请输入学号"
              clearable
              style="width: 200px"
            />
          </el-form-item>
                    <el-form-item label="宿舍号">
            <el-input
              v-model="searchForm.roomNo"
              placeholder="请输入宿舍号"
              clearable
              style="width: 200px"
            />
          </el-form-item>
          <el-form-item label="状态">
            <el-select
              v-model="searchForm.status"
              placeholder="请选择状态"
              clearable
              style="width: 150px"
            >
              <el-option label="申请中" :value="1" />
              <el-option label="已入住" :value="2" />
              <el-option label="已退宿" :value="3" />
              <el-option label="已拒绝" :value="4" />
            </el-select>
          </el-form-item>
          <el-form-item label="入住日期">
            <el-date-picker
              v-model="checkInDateRange"
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
            <el-button type="primary" @click="handleSearch" :loading="loading">
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

    <!-- 数据表格 -->
    <div class="table-container">
      <el-card shadow="never">
        <div class="table-header">
          <div class="table-title">
            <span>入住分配列表</span>
            <span class="table-count">共 {{ pagination.total }} 条记录</span>
          </div>
          <div class="table-actions">
            <el-button
              type="success"
              :disabled="!selectedRows.length"
              @click="handleBatchApprove"
            >
              <el-icon><Select /></el-icon>
              批量通过
            </el-button>
            <el-button
              type="danger"
              :disabled="!selectedRows.length"
              @click="handleBatchReject"
            >
              <el-icon><Close /></el-icon>
              批量拒绝
            </el-button>
          </div>
        </div>

        <el-table
          :data="tableData"
          v-loading="loading"
          stripe
          border
          style="width: 100%"
          @selection-change="handleSelectionChange"
        >
          <el-table-column type="selection" width="55" />
          <el-table-column prop="studentName" label="学生姓名" width="100" />
          <el-table-column prop="studentNo" label="学号" width="130" />
          <el-table-column prop="studentGenderText" label="性别" width="80" />
          <el-table-column prop="college" label="学院" width="120" />
          <el-table-column prop="major" label="专业" width="120" />
          <el-table-column prop="buildingName" label="楼栋" width="100" />
          <el-table-column prop="roomNo" label="宿舍号" width="100" />
          <el-table-column prop="bedNo" label="床位号" width="100" />
          <el-table-column prop="checkInDate" label="入住日期" width="120" />
          <el-table-column prop="statusText" label="状态" width="100" align="center">
            <template #default="{ row }">
              <el-tag
                :type="getStatusType(row.status)"
                effect="light"
              >
                {{ row.statusText }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="240" fixed="right">
            <template #default="{ row }">
              <el-button-group>
                <el-button size="small" @click="handleViewDetails(row)">
                  <el-icon><View /></el-icon>
                  详情
                </el-button>
                <el-button
                  v-if="row.status === 1"
                  size="small"
                  type="success"
                  @click="handleApprove(row)"
                >
                  <el-icon><Check /></el-icon>
                  审批通过
                </el-button>
<!--                <el-button-->
<!--                  v-if="row.status === 1"-->
<!--                  size="small"-->
<!--                  type="success"-->
<!--                  @click="handleAssign(row)"-->
<!--                >-->
<!--                  <el-icon><Check /></el-icon>-->
<!--                  审批通过-->
<!--                </el-button>-->
                <el-button
                  v-if="row.status === 1"
                  size="small"
                  type="danger"
                  @click="handleReject(row)"
                >
                  <el-icon><Close /></el-icon>
                  拒绝
                </el-button>
                <el-button
                  v-if="row.status === 0"
                  size="small"
                  type="danger"
                  @click="handleCancel(row)"
                >
                  <el-icon><Close /></el-icon>
                  取消
                </el-button>
<!--                <el-button-->
<!--                  v-if="row.status === 2"-->
<!--                  size="small"-->
<!--                  type="warning"-->
<!--                  @click="handleCheckout(row)"-->
<!--                >-->
<!--                  <el-icon><Switch /></el-icon>-->
<!--                  退宿-->
<!--                </el-button>-->
              </el-button-group>
            </template>
          </el-table-column>
        </el-table>

        <div class="pagination-container">
          <el-pagination
            v-model:current-page="pagination.current"
            v-model:page-size="pagination.size"
            :page-sizes="[10, 20, 50, 100]"
            :total="pagination.total"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
          />
        </div>
      </el-card>
    </div>

    <!-- 入住申请对话框 -->
    <el-dialog
      v-model="showApplicationDialog"
      title="提交入住申请"
      width="600px"
      :close-on-click-modal="false"
    >
      <el-form
        :model="applicationForm"
        :rules="applicationRules"
        ref="applicationFormRef"
        label-width="100px"
      >
        <el-form-item label="学生" prop="studentId">
          <el-select
            v-model="applicationForm.studentId"
            placeholder="请选择学生"
            filterable
            remote
            :remote-method="searchStudents"
            :loading="searchingStudents"
            style="width: 100%"
          >
            <el-option
              v-for="student in studentOptions"
              :key="student.value"
              :label="student.label"
              :value="student.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="入住日期" prop="checkInDate">
          <el-date-picker
            v-model="applicationForm.checkInDate"
            type="date"
            placeholder="请选择入住日期"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="床位选择" prop="bedId">
          <el-cascader
            v-model="selectedBedPath"
            placeholder="请选择宿舍-床位"
            :options="availableBedOptions"
            :props="{
              expandTrigger: 'hover',
              value: 'value',
              label: 'label',
              children: 'children'
            }"
            clearable
            style="width: 100%"
            @change="handleBedSelection"
          />
        </el-form-item>
        <el-form-item label="申请原因">
          <el-input
            v-model="applicationForm.applicationReason"
            type="textarea"
            :rows="3"
            placeholder="请输入申请原因"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showApplicationDialog = false">取消</el-button>
          <el-button type="primary" @click="handleSubmitApplication" :loading="submitting">
            提交申请
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 审批对话框 -->
    <el-dialog
      v-model="showApprovalDialog"
      title="审批入住申请"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form
        :model="approvalForm"
        :rules="approvalRules"
        ref="approvalFormRef"
        label-width="100px"
      >
        <el-form-item label="审批结果" prop="status">
          <el-radio-group v-model="approvalForm.status">
            <el-radio :value="2">通过</el-radio>
            <el-radio :value="4">拒绝</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="审批备注">
          <el-input
            v-model="approvalForm.approvalRemark"
            type="textarea"
            :rows="3"
            placeholder="请输入审批备注"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showApprovalDialog = false">取消</el-button>
          <el-button type="primary" @click="handleSubmitApproval" :loading="submitting">
            确认审批
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 审批通过对话框 -->
    <el-dialog
      v-model="showAssignDialog"
      title="审批通过入住申请"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form
        :model="assignForm"
        ref="assignFormRef"
        label-width="100px"
      >
        <el-form-item label="学生">
          <el-input :value="currentRecord?.studentName" disabled />
        </el-form-item>
        <el-form-item label="学号">
          <el-input :value="currentRecord?.studentNo" disabled />
        </el-form-item>
        <el-form-item label="审批备注">
          <el-input
            v-model="assignForm.approvalRemark"
            type="textarea"
            :rows="3"
            placeholder="请输入审批备注"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showAssignDialog = false">取消</el-button>
          <el-button type="success" @click="handleSubmitAssignment" :loading="submitting">
            确认审批通过
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 退宿对话框 -->
    <el-dialog
      v-model="showCheckoutDialog"
      title="退宿处理"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form
        :model="checkoutForm"
        ref="checkoutFormRef"
        label-width="100px"
      >
        <el-form-item label="学生">
          <el-input :value="currentRecord?.studentName" disabled />
        </el-form-item>
        <el-form-item label="当前宿舍">
          <el-input :value="`${currentRecord?.buildingName} ${currentRecord?.roomNo}`" disabled />
        </el-form-item>
        <el-form-item label="当前床位">
          <el-input :value="currentRecord?.bedNo" disabled />
        </el-form-item>
        <el-form-item label="退宿原因">
          <el-input
            v-model="checkoutForm.checkoutReason"
            type="textarea"
            :rows="3"
            placeholder="请输入退宿原因"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showCheckoutDialog = false">取消</el-button>
          <el-button type="warning" @click="handleSubmitCheckout" :loading="submitting">
            确认退宿
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 详情对话框 -->
    <el-dialog
      v-model="showDetailsDialog"
      title="入住详情"
      width="800px"
    >
      <el-descriptions :column="2" border>
        <el-descriptions-item label="学生姓名">{{ currentRecord?.studentName }}</el-descriptions-item>
        <el-descriptions-item label="学号">{{ currentRecord?.studentNo }}</el-descriptions-item>
        <el-descriptions-item label="性别">{{ currentRecord?.studentGenderText }}</el-descriptions-item>
        <el-descriptions-item label="学院">{{ currentRecord?.college }}</el-descriptions-item>
        <el-descriptions-item label="专业">{{ currentRecord?.major }}</el-descriptions-item>
        <el-descriptions-item label="班级">{{ currentRecord?.className }}</el-descriptions-item>
        <el-descriptions-item label="楼栋">{{ currentRecord?.buildingName }}</el-descriptions-item>
        <el-descriptions-item label="宿舍号">{{ currentRecord?.roomNo }}</el-descriptions-item>
        <el-descriptions-item label="床位号">{{ currentRecord?.bedNo }}</el-descriptions-item>
        <el-descriptions-item label="入住日期">{{ currentRecord?.checkInDate }}</el-descriptions-item>
        <el-descriptions-item label="预计退宿日期">{{ currentRecord?.expectedCheckoutDate || '未设置' }}</el-descriptions-item>
        <el-descriptions-item label="实际退宿日期">{{ currentRecord?.actualCheckoutDate || '未退宿' }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(currentRecord?.status)">
            {{ currentRecord?.statusText }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="申请原因">{{ currentRecord?.applicationReason || '无' }}</el-descriptions-item>
        <el-descriptions-item label="审批人">{{ currentRecord?.approver || '未审批' }}</el-descriptions-item>
        <el-descriptions-item label="审批时间">{{ currentRecord?.approvalTime || '未审批' }}</el-descriptions-item>
        <el-descriptions-item label="审批备注">{{ currentRecord?.approvalRemark || '无' }}</el-descriptions-item>
        <el-descriptions-item label="退宿原因">{{ currentRecord?.checkoutReason || '无' }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ currentRecord?.createTime }}</el-descriptions-item>
        <el-descriptions-item label="更新时间">{{ currentRecord?.updateTime }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showDetailsDialog = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance } from 'element-plus'
import {
  House,
  Plus,
  Refresh,
  Document,
  UserFilled,
  CircleClose,
  Close,
  Search,
  RefreshRight,
  Select,
  Switch,
  View,
  Check,
  Position
} from '@element-plus/icons-vue'
import {
  getCheckInPage,
  submitApplication,
  approveApplication,
  checkout,
  cancelApplication,
  batchApprove,
  getCheckInStatistics,
  getAvailableStudents,
  getAvailableBeds
} from '@/api/checkIn'
import type { CheckIn, CheckInForm, CheckInParams, AvailableStudentsParams, AvailableStudentsResponse, CheckInApprovalDTO, BatchCheckInApprovalDTO } from '@/api/checkIn'

// 响应式数据
const loading = ref(false)
const submitting = ref(false)
const searchingStudents = ref(false)
const tableData = ref<CheckIn[]>([])
const selectedRows = ref<CheckIn[]>([])
const currentRecord = ref<CheckIn | null>(null)

// 统计数据
const statistics = ref({
  totalCount: 0,
  applyingCount: 0,
  checkedInCount: 0,
  checkedOutCount: 0,
  rejectedCount: 0
})

// 搜索表单
const searchForm = reactive<CheckInParams>({
  studentName: '',
  studentNo: '',
  roomNo: '',
  status: undefined,
  checkInStartDate: '',
  checkInEndDate: '',
  current: 1,
  size: 10
})

// 分页数据
const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

// 日期范围
const checkInDateRange = ref<[string, string] | []>([])

// 床位选择相关
const selectedBedPath = ref<string[]>([])
const availableBedOptions = ref<Array<{
  value: string
  label: string
  children?: Array<{
    value: string
    label: string
  }>
}>>([])

// 对话框状态
const showApplicationDialog = ref(false)
const showApprovalDialog = ref(false)
const showAssignDialog = ref(false)
const showCheckoutDialog = ref(false)
const showDetailsDialog = ref(false)

// 表单引用
const applicationFormRef = ref<FormInstance>()
const approvalFormRef = ref<FormInstance>()
const assignFormRef = ref<FormInstance>()
const checkoutFormRef = ref<FormInstance>()

// 申请表单
const applicationForm = reactive<CheckInForm>({
  studentId: '',
  dormitoryId: '',
  bedId: '',
  bedNo: '',
  buildingName: '',
  roomNo: '',
  checkInDate: '',
  applicationReason: ''
})

// 审批表单
const approvalForm = reactive({
  status: 2,
  approvalRemark: ''
})

// 分配表单（审批通过）
const assignForm = reactive({
  approvalRemark: ''
})

// 退宿表单
const checkoutForm = reactive({
  checkoutReason: ''
})

// 选项数据
const studentOptions = ref<{label: string, value: string}[]>([])

// 表单验证规则
const applicationRules = {
  studentId: [{ required: true, message: '请选择学生', trigger: 'change' }],
  bedId: [{ required: true, message: '请选择床位', trigger: 'change' }],
  checkInDate: [{ required: true, message: '请选择入住日期', trigger: 'change' }]
}

const approvalRules = {
  status: [{ required: true, message: '请选择审批结果', trigger: 'change' }]
}


// 获取状态类型
const getStatusType = (status: number) => {
  switch (status) {
    case 1: return 'warning'
    case 2: return 'success'
    case 3: return 'info'
    case 4: return 'danger'
    default: return 'info'
  }
}

// 加载数据
const loadData = async () => {
  loading.value = true
  try {
    const params = {
      ...searchForm,
      current: pagination.current,
      size: pagination.size,
      checkInStartDate: checkInDateRange.value[0] || '',
      checkInEndDate: checkInDateRange.value[1] || ''
    }

    const response = await getCheckInPage(params)
    tableData.value = response.records
    pagination.total = response.total
  } catch (error) {
    ElMessage.error('获取数据失败')
    console.error(error)
  } finally {
    loading.value = false
  }
}

// 加载统计数据
const loadStatistics = async () => {
  try {
    const response = await getCheckInStatistics()
    statistics.value = response
  } catch (error) {
    ElMessage.error('获取统计数据失败')
    console.error(error)
  }
}

// 搜索可申请学生
const searchStudents = async (query: string) => {
  searchingStudents.value = true
  try {
    const params: AvailableStudentsParams = {
      pageIndex: 1,
      pageSize: 50,
      keyword: query
    }
    const response: AvailableStudentsResponse = await getAvailableStudents(params)
    studentOptions.value = response.records.map(student => ({
      label: `${student.name} (${student.studentNo}) - ${student.college}${student.major}`,
      value: student.id
    }))
  } catch (error) {
    console.error(error)
    ElMessage.error('获取学生列表失败')
    studentOptions.value = []
  } finally {
    searchingStudents.value = false
  }
}

// 加载可用床位
const loadAvailableBeds = async () => {
  try {
    const beds = await getAvailableBeds('')
    availableBedOptions.value = beds.map(dorm => ({
      value: dorm.dormitoryId,
      label: `${dorm.buildingName} - ${dorm.roomNo}`,
      children: dorm.bedList.length > 0 ? dorm.bedList.map(bed => ({
        value: bed.bedId,  // 使用床位ID作为值
        label: bed.bedNo   // 使用床位号作为显示文本
      })) : []
    }))
  } catch (error) {
    console.error(error)
    ElMessage.error('获取可用床位失败')
  }
}

// 床位选择处理
const handleBedSelection = (value: string[]) => {
  if (value && value.length === 2) {
    applicationForm.dormitoryId = value[0]  // 宿舍ID
    applicationForm.bedId = value[1]       // 床位ID（来自后端的bedId字段）

    // 找到对应的宿舍和床位信息
    const dormitory = availableBedOptions.value.find(d => d.value === value[0])
    if (dormitory && dormitory.children) {
      const bed = dormitory.children.find(bed => bed.value === value[1])
      if (bed) {
        applicationForm.bedNo = bed.label        // 床位号（显示文本）
        // 从宿舍标签中解析出楼栋名称和房间号
        const dormLabel = dormitory.label // 格式: "楼栋名 - 房间号"
        const parts = dormLabel.split(' - ')
        if (parts.length >= 2) {
          applicationForm.buildingName = parts[0]  // 楼栋名称
          applicationForm.roomNo = parts[1]        // 房间号
        }
      }
    }
  } else {
    applicationForm.bedId = ''
    applicationForm.dormitoryId = ''
    applicationForm.bedNo = ''
    applicationForm.buildingName = ''
    applicationForm.roomNo = ''
  }
}

// 搜索
const handleSearch = () => {
  pagination.current = 1
  loadData()
}

// 重置
const handleReset = () => {
  Object.assign(searchForm, {
    studentName: '',
    studentNo: '',
    roomNo: '',
    status: undefined,
    checkInStartDate: '',
    checkInEndDate: '',
    current: 1,
    size: 10
  })
  checkInDateRange.value = []
  pagination.current = 1
  loadData()
}

// 分页变化
const handleSizeChange = (size: number) => {
  pagination.size = size
  pagination.current = 1
  loadData()
}

const handleCurrentChange = (current: number) => {
  pagination.current = current
  loadData()
}

// 选择变化
const handleSelectionChange = (rows: CheckIn[]) => {
  selectedRows.value = rows
}

// 提交申请
const handleSubmitApplication = async () => {
  if (!applicationFormRef.value) return

  await applicationFormRef.value.validate(async (valid) => {
    if (valid) {
      submitting.value = true
      try {
        await submitApplication(applicationForm)
        ElMessage.success('申请提交成功')
        showApplicationDialog.value = false
        Object.assign(applicationForm, {
          studentId: '',
          dormitoryId: '',
          bedId: '',
          bedNo: '',
          buildingName: '',
          roomNo: '',
          checkInDate: '',
          applicationReason: ''
        })
        selectedBedPath.value = []
        loadData()
        loadStatistics()
      } catch (error: any) {
        console.error(error)
        // 提取具体的错误信息
        let errorMessage = '申请提交失败'
        if (error && error.response && error.response.data) {
          const data = error.response.data
          if (data.message) {
            errorMessage = data.message
          } else if (data.msg) {
            errorMessage = data.msg
          }
        } else if (error && error.message) {
          errorMessage = error.message
        }
        ElMessage.error(errorMessage)
      } finally {
        submitting.value = false
      }
    }
  })
}

// 审批
const handleApprove = (row: CheckIn) => {
  currentRecord.value = row
  Object.assign(approvalForm, {
    status: 2,
    approvalRemark: ''
  })
  showApprovalDialog.value = true
}

const handleSubmitApproval = async () => {
  if (!currentRecord.value || !approvalFormRef.value) return

  await approvalFormRef.value.validate(async (valid) => {
    if (valid) {
      submitting.value = true
      if (!currentRecord.value) {
        ElMessage.warning('请选择需要分配的记录')
        return
      }
      try {
        const approvalData: CheckInApprovalDTO = {
          id: currentRecord.value.id,
          status: approvalForm.status,
          approvalRemark: approvalForm.approvalRemark
        }
        await approveApplication(approvalData)
        ElMessage.success('审批成功')
        showApprovalDialog.value = false
        loadData()
        loadStatistics()
      } catch (error) {
        ElMessage.error('审批失败')
        console.error(error)
      } finally {
        submitting.value = false
      }
    }
  })
}

// 审批通过
const handleAssign = (row: CheckIn) => {
  currentRecord.value = row
  Object.assign(assignForm, {
    approvalRemark: ''
  })
  showAssignDialog.value = true
}


const handleSubmitAssignment = async () => {
  if (!currentRecord.value) return

  submitting.value = true
  try {
    const approvalData: CheckInApprovalDTO = {
      id: currentRecord.value.id,
      status: 2, // 已入住
      approvalRemark: assignForm.approvalRemark || '审批通过'
    }
    await approveApplication(approvalData)
    ElMessage.success('审批通过成功')
    showAssignDialog.value = false
    loadData()
    loadStatistics()
  } catch (error) {
    ElMessage.error('审批通过失败')
    console.error(error)
  } finally {
    submitting.value = false
  }
}

// 拒绝
const handleReject = (row: CheckIn) => {
  currentRecord.value = row

  ElMessageBox.prompt('请输入拒绝原因', '拒绝入住申请', {
    confirmButtonText: '确定拒绝',
    cancelButtonText: '取消',
    type: 'warning',
    inputPlaceholder: '请输入拒绝原因',
    inputValidator: (value) => {
      if (!value || !value.trim()) {
        return '拒绝原因不能为空'
      }
      return true
    }
  }).then(async ({ value }) => {
    try {
      const approvalData: CheckInApprovalDTO = {
        id: currentRecord.value!.id,
        status: 4, // 已拒绝
        approvalRemark: value
      }
      await approveApplication(approvalData)
      ElMessage.success('拒绝成功')
      loadData()
      loadStatistics()
    } catch (error) {
      ElMessage.error('拒绝失败')
      console.error(error)
    }
  }).catch(() => {
    ElMessage.info('已取消拒绝操作')
  })
}

// 退宿
const handleCheckout = (row: CheckIn) => {
  currentRecord.value = row
  checkoutForm.checkoutReason = ''
  showCheckoutDialog.value = true
}

const handleSubmitCheckout = async () => {
  if (!currentRecord.value) return

  submitting.value = true
  try {
    await checkout(currentRecord.value.id, checkoutForm.checkoutReason)
    ElMessage.success('退宿成功')
    showCheckoutDialog.value = false
    loadData()
    loadStatistics()
  } catch (error) {
    ElMessage.error('退宿失败')
    console.error(error)
  } finally {
    submitting.value = false
  }
}

// 取消申请
const handleCancel = (row: CheckIn) => {
  ElMessageBox.confirm('确定要取消该入住申请吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await cancelApplication(row.id, '用户主动取消')
      ElMessage.success('申请取消成功')
      loadData()
      loadStatistics()
    } catch (error) {
      ElMessage.error('申请取消失败')
      console.error(error)
    }
  })
}

// 批量通过
const handleBatchApprove = () => {
  if (!selectedRows.value.length) return

  ElMessageBox.confirm(`确定要批量通过选中的 ${selectedRows.value.length} 条入住申请吗？`, '批量通过确认', {
    confirmButtonText: '确定通过',
    cancelButtonText: '取消',
    type: 'success',
    dangerouslyUseHTMLString: true,
    message: `
      <div style="color: #67C23A; font-size: 16px; margin-bottom: 10px;">
        <i class="el-icon-success"></i> 确定批量通过以下入住申请？
      </div>
      <div style="color: #909399; font-size: 14px;">
        共 ${selectedRows.value.length} 条记录，通过后将自动分配宿舍
      </div>
    `
  }).then(async () => {
    try {
      const ids = selectedRows.value.map(row => row.id)
      const batchApprovalData: BatchCheckInApprovalDTO = {
        ids: ids,
        status: 2, // 已入住
        approvalRemark: '批量审批通过'
      }
      await batchApprove(batchApprovalData)
      ElMessage.success('批量通过成功')
      loadData()
      loadStatistics()
    } catch (error) {
      ElMessage.error('批量通过失败')
      console.error(error)
    }
  }).catch(() => {
    ElMessage.info('已取消批量通过操作')
  })
}

// 批量拒绝
const handleBatchReject = () => {
  if (!selectedRows.value.length) return

  ElMessageBox.prompt('请输入拒绝原因', '批量拒绝确认', {
    confirmButtonText: '确定拒绝',
    cancelButtonText: '取消',
    type: 'warning',
    inputPlaceholder: '请输入拒绝原因',
    inputValidator: (value) => {
      if (!value || !value.trim()) {
        return '拒绝原因不能为空'
      }
      return true
    },
    dangerouslyUseHTMLString: true,
    message: `
      <div style="color: #F56C6C; font-size: 16px; margin-bottom: 10px;">
        <i class="el-icon-warning"></i> 确定要拒绝以下入住申请？
      </div>
      <div style="color: #909399; font-size: 14px; margin-bottom: 15px;">
        共 ${selectedRows.value.length} 条记录，拒绝后将无法恢复
      </div>
    `
  }).then(async ({ value }) => {
    try {
      const ids = selectedRows.value.map(row => row.id)
      const batchApprovalData: BatchCheckInApprovalDTO = {
        ids: ids,
        status: 4, // 已拒绝
        approvalRemark: `批量拒绝：${value}`
      }
      await batchApprove(batchApprovalData)
      ElMessage.success('批量拒绝成功')
      loadData()
      loadStatistics()
    } catch (error) {
      ElMessage.error('批量拒绝失败')
      console.error(error)
    }
  }).catch(() => {
    ElMessage.info('已取消批量拒绝操作')
  })
}

// 批量退宿
const handleBatchCheckout = () => {
  if (!selectedRows.value.length) return

  ElMessageBox.confirm(`确定要批量退宿选中的 ${selectedRows.value.length} 条记录吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const ids = selectedRows.value.map(row => row.id)
      for (const id of ids) {
        await checkout(id, '批量退宿')
      }
      ElMessage.success('批量退宿成功')
      loadData()
      loadStatistics()
    } catch (error) {
      ElMessage.error('批量退宿失败')
      console.error(error)
    }
  })
}

// 查看详情
const handleViewDetails = (row: CheckIn) => {
  currentRecord.value = row
  showDetailsDialog.value = true
}

// 打开申请对话框
const openApplicationDialog = () => {
  showApplicationDialog.value = true
  searchStudents('') // 预加载学生列表
  loadAvailableBeds() // 加载可用床位
}

// 初始化
onMounted(() => {
  loadData()
  loadStatistics()
})
</script>

<style scoped>
.checkin-management {
  padding: 24px;
  min-height: calc(100vh - 84px);
  background: #f5f7fa;
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

.page-actions {
  display: flex;
  gap: 12px;
}

.stats-container {
  margin-bottom: 24px;
}

.stats-card {
  border-radius: 12px;
  overflow: hidden;
}

.stats-content {
  display: flex;
  align-items: center;
  padding: 8px 0;
}

.stats-icon {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 16px;
  font-size: 24px;
  color: white;
}

.stats-icon.applying {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.stats-icon.checked-in {
  background: linear-gradient(135deg, #84fab0 0%, #8fd3f4 100%);
}

.stats-icon.checked-out {
  background: linear-gradient(135deg, #ffecd2 0%, #fcb69f 100%);
}

.stats-icon.rejected {
  background: linear-gradient(135deg, #ff9a9e 0%, #fecfef 100%);
}

.stats-info {
  flex: 1;
}

.stats-number {
  font-size: 28px;
  font-weight: 700;
  color: #1e293b;
  line-height: 1;
  margin-bottom: 4px;
}

.stats-label {
  font-size: 14px;
  color: #64748b;
  font-weight: 500;
}

.search-container {
  margin-bottom: 24px;
}

.table-container {
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
  overflow: hidden;
}

.table-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 20px 0 20px;
  margin-bottom: 16px;
}

.table-title {
  display: flex;
  align-items: center;
  font-size: 16px;
  font-weight: 600;
  color: #1e293b;
}

.table-count {
  margin-left: 12px;
  font-size: 14px;
  color: #64748b;
  font-weight: normal;
}

.table-actions {
  display: flex;
  gap: 12px;
}

.pagination-container {
  display: flex;
  justify-content: center;
  padding: 20px;
  border-top: 1px solid #f1f5f9;
  margin-top: 16px;
}

.search-form {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
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

:deep(.el-card__body) {
  padding: 20px;
}

:deep(.el-pagination) {
  --el-pagination-button-bg-color: #ffffff;
}

:deep(.el-form-item) {
  margin-bottom: 0;
}

:deep(.el-form-item__label) {
  font-weight: 500;
  color: #374151;
}

:deep(.el-descriptions__label) {
  font-weight: 600;
  color: #374151;
  background-color: #f8fafc;
}

@media (max-width: 1200px) {
  .stats-container .el-col {
    margin-bottom: 16px;
  }
}

@media (max-width: 768px) {
  .checkin-management {
    padding: 16px;
  }

  .page-header {
    flex-direction: column;
    gap: 16px;
  }

  .page-actions {
    width: 100%;
    justify-content: flex-start;
  }

  .table-header {
    flex-direction: column;
    gap: 16px;
    align-items: flex-start;
  }

  .table-actions {
    width: 100%;
    justify-content: flex-start;
  }

  .search-form {
    flex-direction: column;
  }

  .search-form .el-form-item {
    width: 100%;
  }

  .search-form .el-input,
  .search-form .el-select {
    width: 100%;
  }
}
</style>