<template>
  <div class="visitor-management">
    <div class="page-header">
      <div class="page-title">
        <h2>
          <el-icon><User /></el-icon>
          访客登记
        </h2>
        <p>管理访客登记信息，包括来访登记、签到签退等</p>
      </div>
      <div class="page-actions">
        <el-button type="primary" @click="openAddDialog" size="large">
          <el-icon><Plus /></el-icon>
          新增访客
        </el-button>
      </div>
    </div>

    <div class="content-container">
      <!-- 搜索表单 -->
      <div class="search-section">
        <el-card shadow="never">
          <el-form :model="searchForm" label-width="80px">
            <el-row :gutter="20">
              <el-col :span="6">
                <el-form-item label="访客姓名">
                  <el-input
                    v-model="searchForm.visitorName"
                    placeholder="请输入访客姓名"
                    clearable
                  />
                </el-form-item>
              </el-col>
              <el-col :span="6">
                <el-form-item label="访客手机">
                  <el-input
                    v-model="searchForm.visitorPhone"
                    placeholder="请输入访客手机号"
                    clearable
                  />
                </el-form-item>
              </el-col>
              <el-col :span="6">
                <el-form-item label="访问状态">
                  <el-select
                    v-model="searchForm.status"
                    placeholder="请选择状态"
                    clearable
                    style="width: 100%"
                  >
                    <el-option
                      v-for="item in statusOptions"
                      :key="item.value"
                      :label="item.label"
                      :value="item.value"
                    />
                  </el-select>
                </el-form-item>
              </el-col>
              </el-row>

            <el-row :gutter="20">
              <el-col :span="8">
                <el-form-item label="访问日期">
                  <el-date-picker
                    v-model="visitDateRange"
                    type="daterange"
                    range-separator="至"
                    start-placeholder="开始日期"
                    end-placeholder="结束日期"
                    format="YYYY-MM-DD"
                    value-format="YYYY-MM-DD"
                    style="width: 100%"
                  />
                </el-form-item>
              </el-col>
              <el-col :span="16">
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
              </el-col>
            </el-row>
          </el-form>
        </el-card>
      </div>

      <!-- 数据表格 -->
      <div class="table-section">
        <el-card shadow="never">
          <el-table
            :data="tableData"
            v-loading="loading"
            stripe
            border
            style="width: 100%"
          >
            <el-table-column prop="visitorName" label="访客姓名" width="100" />
            <el-table-column prop="visitorPhone" label="访客手机" width="120" />
            <el-table-column prop="visitStudentName" label="被访学生" width="100" />
            <el-table-column prop="roomNo" label="宿舍号" width="100" />
            <el-table-column prop="buildingName" label="楼栋" width="120" />
            <el-table-column prop="expectedVisitTime" label="预计访问时间" width="160">
              <template #default="{ row }">
                {{ formatDate(row.expectedVisitTime) }}
              </template>
            </el-table-column>
            <el-table-column prop="statusText" label="状态" width="100" align="center">
              <template #default="{ row }">
                <el-tag :type="getStatusType(row.status)">
                  {{ row.statusText }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="registrarName" label="登记人" width="120" />
            <el-table-column label="操作" width="280" fixed="right">
              <template #default="{ row }">
                <el-button-group>
                  <el-button size="small" @click="handleView(row)">
                    <el-icon><View /></el-icon>
                    详情
                  </el-button>
                  <el-button size="small" type="primary" @click="handleEdit(row)">
                    <el-icon><Edit /></el-icon>
                    编辑
                  </el-button>
                  <el-button
                    size="small"
                    type="success"
                    @click="handleCheckIn(row)"
                    :disabled="row.status !== 0"
                  >
                    <el-icon><CircleCheck /></el-icon>
                    签到
                  </el-button>
                  <el-button
                    size="small"
                    type="warning"
                    @click="handleCheckOut(row)"
                    :disabled="row.status !== 1"
                  >
                    <el-icon><CircleClose /></el-icon>
                    签退
                  </el-button>
                  <el-button size="small" type="danger" @click="handleDelete(row)">
                    <el-icon><Delete /></el-icon>
                    删除
                  </el-button>
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
    </div>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="showDialog"
      :title="isEdit ? '编辑访客登记' : '新增访客登记'"
      width="800px"
      :close-on-click-modal="false"
    >
      <el-form
        :model="form"
        :rules="rules"
        ref="formRef"
        label-width="100px"
        label-position="right"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="访客姓名" prop="visitorName">
              <el-input
                v-model="form.visitorName"
                placeholder="请输入访客姓名"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="访客手机" prop="visitorPhone">
              <el-input
                v-model="form.visitorPhone"
                placeholder="请输入访客手机号"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="身份证号" prop="visitorIdCard">
              <el-input
                v-model="form.visitorIdCard"
                placeholder="请输入身份证号"
              />
            </el-form-item>
          </el-col>
          <el-col :span="16">
            <el-form-item label="被访学生" prop="visitStudentName">
              <el-select
                v-model="selectedStudent"
                placeholder="请选择或搜索学生"
                filterable
                remote
                :remote-method="handleStudentSearch"
                :loading="studentSearchLoading"
                style="width: 100%"
                @change="handleStudentChange"
              >
                <el-option
                  v-for="student in studentOptions"
                  :key="student.value"
                  :label="student.label"
                  :value="student.value"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="访问宿舍" prop="roomId">
              <el-select
                v-model="selectedDormitory"
                placeholder="请选择或搜索宿舍"
                filterable
                remote
                :remote-method="handleDormitorySearch"
                :loading="dormitorySearchLoading"
                clearable
                style="width: 100%"
                @change="handleDormitoryChange"
              >
                <el-option
                  v-for="room in dormitoryOptions"
                  :key="room.value"
                  :label="room.label"
                  :value="room.value"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="预计访问时间" prop="expectedVisitTime">
              <el-date-picker
                v-model="form.expectedVisitTime"
                type="datetime"
                placeholder="请选择预计访问时间"
                format="YYYY-MM-DD HH:mm:ss"
                value-format="YYYY-MM-DD HH:mm:ss"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="访问状态" prop="status">
              <el-select
                v-model="form.status"
                placeholder="请选择状态"
                style="width: 100%"
              >
                <el-option
                  v-for="item in statusOptions"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row>
          <el-col :span="24">
            <el-form-item label="访问事由">
              <el-input
                v-model="form.visitPurpose"
                type="textarea"
                :rows="3"
                placeholder="请输入访问事由"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row>
          <el-col :span="24">
            <el-form-item label="备注">
              <el-input
                v-model="form.remarks"
                type="textarea"
                :rows="2"
                placeholder="请输入备注信息"
              />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showDialog = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit" :loading="submitting">
            {{ isEdit ? '更新' : '提交' }}
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 详情对话框 -->
    <el-dialog
      v-model="showDetailDialog"
      title="访客登记详情"
      width="600px"
    >
      <el-descriptions :column="2" border v-if="currentRecord">
        <el-descriptions-item label="访客姓名">{{ currentRecord.visitorName }}</el-descriptions-item>
        <el-descriptions-item label="访客手机">{{ currentRecord.visitorPhone }}</el-descriptions-item>
        <el-descriptions-item label="身份证号">{{ currentRecord.visitorIdCard || '无' }}</el-descriptions-item>
        <el-descriptions-item label="被访学生">{{ currentRecord.visitStudentName }}</el-descriptions-item>
        <el-descriptions-item label="学生学号">{{ currentRecord.visitStudentNo || '无' }}</el-descriptions-item>
        <el-descriptions-item label="宿舍号">{{ currentRecord.roomNo }}</el-descriptions-item>
        <el-descriptions-item label="楼栋">{{ currentRecord.buildingName }}</el-descriptions-item>
        <el-descriptions-item label="访问状态">
          <el-tag :type="getStatusType(currentRecord.status)">
            {{ currentRecord.statusText }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="预计访问时间">{{ formatDate(currentRecord.expectedVisitTime) }}</el-descriptions-item>
        <el-descriptions-item label="实际到达时间">{{ formatDate(currentRecord.actualArrivalTime || '') || '无' }}</el-descriptions-item>
        <el-descriptions-item label="离开时间">{{ formatDate(currentRecord.leaveTime || '') || '无' }}</el-descriptions-item>
        <el-descriptions-item label="登记人">{{ currentRecord.registrarName || '无' }}</el-descriptions-item>
        <el-descriptions-item label="访问事由" span="2">{{ currentRecord.visitPurpose || '无' }}</el-descriptions-item>
        <el-descriptions-item label="备注" span="2">{{ currentRecord.remarks || '无' }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="showDetailDialog = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import {
  User,
  Plus,
  Search,
  RefreshRight,
  View,
  Edit,
  Delete,
  CircleCheck,
  CircleClose
} from '@element-plus/icons-vue'
import {
  getVisitorPage,
  getVisitorById,
  addVisitor,
  updateVisitor,
  deleteVisitor,
  getDormitoryOptions,
  searchDormitories,
  searchStudents,
  checkIn,
  checkOut
} from '@/api/visitor'
import type { Visitor, VisitorForm, VisitorParams, StudentSearchParams, DormitorySearchParams } from '@/api/visitor'

// 数据定义
const loading = ref(false)
const submitting = ref(false)
const tableData = ref<Visitor[]>([])
const showDialog = ref(false)
const showDetailDialog = ref(false)
const isEdit = ref(false)
const currentRecord = ref<Visitor | null>(null)

// 选项数据
const dormitoryOptions = ref<{label: string, value: number}[]>([])
const studentOptions = ref<{label: string, value: number}[]>([])
const selectedStudent = ref<number | null>(null)
const selectedDormitory = ref<number | null>(null)
const studentSearchLoading = ref(false)
const dormitorySearchLoading = ref(false)
const statusOptions = [
  { label: '待访问', value: 0 },
  { label: '访问中', value: 1 },
  { label: '已完成', value: 2 },
  { label: '已取消', value: 3 }
]

// 搜索表单
const searchForm = reactive<VisitorParams>({
  current: 1,
  size: 10,
  visitorName: '',
  visitorPhone: '',
  status: undefined as number | undefined,
  startDate: '',
  endDate: ''
})

// 日期范围
const visitDateRange = ref<[string, string] | []>([])

// 分页
const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

// 表单
const form = reactive<VisitorForm>({
  visitorName: '',
  visitorPhone: '',
  visitorIdCard: '',
  visitStudentName: '',
  visitStudentNo: '',
  roomId: null as any,
  visitPurpose: '',
  expectedVisitTime: '',
  actualArrivalTime: '',
  leaveTime: '',
  status: 0,
  remarks: ''
})

// 表单引用
const formRef = ref<FormInstance>()

// 表单验证规则
const rules: FormRules = {
  visitorName: [{ required: true, message: '请输入访客姓名', trigger: 'blur' }],
  visitorPhone: [
    { required: true, message: '请输入访客手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ],
  roomId: [{ required: true, message: '请选择访问宿舍', trigger: 'change' }],
  expectedVisitTime: [{ required: true, message: '请选择预计访问时间', trigger: 'change' }]
}

// 工具方法
const formatDate = (dateStr: string) => {
  if (!dateStr) return ''
  return dateStr.substring(0, 19).replace('T', ' ')
}

const getStatusType = (status: number) => {
  switch (status) {
    case 0: return 'info'    // 待访问
    case 1: return 'success' // 访问中
    case 2: return 'primary' // 已完成
    case 3: return 'danger'  // 已取消
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
      startDate: visitDateRange.value[0] || '',
      endDate: visitDateRange.value[1] || ''
    }

    const response = await getVisitorPage(params)
    if (response && typeof response === 'object') {
      tableData.value = (response as any).records || []
      pagination.total = (response as any).total || 0
    } else {
      tableData.value = []
      pagination.total = 0
    }
  } catch (error: any) {
    ElMessage.error('获取数据失败')
    console.error(error)
  } finally {
    loading.value = false
  }
}

// 加载宿舍选项
const loadDormitoryOptions = async () => {
  try {
    const response = await getDormitoryOptions()
    if (Array.isArray(response)) {
      dormitoryOptions.value = response.map((item: any) => ({
        label: item.label,
        value: item.value
      }))
    }
  } catch (error) {
    console.error('获取宿舍选项失败:', error)
  }
}

// 搜索宿舍
const handleDormitorySearch = async (query: string) => {
  dormitorySearchLoading.value = true
  try {
    const searchParams: DormitorySearchParams = {
      keyword: query || '',  // 如果没有关键字，传空字符串进行默认查询
      current: 1,
      size: 20
    }
    const response = await searchDormitories(searchParams)
    console.log('宿舍搜索API响应:', response)

    // 处理后端返回的数据结构，后端返回的是 IPage<DormitorySearchVO>
    // response已经是响应拦截器解包后的data字段
    let dormitories = []

    // 尝试多种可能的数据结构
    if (response && typeof response === 'object' && 'records' in response) {
      dormitories = (response as any).records
    } else if (Array.isArray(response)) {
      dormitories = response
    } else if (response && Array.isArray(response.data)) {
      dormitories = response.data
    }

    console.log('解析到的宿舍数据:', dormitories)

    dormitoryOptions.value = dormitories.map((item: any) => ({
      label: `${item.buildingName} ${item.roomNo}`,
      value: item.id,
      buildingName: item.buildingName,
      roomNo: item.roomNo
    }))
    console.log('处理后宿舍选项:', dormitoryOptions.value)
  } catch (error) {
    console.error('搜索宿舍失败:', error)
    dormitoryOptions.value = []
  } finally {
    dormitorySearchLoading.value = false
  }
}

// 搜索学生
const handleStudentSearch = async (query: string) => {
  studentSearchLoading.value = true
  try {
    const searchParams: StudentSearchParams = {
      keyword: query || '',  // 如果没有关键字，传空字符串进行默认查询
      current: 1,
      size: 20
    }
    const response = await searchStudents(searchParams)
    console.log('学生搜索API响应:', response)

    // 处理后端返回的数据结构，后端返回的是 IPage<StudentSearchVO>
    // response已经是响应拦截器解包后的data字段
    let students = []

    // 尝试多种可能的数据结构
    if (response && response.records) {
      students = response.records
    } else if (Array.isArray(response)) {
      students = response
    } else if (response && Array.isArray(response.data)) {
      students = response.data
    }

    console.log('解析到的学生数据:', students)

    studentOptions.value = students.map((item: any) => ({
      label: `${item.studentNo} - ${item.name}`,
      value: item.id,
      studentNo: item.studentNo,
      name: item.name
    }))
    console.log('处理后学生选项:', studentOptions.value)
  } catch (error) {
    console.error('搜索学生失败:', error)
    studentOptions.value = []
  } finally {
    studentSearchLoading.value = false
  }
}

// 处理宿舍选择变化
const handleDormitoryChange = (dormitoryId: number) => {
  const selectedDormitoryData = dormitoryOptions.value.find(d => d.value === dormitoryId)
  if (selectedDormitoryData) {
    form.roomId = dormitoryId
  } else {
    form.roomId = null as any
  }
}

// 处理学生选择变化
const handleStudentChange = (studentId: number) => {
  const selectedStudentData = studentOptions.value.find(s => s.value === studentId)
  if (selectedStudentData) {
    form.visitStudentName = selectedStudentData.name
    form.visitStudentNo = selectedStudentData.studentNo
  } else {
    form.visitStudentName = ''
    form.visitStudentNo = ''
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
    current: 1,
    size: 10,
    visitorName: '',
    visitorPhone: '',
    status: undefined,
    startDate: '',
    endDate: ''
  })
  visitDateRange.value = []
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

// 打开新增对话框
const openAddDialog = async () => {
  isEdit.value = false
  selectedStudent.value = null
  selectedDormitory.value = null
  studentOptions.value = []
  dormitoryOptions.value = []
  Object.assign(form, {
    visitorName: '',
    visitorPhone: '',
    visitorIdCard: '',
    visitStudentName: '',
    visitStudentNo: '',
    roomId: null as any,
    visitPurpose: '',
    expectedVisitTime: '',
    actualArrivalTime: '',
    leaveTime: '',
    status: 0,
    remarks: ''
  })
  showDialog.value = true

  // 默认加载学生和宿舍列表
  await Promise.all([
    handleStudentSearch(''),
    handleDormitorySearch('')
  ])
}

// 编辑
const handleEdit = async (row: Visitor) => {
  isEdit.value = true
  selectedStudent.value = null
  selectedDormitory.value = null
  studentOptions.value = []
  dormitoryOptions.value = []

  // 先加载默认选项
  await Promise.all([
    handleStudentSearch(''),
    handleDormitorySearch('')
  ])

  try {
    const detail = await getVisitorById(row.id)

    // 设置宿舍选择
    if (detail.roomId) {
      selectedDormitory.value = detail.roomId
      // 如果当前宿舍不在选项中，添加到选项中
      if (!dormitoryOptions.value.find(d => d.value === detail.roomId)) {
        dormitoryOptions.value.unshift({
          label: `${detail.buildingName || ''} ${detail.roomNo || ''}`.trim(),
          value: detail.roomId,
          buildingName: detail.buildingName || '',
          roomNo: detail.roomNo || ''
        })
      }
    }

    // 如果有学生信息，预设学生选项
    if (detail.visitStudentName && detail.visitStudentNo) {
      // 查找学生是否在选项中，如果不在则添加
      const existingStudent = studentOptions.value.find(s =>
        s.studentNo === detail.visitStudentNo && s.name === detail.visitStudentName
      )
      if (!existingStudent) {
        studentOptions.value.unshift({
          label: `${detail.visitStudentNo} - ${detail.visitStudentName}`,
          value: 0, // 临时值，因为没有学生ID
          studentNo: detail.visitStudentNo,
          name: detail.visitStudentName
        })
      }
      selectedStudent.value = 0 // 临时值，因为没有学生ID
    }

    Object.assign(form, {
      id: detail.id,
      visitorName: detail.visitorName,
      visitorPhone: detail.visitorPhone,
      visitorIdCard: detail.visitorIdCard || '',
      visitStudentName: detail.visitStudentName,
      visitStudentNo: detail.visitStudentNo || '',
      roomId: detail.roomId,
      visitPurpose: detail.visitPurpose || '',
      expectedVisitTime: detail.expectedVisitTime ? detail.expectedVisitTime.substring(0, 19).replace('T', ' ') : '',
      actualArrivalTime: detail.actualArrivalTime ? detail.actualArrivalTime.substring(0, 19).replace('T', ' ') : '',
      leaveTime: detail.leaveTime ? detail.leaveTime.substring(0, 19).replace('T', ' ') : '',
      status: detail.status,
      remarks: detail.remarks || ''
    })
    showDialog.value = true
  } catch (error: any) {
    ElMessage.error('获取详情失败')
  }
}

// 查看详情
const handleView = async (row: Visitor) => {
  try {
    const detail = await getVisitorById(row.id)
    currentRecord.value = detail
    showDetailDialog.value = true
  } catch (error: any) {
    ElMessage.error('获取详情失败')
  }
}

// 删除
const handleDelete = async (row: Visitor) => {
  try {
    await ElMessageBox.confirm('确定要删除这条访客记录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    await deleteVisitor(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

// 签到
const handleCheckIn = async (row: Visitor) => {
  try {
    await ElMessageBox.confirm('确定要为该访客签到吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    await checkIn(row.id)
    ElMessage.success('签到成功')
    loadData()
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error('签到失败')
    }
  }
}

// 签退
const handleCheckOut = async (row: Visitor) => {
  try {
    await ElMessageBox.confirm('确定要为该访客签退吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    await checkOut(row.id)
    ElMessage.success('签退成功')
    loadData()
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error('签退失败')
    }
  }
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return

  try {
    await formRef.value.validate()

    submitting.value = true

    // 准备提交数据，确保数据类型正确
    const submitData = {
      ...form,
      roomId: form.roomId ? String(form.roomId) : null  // 确保roomId是字符串类型（后端期望）
    }

    console.log('提交的表单数据:', submitData)

    if (isEdit.value) {
      await updateVisitor(submitData)
      ElMessage.success('更新成功')
    } else {
      await addVisitor(submitData)
      ElMessage.success('添加成功')
    }

    showDialog.value = false
    loadData()
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(isEdit.value ? '更新失败' : '添加失败')
    }
  } finally {
    submitting.value = false
  }
}

// 初始化
onMounted(() => {
  loadData()
  loadDormitoryOptions()
})
</script>

<style scoped>
.visitor-management {
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

.page-actions {
  display: flex;
  align-items: center;
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

.search-section {
  margin-bottom: 24px;
}

.search-section .el-card {
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
  border: none;
}

.search-section .el-form {
  padding: 8px 0;
}

.search-section .el-row {
  margin-bottom: 16px;
}

.search-section .el-row:last-child {
  margin-bottom: 0;
}

.table-section {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
}

.pagination-container {
  padding: 20px 0;
  text-align: right;
}

.dialog-footer {
  text-align: right;
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
</style>