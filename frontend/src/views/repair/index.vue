<template>
  <div class="repair-management">
    <div class="page-header">
      <div class="page-title">
        <h2>
          <el-icon><Tools /></el-icon>
          报修管理
        </h2>
        <p>管理宿舍报修信息，包括报修申请、处理进度等</p>
      </div>
      <div class="page-actions">
        <el-button type="primary" @click="handleAdd" size="large">
          <el-icon><Plus /></el-icon>
          新增报修
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
            <el-form-item label="状态">
              <el-select
                v-model="searchForm.status"
                placeholder="请选择状态"
                clearable
                style="width: 150px"
              >
                <el-option label="待处理" :value="1" />
                <el-option label="处理中" :value="2" />
                <el-option label="已完成" :value="3" />
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
          <el-table-column prop="title" label="报修标题" min-width="200" show-overflow-tooltip />
          <el-table-column prop="roomNo" label="宿舍号" width="100" />
          <el-table-column prop="studentName" label="报修人" width="120" />
          <el-table-column prop="type" label="报修类型" width="120" />
          <el-table-column prop="reportTime" label="报修时间" width="180" />
          <el-table-column prop="status" label="状态" width="100" align="center">
            <template #default="{ row }">
              <el-tag :type="getStatusType(row.status)">
                {{ getStatusText(row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="200" fixed="right">
            <template #default="{ row }">
              <el-button-group>
                <el-button size="small" @click="handleView(row)">
                  <el-icon><View /></el-icon>
                </el-button>
                <el-button size="small" type="primary" @click="handleProcess(row)">
                  <el-icon><Tools /></el-icon>
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
  Tools,
  Plus,
  Search,
  RefreshRight,
  View,
  Delete
} from '@element-plus/icons-vue'

interface Repair {
  id: number
  title: string
  roomNo: string
  studentName: string
  type: string
  reportTime: string
  status: number
}

const loading = ref(false)
const tableData = ref<Repair[]>([])

const searchForm = reactive({
  roomNo: '',
  status: undefined as number | undefined
})

const pagination = reactive({
  page: 1,
  pageSize: 10,
  itemCount: 0
})

const getStatusType = (status: number) => {
  switch (status) {
    case 1: return 'warning'
    case 2: return 'primary'
    case 3: return 'success'
    default: return 'info'
  }
}

const getStatusText = (status: number) => {
  switch (status) {
    case 1: return '待处理'
    case 2: return '处理中'
    case 3: return '已完成'
    default: return '未知'
  }
}

const loadData = async () => {
  loading.value = true
  try {
    setTimeout(() => {
      tableData.value = [
        {
          id: 1,
          title: '空调漏水',
          roomNo: '101',
          studentName: '张三',
          type: '水电维修',
          reportTime: '2024-09-15 10:30:00',
          status: 2
        },
        {
          id: 2,
          title: '门锁损坏',
          roomNo: '102',
          studentName: '李四',
          type: '门窗维修',
          reportTime: '2024-09-15 14:20:00',
          status: 1
        },
        {
          id: 3,
          title: '灯泡不亮',
          roomNo: '201',
          studentName: '王五',
          type: '照明维修',
          reportTime: '2024-09-14 16:45:00',
          status: 3
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
  searchForm.status = undefined
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
  ElMessage.info('新增报修功能待开发')
}

const handleView = (row: Repair) => {
  ElMessage.info(`查看报修详情: ${row.title}`)
}

const handleProcess = (row: Repair) => {
  ElMessage.info(`处理报修: ${row.title}`)
}

const handleDelete = (row: Repair) => {
  ElMessage.info(`删除报修: ${row.title}`)
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.repair-management {
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
  .repair-management {
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