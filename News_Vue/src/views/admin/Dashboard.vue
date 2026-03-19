<template>
  <div class="dashboard-page">
    <div class="dashboard-header">
      <h1>管理仪表盘</h1>
      <p>欢迎回来，{{ userStore.userInfo?.username }}</p>
    </div>

    <div class="dashboard-stats">
      <div class="stat-card">
        <div class="stat-icon">
          <el-icon><Document /></el-icon>
        </div>
        <div class="stat-content">
          <h3>{{ stats.totalNews }}</h3>
          <p>新闻总数</p>
        </div>
      </div>

      <div class="stat-card">
        <div class="stat-icon">
          <el-icon><User /></el-icon>
        </div>
        <div class="stat-content">
          <h3>{{ stats.totalUsers }}</h3>
          <p>用户总数</p>
        </div>
      </div>

      <div class="stat-card">
        <div class="stat-icon">
          <el-icon><View /></el-icon>
        </div>
        <div class="stat-content">
          <h3>{{ stats.totalViews }}</h3>
          <p>总浏览量</p>
        </div>
      </div>

      <div class="stat-card">
        <div class="stat-icon">
          <el-icon><Star /></el-icon>
        </div>
        <div class="stat-content">
          <h3>{{ stats.totalLikes }}</h3>
          <p>总点赞数</p>
        </div>
      </div>
    </div>

    <div class="dashboard-content">
      <div class="content-section">
        <h2>最新新闻</h2>
        <el-table :data="recentNews" style="width: 100%">
          <el-table-column prop="title" label="标题" />
          <el-table-column prop="author" label="作者" width="120" />
          <el-table-column prop="pageViews" label="浏览量" width="100" />
          <el-table-column prop="status" label="状态" width="100">
            <template #default="scope">
              <el-tag :type="scope.row.status === 1 ? 'success' : 'warning'">
                {{ scope.row.status === 1 ? '已发布' : '草稿' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createdTime" label="创建时间" width="180" />
        </el-table>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'
import { Document, User, View, Star } from '@element-plus/icons-vue'
import type { News } from '@/types'
import { getStatisticsOverview, getAdminRecentNews } from '@/api/modules/admin'

const userStore = useUserStore()

const stats = ref({
  totalNews: 0,
  totalUsers: 0,
  totalViews: 0,
  totalLikes: 0
})

const recentNews = ref<News[]>([])

const fetchDashboardData = async () => {
  try {
    // 获取统计概览
    const statsRes = await getStatisticsOverview() as any
    if (statsRes.code === 200) {
      // 这里的字段名需要对应后端 SystemStatisticsDTO 或具体返回结构
      // 根据之前的 SystemStatisticsDTO，字段是 totalVisits, totalComments 等
      // 我们在 Controller 返回的是 statisticsService.getOverviewStatistics()
      // 假设它返回了一个包含这些字段的对象
      const data = statsRes.data
      stats.value = {
        totalNews: data.totalNews || 0,
        totalUsers: data.totalUsers || 0,
        totalViews: data.totalVisits || data.totalViews || 0,
        totalLikes: data.totalLikes || 0
      }
    }

    // 获取最近新闻
    const newsRes = await getAdminRecentNews(5) as any
    if (newsRes.code === 200) {
      recentNews.value = newsRes.data.items || []
    }
  } catch (error) {
    console.error('Fetch dashboard data error:', error)
    ElMessage.error('获取仪表盘数据失败')
  }
}

onMounted(() => {
  fetchDashboardData()
})
</script>

<style scoped>
.dashboard-page {
  padding: var(--spacing-lg);
  max-width: 1200px;
  margin: 0 auto;
}

.dashboard-header {
  margin-bottom: var(--spacing-xl);
}

.dashboard-header h1 {
  color: var(--text-primary);
  margin-bottom: var(--spacing-sm);
}

.dashboard-header p {
  color: var(--text-secondary);
  margin: 0;
}

.dashboard-stats {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: var(--spacing-lg);
  margin-bottom: var(--spacing-xl);
}

.stat-card {
  background: var(--bg-primary);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-sm);
  padding: var(--spacing-lg);
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: var(--radius-lg);
  background: var(--primary-color);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 24px;
}

.stat-content h3 {
  font-size: 2rem;
  color: var(--text-primary);
  margin: 0 0 var(--spacing-xs);
}

.stat-content p {
  color: var(--text-secondary);
  margin: 0;
}

.dashboard-content {
  background: var(--bg-primary);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-sm);
  overflow: hidden;
}

.content-section {
  padding: var(--spacing-lg);
}

.content-section h2 {
  color: var(--text-primary);
  margin-bottom: var(--spacing-lg);
}
</style>
