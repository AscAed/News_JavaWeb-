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
      <div class="content-left">
        <div class="content-section">
          <div class="section-header">
            <h2>最新新闻</h2>
            <el-button type="primary" link @click="$router.push('/admin/news')">查看全部</el-button>
          </div>
          <el-table :data="recentNews" style="width: 100%" v-loading="loading">
            <el-table-column prop="title" label="标题" min-width="200" show-overflow-tooltip />
            <el-table-column prop="author" label="作者" width="100" />
            <el-table-column prop="pageViews" label="浏览" width="80" align="center" />
            <el-table-column prop="status" label="状态" width="100" align="center">
              <template #default="scope">
                <el-tag :type="scope.row.status === 1 ? 'success' : 'info'" size="small">
                  {{ scope.row.status === 1 ? '已发布' : '草稿' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createdTime" label="发布时间" width="160" />
          </el-table>
        </div>
      </div>

      <div class="content-right">
        <div class="content-section trending-section">
          <div class="section-header">
            <h2>热门话题 (ES 实时聚合)</h2>
            <el-icon class="header-icon"><TrendCharts /></el-icon>
          </div>
          <div class="trending-tags" v-loading="loading">
            <el-tag
              v-for="(item, index) in trendingKeywords"
              :key="index"
              :type="getTagType(index)"
              effect="light"
              round
              class="trending-tag"
              :style="{ fontSize: getFontSize(item.value) }"
            >
              {{ item.name }}
              <span class="tag-count">{{ item.value }}</span>
            </el-tag>
            <el-empty v-if="!trendingKeywords.length" description="暂无热点数据" :image-size="60" />
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'
import { Document, User, View, Star, TrendCharts } from '@element-plus/icons-vue'
import type { News } from '@/types'
import { getOverviewStatistics, getTrendingKeywords } from '@/api/modules/statistics'
import { getNewsList } from '@/api/modules/news'

const userStore = useUserStore()
const loading = ref(false)

const stats = ref({
  totalNews: 0,
  totalUsers: 0,
  totalViews: 0,
  totalLikes: 0
})

const recentNews = ref<News[]>([])
const trendingKeywords = ref<{ name: string, value: number }[]>([])

// 获取标签类型
const getTagType = (index: number) => {
  const types: any[] = ['', 'success', 'info', 'warning', 'danger']
  return types[index % 5]
}

// 动态计算字号
const getFontSize = (count: number) => {
  const max = trendingKeywords.value[0]?.value || 1
  const size = 12 + (count / max) * 8
  return `${size}px`
}

const fetchDashboardData = async () => {
  loading.value = true
  try {
    // 1. 获取统计概览
    const statsRes = await getOverviewStatistics() as any
    if (statsRes.code === 200) {
      const data = statsRes.data
      stats.value = {
        totalNews: data.newsStatistics?.totalNews || 0,
        totalUsers: data.userStatistics?.totalUsers || 0,
        totalViews: data.newsStatistics?.totalViews || 0,
        totalLikes: data.systemStatistics?.totalFavorites || 0
      }
    }

    // 2. 获取最新新闻 (实时数据库数据)
    const newsRes = await getNewsList({ page: 1, size: 8 }) as any
    if (newsRes.code === 200) {
      recentNews.value = newsRes.data.records || []
    }

    // 3. 获取热词趋势 (ES 实时聚合)
    const trendingRes = await getTrendingKeywords(15) as any
    if (trendingRes.code === 200) {
      trendingKeywords.value = trendingRes.data || []
    }
  } catch (error) {
    console.error('Fetch dashboard error:', error)
    ElMessage.error('获取仪表盘数据失败')
  } finally {
    loading.value = false
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
  display: flex;
  gap: var(--spacing-lg);
  align-items: flex-start;
}

.content-left {
  flex: 2;
  background: var(--bg-primary);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-sm);
  overflow: hidden;
}

.content-right {
  flex: 1;
}

.content-section {
  padding: var(--spacing-lg);
}

.trending-section {
  background: var(--bg-primary);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-sm);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--spacing-lg);
}

.section-header h2 {
  color: var(--text-primary);
  margin: 0;
  font-size: 1.25rem;
}

.header-icon {
  font-size: 1.25rem;
  color: var(--primary-color);
}

.trending-tags {
  display: flex;
  flex-wrap: wrap;
  gap: var(--spacing-sm);
  min-height: 200px;
  align-content: flex-start;
}

.trending-tag {
  cursor: default;
  transition: all 0.3s ease;
  padding: 8px 12px;
}

.trending-tag:hover {
  transform: scale(1.1);
  box-shadow: var(--shadow-md);
}

.tag-count {
  font-size: 0.8em;
  opacity: 0.6;
  margin-left: 4px;
}

@media (max-width: 992px) {
  .dashboard-content {
    flex-direction: column;
  }
  
  .content-left, .content-right {
    width: 100%;
  }
}
</style>
