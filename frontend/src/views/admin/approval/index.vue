<template>
  <div class="approval-page">
    <div class="approval-container">
      <div v-if="loading" class="loading-container">
        <el-icon class="is-loading" :size="24">
          <Loading />
        </el-icon>
        <p>正在加载申请信息...</p>
      </div>

      <div v-else-if="error" class="error-container">
        <el-icon class="error-icon"><CircleClose /></el-icon>
        <h2>审批链接无效</h2>
        <p>{{ error }}</p>
        <el-button type="primary" @click="$router.push('/login')">返回登录</el-button>
      </div>

      <div v-else-if="!application" class="error-container">
        <el-icon class="error-icon"><CircleClose /></el-icon>
        <h2>申请不存在</h2>
        <p>该审批链接对应的申请不存在或已被删除</p>
        <el-button type="primary" @click="$router.push('/login')">返回登录</el-button>
      </div>

      <!-- 管理员登录验证 -->
      <div v-else-if="!isLoggedIn && !isAdminVerified" class="login-container">
        <el-icon class="login-icon"><Lock /></el-icon>
        <h2>管理员身份验证</h2>
        <p>请登录管理员账号以进行审批操作</p>

        <el-form :model="loginForm" :rules="loginRules" ref="loginFormRef" class="login-form">
          <el-form-item prop="username">
            <el-input
              v-model="loginForm.username"
              placeholder="管理员用户名"
              prefix-icon="User"
              size="large"
            />
          </el-form-item>
          <el-form-item prop="password">
            <el-input
              v-model="loginForm.password"
              type="password"
              placeholder="密码"
              prefix-icon="Lock"
              size="large"
              show-password
              @keyup.enter="handleAdminLogin"
            />
          </el-form-item>
          <el-form-item>
            <el-button
              type="primary"
              size="large"
              style="width: 100%"
              :loading="loginLoading"
              @click="handleAdminLogin"
            >
              登录验证
            </el-button>
          </el-form-item>
        </el-form>
      </div>

      <div v-else class="approval-content">
        <!-- 已处理的情况 -->
        <div v-if="application.status !== 0" class="processed-status">
          <el-icon class="status-icon"><InfoFilled /></el-icon>
          <h2>申请已处理</h2>
          <p>该申请已被{{ application.statusText }}</p>
          <div v-if="application.approvedTime" class="process-info">
            <p><strong>处理时间：</strong>{{ application.approvedTime }}</p>
            <p v-if="application.approvedByName"><strong>处理人：</strong>{{ application.approvedByName }}</p>
            <p v-if="application.rejectionReason"><strong>驳回原因：</strong>{{ application.rejectionReason }}</p>
          </div>
          <el-button type="primary" @click="$router.push('/login')">返回登录</el-button>
        </div>

        <!-- 待审批的情况 -->
        <div v-else class="pending-approval">
          <div class="application-header">
            <h2>注册申请审批</h2>
            <p>请审阅以下注册申请信息</p>
          </div>

          <el-card class="application-detail">
            <el-descriptions :column="2" border>
              <el-descriptions-item label="申请编号">{{ application.applicationNo }}</el-descriptions-item>
              <el-descriptions-item label="申请状态">
                <el-tag type="warning">待审批</el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="用户名">{{ application.username }}</el-descriptions-item>
              <el-descriptions-item label="姓名">{{ application.realName }}</el-descriptions-item>
              <el-descriptions-item label="性别">{{ application.gender === 1 ? '男' : '女' }}</el-descriptions-item>
              <el-descriptions-item label="手机号">{{ application.phone }}</el-descriptions-item>
              <el-descriptions-item label="邮箱" :span="2">{{ application.email }}</el-descriptions-item>
              <el-descriptions-item label="管理员工号">{{ application.adminEmployeeNo }}</el-descriptions-item>
              <el-descriptions-item label="申请时间">{{ application.createTime }}</el-descriptions-item>
            </el-descriptions>
          </el-card>

          <div class="approval-actions">
            <el-button
              type="success"
              size="large"
              :loading="approving"
              @click="handleApprove"
            >
              <el-icon><CircleCheck /></el-icon>
              通过申请
            </el-button>
            <el-button
              type="danger"
              size="large"
              :loading="rejecting"
              @click="showRejectDialog"
            >
              <el-icon><CircleClose /></el-icon>
              驳回申请
            </el-button>
          </div>
        </div>
      </div>

      <!-- 驳回对话框 -->
      <el-dialog
        v-model="rejectDialog.visible"
        title="驳回申请"
        width="500px"
        :before-close="closeRejectDialog"
      >
        <el-form :model="rejectDialog.form" label-width="80px">
          <el-form-item label="申请编号">
            <span>{{ application?.applicationNo }}</span>
          </el-form-item>
          <el-form-item label="申请人">
            <span>{{ application?.realName }} ({{ application?.username }})</span>
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
            <el-button @click="closeRejectDialog">取消</el-button>
            <el-button
              type="danger"
              :loading="rejectDialog.loading"
              @click="handleReject"
            >
              确认驳回
            </el-button>
          </span>
        </template>
      </el-dialog>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  CircleCheck,
  CircleClose,
  InfoFilled,
  Loading,
  Lock
} from '@element-plus/icons-vue'
import type { RegistrationApplicationVO } from '@/api/registration'
import { login, getCurrentUserInfo } from '@/api/user'
import {
  getApplicationByToken,
  approveApplication,
  rejectApplication
} from '@/api/registration'

