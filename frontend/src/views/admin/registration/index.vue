<template>
  <div class="registration-management">
    <div class="page-header">
      <h2>注册申请管理</h2>
      <p>处理用户的注册申请</p>
    </div>

    <!-- 状态筛选 -->
    <el-card class="filter-card">
      <el-form :model="filterForm" inline>
        <el-form-item label="申请状态">
          <el-select v-model="filterForm.status" placeholder="请选择状态" clearable @change="loadData">
            <el-option label="全部" :value="null" />
            <el-option label="待审批" :value="0" />
            <el-option label="已通过" :value="1" />
            <el-option label="已驳回" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button @click="loadData">刷新</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 申请列表 -->
    <el-card class="table-card">
      <el-table
        v-loading="loading"
        :data="tableData"
        style="width: 100%"
        empty-text="暂无注册申请"
      >
        <el-table-column prop="applicationNo" label="申请编号" width="150" />
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="realName" label="姓名" width="100" />
        <el-table-column prop="gender" label="性别" width="80">
          <template #default="{ row }">
            {{ row.gender === 1 ? '男' : '女' }}
          </template>
        </el-table-column>
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column prop="email" label="邮箱" min-width="180" />
        <el-table-column prop="adminEmployeeNo" label="管理员工号" width="120" />
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ row.statusText }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="申请时间" width="160" />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button-group>
              <el-button size="small" @click="viewDetail(row)">详情</el-button>
              <el-button
                v-if="row.status === 0"
                size="small"
                type="success"
                @click="approveApplication(row)"
              >
                通过
              </el-button>
              <el-button
                v-if="row.status === 0"
                size="small"
                type="danger"
                @click="rejectApplication(row)"
              >
                驳回
              </el-button>
            </el-button-group>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="pagination.current"
          v-model:page-size="pagination.size"
          :page-sizes="[10, 20, 50, 100]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="loadData"
          @current-change="loadData"
        />
      </div>
    </el-card>

    <!-- 详情对话框 -->
    <el-dialog
      v-model="detailDialog.visible"
      title="申请详情"
      width="600px"
      :before-close="closeDetailDialog"
    >
      <div v-if="detailDialog.data" class="application-detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="申请编号">{{ detailDialog.data.applicationNo }}</el-descriptions-item>
          <el-descriptions-item label="申请状态">
            <el-tag :type="getStatusType(detailDialog.data.status)">
              {{ detailDialog.data.statusText }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="用户名">{{ detailDialog.data.username }}</el-descriptions-item>
          <el-descriptions-item label="姓名">{{ detailDialog.data.realName }}</el-descriptions-item>
          <el-descriptions-item label="性别">{{ detailDialog.data.gender === 1 ? '男' : '女' }}</el-descriptions-item>
          <el-descriptions-item label="手机号">{{ detailDialog.data.phone }}</el-descriptions-item>
          <el-descriptions-item label="邮箱" :span="2">{{ detailDialog.data.email }}</el-descriptions-item>
          <el-descriptions-item label="管理员工号">{{ detailDialog.data.adminEmployeeNo }}</el-descriptions-item>
          <el-descriptions-item label="申请时间">{{ detailDialog.data.createTime }}</el-descriptions-item>
          <el-descriptions-item v-if="detailDialog.data.approvedByName" label="审批人">{{ detailDialog.data.approvedByName }}</el-descriptions-item>
          <el-descriptions-item v-if="detailDialog.data.approvedTime" label="审批时间">{{ detailDialog.data.approvedTime }}</el-descriptions-item>
          <el-descriptions-item v-if="detailDialog.data.rejectionReason" label="驳回原因" :span="2">
            {{ detailDialog.data.rejectionReason }}
          </el-descriptions-item>
        </el-descriptions>
      </div>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="closeDetailDialog">关闭</el-button>
          <el-button
            v-if="detailDialog.data?.status === 0"
            type="success"
            @click="approveApplication(detailDialog.data)"
          >
            通过申请
          </el-button>
          <el-button
            v-if="detailDialog.data?.status === 0"
            type="danger"
            @click="rejectApplication(detailDialog.data)"
          >
            驳回申请
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 驳回对话框 -->
    <el-dialog
      v-model="rejectDialog.visible"
      title="驳回申请"
      width="500px"
    >
      <el-form :model="rejectDialog.form" label-width="80px">
        <el-form-item label="申请编号">
          <span>{{ rejectDialog.application?.applicationNo }}</span>
        </el-form-item>
        <el-form-item label="申请人">
          <span>{{ rejectDialog.application?.realName }} ({{ rejectDialog.application?.username }})</span>
        </el-form-item>
        <el-form-item label="驳回原因" required>
          <el-input
            v-model="rejectDialog.form.reason"
            type="textarea"
            :rows="4"
            placeholder="请输入驳回原因..."
            maxlength="500"
            show-word-limit
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="rejectDialog.visible = false">取消</el-button>
          <el-button
            type="danger"
            :loading="rejectDialog.loading"
            @click="confirmReject"
          >
            确认驳回
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { RegistrationApplicationVO } from '@/api/registration'
import {
  getRegistrationApplications,
  approveApplication as approveApp,
  rejectApplication as rejectApp
} from '@/api/registration'

// 响应式数据
const loading = ref(false)
const tableData = ref<RegistrationApplicationVO[]>([])

// 筛选表单
const filterForm = reactive({
  status: null as number | null
})

// 分页数据
const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

// 详情对话框
const detailDialog = reactive({
  visible: false,
  data: null as RegistrationApplicationVO | null
})

// 驳回对话框
const rejectDialog = reactive({
  visible: false,
  loading: false,
  application: null as RegistrationApplicationVO | null,
  form: {
    reason: ''
  }
})

// 获取状态标签类型
const getStatusType = (status: number) => {
  switch (status) {
    case 0: return 'warning'  // 待审批
    case 1: return 'success'  // 已通过
    case 2: return 'danger'   // 已驳回
    default: return 'info'
  }
}

// 加载数据
const loadData = async () => {
  loading.value = true
  try {
    const response = await getRegistrationApplications({
      current: pagination.current,
      size: pagination.size,
      status: filterForm.status || undefined
    })

    tableData.value = response.records
    pagination.total = response.total
  } catch (error: any) {
    ElMessage.error(error.message || '获取数据失败')
  } finally {
    loading.value = false
  }
}

// 查看详情
const viewDetail = async (row: RegistrationApplicationVO) => {
  detailDialog.data = row
  detailDialog.visible = true
}

// 关闭详情对话框
const closeDetailDialog = () => {
  detailDialog.visible = false
  detailDialog.data = null
}

// 通过申请
const approveApplication = async (row: RegistrationApplicationVO) => {
  try {
    await ElMessageBox.confirm(
      `确定要通过 ${row.realName} (${row.username}) 的注册申请吗？`,
      '确认通过',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'success'
      }
    )

    await approveApp(row.applicationNo) // 使用applicationNo作为审批token
    ElMessage.success('审批通过')
    loadData()
    closeDetailDialog()
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '审批失败')
    }
  }
}

