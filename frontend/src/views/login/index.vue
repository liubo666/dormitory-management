<template>
  <div class="login-container">
  <!-- 装饰性背景元素 -->
  <div class="decoration-elements">
    <div class="floating-card card-1"></div>
    <div class="floating-card card-2"></div>
    <div class="floating-card card-3"></div>
    <div class="floating-card card-4"></div>
  </div>

  <div class="content-wrapper">
    <!-- 左侧欢迎区域 -->
    <div class="welcome-section">
      <div class="logo">
        <svg width="80" height="80" viewBox="0 0 80 80" fill="none">
          <circle cx="40" cy="40" r="38" fill="#ffffff" fill-opacity="0.1"/>
          <circle cx="40" cy="40" r="30" fill="#ffffff" fill-opacity="0.2"/>
          <path d="M25 35h30v4H25zM25 42h30v4H25zM25 49h20v4H25z" fill="#ffffff"/>
          <path d="M55 25l8 8-8 8-8-8 8-8z" fill="#FFD700"/>
        </svg>
      </div>
      <h1>欢迎来到宿舍管理系统</h1>
      <p>智能化的学生宿舍管理平台，让校园生活更便捷</p>

      <div class="features-grid">
        <div class="feature-card">
          <div class="feature-icon">
            <svg width="32" height="32" viewBox="0 0 24 24" fill="none">
              <path d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z" fill="#FFD700"/>
            </svg>
          </div>
          <div class="feature-content">
            <h3>智能管理</h3>
            <p>采用先进技术提升管理效率</p>
          </div>
        </div>

        <div class="feature-card">
          <div class="feature-icon">
            <svg width="32" height="32" viewBox="0 0 24 24" fill="none">
              <circle cx="12" cy="12" r="10" stroke="#ffffff" stroke-width="2"/>
              <path d="M8 12l2 2 4-4" stroke="#ffffff" stroke-width="2" stroke-linecap="round"/>
            </svg>
          </div>
          <div class="feature-content">
            <h3>安全保障</h3>
            <p>全面保护学生信息与安全</p>
          </div>
        </div>

        <div class="feature-card">
          <div class="feature-icon">
            <svg width="32" height="32" viewBox="0 0 24 24" fill="none">
              <rect x="3" y="3" width="18" height="18" rx="2" stroke="#ffffff" stroke-width="2"/>
              <path d="M9 12l2 2 4-4" stroke="#ffffff" stroke-width="2" stroke-linecap="round"/>
            </svg>
          </div>
          <div class="feature-content">
            <h3>便捷高效</h3>
            <p>简化流程，提升用户体验</p>
          </div>
        </div>
      </div>

      <div class="stats-row">
        <div class="stat-item">
          <div class="stat-number">5000+</div>
          <div class="stat-label">管理学生</div>
        </div>
        <div class="stat-item">
          <div class="stat-number">200+</div>
          <div class="stat-label">宿舍房间</div>
        </div>
        <div class="stat-item">
          <div class="stat-number">50+</div>
          <div class="stat-label">管理老师</div>
        </div>
      </div>
    </div>

    <!-- 右侧登录表单 -->
    <div class="login-section">
      <el-form
        ref="formRef"
        :model="formData"
        :rules="rules"
        size="large"
        label-position="top"
        @keyup.enter="handleLogin"
      >
        <div class="login-header">
          <h2>登录账号</h2>
          <p>请输入您的登录信息以继续访问系统</p>
        </div>
        <el-form-item prop="username">
          <el-input
            v-model="formData.username"
            placeholder="请输入用户名"
            :prefix-icon="User"
            clearable
            class="custom-input"
          />
        </el-form-item>

        <el-form-item prop="password">
          <el-input
            v-model="formData.password"
            type="password"
            placeholder="请输入密码"
            :prefix-icon="Lock"
            show-password
            clearable
            class="custom-input"
          />
        </el-form-item>

        <el-form-item>
          <div class="form-options">
            <el-checkbox v-model="formData.rememberPassword" class="remember-checkbox">
              记住密码
            </el-checkbox>
            <router-link to="/forgot-password" class="forgot-password">忘记密码？</router-link>
          </div>
        </el-form-item>

        <el-form-item>
          <el-button
            type="primary"
            size="large"
            :loading="userStore.loading"
            @click="handleLogin"
            class="login-button"
          >
            <span v-if="!userStore.loading">立即登录</span>
            <span v-else>登录中...</span>
          </el-button>
        </el-form-item>

        <div class="register-link">
          <span>还没有账号？</span>
          <router-link to="/register" class="link">立即注册</router-link>
        </div>
      </el-form>
    </div>
  </div>
</div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const message = ElMessage
const userStore = useUserStore()

const formRef = ref<FormInstance>()

const formData = reactive({
  username: '',
  password: '',
  rememberPassword: false
})

