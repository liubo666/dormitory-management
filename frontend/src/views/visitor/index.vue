<template>
  <div class="visitor-management">
    <div class="page-header">
      <div class="page-title">
        <h2>
          <el-icon><User /></el-icon>
          访客登记
        </h2>
        <p>管理访客登记信息，包括访客记录、访问状态等</p>
      </div>
      <div class="page-actions">
        <el-button type="primary" @click="handleRegister" size="large">
          <el-icon><Plus /></el-icon>
          访客登记
        </el-button>
      </div>
    </div>

    <div class="content-container">
      <div class="search-section">
        <el-card shadow="never">
          <el-form :model="searchForm" inline class="search-form">
            <el-form-item label="访客姓名">
              <el-input
                v-model="searchForm.visitorName"
                placeholder="请输入访客姓名"
                clearable
                style="width: 200px"
              />
            </el-form-item>
            <el-form-item label="访问日期">
              <el-date-picker
                v-model="searchForm.visitDate"
                type="date"
                placeholder="请选择访问日期"
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
          <el-table-column prop="visitorName" label="访客姓名" width="120" />
          <el-table-column prop="visitorPhone" label="手机号" width="130" />
          <el-table-column prop="visitStudentName" label="受访学生" width="120" />
          <el-table-column prop="visitRoomNo" label="访问宿舍" width="100" />
          <el-table-column prop="visitDate" label="访问日期" width="120" />
          <el-table-column prop="entryTime" label="进入时间" width="120" />
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
                <el-button size="small" type="primary" @click="handleCheckOut(row)" v-if="row.status === 1">
                  <el-icon><SwitchButton /></el-icon>
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
  User,
  Plus,
  Search,
  RefreshRight,
  View,
  Delete,
  SwitchButton
} from '@element-plus/icons-vue'

interface Visitor {
  id: number
  visitorName: string
  visitorPhone: string
  visitStudentName: string
  visitRoomNo: string
  visitDate: string
  entryTime: string
  status: number
}

const loading = ref(false)
const tableData = ref<Visitor[]>([])

const searchForm = reactive({
  visitorName: '',
  visitDate: null as Date | null
})

const pagination = reactive({
  page: 1,
  pageSize: 10,
  itemCount: 0
})

const getStatusType = (status: number) => {
  switch (status) {
    case 1: return 'success'
    case 2: return 'info'
    default: return 'info'
  }
}

const getStatusText = (status: number) => {
  switch (status) {
    case 1: return '访问中'
    case 2: return '已离开'
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
          visitorName: '王先生',
          visitorPhone: '13800138000',
          visitStudentName: '张三',
          visitRoomNo: 'A1-101',
          visitDate: '2024-09-15',
          entryTime: '09:30:00',
          status: 1
        },
        {
          id: 2,
          visitorName: '李女士',
          visitorPhone: '13900139000',
          visitStudentName: '李四',
          visitRoomNo: 'A1-102',
          visitDate: '2024-09-15',
          entryTime: '10:15:00',
          status: 2
        },
        {
          id: 3,
          visitorName: '陈同学',
          visitorPhone: '13700137000',
          visitStudentName: '王五',
          visitRoomNo: 'A1-201',
          visitDate: '2024-09-14',
          entryTime: '14:20:00',
          status: 2
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
  searchForm.visitorName = ''
  searchForm.visitDate = null
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

const handleRegister = () => {
  ElMessage.info('访客登记功能待开发')
}

const handleView = (row: Visitor) => {
  ElMessage.info(`查看访客详情: ${row.visitorName}`)
}

const handleCheckOut = (row: Visitor) => {
  ElMessage.info(`访客离场登记: ${row.visitorName}`)
}

const handleDelete = (row: Visitor) => {
  ElMessage.info(`删除访客记录: ${row.visitorName}`)
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.visitor-management {
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
  .visitor-management {
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