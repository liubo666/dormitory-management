<template>
  <div class="register-container">
    <div class="register-box">
      <div class="register-header">
        <h2>用户注册</h2>
        <p>请填写以下信息完成注册申请</p>
      </div>

      <el-form
        ref="registerFormRef"
        :model="registerForm"
        :rules="registerRules"
        label-width="100px"
        class="register-form"
        size="large"
      >
        <el-form-item label="用户名" prop="username">
          <el-input
            v-model="registerForm.username"
            placeholder="请输入用户名（3-20个字符）"
            :prefix-icon="User"
            clearable
          />
        </el-form-item>

        <el-form-item label="密码" prop="password">
          <el-input
            v-model="registerForm.password"
            type="password"
            placeholder="请输入密码（6-20个字符）"
            :prefix-icon="Lock"
            show-password
            clearable
          />
        </el-form-item>

        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input
            v-model="registerForm.confirmPassword"
            type="password"
            placeholder="请再次输入密码"
            :prefix-icon="Lock"
            show-password
            clearable
          />
        </el-form-item>

        <el-form-item label="真实姓名" prop="realName">
          <el-input
            v-model="registerForm.realName"
            placeholder="请输入真实姓名"
            clearable
          />
        </el-form-item>

        <el-form-item label="性别" prop="gender">
          <el-radio-group v-model="registerForm.gender">
            <el-radio :label="1">男</el-radio>
            <el-radio :label="2">女</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="手机号" prop="phone">
          <el-input
            v-model="registerForm.phone"
            placeholder="请输入手机号"
            :prefix-icon="Phone"
            clearable
          />
        </el-form-item>

        <el-form-item label="邮箱" prop="email">
          <el-input
            v-model="registerForm.email"
            placeholder="请输入邮箱地址"
            :prefix-icon="Message"
            clearable
          />
        </el-form-item>

        <el-form-item label="管理员工号" prop="adminEmployeeNo">
          <div class="admin-employee-input">
            <el-input
              v-model="registerForm.adminEmployeeNo"
              placeholder="请输入推荐管理员的工号"
              :prefix-icon="UserFilled"
              clearable
              @blur="validateAdminEmployeeNo"
              @input="onAdminEmployeeNoChange"
            />
            <div v-if="adminValidationStatus !== null" class="validation-status">
              <el-icon v-if="adminValidationStatus" class="success-icon"><CircleCheck /></el-icon>
              <el-icon v-else class="error-icon"><CircleClose /></el-icon>
              <span :class="adminValidationStatus ? 'success-text' : 'error-text'">
                {{ adminValidationMessage }}
              </span>
            </div>
          </div>
        </el-form-item>

        <el-form-item>
          <el-button
            type="primary"
            :loading="submitting"
            :disabled="!canSubmit"
            class="submit-btn"
            @click="submitRegister"
          >
            {{ submitButtonText }}
          </el-button>
        </el-form-item>

        <el-form-item>
          <div class="login-link">
            已有账号？
            <router-link to="/login" class="link">立即登录</router-link>
          </div>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { User, Lock, Phone, Message, UserFilled, CircleCheck, CircleClose } from '@element-plus/icons-vue'
import { submitRegistration, validateAdminEmployeeNo as validateAdminNo, type RegistrationApplicationDTO } from '@/api/registration'

const router = useRouter()
const registerFormRef = ref<FormInstance>()

// 表单数据
const registerForm = reactive<RegistrationApplicationDTO>({
  username: '',
  password: '',
  confirmPassword: '',
  realName: '',
  gender: 1,
  phone: '',
  email: '',
  adminEmployeeNo: ''
})

// 管理员验证状态
const adminValidationStatus = ref<boolean | null>(null)
const adminValidationMessage = ref('')
const adminValidationLoading = ref(false)

// 提交状态
const submitting = ref(false)

// 计算属性：检查是否可以提交
const canSubmit = computed(() => {
  // 检查所有必填字段是否都有值
  const allFieldsFilled =
    registerForm.username.trim() !== '' &&
    registerForm.password.trim() !== '' &&
    registerForm.confirmPassword.trim() !== '' &&
    registerForm.realName.trim() !== '' &&
    registerForm.phone.trim() !== '' &&
    registerForm.email.trim() !== '' &&
    registerForm.adminEmployeeNo.trim() !== '';

  // 检查管理员工号是否验证通过
  const adminValidated = adminValidationStatus.value === true;

  // 检查密码是否一致
  const passwordsMatch = registerForm.password === registerForm.confirmPassword;

  // 检查表单是否正在提交
  const notSubmitting = !submitting.value;

  return allFieldsFilled && adminValidated && passwordsMatch && notSubmitting;
})

