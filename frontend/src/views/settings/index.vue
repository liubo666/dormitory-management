<template>
  <div class="settings-container">
    <div class="settings-header">
      <h2>个人设置</h2>
    </div>

    <el-card class="info-card">
      <template #header>
        <div class="card-header">
          <span>个人信息</span>
          <div class="header-buttons">
            <el-button @click="goToDashboard">
              返回首页
            </el-button>
            <el-button
              v-if="!isEditing"
              type="primary"
              @click="startEdit"
            >
              编辑
            </el-button>
            <div v-else>
              <el-button @click="cancelEdit">取消</el-button>
              <el-button type="primary" @click="saveInfo" :loading="saving">
                保存
              </el-button>
            </div>
          </div>
        </div>
      </template>

      <el-form
        ref="formRef"
        :model="formData"
        :rules="rules"
        label-width="120px"
        label-position="left"
      >
        <!-- 只读字段：用户名和角色 -->
        <el-row :gutter="24">
          <el-col :span="12">
            <el-form-item label="用户名">
              <el-input
                v-model="formData.username"
                disabled
                placeholder="用户名"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="角色">
              <el-input
                :value="getRoleText(formData.role)"
                disabled
                placeholder="角色"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <!-- 可编辑字段：头像 -->
        <el-row :gutter="24">
          <el-col :span="24">
            <el-form-item label="头像">
              <div class="avatar-upload">
                <el-avatar
                  :size="80"
                  :src="avatarPreviewUrl"
                  v-if="avatarPreviewUrl"
                >
                  <el-icon><User /></el-icon>
                </el-avatar>
                <el-avatar
                  :size="80"
                  v-else
                >
                  <el-icon><User /></el-icon>
                </el-avatar>
                <el-upload
                  v-if="isEditing"
                  class="avatar-uploader"
                  :action="uploadUrl"
                  :show-file-list="false"
                  :on-success="handleAvatarSuccess"
                  :before-upload="beforeAvatarUpload"
                  :http-request="customUpload"
                >
                  <el-button size="small" type="primary">点击上传</el-button>
                </el-upload>
              </div>
            </el-form-item>
          </el-col>
        </el-row>

        <!-- 可编辑字段：真实姓名、性别、手机号、邮箱 -->
        <el-row :gutter="24">
          <el-col :span="12">
            <el-form-item label="真实姓名" prop="name">
              <el-input
                v-model="formData.name"
                :disabled="!isEditing"
                placeholder="请输入真实姓名"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="性别" prop="gender">
              <el-select
                v-model="formData.gender"
                :disabled="!isEditing"
                placeholder="请选择性别"
                style="width: 100%"
              >
                <el-option label="男" :value="1" />
                <el-option label="女" :value="2" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="24">
          <el-col :span="12">
            <el-form-item label="手机号" prop="phone">
              <el-input
                v-model="formData.phone"
                :disabled="!isEditing"
                placeholder="请输入手机号"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="邮箱" prop="email">
              <el-input
                v-model="formData.email"
                :disabled="!isEditing"
                placeholder="请输入邮箱地址"
              />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, type FormInstance, type UploadProps, type UploadRequestOptions } from 'element-plus'
import { User } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { updateUserInfo, getCurrentUserInfo, type UserInfo } from '@/api/user'
import { request } from '@/utils/request'

const userStore = useUserStore()
const router = useRouter()

// 表单相关
const formRef = ref<FormInstance>()
const isEditing = ref(false)
const saving = ref(false)

// 上传地址
const uploadUrl = '/api/upload/avatar'

// 表单数据
const formData = reactive<Partial<UserInfo>>({
  id: '',
  username: '',
  name: '',
  gender: 1,
  phone: '',
  email: '',
  role: '',
  avatar: '',
  previewUrl: '',
})

// 原始数据备份，用于取消编辑时恢复
const originalData = reactive<Partial<UserInfo>>({})

// 头像预览URL
const avatarPreviewUrl = ref<string>('')

// 表单验证规则
const rules = {
  name: [
    { required: true, message: '请输入真实姓名', trigger: 'blur' },
    { min: 2, max: 20, message: '姓名长度为2-20个字符', trigger: 'blur' }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱地址', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ]
}

// 获取角色文本
const getRoleText = (role?: string) => {
  switch (role) {
    case 'admin':
      return '管理员'
    case 'user':
      return '普通用户'
    default:
      return role || '未知角色'
  }
}


// 头像上传前校验
const beforeAvatarUpload: UploadProps['beforeUpload'] = (file) => {
  const isJPG = file.type === 'image/jpeg' || file.type === 'image/png'
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isJPG) {
    ElMessage.error('上传头像图片只能是 JPG/PNG 格式!')
  }
  if (!isLt2M) {
    ElMessage.error('上传头像图片大小不能超过 2MB!')
  }
  return isJPG && isLt2M
}

