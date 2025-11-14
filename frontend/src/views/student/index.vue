<template>
  <div class="student-management">
    <div class="page-header">
      <div class="page-title">
        <h2>
          <el-icon><User /></el-icon>
          学生管理
        </h2>
        <p>管理学生基本信息，包括学号、姓名、学院、专业等</p>
      </div>
      <div class="page-actions">
        <el-button type="primary" @click="handleAdd" size="large">
          <el-icon><Plus /></el-icon>
          新增学生
        </el-button>
      </div>
    </div>

    <div class="content-container">
      <div class="search-section">
        <el-card shadow="never">
          <el-form :model="searchForm" inline class="search-form">
            <el-form-item label="学号">
              <el-input
                v-model="searchForm.studentNo"
                placeholder="请输入学号"
                clearable
                style="width: 200px"
              />
            </el-form-item>
            <el-form-item label="姓名">
              <el-input
                v-model="searchForm.name"
                placeholder="请输入姓名"
                clearable
                style="width: 200px"
              />
            </el-form-item>
            <el-form-item label="学院">
              <el-input
                v-model="searchForm.college"
                placeholder="请输入学院"
                clearable
                style="width: 200px"
              />
            </el-form-item>
            <el-form-item label="专业">
              <el-input
                v-model="searchForm.major"
                placeholder="请输入专业"
                clearable
                style="width: 200px"
              />
            </el-form-item>
            <el-form-item label="性别">
              <el-select
                v-model="searchForm.gender"
                placeholder="请选择性别"
                clearable
                style="width: 100px"
              >
                <el-option label="男" :value="1" />
                <el-option label="女" :value="0" />
              </el-select>
            </el-form-item>
            <el-form-item label="班级">
              <el-input
                v-model="searchForm.className"
                placeholder="请输入班级"
                clearable
                style="width: 150px"
              />
            </el-form-item>
            <el-form-item label="年级">
              <el-input
                v-model="searchForm.grade"
                placeholder="请输入年级"
                clearable
                style="width: 120px"
              />
            </el-form-item>
            <el-form-item label="状态">
              <el-select
                v-model="searchForm.status"
                placeholder="请选择状态"
                clearable
                style="width: 120px"
              >
                <el-option label="在校" :value="1" />
                <el-option label="休学" :value="0" />
                <el-option label="毕业" :value="2" />
                <el-option label="退学" :value="3" />
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
          <el-table-column prop="studentNo" label="学号" width="120" />
          <el-table-column prop="name" label="姓名" width="100" />
          <el-table-column prop="gender" label="性别" width="80" align="center">
            <template #default="{ row }">
              {{ row.gender === 1 ? '男' : '女' }}
            </template>
          </el-table-column>
          <el-table-column prop="phone" label="手机号" width="130" />
          <el-table-column prop="college" label="学院" min-width="150" />
          <el-table-column prop="major" label="专业" min-width="150" />
          <el-table-column prop="className" label="班级" width="120" />
          <el-table-column prop="grade" label="年级" width="100" />
