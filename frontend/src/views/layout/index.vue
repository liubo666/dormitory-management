<template>
  <div class="layout-container">
    <el-header class="layout-header">
      <div class="header-left">
        <h2 class="logo">
          <img src="/dormitory-icon.svg" alt="宿舍管理系统" width="32" height="32" style="margin-right: 10px;" />
          宿舍管理系统
        </h2>
      </div>
      <div class="header-right">
        <el-dropdown trigger="hover" @command="handleUserMenuSelect">
          <div class="user-info">
            <el-avatar :size="32" style="background-color: #1e40af;">
              <el-icon><User /></el-icon>
            </el-avatar>
            <span class="username">{{ userStore.userInfo?.name || '管理员' }}</span>
            <el-icon><ArrowDown /></el-icon>
          </div>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="settings">
                <el-icon><Setting /></el-icon>
                个人设置
              </el-dropdown-item>
              <el-dropdown-item divided command="logout">
                <el-icon><SwitchButton /></el-icon>
                退出登录
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </el-header>

    <el-container class="layout-main">
      <el-aside :width="collapsed ? '64px' : '240px'" class="layout-aside">
        <el-menu
          :default-active="activeKey"
          :collapse="collapsed"
          :collapse-transition="false"
          unique-opened
          @select="handleMenuSelect"
        >
          <el-menu-item index="dashboard">
            <el-icon><Odometer /></el-icon>
            <template #title>首页</template>
          </el-menu-item>
          <el-menu-item index="dormitory">
            <el-icon><House /></el-icon>
            <template #title>宿舍与楼栋管理</template>
          </el-menu-item>
          <el-menu-item index="student">
            <el-icon><User /></el-icon>
            <template #title>学生管理</template>
          </el-menu-item>
          <el-menu-item index="checkin">
            <el-icon><CircleCheck /></el-icon>
            <template #title>入住管理</template>
          </el-menu-item>
          <el-menu-item index="repair">
            <el-icon><Tools /></el-icon>
            <template #title>报修管理</template>
          </el-menu-item>
          <el-menu-item index="inspection">
            <el-icon><Document /></el-icon>
            <template #title>卫生检查</template>
          </el-menu-item>
          <el-menu-item index="visitor">
            <el-icon><UserFilled /></el-icon>
            <template #title>访客登记</template>
          </el-menu-item>
          <el-menu-item index="fee">
            <el-icon><CreditCard /></el-icon>
            <template #title>费用管理</template>
          </el-menu-item>
        </el-menu>
        <div class="collapse-trigger" @click="collapsed = !collapsed">
          <el-icon>
            <Expand v-if="collapsed" />
            <Fold v-else />
          </el-icon>
        </div>
      </el-aside>

      <el-main class="layout-content">
        <div class="content-wrapper">
          <router-view />
        </div>
      </el-main>
    </el-container>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/stores/user'
import {
  School,
  User,
  ArrowDown,
  Setting,
  SwitchButton,
  Odometer,
  CircleCheck,
  Tools,
  Document,
  UserFilled,
  CreditCard,
  Expand,
  Fold
} from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const collapsed = ref(false)
const activeKey = ref('')

// 处理菜单选择
const handleMenuSelect = (key: string) => {
  router.push(`/${key}`)
}

// 处理用户菜单选择
const handleUserMenuSelect = async (key: string) => {
  if (key === 'logout') {
    try {
      await ElMessageBox.confirm(
        '确定要退出登录吗？',
        '提示',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning',
        }
      )
      await userStore.handleLogout()
      ElMessage.success('已退出登录')
      router.push('/login')
    } catch (error: any) {
      if (error !== 'cancel') {
        ElMessage.error(error.message || '退出登录失败')
      }
    }
  } else if (key === 'settings') {
    // 跳转到个人设置页面
    router.push('/settings')
  }
}

onMounted(async () => {
  // 初始化用户信息
  await userStore.initUserInfo()

  // 设置当前激活的菜单
  const path = route.path.replace('/', '')
  if (path) {
    activeKey.value = path
  }
})
</script>

<style scoped>
.layout-container {
  height: 100vh;
}

.layout-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 64px;
  padding: 0 24px;
  background: white;
  border-bottom: 1px solid #e4e7ed;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
}

.header-left .logo {
  margin: 0;
  color: #1e40af;
  font-size: 20px;
  font-weight: 600;
  display: flex;
  align-items: center;
}

.header-right {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 8px 12px;
  border-radius: 6px;
  transition: background-color 0.3s;
}

.user-info:hover {
  background-color: #f5f7fa;
}

.username {
  font-size: 14px;
  color: #303133;
  font-weight: 500;
}

.layout-main {
  height: calc(100vh - 64px);
}

.layout-aside {
  background-color: white;
  border-right: 1px solid #e4e7ed;
  transition: width 0.3s;
  position: relative;
}

.layout-aside :deep(.el-menu) {
  border-right: none;
}

.layout-aside :deep(.el-menu-item) {
  height: 50px;
  line-height: 50px;
  font-size: 14px;
}

.layout-aside :deep(.el-menu-item.is-active) {
  background-color: #ecf5ff;
  color: #1e40af;
}

.layout-aside :deep(.el-menu-item:hover) {
  background-color: #f5f7fa;
}

.collapse-trigger {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  border-top: 1px solid #e4e7ed;
  color: #909399;
  transition: all 0.3s;
}

.collapse-trigger:hover {
  background-color: #f5f7fa;
  color: #1e40af;
}

.layout-content {
  padding: 16px;
  background: #f5f7fa;
}

.content-wrapper {
  height: 100%;
  background: white;
  border-radius: 8px;
  padding: 24px;
  overflow: auto;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.03);
}
</style>