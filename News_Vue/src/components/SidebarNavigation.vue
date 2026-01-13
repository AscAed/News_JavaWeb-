<template>
  <aside class="sidebar-navigation">
    <!-- 分类导航 -->
    <div class="sidebar-section">
      <h3 class="sidebar-title">
        <el-icon><Menu /></el-icon>
        新闻分类
      </h3>
      <div class="category-list">
        <router-link
          v-for="category in categories"
          :key="category.id"
          :to="`/news?category=${category.id}`"
          class="category-item"
          :class="{ active: selectedCategory === category.id }"
          @click="selectCategory(category.id)"
        >
          <div class="category-icon" :style="{ color: category.color }">
            <el-icon><component :is="category.icon" /></el-icon>
          </div>
          <div class="category-info">
            <span class="category-name">{{ category.name }}</span>
            <span class="category-count">{{ category.count }}</span>
          </div>
          <div v-if="category.trending" class="trending-badge">
            <el-icon><TrendCharts /></el-icon>
          </div>
        </router-link>
      </div>
    </div>

    <!-- 热门话题 -->
    <div class="sidebar-section">
      <h3 class="sidebar-title">
        <el-icon><Star /></el-icon>
        热门话题
      </h3>
      <div class="trending-list">
        <div
          v-for="(topic, index) in hotTopics"
          :key="topic.id"
          class="trending-item"
          @click="goToTopic(topic)"
        >
          <div class="trending-rank" :class="{ 'top-three': index < 3 }">
            {{ index + 1 }}
          </div>
          <div class="trending-content">
            <div class="trending-title">{{ topic.title }}</div>
            <div class="trending-stats">
              <span class="trending-heat">
                <el-icon><View /></el-icon>
                {{ formatNumber(topic.heat) }}
              </span>
              <span class="trending-comments">
                <el-icon><ChatDotRound /></el-icon>
                {{ topic.comments }}
              </span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 推荐阅读 -->
    <div class="sidebar-section">
      <h3 class="sidebar-title">
        <el-icon><Reading /></el-icon>
        推荐阅读
      </h3>
      <div class="recommendation-list">
        <div
          v-for="article in recommendations"
          :key="article.id"
          class="recommendation-item"
          @click="goToArticle(article)"
        >
          <div class="recommendation-image">
            <img
              :src="article.coverImage || '/placeholder.jpg'"
              :alt="article.title"
              @error="handleImageError"
            />
          </div>
          <div class="recommendation-content">
            <h4 class="recommendation-title">{{ article.title }}</h4>
            <div class="recommendation-meta">
              <span class="recommendation-author">{{ article.author }}</span>
              <span class="recommendation-time">{{ formatTime(article.publishTime) }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 标签云 -->
    <div class="sidebar-section">
      <h3 class="sidebar-title">
        <el-icon><CollectionTag /></el-icon>
        热门标签
      </h3>
      <div class="tag-cloud">
        <router-link
          v-for="tag in tags"
          :key="tag.name"
          :to="`/news?tag=${tag.name}`"
          class="tag-item"
          :class="getTagSize(tag.count)"
          :style="{ color: getTagColor(tag.count) }"
        >
          {{ tag.name }}
        </router-link>
      </div>
    </div>

    <!-- 订阅通知 -->
    <div class="sidebar-section">
      <h3 class="sidebar-title">
        <el-icon><Bell /></el-icon>
        订阅通知
      </h3>
      <div class="subscription-form">
        <p class="subscription-desc">订阅我们，获取最新资讯推送</p>
        <el-input
          v-model="email"
          placeholder="请输入您的邮箱"
          type="email"
          size="small"
        >
          <template #append>
            <el-button type="primary" size="small" @click="handleSubscribe">
              订阅
            </el-button>
          </template>
        </el-input>
      </div>
    </div>
  </aside>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  Menu, Star, Reading, CollectionTag, Bell, TrendCharts,
  Document, Monitor, Trophy, VideoPlay, ChatDotRound, View
} from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()

// 响应式数据
const selectedCategory = ref<number | null>(null)
const email = ref('')

// 模拟数据
const categories = ref([
  { id: 1, name: '时政要闻', icon: 'Document', count: 15420, color: '#2563eb', trending: true },
  { id: 2, name: '财经资讯', icon: 'TrendCharts', count: 12350, color: '#10b981', trending: true },
  { id: 3, name: '科技前沿', icon: 'Monitor', count: 18960, color: '#8b5cf6', trending: false },
  { id: 4, name: '体育竞技', icon: 'Trophy', count: 9870, color: '#f59e0b', trending: false },
  { id: 5, name: '娱乐八卦', icon: 'VideoPlay', count: 21430, color: '#ef4444', trending: true },
  { id: 6, name: '教育文化', icon: 'Reading', count: 7650, color: '#06b6d4', trending: false },
  { id: 7, name: '健康生活', icon: 'Document', count: 8920, color: '#84cc16', trending: false },
  { id: 8, name: '国际新闻', icon: 'Document', count: 11280, color: '#f97316', trending: false }
])

