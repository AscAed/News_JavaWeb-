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
    // 模拟数据，实际应该调用API
    stats.value = {
      totalNews: 156,
      totalUsers: 1234,
      totalViews: 56789,
      totalLikes: 2345
    }
    
    recentNews.value = [
      {
        hid: 1,
        title: 'Vue 3 新特性介绍',
        author: '张三',
        pageViews: 1234,
        status: 1,
        createdTime: '2025-12-31 10:30:00',
        type: 1,
        typeName: '技术',
        publisher: 1,
        likeCount: 56,
        commentCount: 12,
        favoriteCount: 8,
        isTop: false,
        isHot: true,
        updatedTime: '2025-12-31 10:30:00'
      },
      {
        hid: 2,
        title: 'Spring Boot 最佳实践',
        author: '李四',
        pageViews: 987,
        status: 1,
        createdTime: '2025-12-31 09:15:00',
        type: 1,
        typeName: '技术',
        publisher: 1,
        likeCount: 43,
        commentCount: 8,
        favoriteCount: 5,
        isTop: false,
        isHot: false,
        updatedTime: '2025-12-31 09:15:00'
      }
    ] as News[]
  } catch (error) {
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
