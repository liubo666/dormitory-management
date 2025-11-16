<template>
  <div class="repair-management">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="page-title">
        <h2>
          <el-icon><Tools /></el-icon>
          报修管理
        </h2>
        <p>管理宿舍报修信息，包括报修申请、处理进度等</p>
      </div>
      <div class="page-actions">
        <el-button type="primary" @click="openAddDialog">
          <el-icon><Plus /></el-icon>
          新增报修
        </el-button>
        <el-button @click="loadData">
          <el-icon><Refresh /></el-icon>
          刷新
        </el-button>
      </div>
    </div>

    <!-- 搜索表单 -->
    <div class="search-container">
      <el-card shadow="never">
        <el-form :model="searchForm" label-width="80px">
          <el-row :gutter="20">
            <el-col :span="6">
              <el-form-item label="宿舍号">
                <el-input
                  v-model="searchForm.roomNo"
                  placeholder="请输入宿舍号"
                  clearable
                />
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item label="学生姓名">
                <el-input
                  v-model="searchForm.studentName"
                  placeholder="请输入学生姓名"
                  clearable
                />
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item label="报修类型">
                <el-select
                  v-model="searchForm.type"
                  placeholder="请选择报修类型"
                  clearable
                  style="width: 100%"
                >
                  <el-option
                    v-for="item in repairTypeOptions"
                    :key="item.value"
                    :label="item.label"
                    :value="item.value"
                  />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item label="优先级">
                <el-select
                  v-model="searchForm.priority"
                  placeholder="请选择优先级"
                  clearable
                  style="width: 100%"
                >
                  <el-option
                    v-for="item in priorityOptions"
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
              <el-form-item label="状态">
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
            <el-col :span="10">
              <el-form-item label="报修时间">
                <el-date-picker
                  v-model="reportDateRange"
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
            <el-col :span="6">
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
    <div class="table-container">
      <el-card shadow="never">
        <el-table
          :data="tableData"
          v-loading="loading"
          stripe
          border
          style="width: 100%"
        >
          <el-table-column prop="title" label="报修标题" min-width="200" show-overflow-tooltip />
          <el-table-column prop="roomNo" label="宿舍号" width="100" />
          <el-table-column prop="buildingName" label="楼栋" width="100" />
          <el-table-column prop="studentName" label="报修人" width="120" />
          <el-table-column prop="typeText" label="报修类型" width="120" />
          <el-table-column prop="priorityText" label="优先级" width="100" align="center">
            <template #default="{ row }">
              <el-tag :type="getPriorityType(row.priority)" size="small">
                {{ row.priorityText }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="reportTime" label="报修时间" width="180" />
          <el-table-column prop="status" label="状态" width="100" align="center">
            <template #default="{ row }">
              <el-tag :type="getStatusType(row.status)">
                {{ row.statusText }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="200" fixed="right">
            <template #default="{ row }">
              <el-button-group>
                <el-button size="small" @click="handleViewDetails(row)">
                  <el-icon><View /></el-icon>
                  详情
                </el-button>
                <el-button
                  v-if="row.status === 1"
                  size="small"
                  type="primary"
                  @click="handleProcess(row)"
                >
                  <el-icon><Tools /></el-icon>
                  处理
                </el-button>
                <el-button
                  size="small"
                  type="danger"
                  @click="handleDelete(row)"
                >
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

    <!-- 新增报修对话框 -->
    <el-dialog
      v-model="showAddDialog"
      title="新增报修"
      width="800px"
      :close-on-click-modal="false"
    >
      <el-form
        :model="addForm"
        :rules="addRules"
        ref="addFormRef"
        label-width="100px"
        label-position="right"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="宿舍" prop="roomId">
              <el-select
                v-model="addForm.roomId"
                placeholder="请选择宿舍"
                filterable
                clearable
                style="width: 100%"
              >
                <el-option
                  v-for="room in roomOptions"
                  :key="room.value"
                  :label="room.label"
                  :value="room.value"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="报修人" prop="studentId">
              <el-select
                v-model="addForm.studentId"
                placeholder="请选择报修人"
                filterable
                clearable
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
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="报修标题" prop="title">
              <el-input
                v-model="addForm.title"
                placeholder="请输入报修标题"
                clearable
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="报修类型" prop="type">
              <el-select
                v-model="addForm.type"
                placeholder="请选择报修类型"
                style="width: 100%"
              >
                <el-option
                  v-for="item in repairTypeOptions"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="优先级" prop="priority">
              <el-select
                v-model="addForm.priority"
                placeholder="请选择优先级"
                style="width: 100%"
              >
                <el-option
                  v-for="item in priorityOptions"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="联系电话">
              <el-input
                v-model="addForm.contactPhone"
                placeholder="请输入联系电话"
                clearable
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="报修描述">
              <el-input
                v-model="addForm.description"
                type="textarea"
                :rows="3"
                placeholder="请输入报修描述"
              />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showAddDialog = false">取消</el-button>
          <el-button type="primary" @click="handleSubmitAdd" :loading="submitting">
            提交
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 处理报修对话框 -->
    <el-dialog
      v-model="showProcessDialog"
      title="处理报修"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form
        :model="processForm"
        :rules="processRules"
        ref="processFormRef"
        label-width="100px"
      >
        <el-form-item label="报修标题">
          <el-input :value="currentRecord?.title" disabled />
        </el-form-item>
        <el-form-item label="处理结果" prop="status">
          <el-radio-group v-model="processForm.status">
            <el-radio :value="2">处理中</el-radio>
            <el-radio :value="3">已完成</el-radio>
            <el-radio :value="4">已取消</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="处理备注">
          <el-input
            v-model="processForm.handleRemark"
            type="textarea"
            :rows="3"
            placeholder="请输入处理备注"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showProcessDialog = false">取消</el-button>
          <el-button type="primary" @click="handleSubmitProcess" :loading="submitting">
            确认处理
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 详情对话框 -->
    <el-dialog
      v-model="showDetailsDialog"
      title="报修详情"
      width="800px"
    >
      <el-descriptions :column="2" border v-if="currentRecord">
        <el-descriptions-item label="报修标题">{{ currentRecord.title }}</el-descriptions-item>
        <el-descriptions-item label="宿舍号">{{ currentRecord.buildingName }} {{ currentRecord.roomNo }}</el-descriptions-item>
        <el-descriptions-item label="报修人">{{ currentRecord.studentName }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ currentRecord.contactPhone || '无' }}</el-descriptions-item>
        <el-descriptions-item label="报修类型">{{ currentRecord.typeText }}</el-descriptions-item>
        <el-descriptions-item label="优先级">
          <el-tag :type="getPriorityType(currentRecord.priority)">{{ currentRecord.priorityText }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(currentRecord.status)">{{ currentRecord.statusText }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="报修时间">{{ currentRecord.reportTime }}</el-descriptions-item>
        <el-descriptions-item label="处理人">{{ currentRecord.handlerName || '未处理' }}</el-descriptions-item>
        <el-descriptions-item label="处理时间">{{ currentRecord.handleTime || '未处理' }}</el-descriptions-item>
        <el-descriptions-item label="完成时间">{{ currentRecord.completeTime || '未完成' }}</el-descriptions-item>
        <el-descriptions-item label="报修描述" :span="2">{{ currentRecord.description || '无' }}</el-descriptions-item>
        <el-descriptions-item label="处理备注" :span="2">{{ currentRecord.handleRemark || '无' }}</el-descriptions-item>
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
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance } from 'element-plus'
import {
  Tools,
  Plus,
  Refresh,
  Search,
  RefreshRight,
  View,
  Delete
} from '@element-plus/icons-vue'
import {
  getRepairPage,
  addRepair,
  handleRepair,
  deleteRepair,
  getRepairById,
  getRepairTypeOptions,
  getRepairStatusOptions,
  getRepairPriorityOptions,
  getDormitoryOptions,
  getStudentOptions
} from '@/api/repair'
import type { Repair, RepairForm, RepairParams, RepairHandleForm } from '@/api/repair'

// 响应式数据
const loading = ref(false)
const submitting = ref(false)
const tableData = ref<Repair[]>([])
const currentRecord = ref<Repair | null>(null)

// 搜索表单
const searchForm = reactive<RepairParams>({
  current: 1,
  size: 10,
  roomNo: '',
  studentName: '',
  type: '',
  status: undefined,
  priority: undefined
})

// 分页数据
const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

// 日期范围
const reportDateRange = ref<[string, string] | []>([])

// 选项数据
const repairTypeOptions = getRepairTypeOptions()
const statusOptions = getRepairStatusOptions()
const priorityOptions = getRepairPriorityOptions()
const roomOptions = ref<{label: string, value: number}[]>([])
const studentOptions = ref<{label: string, value: number}[]>([])

// 对话框状态
const showAddDialog = ref(false)
const showProcessDialog = ref(false)
const showDetailsDialog = ref(false)

// 表单引用
const addFormRef = ref<FormInstance>()
const processFormRef = ref<FormInstance>()

// 新增表单
const addForm = reactive<RepairForm>({
  roomId: null as any,
  studentId: null as any,
  title: '',
  description: '',
  type: '',
  priority: 2,
  contactPhone: '',
  images: []
})

// 处理表单
const processForm = reactive<RepairHandleForm>({
  id: 0,
  status: 2,
  handleRemark: ''
})

// 表单验证规则
const addRules = {
  roomId: [{ required: true, message: '请选择宿舍', trigger: 'change' }],
  studentId: [{ required: true, message: '请选择报修学生', trigger: 'change' }],
  title: [{ required: true, message: '请输入报修标题', trigger: 'blur' }],
  type: [{ required: true, message: '请选择报修类型', trigger: 'change' }]
}

const processRules = {
  status: [{ required: true, message: '请选择处理结果', trigger: 'change' }]
}

// 获取状态类型
const getStatusType = (status: number) => {
  switch (status) {
    case 1: return 'warning'
    case 2: return 'primary'
    case 3: return 'success'
    case 4: return 'info'
    default: return 'info'
  }
}

// 获取优先级类型
const getPriorityType = (priority: number) => {
  switch (priority) {
    case 1: return 'info'
    case 2: return 'warning'
    case 3: return 'danger'
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
      startDate: reportDateRange.value[0] || '',
      endDate: reportDateRange.value[1] || ''
    }

    const response = await getRepairPage(params)
    if (response && typeof response === 'object') {
      tableData.value = (response as any).records || []
      pagination.total = (response as any).total || 0
    } else {
      tableData.value = []
      pagination.total = 0
    }
  } catch (error) {
    ElMessage.error('获取数据失败')
    console.error(error)
  } finally {
    loading.value = false
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
    roomNo: '',
    studentName: '',
    type: '',
    status: undefined,
    priority: undefined
  })
  reportDateRange.value = []
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
  try {
    // 加载宿舍选项
    const dormitories = await getDormitoryOptions()
    if (Array.isArray(dormitories)) {
      roomOptions.value = dormitories.map((dorm: any) => ({
        label: `${dorm.buildingName} ${dorm.roomNo}`,
        value: dorm.id
      }))
    }

    // 加载学生选项
    const students = await getStudentOptions()
    if (Array.isArray(students)) {
      studentOptions.value = students.map((student: any) => ({
        label: `${student.name} (${student.studentNo})`,
        value: student.id
      }))
    }

    showAddDialog.value = true
  } catch (error: any) {
    console.error(error)
    ElMessage.error(error.message || '加载选项失败')
  }
}

