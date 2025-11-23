<template>
  <div class="reset-password-container">
    <!-- 装饰性背景元素 -->
    <div class="decoration-elements">
      <div class="floating-card card-1"></div>
      <div class="floating-card card-2"></div>
      <div class="floating-card card-3"></div>
      <div class="floating-card card-4"></div>
    </div>

    <div class="content-wrapper">
      <!-- 返回登录 -->
      <div class="back-to-login">
        <router-link to="/login" class="back-link">
          <el-icon><ArrowLeft /></el-icon>
          返回登录
        </router-link>
      </div>

      <!-- 主要内容 -->
      <div class="reset-content">
        <div class="logo">
          <svg width="80" height="80" viewBox="0 0 80 80" fill="none">
            <circle cx="40" cy="40" r="38" fill="#ffffff" fill-opacity="0.1"/>
            <circle cx="40" cy="40" r="30" fill="#ffffff" fill-opacity="0.2"/>
            <path d="M25 35h30v4H25zM25 42h30v4H25zM25 49h20v4H25z" fill="#ffffff"/>
            <path d="M55 25l8 8-8 8-8-8 8-8z" fill="#FFD700"/>
          </svg>
        </div>

        <div class="reset-header">
          <h1>重置密码</h1>
          <p>请输入您的新密码</p>
        </div>

        <el-form
          ref="formRef"
          :model="formData"
          :rules="rules"
          size="large"
          label-position="top"
          @keyup.enter="handleResetPassword"
        >
          <el-form-item prop="password">
            <el-input
              v-model="formData.password"
              type="password"
              placeholder="请输入新密码"
              :prefix-icon="Lock"
              show-password
              clearable
              class="custom-input"
            />
          </el-form-item>

          <el-form-item prop="confirmPassword">
            <el-input
              v-model="formData.confirmPassword"
              type="password"
              placeholder="请确认新密码"
              :prefix-icon="Lock"
              show-password
              clearable
              class="custom-input"
            />
          </el-form-item>

          <el-form-item>
            <el-button
              type="primary"
              size="large"
              :loading="loading"
              @click="handleResetPassword"
              class="submit-button"
            >
              <span v-if="!loading">重置密码</span>
              <span v-else>重置中...</span>
            </el-button>
          </el-form-item>
        </el-form>

        <div class="help-text">
          <p>密码要求：</p>
          <ul>
            <li>长度至少8位字符</li>
            <li>包含字母和数字</li>
            <li>建议包含特殊字符</li>
          </ul>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { ArrowLeft, Lock } from '@element-plus/icons-vue'
import { resetPassword, type ResetPasswordParams } from '@/api/user'

const router = useRouter()
const route = useRoute()
const message = ElMessage

const formRef = ref<FormInstance>()
const loading = ref(false)

const formData = reactive<ResetPasswordParams & { confirmPassword: string }>({
  token: '',
  password: '',
  confirmPassword: ''
})

const validatePass = (rule: any, value: any, callback: any) => {
  if (value === '') {
    callback(new Error('请输入密码'))
  } else if (value.length < 8) {
    callback(new Error('密码长度不能少于8位'))
  } else if (!/(?=.*[a-zA-Z])(?=.*\d)/.test(value)) {
    callback(new Error('密码必须包含字母和数字'))
  } else {
    if (formData.confirmPassword !== '') {
      if (formRef.value) {
        formRef.value.validateField('confirmPassword')
      }
    }
    callback()
  }
}

const validateConfirmPass = (rule: any, value: any, callback: any) => {
  if (value === '') {
    callback(new Error('请再次输入密码'))
  } else if (value !== formData.password) {
    callback(new Error('两次输入密码不一致!'))
  } else {
    callback()
  }
}

const rules: FormRules = {
  password: [
    {
      required: true,
      validator: validatePass,
      trigger: ['blur', 'change']
    }
  ],
  confirmPassword: [
    {
      required: true,
      validator: validateConfirmPass,
      trigger: ['blur', 'change']
    }
  ]
}

onMounted(() => {
  // 从URL参数中获取token
  const token = route.query.token as string
  if (!token) {
    message.error('重置链接无效或已过期')
    router.push('/forgot-password')
    return
  }
  formData.token = token
})