const hotTopics = ref([
  { id: 1, title: '人工智能技术突破', heat: 125680, comments: 892 },
  { id: 2, title: '新能源汽车销量创新高', heat: 98450, comments: 654 },
  { id: 3, title: '数字经济发展趋势', heat: 87630, comments: 523 },
  { id: 4, title: '健康生活方式指南', heat: 76540, comments: 412 },
  { id: 5, title: '教育改革新政策', heat: 65230, comments: 387 },
  { id: 6, title: '环保科技新进展', heat: 54320, comments: 298 }
])

const recommendations = ref([
  {
    id: 1,
    title: '深度解析：人工智能如何改变我们的生活',
    author: '张明',
    publishTime: '2025-01-15T10:30:00Z',
    coverImage: '/images/ai-article.jpg'
  },
  {
    id: 2,
    title: '专家观点：2025年经济发展预测',
    author: '李华',
    publishTime: '2025-01-14T15:45:00Z',
    coverImage: '/images/economy-article.jpg'
  },
  {
    id: 3,
    title: '健康生活：科学饮食与运动指南',
    author: '王芳',
    publishTime: '2025-01-13T09:20:00Z',
    coverImage: '/images/health-article.jpg'
  },
  {
    id: 4,
    title: '教育前沿：数字化教学新模式',
    author: '赵强',
    publishTime: '2025-01-12T14:15:00Z',
    coverImage: '/images/education-article.jpg'
  }
])

const tags = ref([
  { name: '人工智能', count: 1250 },
  { name: '新能源', count: 980 },
  { name: '数字经济', count: 856 },
  { name: '健康生活', count: 743 },
  { name: '教育改革', count: 621 },
  { name: '环保科技', count: 589 },
  { name: '文化传承', count: 432 },
  { name: '体育竞技', count: 398 },
  { name: '娱乐资讯', count: 376 },
  { name: '国际关系', count: 321 }
])

// 方法
const selectCategory = (categoryId: number) => {
  selectedCategory.value = categoryId
}

const goToTopic = (topic: any) => {
  router.push({
    path: '/news',
    query: { topic: topic.id }
  })
}

const goToArticle = (article: any) => {
  router.push(`/news/${article.id}`)
}

const handleSubscribe = () => {
  if (!email.value.trim()) {
    ElMessage.warning('请输入邮箱地址')
    return
  }
  
  if (!isValidEmail(email.value)) {
    ElMessage.warning('请输入有效的邮箱地址')
    return
  }
  
  // 模拟订阅API调用
  ElMessage.success('订阅成功！感谢您的关注')
  email.value = ''
}

const isValidEmail = (email: string): boolean => {
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  return emailRegex.test(email)
}

const formatNumber = (num: number): string => {
  if (num >= 10000) {
    return (num / 10000).toFixed(1) + '万'
  }
  return num.toString()
}

const formatTime = (time: string): string => {
  const date = new Date(time)
  const now = new Date()
  const diff = now.getTime() - date.getTime()
  const days = Math.floor(diff / (1000 * 60 * 60 * 24))
  
  if (days === 0) {
    return '今天'
  } else if (days === 1) {
    return '昨天'
  } else if (days < 7) {
    return `${days}天前`
  } else {
    return date.toLocaleDateString()
  }
}

const getTagSize = (count: number): string => {
  if (count >= 1000) return 'tag-large'
  if (count >= 500) return 'tag-medium'
  return 'tag-small'
}

const getTagColor = (count: number): string => {
  const colors = [
    '#2563eb', '#10b981', '#f59e0b', '#ef4444', '#8b5cf6',
    '#06b6d4', '#84cc16', '#f97316', '#ec4899', '#6366f1'
  ]
  return colors[count % colors.length] || '#2563eb'
}

const handleImageError = (event: Event) => {
  const img = event.target as HTMLImageElement
  img.src = '/placeholder.jpg'
}

// 生命周期
onMounted(() => {
  // 从路由参数获取当前选中的分类
  const route = useRoute()
  if (route.query.category) {
    selectedCategory.value = Number(route.query.category)
  }
})
</script>

<style scoped>
@import '@/assets/design-system.css';

.sidebar-navigation {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-xl);
}

/* 侧边栏区块 */
.sidebar-section {
  background: var(--bg-primary);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-md);
  padding: var(--spacing-lg);
  transition: var(--transition-normal);
}

.sidebar-section:hover {
  box-shadow: var(--shadow-lg);
}

.sidebar-title {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  font-size: var(--text-lg);
  font-weight: 600;
  color: var(--text-primary);
  margin: 0 0 var(--spacing-lg) 0;
  padding-bottom: var(--spacing-sm);
  border-bottom: 1px solid var(--border-primary);
}

.sidebar-title .el-icon {
  color: var(--primary-color);
}

/* 分类列表 */
.category-list {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-xs);
}

.category-item {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  padding: var(--spacing-sm);
  border-radius: var(--radius-md);
  text-decoration: none;
  color: var(--text-secondary);
  transition: var(--transition-normal);
  position: relative;
}

.category-item:hover {
  background: var(--bg-secondary);
  color: var(--text-primary);
  transform: translateX(4px);
}

