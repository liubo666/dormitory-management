<template>
  <div class="dashboard">
    <div class="dashboard-header">
      <h1>欢迎使用宿舍管理系统</h1>
      <p v-if="statistics && statistics.statisticsTime">
        数据更新时间：{{ formatDateTime(statistics.statisticsTime) }}
      </p>
    </div>

    <div class="stats-grid" v-loading="loading">
      <!-- 主要统计卡片 -->
      <el-card class="stat-card primary-card">
        <div class="stat-content">
          <div class="stat-icon primary-icon">
            <el-icon size="32">
              <School />
            </el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-number primary-number">{{ statistics?.studentStatistics?.totalStudents || 0 }}</div>
            <div class="stat-label">学生总数</div>
            <div class="stat-description">系统注册学生</div>
          </div>
        </div>
      </el-card>

      <!-- 次要统计卡片组 -->
      <div class="secondary-stats">
        <el-card class="stat-card secondary-card">
          <div class="stat-content">
            <div class="stat-icon success-icon">
              <el-icon size="20">
                <User />
              </el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ statistics?.studentStatistics?.checkedInStudents || 0 }}</div>
              <div class="stat-label">入住学生</div>
            </div>
          </div>
        </el-card>

        <el-card class="stat-card secondary-card">
          <div class="stat-content">
            <div class="stat-icon warning-icon">
              <el-icon size="20">
                <House />
              </el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ statistics?.dormitoryStatistics?.totalRooms || 0 }}</div>
              <div class="stat-label">宿舍房间</div>
            </div>
          </div>
        </el-card>

        <el-card class="stat-card secondary-card">
          <div class="stat-content">
            <div class="stat-icon info-icon">
              <el-icon size="20">
                <DataLine />
              </el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ (statistics?.dormitoryStatistics?.occupancyRate || 0).toFixed(1) }}%</div>
              <div class="stat-label">入住率</div>
            </div>
          </div>
        </el-card>
      </div>
    </div>

    <!-- 额外统计信息区域 -->
    <div class="extended-stats" v-loading="loading" v-if="statistics">
      <el-row :gutter="16">
        <el-col :span="8">
          <el-card class="extended-card">
            <div class="card-header">
              <h3>费用统计</h3>
              <el-icon size="16" color="#10b981"><Money /></el-icon>
            </div>
            <div class="card-content">
              <div class="stat-item">
                <span class="label">总费用：</span>
                <span class="value">¥{{ (statistics?.feeStatistics?.totalAmount || 0).toFixed(2) }}</span>
              </div>
              <div class="stat-item">
                <span class="label">已收费：</span>
                <span class="value">¥{{ (statistics?.feeStatistics?.paidAmount || 0).toFixed(2) }}</span>
              </div>
              <div class="stat-item">
                <span class="label">收费率：</span>
                <span class="value">{{ (statistics?.feeStatistics?.collectionRate || 0).toFixed(1) }}%</span>
              </div>
            </div>
          </el-card>
        </el-col>

        <el-col :span="8">
          <el-card class="extended-card">
            <div class="card-header">
              <h3>访客统计</h3>
              <el-icon size="16" color="#8b5cf6"><UserFilled /></el-icon>
            </div>
            <div class="card-content">
              <div class="stat-item">
                <span class="label">今日访客：</span>
                <span class="value">{{ statistics?.visitorStatistics?.todayVisitors || 0 }}</span>
              </div>
              <div class="stat-item">
                <span class="label">本周访客：</span>
                <span class="value">{{ statistics?.visitorStatistics?.weeklyVisitors || 0 }}</span>
              </div>
              <div class="stat-item">
                <span class="label">本月访客：</span>
                <span class="value">{{ statistics?.visitorStatistics?.monthlyVisitors || 0 }}</span>
              </div>
            </div>
          </el-card>
        </el-col>

        <el-col :span="8">
          <el-card class="extended-card">
            <div class="card-header">
              <h3>学生状态</h3>
              <el-icon size="16" color="#f59e0b"><Avatar /></el-icon>
            </div>
            <div class="card-content">
              <div class="stat-item">
                <span class="label">申请中：</span>
                <span class="value">{{ statistics?.studentStatistics?.applyingStudents || 0 }}</span>
              </div>
              <div class="stat-item">
                <span class="label">已退宿：</span>
                <span class="value">{{ statistics?.studentStatistics?.checkedOutStudents || 0 }}</span>
              </div>
              <div class="stat-item">
                <span class="label">入住率：</span>
                <span class="value">{{ (statistics?.studentStatistics?.checkInRate || 0).toFixed(1) }}%</span>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <div class="action-area">
      <el-button type="primary" size="large" @click="$router.push('/dormitory')">
        <el-icon><Right /></el-icon>
        进入宿舍与楼栋管理
      </el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import {
  School,
  User,
  House,
  DataLine,
  Right,
  Money,
  UserFilled,
  Avatar
} from '@element-plus/icons-vue'
import { getOverallStatistics } from '@/api/statistics'
import type { OverallStatistics } from '@/api/statistics'