// 计算按钮文本
const submitButtonText = computed(() => {
  if (submitting.value) {
    return '提交中...'
  }

  if (adminValidationStatus.value === false) {
    return '管理员工号无效'
  }

  if (adminValidationStatus.value === null && registerForm.adminEmployeeNo.trim() !== '') {
    return '正在验证管理员工号...'
  }

  const allFieldsFilled =
    registerForm.username.trim() !== '' &&
    registerForm.password.trim() !== '' &&
    registerForm.confirmPassword.trim() !== '' &&
    registerForm.realName.trim() !== '' &&
    registerForm.phone.trim() !== '' &&
    registerForm.email.trim() !== '' &&
    registerForm.adminEmployeeNo.trim() !== '';

  if (!allFieldsFilled) {
    return '请完善所有必填信息'
  }

  if (registerForm.password !== registerForm.confirmPassword) {
    return '两次输入的密码不一致'
  }

  return '提交注册申请'
})

// 表单验证规则
const registerRules: FormRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在3到20个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在6到20个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== registerForm.password) {
          callback(new Error('两次输入密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ],
  realName: [
    { required: true, message: '请输入真实姓名', trigger: 'blur' },
    { max: 10, message: '姓名长度不能超过10个字符', trigger: 'blur' }
  ],
  gender: [
    { required: true, message: '请选择性别', trigger: 'change' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  adminEmployeeNo: [
    { required: true, message: '请输入管理员工号', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (adminValidationStatus.value === false) {
          callback(new Error('管理员工号无效'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

// 管理员工号变化处理
const onAdminEmployeeNoChange = () => {
  // 当管理员工号发生变化时，重置验证状态
  adminValidationStatus.value = null
  adminValidationMessage.value = ''
}

// 验证管理员工号
const validateAdminEmployeeNo = async () => {
  if (!registerForm.adminEmployeeNo) {
    adminValidationStatus.value = null
    adminValidationMessage.value = ''
    return
  }

  adminValidationLoading.value = true
  try {
    const isValid = await validateAdminNo(registerForm.adminEmployeeNo)
    adminValidationStatus.value = isValid
    adminValidationMessage.value = isValid ? '管理员工号验证成功' : '管理员工号无效'
  } catch (error) {
    adminValidationStatus.value = false
    adminValidationMessage.value = '验证失败，请稍后重试'
  } finally {
    adminValidationLoading.value = false
  }
}

// 提交注册申请
const submitRegister = async () => {
  if (!registerFormRef.value) return

  try {
    await registerFormRef.value.validate()

    submitting.value = true
    const response = await submitRegistration(registerForm)

    ElMessage.success('注册申请提交成功！请等待管理员审批，审批结果将通过邮件通知您。')

    // 延迟跳转到登录页面
    setTimeout(() => {
      router.push('/login')
    }, 1000)

  } catch (error: any) {
    if (error.message) {
      ElMessage.error(error.message)
    }
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.register-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.register-box {
  width: 100%;
  max-width: 500px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  padding: 40px;
}

.register-header {
  text-align: center;
  margin-bottom: 40px;
}

.register-header h2 {
  color: #333;
  margin-bottom: 10px;
  font-size: 28px;
  font-weight: 600;
}

.register-header p {
  color: #666;
  font-size: 14px;
}

.register-form {
  width: 100%;
}

.admin-employee-input {
  width: 100%;
}

.validation-status {
  display: flex;
  align-items: center;
  margin-top: 5px;
  font-size: 12px;
}

.success-icon {
  color: #67c23a;
  margin-right: 4px;
}

.error-icon {
  color: #f56c6c;
  margin-right: 4px;
}

.success-text {
  color: #67c23a;
}

.error-text {
  color: #f56c6c;
}

.submit-btn {
  width: 100%;
  height: 45px;
  font-size: 16px;
  font-weight: 600;
  transition: all 0.3s ease;
}

.submit-btn:disabled {
  background-color: #c0c4cc !important;
  border-color: #c0c4cc !important;
  color: #909399 !important;
  cursor: not-allowed;
}

.login-link {
  text-align: center;
  color: #666;
  font-size: 14px;
}

.link {
  color: #409eff;
  text-decoration: none;
  margin-left: 5px;
}

.link:hover {
  text-decoration: underline;
}

@media (max-width: 768px) {
  .register-box {
    padding: 30px 20px;
  }

  .register-header h2 {
    font-size: 24px;
  }
}
</style>