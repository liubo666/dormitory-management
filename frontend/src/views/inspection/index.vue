<template>
  <div class="inspection-management">
    <div class="page-header">
      <div class="page-title">
        <h2>
          <el-icon><Document /></el-icon>
          卫生检查
        </h2>
        <p>管理宿舍卫生检查记录，包括检查评分、等级评定等</p>
      </div>
      <div class="page-actions">
        <el-button type="primary" @click="openAddDialog" size="large">
          <el-icon><Plus /></el-icon>
          新增检查
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
                <el-form-item label="宿舍号">
                  <el-input
                    v-model="searchForm.roomNo"
                    placeholder="请输入宿舍号"
                    clearable
                  />
                </el-form-item>
              </el-col>
              <el-col :span="6">
                <el-form-item label="宿舍">
                  <el-select
                    v-model="searchForm.roomId"
                    placeholder="请选择宿舍"
                    clearable
                    filterable
                    style="width: 100%"
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
              <el-col :span="6">
                <el-form-item label="检查等级">
                  <el-select
                    v-model="searchForm.level"
                    placeholder="请选择等级"
                    clearable
                    style="width: 100%"
                  >
                    <el-option
                      v-for="item in levelOptions"
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
                <el-form-item label="检查日期">
                  <el-date-picker
                    v-model="inspectionDateRange"
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
            <el-table-column prop="roomNo" label="宿舍号" width="100" />
            <el-table-column prop="buildingName" label="楼栋" width="120" />
            <el-table-column prop="inspectionDate" label="检查日期" width="120">
              <template #default="{ row }">
                {{ formatDate(row.inspectionDate) }}
              </template>
            </el-table-column>
            <el-table-column prop="score" label="卫生分数" width="100" align="center">
              <template #default="{ row }">
                <el-tag :type="getScoreType(row.score)">
                  {{ row.score }}分
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="levelText" label="等级" width="100" align="center">
              <template #default="{ row }">
                <el-tag :type="getLevelType(row.level)">
                  {{ row.levelText }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="inspectorName" label="检查人" width="120" />
            <el-table-column prop="remarks" label="备注" min-width="200" show-overflow-tooltip />
            <el-table-column label="操作" width="200" fixed="right">
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
      :title="isEdit ? '编辑检查记录' : '新增检查记录'"
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
            <el-form-item label="宿舍" prop="roomId">
              <el-select
                v-model="form.roomId"
                placeholder="请选择宿舍"
                filterable
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
            <el-form-item label="检查日期" prop="inspectionDate">
              <el-date-picker
                v-model="form.inspectionDate"
                type="datetime"
                placeholder="请选择检查日期"
                format="YYYY-MM-DD HH:mm:ss"
                value-format="YYYY-MM-DD HH:mm:ss"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="卫生分数" prop="score">
              <el-input-number
                v-model="form.score"
                :min="0"
                :max="100"
                placeholder="请输入卫生分数"
                style="width: 100%"
                @change="handleScoreChange"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="检查等级" prop="level">
              <el-select
                v-model="form.level"
                placeholder="请选择等级"
                style="width: 100%"
              >
                <el-option
                  v-for="item in levelOptions"
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
            <el-form-item label="备注">
              <el-input
                v-model="form.remarks"
                type="textarea"
                :rows="3"
                placeholder="请输入备注"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row>
          <el-col :span="24">
            <el-form-item label="问题描述">
              <el-input
                v-model="form.issues"
                type="textarea"
                :rows="3"
                placeholder="请输入详细问题描述"
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
      title="检查记录详情"
      width="600px"
    >
      <el-descriptions :column="2" border v-if="currentRecord">
        <el-descriptions-item label="宿舍号">{{ currentRecord.roomNo }}</el-descriptions-item>
        <el-descriptions-item label="楼栋">{{ currentRecord.buildingName }}</el-descriptions-item>
        <el-descriptions-item label="检查日期">{{ formatDate(currentRecord.inspectionDate) }}</el-descriptions-item>
        <el-descriptions-item label="卫生分数">{{ currentRecord.score }}分</el-descriptions-item>
        <el-descriptions-item label="检查等级">
          <el-tag :type="getLevelType(currentRecord.level)">
            {{ currentRecord.levelText }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="检查人">{{ currentRecord.inspectorName }}</el-descriptions-item>
        <el-descriptions-item label="备注" span="2">{{ currentRecord.remarks || '无' }}</el-descriptions-item>
        <el-descriptions-item label="问题描述" span="2">{{ currentRecord.issues || '无' }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="showDetailDialog = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import {
  Document,
  Plus,
  Search,
  RefreshRight,
  View,
  Edit,
  Delete
} from '@element-plus/icons-vue'
import {
  getInspectionPage,
  getInspectionById,
  addInspection,
  updateInspection,
  deleteInspection,
  getDormitoryOptions,
  getInspectionLevelOptions,
  getLevelByScore,
  getLevelType
} from '@/api/inspection'
import type { Inspection, InspectionForm, InspectionParams } from '@/api/inspection'

// 数据定义
const loading = ref(false)
const submitting = ref(false)
const tableData = ref<Inspection[]>([])
const showDialog = ref(false)
const showDetailDialog = ref(false)
const isEdit = ref(false)
const currentRecord = ref<Inspection | null>(null)

// 选项数据
const dormitoryOptions = ref<{label: string, value: number}[]>([])
const levelOptions = getInspectionLevelOptions()

// 搜索表单
const searchForm = reactive<InspectionParams>({
  current: 1,
  size: 10,
  roomNo: '',
  roomId: undefined,
  level: '',
  startDate: '',
  endDate: ''
})

// 日期范围
const inspectionDateRange = ref<[string, string] | []>([])

// 分页
const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

// 表单
const form = reactive<InspectionForm>({
  roomId: null as any,
  inspectionDate: '',
  score: null as any,
  level: '',
  remarks: '',
  issues: '',
  images: []
})

// 表单引用
const formRef = ref<FormInstance>()

// 表单验证规则
const rules: FormRules = {
  roomId: [{ required: true, message: '请选择宿舍', trigger: 'change' }],
  inspectionDate: [{ required: true, message: '请选择检查日期', trigger: 'change' }],
  score: [{ required: true, message: '请输入卫生分数', trigger: 'blur' }],
  level: [{ required: true, message: '请选择检查等级', trigger: 'change' }]
}

// 工具方法
const formatDate = (dateStr: string) => {
  if (!dateStr) return ''
  return dateStr.substring(0, 19).replace('T', ' ')
}

const getScoreType = (score: number) => {
  if (score >= 90) return 'success'
  if (score >= 80) return 'primary'
  if (score >= 60) return 'warning'
  return 'danger'
}

// 加载数据
const loadData = async () => {
  loading.value = true
  try {
    const params = {
      ...searchForm,
      current: pagination.current,
      size: pagination.size,
      startDate: inspectionDateRange.value[0] || '',
      endDate: inspectionDateRange.value[1] || ''
    }

    const response = await getInspectionPage(params)
    if (response && typeof response === 'object') {
      tableData.value = (response as any).records || []
      pagination.total = (response as any).total || 0
    } else {
      tableData.value = []
      pagination.total = 0
    }
  } catch (error: any) {
    ElMessage.error('获取数据失败')
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
        label: `${item.buildingName || ''} ${item.roomNo}`,
        value: item.id
      }))
    } else {
      dormitoryOptions.value = []
    }
  } catch (error) {
    // 获取宿舍选项失败
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
    roomId: undefined,
    level: '',
    minScore: undefined,
    maxScore: undefined,
    startDate: '',
    endDate: ''
  })
  inspectionDateRange.value = []
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
const openAddDialog = () => {
  isEdit.value = false
  Object.assign(form, {
    roomId: null as any,
    inspectionDate: '',
    score: null as any,
    level: '',
    remarks: '',
    issues: '',
    images: []
  })
  showDialog.value = true
}