const route = useRoute()
const router = useRouter()

// 响应式数据
const loading = ref(true)
const error = ref('')
const application = ref<RegistrationApplicationVO | null>(null)
const approving = ref(false)
const rejecting = ref(false)

// 管理员登录相关
const isLoggedIn = ref(false)
const isAdminVerified = ref(false)
const loginLoading = ref(false)
const loginFormRef = ref()

// 登录表单
const loginForm = reactive({
  username: '',
  password: ''
})

// 登录表单验证规则
const loginRules = reactive({
  username: [
    { required: true, message: '请输入管理员用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' }
  ]
})

// 驳回对话框
const rejectDialog = reactive({
  visible: false,
  loading: false,
  form: {
    reason: ''
  }
})

// 获取token参数
const token = route.params.token as string

// 加载申请信息
const loadApplication = async () => {
  if (!token) {
    error.value = '审批链接不完整'
    loading.value = false
    return
  }

  try {
    loading.value = true
    const data = await getApplicationByToken(token)
    application.value = data
  } catch (err: any) {
    error.value = err.message || '获取申请信息失败'
  } finally {
    loading.value = false
  }
}

// 处理通过
const handleApprove = async () => {
  if (!application.value) return

  try {
    approving.value = true
    await approveApplication(token)
    ElMessage.success('申请已通过')

    // 重新加载申请信息以显示最新状态
    await loadApplication()
  } catch (err: any) {
    ElMessage.error(err.message || '审批失败')
  } finally {
    approving.value = false
  }
}

// 显示驳回对话框
const showRejectDialog = () => {
  rejectDialog.form.reason = ''
  rejectDialog.visible = true
}

// 关闭驳回对话框
const closeRejectDialog = () => {
  rejectDialog.visible = false
  rejectDialog.form.reason = ''
}

// 处理驳回
const handleReject = async () => {
  if (!rejectDialog.form.reason.trim()) {
    ElMessage.warning('请输入驳回原因')
    return
  }

  try {
    rejecting.value = true
    rejectDialog.loading = true
    await rejectApplication(token, rejectDialog.form.reason)
    ElMessage.success('申请已驳回')

    // 关闭对话框
    closeRejectDialog()

    // 重新加载申请信息以显示最新状态
    await loadApplication()
  } catch (err: any) {
    ElMessage.error(err.message || '驳回失败')
  } finally {
    rejecting.value = false
    rejectDialog.loading = false
  }
}