const handleResetPassword = async () => {
  if (!formRef.value) return

  try {
    await formRef.value.validate()

    loading.value = true

    // 调用重置密码API
    await resetPassword({
      token: formData.token,
      password: formData.password
    })

    message.success('密码重置成功，请使用新密码登录')

    // 延迟跳转到登录页面，给用户时间看到成功消息
    setTimeout(() => {
      router.push('/login')
    }, 1000)

  } catch (error: any) {
    console.error('重置密码失败:', error)
    message.error(error.response?.data?.message || '重置密码失败，请稍后重试')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.reset-password-container {
  display: flex;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  position: relative;
  overflow: hidden;
}

/* 内容包装器 */
.content-wrapper {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  width: 100%;
  max-width: 500px;
  margin: 0 auto;
  padding: 40px;
  min-height: 100vh;
  box-sizing: border-box;
}

/* 返回登录链接 */
.back-to-login {
  position: absolute;
  top: 40px;
  left: 40px;
}

.back-link {
  display: flex;
  align-items: center;
  gap: 8px;
  color: white;
  text-decoration: none;
  font-size: 16px;
  font-weight: 500;
  transition: all 0.3s ease;
  padding: 8px 16px;
  border-radius: 8px;
}

.back-link:hover {
  background: rgba(255, 255, 255, 0.1);
  transform: translateX(-2px);
}

/* 主要内容区域 */
.reset-content {
  background: rgba(255, 255, 255, 0.98);
  backdrop-filter: blur(20px);
  border-radius: 20px;
  box-shadow: 0 25px 50px rgba(0, 0, 0, 0.12);
  padding: 50px 40px;
  width: 100%;
  max-width: 450px;
  box-sizing: border-box;
}

.logo {
  margin-bottom: 30px;
  display: inline-block;
  animation: float 6s ease-in-out infinite;
}

@keyframes float {
  0%, 100% { transform: translateY(0px); }
  50% { transform: translateY(-15px); }
}

.reset-header {
  text-align: center;
  margin-bottom: 40px;
}

.reset-header h1 {
  font-size: 28px;
  font-weight: 700;
  color: #2d3748;
  margin-bottom: 12px;
  line-height: 1.2;
}

.reset-header p {
  font-size: 16px;
  color: #4a5568;
  font-weight: 400;
  line-height: 1.5;
  margin: 0;
}

:deep(.el-form-item) {
  margin-bottom: 24px;
}

:deep(.custom-input .el-input__wrapper) {
  height: 56px;
  border-radius: 10px;
  border: 2px solid rgba(229, 231, 235, 0.8);
  background: rgba(249, 250, 251, 0.9);
  transition: all 0.3s ease;
  box-shadow: none;
  padding: 0 20px;
}

:deep(.custom-input .el-input__wrapper:hover) {
  border-color: rgba(102, 126, 234, 0.8);
  background: rgba(255, 255, 255, 0.95);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.15);
}

:deep(.custom-input .el-input__wrapper.is-focus) {
  border-color: rgba(102, 126, 234, 0.9);
  background: rgba(255, 255, 255, 0.98);
  box-shadow: 0 0 0 4px rgba(102, 126, 234, 0.1);
}

:deep(.custom-input .el-input__inner) {
  font-size: 16px;
  color: #2d3748;
  font-weight: 500;
  line-height: 1.4;
}

:deep(.custom-input .el-input__inner::placeholder) {
  color: #9ca3af;
  font-size: 15px;
}

.submit-button {
  width: 100%;
  height: 56px;
  border-radius: 10px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  font-size: 16px;
  font-weight: 600;
  transition: all 0.3s ease;
  letter-spacing: 0.5px;
  line-height: 1.4;
}

:deep(.submit-button:hover) {
  background: linear-gradient(135deg, #5a67d8 0%, #6b5b95 100%);
  transform: translateY(-2px);
  box-shadow: 0 15px 30px rgba(102, 126, 234, 0.3);
}

:deep(.submit-button:active) {
  transform: translateY(0);
}

.help-text {
  margin-top: 30px;
  padding-top: 30px;
  border-top: 1px solid rgba(229, 231, 235, 0.8);
}

.help-text p {
  font-size: 14px;
  color: #4a5568;
  font-weight: 500;
  margin-bottom: 12px;
}

.help-text ul {
  list-style: none;
  padding: 0;
  margin: 0;
}

.help-text li {
  font-size: 13px;
  color: #6b7280;
  margin-bottom: 8px;
  padding-left: 20px;
  position: relative;
}

.help-text li::before {
  content: '•';
  position: absolute;
  left: 8px;
  color: #667eea;
}

/* 装饰元素 */
.decoration-elements {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  pointer-events: none;
  z-index: 1;
}

.floating-card {
  position: absolute;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  border-radius: 12px;
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.card-1 {
  width: 120px;
  height: 80px;
  top: 15%;
  right: 10%;
  animation: float 8s ease-in-out infinite;
}

.card-2 {
  width: 80px;
  height: 120px;
  bottom: 20%;
  right: 5%;
  animation: float 7s ease-in-out infinite 2s;
}

.card-3 {
  width: 100px;
  height: 60px;
  top: 25%;
  left: 8%;
  animation: float 9s ease-in-out infinite 1s;
}

.card-4 {
  width: 60px;
  height: 100px;
  bottom: 30%;
  left: 5%;
  animation: float 6s ease-in-out infinite 3s;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .content-wrapper {
    padding: 20px;
  }

  .back-to-login {
    top: 20px;
    left: 20px;
  }

  .reset-content {
    padding: 40px 30px;
  }

  .reset-header h1 {
    font-size: 24px;
  }

  :deep(.custom-input .el-input__wrapper),
  .submit-button {
    height: 50px;
  }
}

@media (max-width: 480px) {
  .content-wrapper {
    padding: 16px;
  }

  .back-to-login {
    top: 16px;
    left: 16px;
  }

  .reset-content {
    padding: 30px 20px;
    border-radius: 16px;
  }

  .reset-header h1 {
    font-size: 22px;
  }

  .reset-header p {
    font-size: 14px;
  }

  :deep(.custom-input .el-input__wrapper),
  .submit-button {
    height: 48px;
  }
}
</style>