.category-item.active {
  background: rgba(37, 99, 235, 0.1);
  color: var(--primary-color);
  font-weight: 500;
}

.category-icon {
  width: 32px;
  height: 32px;
  border-radius: var(--radius-md);
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--bg-secondary);
  font-size: 16px;
}

.category-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.category-name {
  font-size: var(--text-sm);
  font-weight: 500;
  line-height: var(--leading-tight);
}

.category-count {
  font-size: var(--text-xs);
  color: var(--text-muted);
  line-height: var(--leading-tight);
}

.trending-badge {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 20px;
  height: 20px;
  background: var(--danger-color);
  color: white;
  border-radius: var(--radius-full);
  font-size: 10px;
}

/* 热门话题 */
.trending-list {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-sm);
}

.trending-item {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  padding: var(--spacing-sm);
  border-radius: var(--radius-md);
  cursor: pointer;
  transition: var(--transition-normal);
}

.trending-item:hover {
  background: var(--bg-secondary);
}

.trending-rank {
  width: 24px;
  height: 24px;
  border-radius: var(--radius-md);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: var(--text-xs);
  font-weight: 600;
  background: var(--bg-tertiary);
  color: var(--text-muted);
}

.trending-rank.top-three {
  background: var(--primary-color);
  color: white;
}

.trending-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.trending-title {
  font-size: var(--text-sm);
  font-weight: 500;
  color: var(--text-primary);
  line-height: var(--leading-tight);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.trending-stats {
  display: flex;
  gap: var(--spacing-md);
  font-size: var(--text-xs);
  color: var(--text-muted);
}

.trending-heat,
.trending-comments {
  display: flex;
  align-items: center;
  gap: 2px;
}

/* 推荐阅读 */
.recommendation-list {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
}

.recommendation-item {
  display: flex;
  gap: var(--spacing-sm);
  cursor: pointer;
  transition: var(--transition-normal);
  padding: var(--spacing-xs);
  border-radius: var(--radius-md);
}

.recommendation-item:hover {
  background: var(--bg-secondary);
}

.recommendation-image {
  width: 60px;
  height: 45px;
  border-radius: var(--radius-md);
  overflow: hidden;
  flex-shrink: 0;
}

.recommendation-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.recommendation-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.recommendation-title {
  font-size: var(--text-sm);
  font-weight: 500;
  color: var(--text-primary);
  line-height: var(--leading-tight);
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.recommendation-meta {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  font-size: var(--text-xs);
  color: var(--text-muted);
}

/* 标签云 */
.tag-cloud {
  display: flex;
  flex-wrap: wrap;
  gap: var(--spacing-sm);
}

.tag-item {
  padding: var(--spacing-xs) var(--spacing-sm);
  border-radius: var(--radius-full);
  text-decoration: none;
  font-weight: 500;
  transition: var(--transition-normal);
  border: 1px solid var(--border-primary);
  background: var(--bg-primary);
}

.tag-item:hover {
  background: var(--bg-secondary);
  border-color: var(--primary-color);
  transform: translateY(-2px);
}

.tag-small {
  font-size: var(--text-xs);
}

.tag-medium {
  font-size: var(--text-sm);
}

.tag-large {
  font-size: var(--text-base);
}

/* 订阅表单 */
.subscription-form {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-sm);
}

.subscription-desc {
  font-size: var(--text-sm);
  color: var(--text-secondary);
  line-height: var(--leading-normal);
  margin: 0;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .sidebar-navigation {
    gap: var(--spacing-lg);
  }
  
  .sidebar-section {
    padding: var(--spacing-md);
  }
  
  .sidebar-title {
    font-size: var(--text-base);
  }
  
  .category-icon {
    width: 28px;
    height: 28px;
    font-size: 14px;
  }
  
  .recommendation-image {
    width: 50px;
    height: 35px;
  }
  
  .recommendation-title {
    font-size: var(--text-xs);
  }
  
  .tag-cloud {
    gap: var(--spacing-xs);
  }
}

/* 动画效果 */
.sidebar-section {
  animation: slideInRight var(--transition-slow) ease-out;
}

.sidebar-section:nth-child(1) { animation-delay: 0.1s; }
.sidebar-section:nth-child(2) { animation-delay: 0.2s; }
.sidebar-section:nth-child(3) { animation-delay: 0.3s; }
.sidebar-section:nth-child(4) { animation-delay: 0.4s; }
.sidebar-section:nth-child(5) { animation-delay: 0.5s; }

@keyframes slideInRight {
  from {
    opacity: 0;
    transform: translateX(20px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

/* 滚动条样式 */
.sidebar-navigation::-webkit-scrollbar {
  width: 6px;
}

.sidebar-navigation::-webkit-scrollbar-track {
  background: var(--bg-secondary);
  border-radius: var(--radius-full);
}

.sidebar-navigation::-webkit-scrollbar-thumb {
  background: var(--border-secondary);
  border-radius: var(--radius-full);
}

.sidebar-navigation::-webkit-scrollbar-thumb:hover {
  background: var(--border-primary);
}
</style>
