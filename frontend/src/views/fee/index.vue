<template>
  <div class="fee-management">
    <div class="page-header">
      <div class="page-title">
        <h2>
          <el-icon><Money /></el-icon>
          费用管理
        </h2>
        <p>管理宿舍费用信息，包括水费、电费、住宿费等</p>
      </div>
      <div class="page-actions">
        <el-button type="primary" @click="handleAdd" size="large">
          <el-icon><Plus /></el-icon>
          新增费用
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
            <el-form-item label="费用类型">
              <el-select
                v-model="searchForm.type"
                placeholder="请选择费用类型"
                clearable
                style="width: 150px"
              >
                <el-option label="水费" value="water" />
                <el-option label="电费" value="electric" />
                <el-option label="住宿费" value="rent" />
              </el-select>
            </el-form-item>
            <el-form-item label="状态">
              <el-select
                v-model="searchForm.status"
                placeholder="请选择状态"
                clearable
                style="width: 120px"
              >
                <el-option label="未缴费" :value="1" />
                <el-option label="已缴费" :value="2" />
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
          <el-table-column prop="roomNo" label="宿舍号" width="100" />
          <el-table-column prop="type" label="费用类型" width="120">
            <template #default="{ row }">
              <el-tag :type="getTypeTagType(row.type)">
                {{ getTypeText(row.type) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="amount" label="金额" width="120" align="right">
            <template #default="{ row }">
              <span class="amount-text">¥{{ row.amount.toFixed(2) }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="period" label="费用周期" width="150" />
          <el-table-column prop="dueDate" label="截止日期" width="120" />
          <el-table-column prop="status" label="状态" width="100" align="center">
            <template #default="{ row }">
              <el-tag :type="row.status === 2 ? 'success' : 'warning'">
                {{ row.status === 2 ? '已缴费' : '未缴费' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="payDate" label="缴费日期" width="120" />
          <el-table-column label="操作" width="200" fixed="right">
            <template #default="{ row }">
              <el-button-group>
                <el-button size="small" @click="handleView(row)">
                  <el-icon><View /></el-icon>
                </el-button>
                <el-button size="small" type="primary" @click="handlePay(row)" v-if="row.status === 1">
                  <el-icon><Money /></el-icon>
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
  Money,
  Plus,
  Search,
  RefreshRight,
  View,
  Delete
} from '@element-plus/icons-vue'

interface Fee {
  id: number
  roomNo: string
  type: string
  amount: number
  period: string
  dueDate: string
  status: number
  payDate?: string
}

const loading = ref(false)
const tableData = ref<Fee[]>([])

const searchForm = reactive({
  roomNo: '',
  type: undefined as string | undefined,
  status: undefined as number | undefined
})

const pagination = reactive({
  page: 1,
  pageSize: 10,
  itemCount: 0
})

const getTypeTagType = (type: string) => {
  switch (type) {
    case 'water': return 'primary'
    case 'electric': return 'success'
    case 'rent': return 'warning'
    default: return 'info'
  }
}

const getTypeText = (type: string) => {
  switch (type) {
    case 'water': return '水费'
    case 'electric': return '电费'
    case 'rent': return '住宿费'
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
          roomNo: '101',
          type: 'rent',
          amount: 1200.00,
          period: '2024年9月',
          dueDate: '2024-09-30',
          status: 2,
          payDate: '2024-09-15'
        },
        {
          id: 2,
          roomNo: '102',
          type: 'water',
          amount: 45.50,
          period: '2024年8-9月',
          dueDate: '2024-09-25',
          status: 1
        },
        {
          id: 3,
          roomNo: '201',
          type: 'electric',
          amount: 128.80,
          period: '2024年8月',
          dueDate: '2024-09-20',
          status: 1
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
  searchForm.type = undefined
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
  ElMessage.info('新增费用功能待开发')
}

const handleView = (row: Fee) => {
  ElMessage.info(`查看费用详情: ${row.roomNo} - ${getTypeText(row.type)}`)
}

const handlePay = (row: Fee) => {
  ElMessage.info(`缴费处理: ${row.roomNo} - ¥${row.amount.toFixed(2)}`)
}

const handleDelete = (row: Fee) => {
  ElMessage.info(`删除费用记录: ${row.roomNo} - ${getTypeText(row.type)}`)
}

onMounted(() => {
  loadData()
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

.amount-text {
  color: #f56c6c;
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