// 驳回申请
const rejectApplication = (row: RegistrationApplicationVO) => {
  rejectDialog.application = row
  rejectDialog.form.reason = ''
  rejectDialog.visible = true
}

// 确认驳回
const confirmReject = async () => {
  if (!rejectDialog.form.reason.trim()) {
    ElMessage.warning('请输入驳回原因')
    return
  }

  try {
    rejectDialog.loading = true
    await rejectApp(rejectDialog.application!.applicationNo, rejectDialog.form.reason) // 使用applicationNo作为审批token
    ElMessage.success('申请已驳回')
    rejectDialog.visible = false
    loadData()
    closeDetailDialog()
  } catch (error: any) {
    ElMessage.error(error.message || '驳回失败')
  } finally {
    rejectDialog.loading = false
  }
}

// 页面加载时获取数据
onMounted(() => {
  loadData()
})
</script>

<style scoped>
.registration-management {
  padding: 20px;
}

.page-header {
  margin-bottom: 20px;
}

.page-header h2 {
  margin: 0 0 8px 0;
  color: #333;
}

.page-header p {
  margin: 0;
  color: #666;
  font-size: 14px;
}

.filter-card {
  margin-bottom: 20px;
}

.table-card {
  margin-bottom: 20px;
}

.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

.application-detail {
  margin: 20px 0;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style>