// 自定义上传方法
const customUpload = async (options: UploadRequestOptions) => {
  const { file, onSuccess, onError, onProgress } = options

  try {
    // 生成临时文件名，用于空响应时的fallback
    const timestamp = Date.now()
    const randomId = Math.random().toString(36).substring(2)
    const tempFileName = `avatars/${timestamp}-${randomId}.png`

    // 创建FormData对象
    const formData = new FormData()
    formData.append('file', file)

    // 获取token
    const token = localStorage.getItem('token')

    // 使用原生fetch上传，确保携带token
    const xhr = new XMLHttpRequest()

    // 监听上传进度
    xhr.upload.addEventListener('progress', (event: ProgressEvent) => {
      if (event.lengthComputable) {
        const percentCompleted = Math.round((event.loaded * 100) / event.total)
        onProgress({
          percent: percentCompleted,
          lengthComputable: true,
          loaded: event.loaded,
          total: event.total,
          target: xhr,
          currentTarget: xhr.upload
        } as any)
      }
    })

    // 监听响应
    xhr.addEventListener('load', () => {
      if (xhr.status >= 200 && xhr.status < 300) {
        try {
          let response = null

          // 如果响应文本为空，可能是后端返回了空响应但有正确的状态码
          if (!xhr.responseText || xhr.responseText.trim() === '') {
            // 对于空响应，构造一个默认成功响应
            response = {
              success: true,
              message: '上传成功',
              data: {
                filePath: `dormitory-avatars/${tempFileName}`, // 使用临时文件名
                previewUrl: `/files/avatars/${tempFileName}`
              }
            }
          } else {
            response = JSON.parse(xhr.responseText)
          }

          // 确保 response 不为 undefined
          if (response) {
            onSuccess(response)
          } else {
            onError({
              status: xhr.status,
              method: 'POST',
              url: uploadUrl,
              message: '响应对象为空',
              name: 'EmptyResponseError'
            } as any)
          }
        } catch (e) {
          // 如果解析失败，但状态码是成功的，可能是纯文本响应
          if (xhr.responseText && xhr.responseText.trim()) {
            try {
              // 尝试作为纯文本处理
              const textResponse = xhr.responseText.trim()
              const fallbackResponse = {
                success: true,
                message: '上传成功',
                data: {
                  filePath: textResponse, // 假设返回的是文件路径
                  previewUrl: `/files/${textResponse.replace('dormitory-avatars/', '')}`
                }
              }
              onSuccess(fallbackResponse)
              return
            } catch (textError) {
              console.error('文本处理也失败:', textError)
            }
          }
          onError({
            status: xhr.status,
            method: 'POST',
            url: uploadUrl,
            message: `响应解析失败: ${(e as Error).message}，原始响应: ${xhr.responseText}`,
            name: 'ParseError'
          } as any)
        }
      } else {
        onError({
          status: xhr.status,
          method: 'POST',
          url: uploadUrl,
          message: `上传失败，状态码: ${xhr.status}, 响应: ${xhr.responseText}`,
          name: 'UploadError'
        } as any)
      }
    })

    // 监听错误
    xhr.addEventListener('error', () => {
      onError({
        status: 0,
        method: 'POST',
        url: uploadUrl,
        message: '网络错误',
        name: 'NetworkError'
      } as any)
    })

    // 配置并发送请求
    xhr.open('POST', uploadUrl, true)

    // 设置请求头，包含JWT token
    if (token) {
      xhr.setRequestHeader('Authorization', `Bearer ${token}`)
    }

    // 不设置Content-Type，让浏览器自动设置multipart/form-data边界
    xhr.send(formData)

  } catch (error: any) {
    console.error('头像上传失败:', error)
    onError(error)
  }
}

