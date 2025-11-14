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
        <el-button type="primary" @click="handleAdd" size="large">
          <el-icon><Plus /></el-icon>
          新增检查
        </el-button>
      </div>
    </div>

    <div class="content-container">
      <div class="search-section">
        <el-card shadow="never">
          <el-form :model="searchForm" inline class="search-form">
            <el-form-item label="宿舍号">
              <el-input
                v-model="searchForm.roomNo"
                placeholder="请输入宿舍号"
                clearable
                style="width: 200px"
              />
            </el-form-item>
            <el-form-item label="检查日期">
              <el-date-picker
                v-model="searchForm.inspectionDate"
                type="date"
                placeholder="请选择检查日期"
                clearable
                style="width: 200px"
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
          <el-table-column prop="roomNo" label="宿舍号" width="100" />
          <el-table-column prop="buildingName" label="楼栋" width="100" />
          <el-table-column prop="inspectionDate" label="检查日期" width="120" />
          <el-table-column prop="score" label="卫生分数" width="100" align="center" />
          <el-table-column prop="level" label="等级" width="100" align="center">
            <template #default="{ row }">
              <el-tag :type="getLevelType(row.level)">
                {{ row.level }}
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
                </el-button>
                <el-button size="small" type="primary" @click="handleEdit(row)">
                  <el-icon><Edit /></el-icon>
                </el-button>
                <el-button size="small" type="danger" @click="handleDelete(row)">
                  <el-icon><Delete /></el-icon>
                </el-button>
              </el-button-group>
            </template>
          </el-table-column>
        </el-table>

        <div class="pagination-section">
          <el-pagination
            v-model:current-page="pagination.page"
            v-model:page-size="pagination.pageSize"
            :page-sizes="[10, 20, 50, 100]"
            :total="pagination.itemCount"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleSizeChange"
            @current-change="handlePageChange"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import {
  Document,
  Plus,
  Search,
  RefreshRight,
  View,
  Edit,
  Delete
} from '@element-plus/icons-vue'

interface Inspection {
  id: number
  roomNo: string
  buildingName: string
  inspectionDate: string
  score: number
  level: string
  inspectorName: string
  remarks: string
}

const loading = ref(false)
const tableData = ref<Inspection[]>([])

const searchForm = reactive({
  roomNo: '',
  inspectionDate: null as Date | null
})

const pagination = reactive({
  page: 1,
  pageSize: 10,
  itemCount: 0
})

const getLevelType = (level: string) => {
  switch (level) {
    case '优秀': return 'success'
    case '良好': return 'primary'
    case '合格': return 'warning'
    case '不合格': return 'danger'
    default: return 'info'
  }
}

const loadData = async () => {
  loading.value = true
  try {
    setTimeout(() => {
      tableData.value = [
        {
          id: 1,
          roomNo: '101',
          buildingName: 'A1栋',
          inspectionDate: '2024-09-15',
          score: 95,
          level: '优秀',
          inspectorName: '张老师',
          remarks: '宿舍整洁，卫生状况良好'
        },
        {
          id: 2,
          roomNo: '102',
          buildingName: 'A1栋',
          inspectionDate: '2024-09-15',
          score: 78,
          level: '良好',
          inspectorName: '张老师',
          remarks: '地面有少量杂物，需要清理'
        },
        {
          id: 3,
          roomNo: '201',
          buildingName: 'A1栋',
          inspectionDate: '2024-09-14',
          score: 62,
          level: '合格',
          inspectorName: '李老师',
          remarks: '垃圾桶已满，需要及时清理'
        }
      ]
      pagination.itemCount = 3
      loading.value = false
    }, 1000)
  } catch (error) {
    loading.value = false
    ElMessage.error('获取数据失败')
  }
}

const handleSearch = () => {
  pagination.page = 1
  loadData()
}

const handleReset = () => {
  searchForm.roomNo = ''
  searchForm.inspectionDate = null
  handleSearch()
}

const handlePageChange = (page: number) => {
  pagination.page = page
  loadData()
}

const handleSizeChange = (size: number) => {
  pagination.pageSize = size
  pagination.page = 1
  loadData()
}

const handleAdd = () => {
  ElMessage.info('新增检查功能待开发')
}

const handleView = (row: Inspection) => {
  ElMessage.info(`查看检查详情: ${row.roomNo} - ${row.level}`)
}

const handleEdit = (row: Inspection) => {
  ElMessage.info(`编辑检查记录: ${row.roomNo}`)
}

const handleDelete = (row: Inspection) => {
  ElMessage.info(`删除检查记录: ${row.roomNo}`)
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.inspection-management {
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

@media (max-width: 768px) {
  .inspection-management {
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
  .search-form .el-date-picker {
    width: 100%;
  }
}
</style>