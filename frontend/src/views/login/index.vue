<template>
  <div class="login-container">
    <div class="login-form">
      <div class="login-header">
        <div class="school-logo">
          <img src="/logo-placeholder.svg" alt="宿舍管理系统" width="48" height="48" />
        </div>
        <h1>宿舍管理系统</h1>
        <p>Dormitory Management System</p>
      </div>

      <el-form
        ref="formRef"
        :model="formData"
        :rules="rules"
        size="large"
        label-position="top"
        label-width="80px"
        @keyup.enter="handleLogin"
      >
        <el-form-item label="用户名" prop="username">
          <el-input
            v-model="formData.username"
            placeholder="请输入用户名"
            :prefix-icon="User"
            clearable
          />
        </el-form-item>

        <el-form-item label="密码" prop="password">
          <el-input
            v-model="formData.password"
            type="password"
            placeholder="请输入密码"
            :prefix-icon="Lock"
            show-password
            clearable
          />
        </el-form-item>

        <el-form-item>
          <el-button
            type="primary"
            size="large"
            :loading="userStore.loading"
            @click="handleLogin"
            style="width: 100%; height: 45px;"
          >
            <span v-if="!userStore.loading">登录系统</span>
            <span v-else>登录中...</span>
          </el-button>
        </el-form-item>
      </el-form>

      <div class="login-footer">
        <p>学生宿舍管理平台 | Student Dormitory Management</p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { User, Lock, School } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const message = ElMessage
const userStore = useUserStore()

const formRef = ref<FormInstance>()

const formData = reactive({
  username: 'admin',
  password: '123456'
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
      message.success('登录成功！欢迎进入宿舍管理系统')
      router.push('/')
    }
  } catch (error: any) {
    if (error.message) {
      message.error(error.message)
    }
  }
}
</script>

<style scoped>
.login-container {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.login-form {
  width: 420px;
  padding: 50px 40px 30px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 16px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
  position: relative;
  overflow: hidden;
}

.login-form::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, #1e40af, #3b82f6, #60a5fa);
}

.login-header {
  text-align: center;
  margin-bottom: 40px;
}

.school-logo {
  margin-bottom: 20px;
}

.login-header h1 {
  font-size: 32px;
  font-weight: 600;
  color: #1e40af;
  margin-bottom: 10px;
  letter-spacing: 1px;
}

.login-header p {
  font-size: 14px;
  color: #64748b;
  font-weight: 300;
  letter-spacing: 0.5px;
}

.login-footer {
  text-align: center;
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid #e2e8f0;
}

.login-footer p {
  font-size: 13px;
  color: #94a3b8;
  letter-spacing: 0.5px;
}

:deep(.el-form-item__label) {
  font-weight: 500;
  color: #374151;
}

:deep(.el-input__wrapper) {
  border-radius: 8px;
  box-shadow: 0 0 0 1px #e5e7eb;
  transition: all 0.3s ease;
}

:deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px #1e40af;
}

:deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 2px rgba(30, 64, 175, 0.2);
}

:deep(.el-button--primary) {
  background: linear-gradient(135deg, #1e40af 0%, #3b82f6 100%);
  border: none;
  border-radius: 8px;
  font-weight: 500;
  letter-spacing: 0.5px;
  transition: all 0.3s ease;
}

:deep(.el-button--primary:hover) {
  background: linear-gradient(135deg, #1e3a8a 0%, #1e40af 100%);
  transform: translateY(-1px);
  box-shadow: 0 8px 20px rgba(30, 64, 175, 0.3);
}

:deep(.el-button--primary:active) {
  transform: translateY(0);
}

@media (max-width: 480px) {
  .login-form {
    width: 100%;
    margin: 0 20px;
    padding: 40px 30px 20px;
  }

  .login-header h1 {
    font-size: 28px;
  }
}
</style>