<template>
  <div class="dormitory-management">
    <div class="page-header">
      <div class="page-title">
        <h2>
          <el-icon><House /></el-icon>
          宿舍与楼栋管理
        </h2>
        <p>管理宿舍楼和宿舍基本信息，包括楼栋、房间号、床位、入住情况等</p>
      </div>
    </div>

    <div class="content-container">
      <!-- 宿舍管理区域 -->
      <div class="dormitory-section">
        <div class="section-header">
          <h3>
            <el-icon><House /></el-icon>
            宿舍管理
          </h3>
          <el-button type="primary" @click="handleAdd" size="small">
            <el-icon><Plus /></el-icon>
            新增宿舍
          </el-button>
        </div>

        <div class="search-section">
          <el-card shadow="never">
            <el-form :model="searchForm" inline class="search-form">
              <el-form-item label="楼栋号">
                <el-input
                  v-model="searchForm.buildingNo"
                  placeholder="请输入楼栋号"
                  clearable
                  style="width: 200px"
                />
              </el-form-item>
              <el-form-item label="房间号">
                <el-input
                  v-model="searchForm.roomNo"
                  placeholder="请输入房间号"
                  clearable
                  style="width: 200px"
                />
              </el-form-item>
              <el-form-item label="楼层">
                <el-input-number
                  v-model="searchForm.floorNumber"
                  :min="1"
                  :max="30"
                  placeholder="请输入楼层"
                  style="width: 150px"
                />
              </el-form-item>
              <el-form-item label="状态">
                <el-select
                  v-model="searchForm.status"
                  placeholder="请选择状态"
                  clearable
                  style="width: 120px"
                >
                  <el-option label="可用" :value="1" />
                  <el-option label="已满" :value="0" />
                  <el-option label="维护" :value="2" />
                </el-select>
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
            <el-table-column prop="buildingNo" label="楼栋号" width="100" />
            <el-table-column prop="buildingName" label="楼栋名称" width="120" />
            <el-table-column prop="roomNo" label="房间号" width="100" />
            <el-table-column prop="floorNumber" label="楼层" width="80" align="center" />
            <el-table-column prop="totalBeds" label="床位数" width="80" align="center" />
            <el-table-column prop="occupiedBeds" label="已入住" width="80" align="center" />
            <el-table-column prop="availableBeds" label="空床位" width="80" align="center">
              <template #default="{ row }">
                <el-tag :type="row.availableBeds > 0 ? 'success' : 'danger'">
                  {{ row.availableBeds }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="description" label="描述" min-width="200" show-overflow-tooltip />
            <el-table-column prop="status" label="状态" width="100" align="center">
              <template #default="{ row }">
                <el-tag :type="getStatusType(row.status)">
                  {{ getStatusText(row.status) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="创建时间" width="180" />
            <el-table-column label="操作" width="320" fixed="right">
              <template #default="{ row }">
                <el-button-group>
                  <el-button
                    size="small"
                    @click="handleEdit(row)"
                    :disabled="row.occupiedBeds > 0"
                    :title="row.occupiedBeds > 0 ? '该宿舍已有学生入住，无法编辑' : '编辑宿舍'"
                  >
                    <el-icon><Edit /></el-icon>
                  </el-button>
                  <el-button size="small" type="info" @click="handleViewDetail(row)">
                    <el-icon><View /></el-icon>
                  </el-button>
                  <el-button
                    size="small"
                    :disabled="row.occupiedBeds > 0"
                    :type="getStatusButtonType(row.status)"
                    @click="handleToggleStatus(row)"
                  >
                    <el-icon><VideoPlay v-if="row.status !== 1" /><VideoPause v-else /></el-icon>
                  </el-button>
                  <el-button
                    size="small"
                    type="danger"
                    @click="handleDelete(row)"
                    :disabled="row.occupiedBeds > 0"
                    :title="row.occupiedBeds > 0 ? '该宿舍已有学生入住，无法删除' : '删除宿舍'"
                  >
                    <el-icon><Delete /></el-icon>
                  </el-button>
                </el-button-group>
              </template>
            </el-table-column>
          </el-table>

          <div class="pagination-section">
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
        </div>
      </div>
    </div>

    <!-- 新增/编辑宿舍对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      :before-close="handleDialogClose"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="rules"
        label-width="100px"
        label-position="top"
      >
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="楼栋号" prop="buildingNo">
              <el-input v-model="formData.buildingNo" placeholder="请输入楼栋号，如：A1" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="楼栋名称" prop="buildingName">
              <el-input v-model="formData.buildingName" placeholder="请输入楼栋名称，如：A1栋" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="房间号" prop="roomNo">
              <el-input v-model="formData.roomNo" placeholder="请输入房间号，如：101" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="楼层" prop="floorNumber">
              <el-input-number
                v-model="formData.floorNumber"
                :min="1"
                :max="30"
                placeholder="请输入楼层"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>

        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="床位数" prop="bedInfos">
              <el-input-number
                v-model="bedCount"
                :min="1"
                :max="12"
                placeholder="请输入床位数"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <!-- 床位信息展示 -->
        <el-form-item label="床位信息" v-if="formData.bedInfos.length > 0">
          <div class="bed-info-display">
            <div class="bed-list">
              <div v-for="(bed, index) in formData.bedInfos" :key="index" class="bed-item">
                <el-tag
                  :type="bed.status === 1 ? 'success' : bed.status === 2 ? 'danger' : 'warning'"
                  class="bed-tag"
                >
                  {{ bed.bedNo }}
                  <span class="bed-status">({{ bed.status === 1 ? '可用' : '已占用' }})</span>
                </el-tag>
                <el-input
                  v-model="bed.bedNo"
                  placeholder="床位号"
                  size="small"
                  style="width: 120px; margin-left: 10px;"
                />
                <el-select
                  v-model="bed.status"
                  size="small"
                  style="width: 100px; margin-left: 5px;"
                >
                  <el-option label="可用" :value="1" />
                  <el-option label="已占用" :value="2" />
                </el-select>
              </div>
            </div>
          </div>
        </el-form-item>

        <el-form-item label="描述" prop="description">
          <el-input
            v-model="formData.description"
            type="textarea"
            :rows="3"
            placeholder="请输入宿舍描述"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit" :loading="submitLoading">
            {{ isEdit ? '更新' : '创建' }}
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 床位信息对话框 -->
    <el-dialog
      v-model="bedsDialogVisible"
      title="床位信息"
      width="800px"
    >
      <div v-if="currentDormitoryDetail" class="dormitory-info">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="宿舍">{{ currentDormitoryDetail.buildingName }}{{ currentDormitoryDetail.roomNo }}</el-descriptions-item>
          <el-descriptions-item label="楼层">{{ currentDormitoryDetail.floorNumber }}层</el-descriptions-item>
          <el-descriptions-item label="总床位">{{ currentDormitoryDetail.totalBeds }}个</el-descriptions-item>
          <el-descriptions-item label="已入住">{{ currentDormitoryDetail.occupiedBeds }}个</el-descriptions-item>
          <el-descriptions-item label="空床位">{{ currentDormitoryDetail.availableBeds }}个</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="getStatusType(currentDormitoryDetail.status)">
              {{ getStatusText(currentDormitoryDetail.status) }}
            </el-tag>
          </el-descriptions-item>
        </el-descriptions>
      </div>

      <div class="bed-list-section" style="margin-top: 20px;">
        <h4>床位详情</h4>
        <el-table
          :data="bedList"
          border
          style="width: 100%"
        >
          <el-table-column prop="bedNo" label="床位号" width="120" />
          <el-table-column prop="status" label="床位状态" width="100" align="center">
            <template #default="{ row }">
              <el-tag :type="getBedStatusType(row.status)">
                {{ getBedStatusText(row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="name" label="入住学生" />
          <el-table-column prop="studentNo" label="学号" width="120" />
          <el-table-column prop="description" label="备注" />
        </el-table>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed, watch } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { useErrorHandler } from '@/utils/error-handler'
import {
  House,
  School,
  Plus,
  Search,
  RefreshRight,
  Edit,
  Delete,
  VideoPlay,
  VideoPause,
  View
} from '@element-plus/icons-vue'
import {
  getDormitoryPage,
  createDormitory,
  updateDormitory,
  deleteDormitory,
  updateDormitoryStatus,
  getDormitoryById
} from '@/api/dormitory'
import {
  getBuildingPage,
  createBuilding,
  updateBuilding,
  deleteBuilding,
  updateBuildingStatus,
  getBuildingById
} from '@/api/building'
import type { Dormitory, DormitoryForm, BedInfoDTO } from '@/api/dormitory'
import type { Building, BuildingForm } from '@/api/building'

// 使用错误处理hook
const { handleError, showSuccess } = useErrorHandler()

// 宿舍相关数据定义
const loading = ref(false)
const submitLoading = ref(false)
const tableData = ref<Dormitory[]>([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const bedsDialogVisible = ref(false)
const bedList = ref<any[]>([])
const currentDormitoryDetail = ref<Dormitory | null>(null)

// 宿舍搜索表单
const searchForm = reactive({
  buildingNo: '',
  roomNo: '',
  floorNumber: undefined as number | undefined,
  status: undefined as number | undefined
})

// 宿舍表单数据
const formData = reactive<DormitoryForm>({
  bedInfos: [],
  buildingName: "",
  buildingNo: "",
  floorNumber: 0,
  roomNo: ""
})

// 辅助数据：床位数（用于前端表单显示）
const bedCount = ref(4)

// 宿舍分页
const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

// 表单引用
const formRef = ref<FormInstance>()

// 计算属性
const dialogTitle = computed(() => (isEdit.value ? '编辑宿舍' : '新增宿舍'))

// 宿舍表单验证规则
const rules: FormRules = {
  buildingNo: [
    { required: true, message: '请输入楼栋号', trigger: 'blur' },
    { pattern: /^[A-Z]\d*$/, message: '楼栋号格式不正确，如：A1', trigger: 'blur' }
  ],
  buildingName: [
    { required: true, message: '请输入楼栋名称', trigger: 'blur' },
    { min: 2, max: 50, message: '楼栋名称长度应在2-50个字符之间', trigger: 'blur' }
  ],
  roomNo: [
    { required: true, message: '请输入房间号', trigger: 'blur' },
    { pattern: /^\d{1,4}$/, message: '房间号格式不正确', trigger: 'blur' }
  ],
  floorNumber: [
    { required: true, message: '请输入楼层', trigger: 'blur' },
    { type: 'number', min: 1, max: 30, message: '楼层应在1-30之间', trigger: 'blur' }
  ],
  floorCount: [
    { required: true, message: '请输入楼层数', trigger: 'blur' },
    { type: 'number', min: 1, max: 30, message: '楼层数应在1-30之间', trigger: 'blur' }
  ],
  bedInfos: [
    { required: true, message: '床位信息不能为空', trigger: 'change' }
  ],
  description: [
    { max: 200, message: '描述长度不能超过200个字符', trigger: 'blur' }
  ]
}

// 方法定义
const fetchData = async () => {
  loading.value = true
  try {
    const params = {
      current: pagination.current,
      size: pagination.size,
      buildingNo: searchForm.buildingNo || undefined,
      roomNo: searchForm.roomNo || undefined,
      floorNumber: searchForm.floorNumber,
      status: searchForm.status
    }
    const response = await getDormitoryPage(params)
    tableData.value = response.records
    pagination.total = response.total
  } catch (error: any) {
    handleError(error, '获取数据失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.current = 1
  fetchData()
}

const handleReset = () => {
  searchForm.buildingNo = ''
  searchForm.roomNo = ''
  searchForm.floorNumber = undefined
  searchForm.status = undefined
  pagination.current = 1
  fetchData()
}

const handlePageChange = (page: number) => {
  pagination.current = page
  fetchData()
}

const handleSizeChange = (size: number) => {
  pagination.size = size
  pagination.current = 1
  fetchData()
}

const handleCurrentChange = (current: number) => {
  pagination.current = current
  fetchData()
}



const handleAdd = () => {
  isEdit.value = false
  dialogVisible.value = true
  resetForm()
}

const handleEdit = async (row: Dormitory) => {
  try {
    // 检查是否有已入住床位
    if (row.occupiedBeds > 0) {
      ElMessage.warning('该宿舍已有学生入住，无法编辑')
      return
    }

    isEdit.value = true
    dialogVisible.value = true

    // 获取宿舍详情，包括床位信息
    const dormitoryDetail = await getDormitoryById(row.id)

    // 设置床位数
    bedCount.value = dormitoryDetail.totalBeds || 4

    Object.assign(formData, {
      id: row.id,
      buildingNo: row.buildingNo,
      buildingName: row.buildingName,
      roomNo: row.roomNo,
      floorNumber: row.floorNumber,
      bedInfos: dormitoryDetail.bedInfos?.map((bed: any) => ({
        bedNo: bed.bedNo,
        status: bed.status ,
        description: bed.description || ''
      })) || generateBedInfos(bedCount.value),
      description: row.description || ''
    })
  } catch (error) {
    handleError(error, '获取宿舍详情失败')
  }
}

const handleDelete = (row: Dormitory) => {
  ElMessageBox.confirm(
    `确定要删除宿舍"${row.buildingName}${row.roomNo}"吗？此操作不可恢复！`,
    '删除确认',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      await deleteDormitory(row.id)
      showSuccess('删除成功')
      fetchData()
    } catch (error: any) {
      handleError(error, '删除失败')
    }
  })
}

const handleToggleStatus = (row: Dormitory) => {
  const action = row.status === 1 ? '停用' : '启用'
  ElMessageBox.confirm(
    `确定要${action}宿舍"${row.buildingName}${row.roomNo}"吗？`,
    `${action}确认`,
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      await updateDormitoryStatus(row.id, row.status === 1 ? 2 : 1)
      showSuccess(`${action}成功`)
      fetchData()
    } catch (error: any) {
      handleError(error, `${action}失败`)
    }
  })
}

const handleViewDetail = async (row: Dormitory) => {
  try {
    // 获取宿舍详情，包括床位信息
    const dormitoryDetail = await getDormitoryById(row.id)

    // 保存当前宿舍详情信息
    currentDormitoryDetail.value = dormitoryDetail

    // 设置床位列表数据
    bedList.value = dormitoryDetail.bedInfos || []

    bedsDialogVisible.value = true
  } catch (error) {
    handleError(error, '获取宿舍详情失败')
  }
}


// 生成床位信息
const generateBedInfos = (count: number): BedInfoDTO[] => {
  const bedInfos: BedInfoDTO[] = []
  for (let i = 1; i <= count; i++) {
    bedInfos.push({
      isOccupied: false,
      statusText: "",
      bedNo: `${i}号床`,
      status: 1, // 1: 可用状态
      description: ''
    })
  }
  return bedInfos
}

// 监听床位数变化，自动生成bedInfos
watch(bedCount, (newCount) => {
  if (newCount && newCount > 0) {
    formData.bedInfos = generateBedInfos(newCount)
  }
}, { immediate: true })

const handleSubmit = async () => {
  if (!formRef.value) return

  try {
    await formRef.value.validate()

    submitLoading.value = true

    if (isEdit.value) {
      await createDormitory(formData)
      showSuccess('更新成功')
    } else {
      await createDormitory(formData)
      showSuccess('创建成功')
    }

    dialogVisible.value = false
    fetchData()
  } catch (error: any) {
    handleError(error, isEdit.value ? '更新失败' : '创建失败')
  } finally {
    submitLoading.value = false
  }
}

const handleDialogClose = () => {
  dialogVisible.value = false
  resetForm()
}

const resetForm = () => {
  if (formRef.value) {
    formRef.value.resetFields()
  }
  // 重置床位数
  bedCount.value = 4
  Object.assign(formData, {
    id: '',
    buildingNo: '',
    roomNo: '',
    buildingName:'',
    floorNumber: 1,
    bedInfos: generateBedInfos(4),
    description: ''
  })
}

const getStatusType = (status: number) => {
  switch (status) {
    case 1: return 'success'
    case 0: return 'danger'
    case 2: return 'warning'
    default: return 'info'
  }
}

const getStatusText = (status: number) => {
  switch (status) {
    case 1: return '可用'
    case 0: return '已满'
    case 2: return '维护'
    default: return '未知'
  }
}

const getStatusButtonType = (status: number) => {
  return status === 1 ? 'warning' : 'success'
}

const getBedStatusType = (status: number) => {
  switch (status) {
    case 1: return 'success'  // 可用
    case 2: return 'warning'  // 已占用
    case 0: return 'info'     // 维修中
    default: return 'info'
  }
}

const getBedStatusText = (status: number) => {
  switch (status) {
    case 1: return '可用'
    case 2: return '已占用'
    default: return '未知'
  }
}



// 生命周期
onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.dormitory-management {
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

.content-container {
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
  overflow: hidden;
}

.quick-add-options {
  padding: 20px 0;
}

.option-card {
  text-align: center;
  cursor: pointer;
  transition: all 0.3s;
  border: 2px solid transparent;
}

.option-card:hover {
  border-color: #409eff;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.option-card.active {
  border-color: #409eff;
  background-color: #f0f9ff;
}

.option-card .el-icon {
  font-size: 48px;
  color: #409eff;
  margin-bottom: 10px;
}

.option-card h4 {
  margin: 10px 0;
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.option-card p {
  margin: 0;
  color: #666;
  font-size: 14px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.section-header h3 {
  display: flex;
  align-items: center;
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #1e40af;
}

.section-header h3 .el-icon {
  margin-right: 8px;
  font-size: 20px;
}

.building-section {
  margin-bottom: 32px;
}

.dormitory-section {
  margin-top: 32px;
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

.pagination-section {
  display: flex;
  justify-content: center;
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #f1f5f9;
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

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.bed-management {
  border: 1px solid #e4e7ed;
  border-radius: 6px;
  padding: 15px;
  background-color: #fafafa;
}

.bed-list {
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
}

.bed-item {
  display: flex;
  align-items: center;
  padding: 8px 12px;
  background: white;
  border-radius: 6px;
  border: 1px solid #e4e7ed;
  box-shadow: 0 2px 4px rgba(0,0,0,0.05);
}

.bed-tag {
  font-weight: 500;
}

.bed-status {
  margin-left: 5px;
  font-size: 12px;
  opacity: 0.8;
}

@media (max-width: 768px) {
  .dormitory-management {
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