const rules: FormRules = {
  username: [
    {
      required: true,
      message: '请输入用户名',
      trigger: ['blur', 'change']
    },
    {
      min: 2,
      max: 20,
      message: '用户名长度应在2-20个字符之间',
      trigger: ['blur', 'change']
    }
  ],
  password: [
    {
      required: true,
      message: '请输入密码',
      trigger: ['blur', 'change']
    },
    {
      min: 6,
      max: 20,
      message: '密码长度应在6-20个字符之间',
      trigger: ['blur', 'change']
    }
  ]
}

const handleLogin = async () => {
  if (!formRef.value) return

  try {
    await formRef.value.validate()

    const success = await userStore.handleLogin({
      username: formData.username,
      password: formData.password
    })

    if (success) {
      // 处理记住密码逻辑
      if (formData.rememberPassword) {
        // 保存用户名和密码到 localStorage
        localStorage.setItem('rememberedUsername', formData.username)
        localStorage.setItem('rememberedPassword', formData.password)
        localStorage.setItem('rememberPassword', 'true')
      } else {
        // 清除保存的信息
        localStorage.removeItem('rememberedUsername')
        localStorage.removeItem('rememberedPassword')
        localStorage.removeItem('rememberPassword')
      }

      message.success('登录成功！欢迎进入宿舍管理系统')
      router.push('/')
    }
  } catch (error: any) {
    if (error.message) {
      message.error(error.message)
    }
  }
}

// 页面加载时检查是否有记住的密码
onMounted(() => {
  const shouldRemember = localStorage.getItem('rememberPassword')
  if (shouldRemember === 'true') {
    const rememberedUsername = localStorage.getItem('rememberedUsername')
    const rememberedPasswordValue = localStorage.getItem('rememberedPassword')

    if (rememberedUsername && rememberedPasswordValue) {
      formData.username = rememberedUsername
      formData.password = rememberedPasswordValue
      formData.rememberPassword = true
    }
  }
})
</script>

<style scoped>
.login-container {
  display: flex;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  position: relative;
  overflow: hidden;
  transform: scale(0.8);
  transform-origin: center center;
  width: 125%;
  height: 125%;
  position: fixed;
  top: -12.5%;
  left: -12.5%;
}

/* 内容包装器 */
.content-wrapper {
  display: flex;
  width: 100%;
  max-width: 1400px;
  margin: 0 auto;
  padding: 40px;
  gap: 60px;
  align-items: center;
  min-height: 100vh;
  box-sizing: border-box;
}

/* 左侧欢迎区域 */
.welcome-section {
  flex: 1;
  color: white;
  padding: 40px;
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  justify-content: center;
  max-width: 700px;
}

.logo {
  margin-bottom: 40px;
  display: inline-block;
  animation: float 6s ease-in-out infinite;
}

@keyframes float {
  0%, 100% { transform: translateY(0px); }
  50% { transform: translateY(-15px); }
}

.welcome-section h1 {
  font-size: 48px;
  font-weight: 700;
  margin-bottom: 20px;
  line-height: 1.3;
  text-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  letter-spacing: -0.5px;
}

.welcome-section p {
  font-size: 20px;
  opacity: 0.9;
  font-weight: 400;
  margin-bottom: 50px;
  line-height: 1.6;
  letter-spacing: 0.2px;
}

/* 功能卡片网格 */
.features-grid {
  display: grid;
  grid-template-columns: 1fr;
  gap: 20px;
  margin-bottom: 50px;
  width: 100%;
}

.feature-card {
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  border-radius: 14px;
  padding: 24px;
  border: 1px solid rgba(255, 255, 255, 0.2);
  transition: all 0.3s ease;
  text-align: left;
  display: flex;
  align-items: center;
  gap: 20px;
  width: 100%;
  box-sizing: border-box;
}

.feature-card:hover {
  background: rgba(255, 255, 255, 0.15);
  transform: translateY(-5px);
  box-shadow: 0 15px 30px rgba(0, 0, 0, 0.2);
}