// 编辑
const handleEdit = async (row: Inspection) => {
  isEdit.value = true
  try {
    const detail = await getInspectionById(row.id)
    if (detail && typeof detail === 'object') {
      Object.assign(form, {
        id: (detail as any).id,
        roomId: (detail as any).roomId,
        inspectionDate: (detail as any).inspectionDate,
        score: (detail as any).score,
        level: (detail as any).level,
        remarks: (detail as any).remarks || '',
        issues: (detail as any).issues || '',
        images: (detail as any).imageList || []
      })
    }
    showDialog.value = true
  } catch (error: any) {
    ElMessage.error('获取详情失败')
  }
}

// 查看详情
const handleView = async (row: Inspection) => {
  try {
    const detail = await getInspectionById(row.id)
    currentRecord.value = detail
    showDetailDialog.value = true
  } catch (error: any) {
    ElMessage.error('获取详情失败')
  }
}

// 删除
const handleDelete = async (row: Inspection) => {
  try {
    await ElMessageBox.confirm('确定要删除这条检查记录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    await deleteInspection(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

// 宿舍变化处理
const handleDormitoryChange = () => {
  // 可以在这里添加宿舍变化的逻辑
}

// 分数变化处理
const handleScoreChange = (score: number) => {
  if (score !== null && score !== undefined) {
    form.level = getLevelByScore(score)
  }
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return

  try {
    await formRef.value.validate()

    submitting.value = true
    if (isEdit.value) {
      await updateInspection(form)
      ElMessage.success('更新成功')
    } else {
      await addInspection(form)
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
.inspection-management {
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

.page-title h2 .header-btn {
  margin-left: auto;
  font-size: 14px;
  padding: 8px 16px;
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