const loading = ref(false)
const statistics = ref<OverallStatistics | null>(null)

// 格式化日期时间
const formatDateTime = (dateTime: string) => {
  if (!dateTime) return ''
  return new Date(dateTime).toLocaleString('zh-CN')
}

// 获取统计数据
const fetchStatistics = async () => {
  try {
    loading.value = true
    const response = await getOverallStatistics()
    statistics.value = response // response已经是data，不需要再取.data
  } catch (error) {
    console.error('获取统计数据失败:', error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchStatistics()
})
</script>

<style scoped>
.dashboard {
  padding: 12px;
}

.dashboard-header {
  text-align: center;
  margin-bottom: 16px;
}

.dashboard-header h1 {
  font-size: 24px;
  font-weight: 600;
  color: #1e40af;
  margin-bottom: 6px;
}

.dashboard-header p {
  font-size: 12px;
  color: #64748b;
  margin-top: 4px;
}

.stats-grid {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-bottom: 12px;
}

/* 主要统计卡片 */
.primary-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
}

.primary-card .stat-content {
  display: flex;
  align-items: center;
  padding: 12px;
  gap: 10px;
}

.primary-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 40px;
  height: 40px;
  border-radius: 10px;
  background: rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(10px);
  color: white;
}

.primary-number {
  font-size: 26px;
  font-weight: 800;
  color: white;
  margin-bottom: 2px;
}

.primary-card .stat-label {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.9);
  font-weight: 600;
  margin-bottom: 2px;
}

.stat-description {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.7);
  font-weight: 400;
}

/* 次要统计卡片组 */
.secondary-stats {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(160px, 1fr));
  gap: 8px;
}

.secondary-card {
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
  transition: all 0.3s ease;
  border: 1px solid #f1f5f9;
}

.secondary-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.12);
  border-color: #e2e8f0;
}

.secondary-card .stat-content {
  display: flex;
  align-items: center;
  padding: 8px;
  gap: 6px;
}

.success-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  border-radius: 6px;
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
  color: white;
}

.warning-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  border-radius: 6px;
  background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%);
  color: white;
}

.info-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  border-radius: 6px;
  background: linear-gradient(135deg, #3b82f6 0%, #1d4ed8 100%);
  color: white;
}

.stat-number {
  font-size: 20px;
  font-weight: 700;
  color: #1f2937;
  margin-bottom: 1px;
}

.stat-label {
  font-size: 12px;
  color: #6b7280;
  font-weight: 500;
}

.extended-stats {
  margin-bottom: 12px;
}

.extended-card {
  height: 100%;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
  transition: transform 0.2s ease;
}

.extended-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 12px 0;
  border-bottom: 1px solid #f1f5f9;
  margin-bottom: 12px;
}

.card-header h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #1f2937;
}

.card-content {
  padding: 0 12px 12px;
}

.stat-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 6px;
  padding: 4px 0;
  border-bottom: 1px solid #f8fafc;
}

.stat-item:last-child {
  margin-bottom: 0;
  border-bottom: none;
}

.stat-item .label {
  font-size: 12px;
  color: #64748b;
  font-weight: 500;
}

.stat-item .value {
  font-size: 14px;
  font-weight: 600;
  color: #1f2937;
}

.action-area {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 16px;
  margin-top: 12px;
}

.action-area .el-button {
  min-width: 160px;
  height: 36px;
  font-size: 14px;
  font-weight: 500;
}

@media (max-width: 768px) {
  .dashboard {
    padding: 16px;
  }

  .primary-card .stat-content {
    padding: 20px;
    gap: 16px;
  }

  .primary-icon {
    width: 60px;
    height: 60px;
  }

  .primary-number {
    font-size: 36px;
  }

  .primary-card .stat-label {
    font-size: 16px;
  }

  .secondary-stats {
    grid-template-columns: 1fr;
  }

  .extended-stats .el-col {
    margin-bottom: 16px;
  }

  .action-area {
    flex-direction: column;
    align-items: center;
  }

  .action-area .el-button {
    width: 100%;
    max-width: 280px;
  }
}

@media (max-width: 1200px) {
  .extended-stats .el-col {
    margin-bottom: 20px;
  }
}

@media (max-width: 1024px) {
  .secondary-stats {
    grid-template-columns: 1fr;
  }
}
</style>