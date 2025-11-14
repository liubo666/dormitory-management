<template>
  <div class="statistics-management">
    <div class="page-header">
      <div class="page-title">
        <h2>
          <el-icon><DataAnalysis /></el-icon>
          数据统计
        </h2>
        <p>查看宿舍管理系统的各项数据统计和分析报表</p>
      </div>
    </div>

    <div class="content-container">
      <!-- 概览统计 -->
      <div class="overview-section">
        <el-row :gutter="16">
          <el-col :span="6">
            <el-card class="stat-card">
              <div class="stat-content">
                <div class="stat-icon success">
                  <el-icon><School /></el-icon>
                </div>
                <div class="stat-info">
                  <div class="stat-value">{{ statistics.buildingCount }}</div>
                  <div class="stat-label">宿舍楼总数</div>
                </div>
              </div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card class="stat-card">
              <div class="stat-content">
                <div class="stat-icon primary">
                  <el-icon><User /></el-icon>
                </div>
                <div class="stat-info">
                  <div class="stat-value">{{ statistics.studentCount }}</div>
                  <div class="stat-label">学生总数</div>
                </div>
              </div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card class="stat-card">
              <div class="stat-content">
                <div class="stat-icon warning">
                  <el-icon><House /></el-icon>
                </div>
                <div class="stat-info">
                  <div class="stat-value">{{ statistics.occupancyRate }}%</div>
                  <div class="stat-label">入住率</div>
                </div>
              </div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card class="stat-card">
              <div class="stat-content">
                <div class="stat-icon danger">
                  <el-icon><Tools /></el-icon>
                </div>
                <div class="stat-info">
                  <div class="stat-value">{{ statistics.todayRepairs }}</div>
                  <div class="stat-label">今日报修</div>
                </div>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </div>

      <!-- 图表区域 -->
      <div class="charts-section">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-card class="chart-card">
              <template #header>
                <div class="chart-header">
                  <span>性别分布</span>
                  <el-button type="text" size="small">
                    <el-icon><Refresh /></el-icon>
                  </el-button>
                </div>
              </template>
              <div class="chart-placeholder">
                <el-empty description="图表开发中..." />
              </div>
            </el-card>
          </el-col>
          <el-col :span="12">
            <el-card class="chart-card">
              <template #header>
                <div class="chart-header">
                  <span>入住趋势</span>
                  <el-button type="text" size="small">
                    <el-icon><Refresh /></el-icon>
                  </el-button>
                </div>
              </template>
              <div class="chart-placeholder">
                <el-empty description="图表开发中..." />
              </div>
            </el-card>
          </el-col>
          <el-col :span="12">
            <el-card class="chart-card">
              <template #header>
                <div class="chart-header">
                  <span>楼栋入住率</span>
                  <el-button type="text" size="small">
                    <el-icon><Refresh /></el-icon>
                  </el-button>
                </div>
              </template>
              <div class="chart-placeholder">
                <el-empty description="图表开发中..." />
              </div>
            </el-card>
          </el-col>
          <el-col :span="12">
            <el-card class="chart-card">
              <template #header>
                <div class="chart-header">
                  <span>报修统计</span>
                  <el-button type="text" size="small">
                    <el-icon><Refresh /></el-icon>
                  </el-button>
                </div>
              </template>
              <div class="chart-placeholder">
                <el-empty description="图表开发中..." />
              </div>
            </el-card>
          </el-col>
        </el-row>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import {
  DataAnalysis,
  School,
  User,
  House,
  Tools,
  Refresh
} from '@element-plus/icons-vue'

const statistics = reactive({
  buildingCount: 0,
  studentCount: 0,
  occupancyRate: 0,
  todayRepairs: 0
})

const loadStatistics = () => {
  // 模拟数据，实际应该从API获取
  statistics.buildingCount = 12
  statistics.studentCount = 2456
  statistics.occupancyRate = 78.5
  statistics.todayRepairs = 8
}

onMounted(() => {
  loadStatistics()
})
</script>

<style scoped>
.statistics-management {
  padding: 24px;
  min-height: calc(100vh - 84px);
}

.page-header {
  background: white;
  padding: 24px;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
  margin-bottom: 24px;
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
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.overview-section {
  margin-bottom: 24px;
}

.stat-card {
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
  transition: transform 0.2s, box-shadow 0.2s;
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

.stat-content {
  display: flex;
  align-items: center;
  padding: 8px;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 16px;
}

.stat-icon.success {
  background: linear-gradient(135deg, #10b981, #34d399);
  color: white;
}

.stat-icon.primary {
  background: linear-gradient(135deg, #3b82f6, #60a5fa);
  color: white;
}

.stat-icon.warning {
  background: linear-gradient(135deg, #f59e0b, #fbbf24);
  color: white;
}

.stat-icon.danger {
  background: linear-gradient(135deg, #ef4444, #f87171);
  color: white;
}

.stat-icon .el-icon {
  font-size: 24px;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: #1f2937;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 14px;
  color: #6b7280;
}

.charts-section {
  margin-top: 24px;
}

.chart-card {
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
  margin-bottom: 16px;
}

.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 600;
  color: #374151;
}

.chart-placeholder {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 300px;
  color: #999;
}

:deep(.el-card__header) {
  border-bottom: 1px solid #f1f5f9;
  padding: 16px 20px;
}

:deep(.el-card__body) {
  padding: 20px;
}

:deep(.el-empty__description) {
  color: #9ca3af;
}

@media (max-width: 1200px) {
  .stat-content {
    flex-direction: column;
    text-align: center;
  }

  .stat-icon {
    margin-right: 0;
    margin-bottom: 12px;
  }
}

@media (max-width: 768px) {
  .statistics-management {
    padding: 16px;
  }

  .stat-value {
    font-size: 24px;
  }

  .chart-placeholder {
    height: 200px;
  }

  :deep(.el-col) {
    margin-bottom: 16px;
  }
}
</style>