// 头像上传成功回调
const handleAvatarSuccess: UploadProps['onSuccess'] = (response) => {
  // 如果response为空，说明上传可能有问题，不做处理
  if (!response) {
    return
  }

  // 检查多种成功状态格式
  const isSuccess = response.code === 200 || response.success || response.status === 'success'

  if (isSuccess && response.data) {
    // 直接使用后端返回的字段
    const newAvatarPath = response.data.filePath
    const newPreviewUrl = response.data.previewUrl

    if (!newAvatarPath || !newPreviewUrl) {
      console.error('响应数据中缺少filePath或previewUrl字段', response.data)
      ElMessage.error('上传响应格式错误：缺少必要字段')
      return
    }

    formData.avatar = newAvatarPath
    formData.previewUrl = newPreviewUrl
    avatarPreviewUrl.value = newPreviewUrl

    ElMessage.success('头像上传成功')
  } else {
    console.error('响应状态不成功或缺少data字段', response)
    const errorMessage = response.message || response.error || '头像上传失败'
    ElMessage.error(errorMessage)
  }
}

// 初始化表单数据
const initFormData = async () => {
  try {
    // 调用后端接口获取最新的用户信息
    const userInfo = await getCurrentUserInfo()

    // 更新store中的用户信息
    userStore.userInfo = userInfo
    localStorage.setItem('userInfo', JSON.stringify(userInfo))

    // 设置表单数据
    Object.assign(formData, {
      id: userInfo.id || '',
      username: userInfo.username || '',
      name: userInfo.name || '',
      gender: userInfo.gender || 1,
      phone: userInfo.phone || '',
      email: userInfo.email || '',
      role: userInfo.role || '',
      avatar: userInfo.avatar || '',
      previewUrl: userInfo.previewUrl || ''
    })

    // 使用后端返回的previewUrl，如果有的话
    avatarPreviewUrl.value = userInfo.previewUrl || ''

    // 备份原始数据
    Object.assign(originalData, formData)
  } catch (error: any) {
    ElMessage.error('获取用户信息失败：' + (error.message || '未知错误'))
  }
}

// 开始编辑
const startEdit = () => {
  isEditing.value = true
  // 备份当前数据
  Object.assign(originalData, formData)
}

// 取消编辑
const cancelEdit = () => {
  isEditing.value = false
  // 恢复原始数据
  Object.assign(formData, originalData)
  // 直接使用原始数据中的previewUrl
  avatarPreviewUrl.value = originalData.previewUrl || ''
}

// 跳转到dashboard页面
const goToDashboard = () => {
  router.push('/dashboard')
}

// 保存个人信息
const saveInfo = async () => {
  if (!formRef.value) return

  try {
    await formRef.value.validate()
    saving.value = true

    // 构造提交数据，只包含avatar字段（filePath），不包含previewUrl
    const submitData = {
      id:formData.id,
      name: formData.name,
      gender: formData.gender,
      phone: formData.phone,
      email: formData.email,
      avatar: formData.avatar // 只传递filePath到avatar字段
    }

    const updatedUserInfo = await updateUserInfo(submitData)

    // 更新store中的用户信息 - 使用响应式更新
    if (userStore.userInfo) {
      Object.assign(userStore.userInfo, updatedUserInfo)
    } else {
      userStore.userInfo = updatedUserInfo
    }
    localStorage.setItem('userInfo', JSON.stringify(userStore.userInfo))

      isEditing.value = false
    ElMessage.success('个人信息保存成功')

    // 保存成功后立即刷新页面数据，头像会自动同步
    await initFormData()
  } catch (error: any) {
    if (error.message) {
      ElMessage.error(error.message)
    }
  } finally {
    saving.value = false
  }
}

onMounted(async () => {
  await initFormData()
})
</script>

<style scoped>
.settings-container {
  max-width: 800px;
  margin: 0 auto;
}

.settings-header {
  margin-bottom: 24px;
}

.settings-header h2 {
  margin: 0;
  color: #303133;
  font-size: 24px;
  font-weight: 600;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-buttons {
  display: flex;
  gap: 12px;
  align-items: center;
}

.info-card :deep(.el-card__body) {
  padding: 24px;
}

:deep(.el-form-item__label) {
  color: #606266;
  font-weight: 500;
}

:deep(.el-input.is-disabled .el-input__inner) {
  background-color: #f5f7fa;
  color: #909399;
}

.avatar-upload {
  display: flex;
  align-items: center;
  gap: 16px;
}

.avatar-uploader {
  display: inline-block;
}

.avatar-uploader :deep(.el-upload) {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: var(--el-transition-duration-fast);
}

.avatar-uploader :deep(.el-upload:hover) {
  border-color: var(--el-color-primary);
}
</style>