// 提交新增
const handleSubmitAdd = async () => {
  if (!addFormRef.value) return

  await addFormRef.value.validate(async (valid) => {
    if (valid) {
      submitting.value = true
      try {
        await addRepair(addForm)
        ElMessage.success('新增报修成功')
        showAddDialog.value = false
        Object.assign(addForm, {
          roomId: 0,
          studentId: 0,
          title: '',
          description: '',
          type: '',
          priority: 2,
          contactPhone: '',
          images: []
        })
        loadData()
      } catch (error: any) {
        console.error(error)
        ElMessage.error(error.message || '新增报修失败')
      } finally {
        submitting.value = false
      }
    }
  })
}

// 处理报修
const handleProcess = (row: Repair) => {
  currentRecord.value = row
  Object.assign(processForm, {
    id: row.id,
    status: 2,
    handleRemark: ''
  })
  showProcessDialog.value = true
}

// 提交处理
const handleSubmitProcess = async () => {
  if (!processFormRef.value || !currentRecord.value) return

  await processFormRef.value.validate(async (valid) => {
    if (valid) {
      submitting.value = true
      try {
        await handleRepair(processForm)
        ElMessage.success('处理报修成功')
        showProcessDialog.value = false
        loadData()
      } catch (error: any) {
        console.error(error)
        ElMessage.error(error.message || '处理报修失败')
      } finally {
        submitting.value = false
      }
    }
  })
}