.feature-icon {
  width: 56px;
  height: 56px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.feature-content h3 {
  font-size: 20px;
  font-weight: 600;
  margin-bottom: 8px;
  color: white;
  letter-spacing: 0.3px;
}

.feature-content p {
  font-size: 16px;
  opacity: 0.85;
  margin-bottom: 0;
  line-height: 1.5;
}

/* 统计数据行 */
.stats-row {
  display: flex;
  justify-content: flex-start;
  gap: 50px;
  margin-top: 20px;
}

.stat-item {
  text-align: center;
  min-width: 90px;
}

.stat-number {
  font-size: 36px;
  font-weight: 700;
  color: #FFD700;
  margin-bottom: 10px;
  display: block;
  letter-spacing: -0.5px;
}

.stat-label {
  font-size: 16px;
  opacity: 0.85;
  font-weight: 500;
  letter-spacing: 0.2px;
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

/* 右侧登录表单 */
.login-section {
  flex: 0 0 520px;
  background: rgba(255, 255, 255, 0.98);
  backdrop-filter: blur(20px);
  border-radius: 20px;
  box-shadow: 0 25px 50px rgba(0, 0, 0, 0.12);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 50px 45px;
  min-height: 600px;
  height: fit-content;
  max-height: 80vh;
  overflow-y: auto;
  box-sizing: border-box;
}

.login-container-inner {
  width: 100%;
  max-width: 450px;
}

.login-header {
  text-align: center;
  margin-bottom: 35px;
  padding-bottom: 20px;
}

.login-header h2 {
  font-size: 28px;
  font-weight: 700;
  color: #2d3748;
  margin-bottom: 8px;
  line-height: 1.2;
}

.login-header p {
  font-size: 14px;
  color: #4a5568;
  font-weight: 400;
  line-height: 1.5;
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
  min-width: 0;
}

:deep(.custom-input .el-input__wrapper:hover) {
  border-color: rgba(102, 126, 234, 0.8);
}

:deep(.custom-input .el-input__wrapper.is-focus) {
  border-color: rgba(102, 126, 234, 0.9);
}

/* 确保密码输入框也没有大小变化 */
:deep(.custom-input .el-input__wrapper) {
  transition: border-color 0.3s ease !important;
}

:deep(.custom-input .el-input__inner) {
  font-size: 16px;
  color: #2d3748;
  font-weight: 500;
  line-height: 1.4;
  padding: 0 8px;
}

:deep(.custom-input .el-input__inner::placeholder) {
  color: #9ca3af;
  font-size: 15px;
}

.form-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}

:deep(.remember-checkbox) {
  color: #4a5568;
  font-size: 14px;
  font-weight: 500;
}

:deep(.remember-checkbox .el-checkbox__input.is-checked .el-checkbox__inner) {
  background-color: #667eea;
  border-color: #667eea;
}

.forgot-password {
  color: #667eea;
  text-decoration: none;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.3s ease;
}

.forgot-password:hover {
  color: #5a67d8;
  text-decoration: underline;
}

.login-button {
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

:deep(.login-button:hover) {
  background: linear-gradient(135deg, #5a67d8 0%, #6b5b95 100%);
  transform: translateY(-2px);
  box-shadow: 0 15px 30px rgba(102, 126, 234, 0.3);
}

:deep(.login-button:active) {
  transform: translateY(0);
}

.register-link {
  text-align: center;
  margin-top: 25px;
  font-size: 13px;
  color: #4a5568;
}

.register-link .link {
  color: #667eea;
  text-decoration: none;
  font-weight: 600;
  margin-left: 6px;
  transition: all 0.3s ease;
}

.register-link .link:hover {
  color: #5a67d8;
  text-decoration: underline;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .content-wrapper {
    padding: 30px;
    gap: 50px;
  }

  .welcome-section {
    padding: 30px;
  }

  .welcome-section h1 {
    font-size: 42px;
  }

  .welcome-section p {
    font-size: 18px;
  }

  .login-section {
    flex: 0 0 450px;
  }
}

@media (max-width: 1024px) {
  .content-wrapper {
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 40px 20px;
    gap: 50px;
    min-height: 100vh;
  }

  .welcome-section {
    max-width: 600px;
    align-items: center;
    text-align: center;
    padding: 20px;
  }

  .login-section {
    flex: none;
    width: 100%;
    max-width: 480px;
    padding: 45px 35px;
  }

  .features-grid {
    max-width: 500px;
  }

  .stats-row {
    justify-content: center;
  }
}

@media (max-width: 768px) {
  .content-wrapper {
    padding: 16px;
  }

  .welcome-section h1 {
    font-size: 32px;
  }

  .welcome-section p {
    font-size: 16px;
  }

  .features-grid {
    gap: 16px;
  }

  .feature-card {
    padding: 16px;
  }

  .stats-row {
    gap: 20px;
  }

  .login-section {
    padding: 30px 20px;
  }

  .login-header h2 {
    font-size: 24px;
  }

  :deep(.custom-input .el-input__wrapper),
  .login-button {
    height: 50px;
  }

  .form-options {
    flex-direction: column;
    gap: 12px;
    align-items: flex-start;
  }
}

@media (max-width: 480px) {
  .content-wrapper {
    padding: 12px;
  }

  .welcome-section {
    padding: 16px;
  }

  .welcome-section h1 {
    font-size: 28px;
  }

  .welcome-section p {
    font-size: 14px;
  }

  .features-grid {
    gap: 12px;
  }

  .feature-card {
    flex-direction: column;
    text-align: center;
    padding: 14px;
  }

  .stats-row {
    flex-direction: column;
    gap: 16px;
  }

  .login-section {
    padding: 24px 16px;
    border-radius: 16px;
  }

  .login-header h2 {
    font-size: 22px;
  }

  .login-header p {
    font-size: 13px;
  }

  :deep(.custom-input .el-input__wrapper),
  .login-button {
    height: 48px;
  }
}
</style>