<!--          <el-table-column prop="buildingName" label="楼栋" width="100" />-->
<!--          <el-table-column prop="roomNo" label="房间号" width="100" />-->
<!--          <el-table-column prop="bedNo" label="床位号" width="100" />-->
          <el-table-column prop="status" label="状态" width="100" align="center">
            <template #default="{ row }">
              <el-tag :type="getStatusType(row.status)">
                {{ row.statusText || getStatusText(row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="160" fixed="right">
            <template #default="{ row }">
              <el-button-group>
                <el-button size="small" @click="handleViewDetail(row)">
                  <el-icon><View /></el-icon>
                  详情
                </el-button>
                <el-button size="small" @click="handleEdit(row)" >
                  <el-icon><Edit /></el-icon>
                  编辑
                </el-button>
              </el-button-group>
            </template>
          </el-table-column>
        </el-table>

        <div class="pagination-section">
          <el-pagination
            v-model:current-page="pagination.current"
            v-model:page-size="pagination.size"
            :page-sizes="[10, 20, 50, 100]"
            :total="pagination.total"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
          />
        </div>
      </div>
    </div>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="700px"
      :before-close="handleDialogClose"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="rules"
        label-width="100px"
        label-position="top"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="学号" prop="studentNo">
              <el-input v-model="formData.studentNo" placeholder="请输入学号，如：2024001001" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="姓名" prop="name">
              <el-input v-model="formData.name" placeholder="请输入姓名" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="性别" prop="gender">
              <el-select v-model="formData.gender" placeholder="请选择性别" style="width: 100%">
                <el-option label="男" :value="1" />
                <el-option label="女" :value="0" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="出生日期" prop="birthDate">
              <el-date-picker
                v-model="formData.birthDate"
                type="date"
                placeholder="请选择出生日期"
                format="YYYY-MM-DD"
                value-format="YYYY-MM-DD"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="身份证号" prop="idCard">
              <el-input v-model="formData.idCard" placeholder="请输入身份证号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="手机号" prop="phone">
              <el-input v-model="formData.phone" placeholder="请输入手机号" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="formData.email" placeholder="请输入邮箱" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="学院" prop="college">
              <el-input v-model="formData.college" placeholder="请输入学院" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="专业" prop="major">
              <el-input v-model="formData.major" placeholder="请输入专业" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="班级" prop="className">
              <el-input v-model="formData.className" placeholder="请输入班级" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="年级" prop="grade">
              <el-input v-model="formData.grade" placeholder="请输入年级，如：2024级" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="入学日期" prop="enrollmentDate">
              <el-date-picker
                v-model="formData.enrollmentDate"
                type="date"
                placeholder="请选择入学日期"
                format="YYYY-MM-DD"
                value-format="YYYY-MM-DD"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="毕业日期" prop="graduationDate">
              <el-date-picker
                v-model="formData.graduationDate"
                type="date"
                placeholder="请选择毕业日期"
                format="YYYY-MM-DD"
                value-format="YYYY-MM-DD"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="学生状态" prop="status">
              <el-select v-model="formData.status" placeholder="请选择状态" style="width: 100%">
                <el-option label="在校" :value="1" />
                <el-option label="休学" :value="0" />
                <el-option label="毕业" :value="2" />
                <el-option label="退学" :value="3" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="家庭住址" prop="homeAddress">
          <el-input
            v-model="formData.homeAddress"
            type="textarea"
            :rows="2"
            placeholder="请输入家庭住址"
            style="width: 100%"
          />
        </el-form-item>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="紧急联系人" prop="emergencyContact">
              <el-input v-model="formData.emergencyContact" placeholder="请输入紧急联系人姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="紧急联系人电话" prop="emergencyPhone">
              <el-input v-model="formData.emergencyPhone" placeholder="请输入紧急联系人电话" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="备注" prop="remark">
          <el-input
            v-model="formData.remark"
            type="textarea"
            :rows="3"
            placeholder="请输入备注信息"
            style="width: 100%"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit" :loading="submitLoading">
            {{ isEdit ? '更新' : '创建' }}
          </el-button>
        </span>
      </template>
    </el-dialog>

  
    <!-- 学生详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="学生详情"
      width="800px"
      :before-close="handleDetailDialogClose"
    >
      <div v-if="studentDetail" class="student-detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="学号">{{ studentDetail.studentNo }}</el-descriptions-item>
          <el-descriptions-item label="姓名">{{ studentDetail.name }}</el-descriptions-item>
          <el-descriptions-item label="性别">{{ studentDetail.gender === 1 ? '男' : '女' }}</el-descriptions-item>
          <el-descriptions-item label="出生日期">{{ studentDetail.birthDate }}</el-descriptions-item>
          <el-descriptions-item label="年龄">{{ studentDetail.age }}岁</el-descriptions-item>
          <el-descriptions-item label="身份证号">{{ studentDetail.idCard || '未填写' }}</el-descriptions-item>
          <el-descriptions-item label="手机号">{{ studentDetail.phone || '未填写' }}</el-descriptions-item>
          <el-descriptions-item label="邮箱">{{ studentDetail.email || '未填写' }}</el-descriptions-item>
          <el-descriptions-item label="学院">{{ studentDetail.college }}</el-descriptions-item>
          <el-descriptions-item label="专业">{{ studentDetail.major }}</el-descriptions-item>
          <el-descriptions-item label="班级">{{ studentDetail.className || '未填写' }}</el-descriptions-item>
          <el-descriptions-item label="年级">{{ studentDetail.grade || '未填写' }}</el-descriptions-item>
          <el-descriptions-item label="入学日期">{{ studentDetail.enrollmentDate || '未填写' }}</el-descriptions-item>
          <el-descriptions-item label="毕业日期">{{ studentDetail.graduationDate || '未填写' }}</el-descriptions-item>
          <el-descriptions-item label="宿舍信息" :span="2">
            <span v-if="studentDetail.dormitoryId">
              {{ studentDetail.buildingName }} - {{ studentDetail.roomNo }} - {{ studentDetail.bedNo }}号床位
            </span>
            <span v-else>未分配宿舍</span>
          </el-descriptions-item>
          <el-descriptions-item label="学生状态">
            <el-tag :type="getStatusType(studentDetail.status)">
              {{ studentDetail.statusText || getStatusText(studentDetail.status) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="家庭住址" :span="2">{{ studentDetail.homeAddress || '未填写' }}</el-descriptions-item>
          <el-descriptions-item label="紧急联系人">{{ studentDetail.emergencyContact || '未填写' }}</el-descriptions-item>
          <el-descriptions-item label="紧急联系电话">{{ studentDetail.emergencyPhone || '未填写' }}</el-descriptions-item>
          <el-descriptions-item label="备注" :span="2">{{ studentDetail.remark || '无' }}</el-descriptions-item>
          <el-descriptions-item label="创建时间">{{ studentDetail.createTime }}</el-descriptions-item>
          <el-descriptions-item label="更新时间">{{ studentDetail.updateTime }}</el-descriptions-item>
          <el-descriptions-item label="创建人">{{ studentDetail.createBy || '系统' }}</el-descriptions-item>
          <el-descriptions-item label="更新人">{{ studentDetail.updateBy || '系统' }}</el-descriptions-item>
        </el-descriptions>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import {
  User,
  Plus,
  Search,
  RefreshRight,
  Edit,
  View
} from '@element-plus/icons-vue'
import { useErrorHandler } from '@/utils/error-handler'
import {
  getStudentPage,
  getStudentById,
  createStudent,
  updateStudent,
  updateStudentStatus
} from '@/api/student'
import type { Student, StudentForm } from '@/api/student'

// 数据定义
const loading = ref(false)
const submitLoading = ref(false)
const tableData = ref<Student[]>([])
const dialogVisible = ref(false)
const isEdit = ref(false)


// 学生详情相关
const detailDialogVisible = ref(false)
const studentDetail = ref<Student | null>(null)

// 搜索表单
const searchForm = reactive({
  studentNo: '',
  name: '',
  gender: undefined as number | undefined,
  college: '',
  major: '',
  className: '',
  grade: '',
  status: undefined as number | undefined
})

// 表单数据
const formData = reactive<StudentForm>({
  id: undefined,
  studentNo: '',
  name: '',
  gender: 1,
  birthDate: '',
  idCard: '',
  phone: '',
  email: '',
  college: '',
  major: '',
  className: '',
  grade: '',
  enrollmentDate: '',
  graduationDate: '',
  dormitoryId: '',
  bedNo: '',
  status: 1,
  homeAddress: '',
  emergencyContact: '',
  emergencyPhone: '',
  remark: ''
})

// 使用错误处理hook
const { handleError, showSuccess, showInfo } = useErrorHandler()

// 分页
const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

// 表单引用
const formRef = ref<FormInstance>()

// 计算属性
const dialogTitle = computed(() => (isEdit.value ? '编辑学生' : '新增学生'))

// 表单验证规则
const rules: FormRules = {
  studentNo: [
    { required: true, message: '请输入学号', trigger: 'blur' },
    { pattern: /^\d{4,10}$/, message: '学号格式不正确', trigger: 'blur' }
  ],
  name: [
    { required: true, message: '请输入姓名', trigger: 'blur' },
    { min: 2, max: 20, message: '姓名长度应在2-20个字符之间', trigger: 'blur' }
  ],
  gender: [
    { required: true, message: '请选择性别', trigger: 'change' }
  ],
  birthDate: [
    { required: true, message: '请选择出生日期', trigger: 'change' }
  ],
  idCard: [
    { required: false, message: '请输入身份证号', trigger: 'blur' },
    { pattern: /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/, message: '身份证号格式不正确', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ],
  email: [
    { required: false, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '邮箱格式不正确', trigger: 'blur' }
  ],
  college: [
    { required: true, message: '请输入学院', trigger: 'blur' },
    { min: 2, max: 50, message: '学院名称长度应在2-50个字符之间', trigger: 'blur' }
  ],
  major: [
    { required: true, message: '请输入专业', trigger: 'blur' },
    { min: 2, max: 50, message: '专业名称长度应在2-50个字符之间', trigger: 'blur' }
  ],
  className: [
    { required: false, message: '请输入班级', trigger: 'blur' },
    { min: 2, max: 30, message: '班级名称长度应在2-30个字符之间', trigger: 'blur' }
  ],
  grade: [
    { required: false, message: '请输入年级', trigger: 'blur' },
    { pattern: /^\d{4}级$/, message: '年级格式不正确，如：2024级', trigger: 'blur' }
  ],
  enrollmentDate: [
    { required: false, message: '请选择入学日期', trigger: 'change' }
  ],
  graduationDate: [
    { required: false, message: '请选择毕业日期', trigger: 'change' }
  ],
  dormitoryId: [
    { required: false, message: '请选择宿舍', trigger: 'change' }
  ],
  bedNo: [
    { required: false, message: '请输入床位号', trigger: 'blur' }
  ],
  status: [
    { required: true, message: '请选择状态', trigger: 'change' }
  ],
  homeAddress: [
    { required: false, message: '请输入家庭地址', trigger: 'blur' },
    { max: 200, message: '家庭地址长度不能超过200个字符', trigger: 'blur' }
  ],
  emergencyContact: [
    { required: false, message: '请输入紧急联系人', trigger: 'blur' },
    { min: 2, max: 20, message: '紧急联系人姓名长度应在2-20个字符之间', trigger: 'blur' }
  ],
  emergencyPhone: [
    { required: false, message: '请输入紧急联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '紧急联系电话格式不正确', trigger: 'blur' }
  ],
  remark: [
    { required: false, message: '请输入备注', trigger: 'blur' },
    { max: 500, message: '备注长度不能超过500个字符', trigger: 'blur' }
  ]
}

const loadData = async () => {
  loading.value = true
  try {
    const params = {
      current: pagination.current,
      size: pagination.size,
      studentNo: searchForm.studentNo || undefined,
      name: searchForm.name || undefined,
      gender: searchForm.gender,
      college: searchForm.college || undefined,
      major: searchForm.major || undefined,
      className: searchForm.className || undefined,
      grade: searchForm.grade || undefined,
      status: searchForm.status
    }
    const response = await getStudentPage(params)
    tableData.value = response.records
    pagination.total = response.total
  } catch (error: any) {
    handleError(error, '获取学生数据失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.current = 1
  loadData()
}

const handleReset = () => {
  searchForm.studentNo = ''
  searchForm.name = ''
  searchForm.gender = undefined
  searchForm.college = ''
  searchForm.major = ''
  searchForm.className = ''
  searchForm.grade = ''
  searchForm.status = undefined
  handleSearch()
}

const handleCurrentChange = (current: number) => {
  pagination.current = current
  loadData()
}

const handleSizeChange = (size: number) => {
  pagination.size = size
  pagination.current = 1
  loadData()
}

const getStatusType = (status: number) => {
  switch (status) {
    case 1: return 'success'
    case 0: return 'warning'
    case 2: return 'info'
    case 3: return 'danger'
    default: return 'info'
  }
}

const getStatusText = (status: number) => {
  switch (status) {
    case 1: return '在校'
    case 0: return '休学'
    case 2: return '毕业'
    case 3: return '退学'
    default: return '未知'
  }
}

const handleAdd = () => {
  isEdit.value = false
  dialogVisible.value = true
  resetForm()
}

const handleEdit = async (row: Student) => {
  try {
    // 调用接口获取学生详细信息
    const studentDetail = await getStudentById(row.id)

    isEdit.value = true
    dialogVisible.value = true

    // 将完整的学生详情数据赋值给表单
    Object.assign(formData, {
      id: studentDetail.id,
      studentNo: studentDetail.studentNo,
      name: studentDetail.name,
      gender: studentDetail.gender,
      birthDate: studentDetail.birthDate,
      idCard: studentDetail.idCard || '',
      phone: studentDetail.phone || '',
      email: studentDetail.email || '',
      college: studentDetail.college,
      major: studentDetail.major,
      className: studentDetail.className || '',
      grade: studentDetail.grade || '',
      enrollmentDate: studentDetail.enrollmentDate || '',
      graduationDate: studentDetail.graduationDate || '',
      dormitoryId: studentDetail.dormitoryId || '',
      bedNo: studentDetail.bedNo || '',
      status: studentDetail.status,
      homeAddress: studentDetail.homeAddress || '',
      emergencyContact: studentDetail.emergencyContact || '',
      emergencyPhone: studentDetail.emergencyPhone || '',
      remark: studentDetail.remark || ''
    })
  } catch (error: any) {
    handleError(error, '获取学生详情失败')
  }
}


const handleViewDetail = async (row: Student) => {
  try {
    studentDetail.value = await getStudentById(row.id)
    detailDialogVisible.value = true
  } catch (error: any) {
    handleError(error, '获取学生详情失败')
  }
}

const handleDetailDialogClose = () => {
  detailDialogVisible.value = false
  studentDetail.value = null
}


const handleSubmit = async () => {
  if (!formRef.value) return

  try {
    await formRef.value.validate()
    submitLoading.value = true

    if (isEdit.value) {
      await updateStudent(formData)
      showSuccess('更新成功')
    } else {
      await createStudent(formData)
      showSuccess('创建成功')
    }

    dialogVisible.value = false
    loadData()
  } catch (error: any) {
    handleError(error, isEdit.value ? '更新失败' : '创建失败')
  } finally {
    submitLoading.value = false
  }
}

const handleDialogClose = () => {
  dialogVisible.value = false
  resetForm()
}

const resetForm = () => {
  if (formRef.value) {
    formRef.value.resetFields()
  }
  Object.assign(formData, {
    id: undefined,
    studentNo: '',
    name: '',
    gender: 1,
    birthDate: '',
    idCard: '',
    phone: '',
    email: '',
    college: '',
    major: '',
    className: '',
    grade: '',
    enrollmentDate: '',
    graduationDate: '',
    dormitoryId: '',
    bedNo: '',
    status: 1,
    homeAddress: '',
    emergencyContact: '',
    emergencyPhone: '',
    remark: ''
  })
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.student-management {
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

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.student-detail {
  padding: 10px 0;
}

.student-detail :deep(.el-descriptions__label) {
  font-weight: 600;
  color: #606266;
  min-width: 120px;
}

.student-detail :deep(.el-descriptions__content) {
  color: #303133;
}

@media (max-width: 768px) {
  .student-management {
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

  .search-form .el-input {
    width: 100%;
  }
}
</style>