// 删除报修
const handleDelete = (row: Repair) => {
  ElMessageBox.confirm('确定要删除该报修记录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteRepair(row.id)
      ElMessage.success('删除成功')
      loadData()
    } catch (error: any) {
      console.error(error)
      ElMessage.error(error.message || '删除失败')
    }
  })
}

// 查看详情
const handleViewDetails = async (row: Repair) => {
  try {
    const detail = await getRepairById(row.id)
    currentRecord.value = detail
    showDetailsDialog.value = true
  } catch (error: any) {
    console.error(error)
    ElMessage.error(error.message || '获取详情失败')
  }
}

// 初始化
onMounted(() => {
  loadData()
})
</script>

<style scoped>
.repair-management {
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

.search-container {
  margin-bottom: 24px;
}

.search-container .el-card {
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
}

.search-container .el-form {
  padding: 8px 0;
}

.search-container .el-row {
  margin-bottom: 16px;
}

.search-container .el-row:last-child {
  margin-bottom: 0;
}

.table-container {
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
}

.table-container .el-card {
  border-radius: 12px;
  border: none;
}

.pagination-container {
  padding: 20px 0;
  text-align: right;
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

.search-container {
  margin-bottom: 24px;
}

.table-container {
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
  overflow: hidden;
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

@media (max-width: 768px) {
  .repair-management {
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