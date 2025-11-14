<template>
  <div class="dashboard">
    <div class="dashboard-header">
      <h1>欢迎使用宿舍管理系统</h1>
    </div>

    <div class="stats-grid">
      <el-card class="stat-card">
        <div class="stat-content">
          <div class="stat-icon">
            <el-icon size="32" color="#1e40af">
              <School />
            </el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-number">12</div>
            <div class="stat-label">宿舍楼栋</div>
          </div>
        </div>
      </el-card>

      <el-card class="stat-card">
        <div class="stat-content">
          <div class="stat-icon">
            <el-icon size="32" color="#059669">
              <User />
            </el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-number">2456</div>
            <div class="stat-label">入住学生</div>
          </div>
        </div>
      </el-card>

      <el-card class="stat-card">
        <div class="stat-content">
          <div class="stat-icon">
            <el-icon size="32" color="#d97706">
              <House />
            </el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-number">480</div>
            <div class="stat-label">宿舍房间</div>
          </div>
        </div>
      </el-card>

      <el-card class="stat-card">
        <div class="stat-content">
          <div class="stat-icon">
            <el-icon size="32" color="#0891b2">
              <DataLine />
            </el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-number">78.5%</div>
            <div class="stat-label">入住率</div>
          </div>
        </div>
      </el-card>
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
import {
  School,
  User,
  House,
  DataLine,
  Right
} from '@element-plus/icons-vue'
</script>

<style scoped>
.dashboard {
  padding: 24px;
}

.dashboard-header {
  text-align: center;
  margin-bottom: 40px;
}

.dashboard-header h1 {
  font-size: 32px;
  font-weight: 600;
  color: #1e40af;
  margin-bottom: 12px;
}

.dashboard-header p {
  font-size: 16px;
  color: #64748b;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 20px;
  margin-bottom: 40px;
}

.stat-card {
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
  transition: transform 0.2s ease;
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

.stat-content {
  display: flex;
  align-items: center;
  padding: 20px;
  gap: 16px;
}

.stat-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 60px;
  height: 60px;
  border-radius: 12px;
  background: linear-gradient(135deg, #f8fafc 0%, #e2e8f0 100%);
}

.stat-info {
  flex: 1;
}

.stat-number {
  font-size: 28px;
  font-weight: 700;
  color: #1f2937;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 14px;
  color: #6b7280;
  font-weight: 500;
}

.action-area {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

@media (max-width: 768px) {
  .dashboard {
    padding: 16px;
  }

  .stats-grid {
    grid-template-columns: 1fr;
  }
}
</style>