// 管理员登录处理
const handleAdminLogin = async () => {
  if (!loginFormRef.value) return

  try {
    await loginFormRef.value.validate()
    loginLoading.value = true

    const loginData = {
      username: loginForm.username,
      password: loginForm.password
    }

    // 1. 先登录获取token
    const loginResponse = await login(loginData)

    // 保存token到localStorage
    localStorage.setItem('token', loginResponse.token)

    // 2. 获取用户信息验证角色和权限
    setTimeout(async () => {
      try {
        const userInfo = await getCurrentUserInfo()

        // 检查登录用户是否为管理员
        if (userInfo.role !== 'admin') {
          ElMessage.error('您不是管理员，无权限进行审批操作')
          localStorage.removeItem('token') // 清除无效token
          return
        }

        // 检查是否为指定的管理员（通过用户名匹配）
        if (application.value && loginData.username !== application.value.adminEmployeeNo) {
          ElMessage.error('您无权处理此申请，只有指定的管理员才能审批')
          localStorage.removeItem('token') // 清除无效token
          return
        }

        // 验证通过，更新状态
        isLoggedIn.value = true
        isAdminVerified.value = true
        ElMessage.success('管理员身份验证成功')

      } catch (infoError: any) {
        ElMessage.error('获取用户信息失败，请重新登录')
        localStorage.removeItem('token')
      }
    }, 100)

  } catch (err: any) {
    ElMessage.error(err.message || '登录失败')
  } finally {
    loginLoading.value = false
  }
}

// 页面加载时获取申请信息
onMounted(() => {
  loadApplication()
})
</script>

<style scoped>
.approval-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
}

.approval-container {
  width: 100%;
  max-width: 800px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  padding: 40px;
  min-height: 400px;
}

.loading-container {
  text-align: center;
  padding: 60px 0;
}

.loading-container p {
  margin-top: 20px;
  color: #666;
}

.loading-container .is-loading {
  animation: rotating 2s linear infinite;
}

@keyframes rotating {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}

.error-container {
  text-align: center;
  padding: 60px 0;
}

.error-icon {
  font-size: 64px;
  color: #f56c6c;
  margin-bottom: 20px;
}

.login-container {
  text-align: center;
  padding: 40px 0;
}

.login-icon {
  font-size: 64px;
  color: #409eff;
  margin-bottom: 20px;
}

.login-form {
  max-width: 400px;
  margin: 0 auto;
  padding: 30px;
  background: #f8fafc;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.error-container h2 {
  color: #333;
  margin-bottom: 10px;
}

.error-container p {
  color: #666;
  margin-bottom: 30px;
}

.approval-content {
  width: 100%;
}

.processed-status {
  text-align: center;
  padding: 40px 0;
}

.status-icon {
  font-size: 64px;
  color: #409eff;
  margin-bottom: 20px;
}

.processed-status h2 {
  color: #333;
  margin-bottom: 10px;
}

.processed-status p {
  color: #666;
  margin-bottom: 20px;
}

.process-info {
  background: #f5f7fa;
  border-radius: 8px;
  padding: 20px;
  margin: 20px 0;
  text-align: left;
}

.process-info p {
  margin: 8px 0;
  color: #666;
}

.pending-approval {
  width: 100%;
}

.application-header {
  text-align: center;
  margin-bottom: 30px;
}

.application-header h2 {
  color: #333;
  margin-bottom: 10px;
}

.application-header p {
  color: #666;
}

.application-detail {
  margin-bottom: 30px;
}

.approval-actions {
  display: flex;
  justify-content: center;
  gap: 20px;
  margin-top: 30px;
}

.approval-actions .el-button {
  min-width: 150px;
  height: 45px;
  font-size: 16px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

@media (max-width: 768px) {
  .approval-container {
    padding: 30px 20px;
  }

  .approval-actions {
    flex-direction: column;
    align-items: center;
  }

  .approval-actions .el-button {
    width: 100%;
    max-width: 300px;
  }
}
</style>