<template>
  <div class="news-card" :class="[cardSize, { 'featured': isFeatured }]">
    <!-- 封面图片 -->
    <div class="card-image-container" @click="goToDetail">
      <img
        v-if="news.coverImageUrl"
        :src="news.coverImageUrl"
        :alt="news.title"
        class="card-image"
        @error="handleImageError"
        loading="lazy"
      />
      <div v-else class="card-image-placeholder">
        <el-icon class="placeholder-icon"><Picture /></el-icon>
      </div>
      
      <!-- 图片覆盖层 -->
      <div class="card-image-overlay">
        <div class="overlay-content">
          <div class="category-badge" v-if="news.typeName">
            {{ news.typeName }}
          </div>
          <div class="reading-time" v-if="(news as any).readingTime">
            <el-icon><Clock /></el-icon>
            {{ (news as any).readingTime }}分钟阅读
          </div>
        </div>
      </div>
    </div>

    <!-- 卡片内容 -->
    <div class="card-content">
      <!-- 标题 -->
      <h3 class="card-title" @click="goToDetail">
        {{ news.title }}
      </h3>
      
      <!-- 摘要 -->
      <p v-if="news.summary" class="card-summary" @click="goToDetail">
        {{ news.summary }}
      </p>
      
      <!-- 标签 -->
      <div v-if="news.tags" class="card-tags">
        <el-tag
          v-for="tag in getTagList(news.tags)"
          :key="tag"
          size="small"
          :type="getTagType(tag)"
          effect="plain"
          class="news-tag"
        >
          {{ tag }}
        </el-tag>
      </div>
      
      <!-- 元信息 -->
      <div class="card-meta">
        <div class="meta-left">
          <div class="author-info" v-if="news.author">
            <el-avatar :size="24">
              {{ news.author.charAt(0) }}
            </el-avatar>
            <span class="author-name">{{ news.author }}</span>
          </div>
          <span class="publish-time">{{ formatTime(news.publishedTime) }}</span>
        </div>
        
        <div class="meta-right">
          <div class="stats">
            <span class="stat-item">
              <el-icon><View /></el-icon>
              {{ formatNumber(news.pageViews) }}
            </span>
            <span class="stat-item">
              <el-icon><Star /></el-icon>
              {{ formatNumber(news.likeCount) }}
            </span>
            <span class="stat-item" v-if="news.commentCount">
              <el-icon><ChatDotRound /></el-icon>
              {{ news.commentCount }}
            </span>
          </div>
        </div>
      </div>
    </div>

    <!-- 特色标记 -->
    <div v-if="isFeatured" class="featured-badge">
      <el-icon><Trophy /></el-icon>
      精选
    </div>
    
    <!-- 热门标记 -->
    <div v-if="news.isHot" class="hot-badge">
      <el-icon><LocalFire /></el-icon>
      热门
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import type { News } from '@/types'
import {
  Picture, Clock, View, Star, ChatDotRound, Trophy, TrendCharts
} from '@element-plus/icons-vue'

interface Props {
  news: News
  size?: 'small' | 'medium' | 'large'
  featured?: boolean
  showMeta?: boolean
  showTags?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  size: 'medium',
  featured: false,
  showMeta: true,
  showTags: true
})

const router = useRouter()

// 计算属性
const isFeatured = computed(() => props.featured || props.news.isTop)
const cardSize = computed(() => `card-${props.size}`)

// 方法
const goToDetail = () => {
  router.push(`/news/${props.news.hid}`)
}

const handleImageError = (event: Event) => {
  const img = event.target as HTMLImageElement
  img.src = '/placeholder-news.jpg'
}

const getTagList = (tags: string): string[] => {
  if (!tags) return []
  return tags.split(',').map(tag => tag.trim()).filter(Boolean)
}

const getTagType = (tag: string): string => {
  const tagTypes: Record<string, string> = {
    '热点': 'danger',
    '独家': 'warning',
    '推荐': 'success',
    '原创': 'primary',
    '精选': 'info'
  }
  return tagTypes[tag] || ''
}

const formatNumber = (num: number): string => {
  if (num >= 10000) {
    return (num / 10000).toFixed(1) + '万'
  } else if (num >= 1000) {
    return (num / 1000).toFixed(1) + 'k'
  }
  return num.toString()
}

const formatTime = (time: string | undefined): string => {
  if (!time) return '未知时间'
  
  const date = new Date(time)
  const now = new Date()
  const diff = now.getTime() - date.getTime()
  const hours = Math.floor(diff / (1000 * 60 * 60))
  const days = Math.floor(diff / (1000 * 60 * 60 * 24))
  
  if (hours < 1) {
    return '刚刚'
  } else if (hours < 24) {
    return `${hours}小时前`
  } else if (days < 7) {
    return `${days}天前`
  } else {
    return date.toLocaleDateString()
  }
}
</script>

<style scoped>
@import '@/assets/design-system.css';

.news-card {
  background: var(--bg-primary);
  border: 1px solid var(--border-primary);
  border-radius: var(--radius-lg);
  overflow: hidden;
  transition: var(--transition-normal);
  cursor: pointer;
  position: relative;
  display: flex;
  flex-direction: column;
  height: 100%;
  box-shadow: var(--shadow-card);
}

.news-card:hover {
  transform: translateY(-8px);
  box-shadow: var(--shadow-xl);
  border-color: var(--primary-color);
  box-shadow: 0 12px 24px -8px rgba(0, 0, 0, 0.12), 0 8px 12px -6px rgba(0, 0, 0, 0.08);
}

/* 卡片尺寸 */
.card-small {
  max-width: 320px;
}

.card-medium {
  max-width: 400px;
}

.card-large {
  max-width: 480px;
}

/* 特色卡片 */
.news-card.featured {
  border: 2px solid var(--primary-color);
  background: linear-gradient(135deg, var(--bg-primary), rgba(26, 54, 93, 0.03));
  box-shadow: var(--shadow-lg);
}

/* 图片容器 */
.card-image-container {
  position: relative;
  width: 100%;
  padding-top: 56.25%; /* 16:9 比例 */
  overflow: hidden;
}

.card-image {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: var(--transition-normal);
}

.news-card:hover .card-image {
  transform: scale(1.05);
}

.card-image-placeholder {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: var(--bg-tertiary);
  display: flex;
  align-items: center;
  justify-content: center;
}

.placeholder-icon {
  font-size: 48px;
  color: var(--text-muted);
}

/* 图片覆盖层 */
.card-image-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(to bottom, transparent 50%, rgba(26, 54, 93, 0.8) 100%);
  opacity: 0;
  transition: var(--transition-normal);
}

.news-card:hover .card-image-overlay {
  opacity: 1;
}

.overlay-content {
  position: absolute;
  bottom: 0;
  left: 0;
  width: 100%;
  padding: var(--spacing-lg);
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
}

.category-badge {
  background: var(--primary-color);
  color: white;
  padding: var(--spacing-xs) var(--spacing-sm);
  border-radius: var(--radius-full);
  font-size: var(--text-xs);
  font-weight: 600;
  letter-spacing: 0.05em;
  text-transform: uppercase;
  backdrop-filter: blur(4px);
}

.reading-time {
  display: flex;
  align-items: center;
  gap: var(--spacing-xs);
  color: white;
  font-size: var(--text-xs);
  background: rgba(26, 54, 93, 0.9);
  padding: var(--spacing-xs) var(--spacing-sm);
  border-radius: var(--radius-full);
  font-weight: 500;
  backdrop-filter: blur(4px);
}

/* 卡片内容 */
.card-content {
  padding: var(--spacing-xl);
  display: flex;
  flex-direction: column;
  gap: var(--spacing-lg);
  flex: 1;
}

.card-title {
  font-family: var(--font-family-heading);
  font-size: var(--headline-size-sm);
  font-weight: 700;
  color: var(--text-primary);
  line-height: var(--leading-headline);
  margin: 0;
  cursor: pointer;
  transition: var(--transition-normal);
  display: -webkit-box;
  -webkit-line-clamp: 2;
  line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  letter-spacing: -0.02em;
}

.card-title:hover {
  color: var(--primary-color);
}

.card-small .card-title {
  font-size: var(--text-base);
}

.card-large .card-title {
  font-size: var(--text-xl);
}

.card-summary {
  font-family: var(--font-family-body);
  font-size: var(--body-size-sm);
  color: var(--text-secondary);
  line-height: var(--leading-body);
  margin: 0;
  cursor: pointer;
  transition: var(--transition-normal);
  display: -webkit-box;
  -webkit-line-clamp: 3;
  line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
  letter-spacing: 0.01em;
}

.card-summary:hover {
  color: var(--text-primary);
}

/* 标签 */
.card-tags {
  display: flex;
  flex-wrap: wrap;
  gap: var(--spacing-xs);
}

.news-tag {
  font-size: var(--text-xs);
  border-radius: var(--radius-full);
}

/* 元信息 */
.card-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: var(--spacing-sm);
  border-top: 1px solid var(--border-primary);
}

.meta-left {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-xs);
  flex: 1;
}

.author-info {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
}

.author-name {
  font-family: var(--font-family-body);
  font-size: var(--body-size-sm);
  color: var(--text-secondary);
  font-weight: 600;
  letter-spacing: 0.01em;
}

.publish-time {
  font-family: var(--font-family-ui);
  font-size: var(--text-xs);
  color: var(--text-muted);
  font-weight: 500;
  letter-spacing: 0.02em;
}

.meta-right {
  display: flex;
  align-items: center;
}

.stats {
  display: flex;
  gap: var(--spacing-md);
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-family: var(--font-family-ui);
  font-size: var(--text-xs);
  color: var(--text-muted);
  transition: var(--transition-normal);
  font-weight: 500;
  letter-spacing: 0.02em;
}

.stat-item:hover {
  color: var(--primary-color);
}

/* 徽章 */
.featured-badge,
.hot-badge {
  position: absolute;
  top: var(--spacing-sm);
  right: var(--spacing-sm);
  display: flex;
  align-items: center;
  gap: var(--spacing-xs);
  padding: var(--spacing-xs) var(--spacing-sm);
  border-radius: var(--radius-full);
  font-size: var(--text-xs);
  font-weight: 500;
  z-index: 1;
}

.featured-badge {
  background: linear-gradient(135deg, var(--warning-color), var(--accent-color));
  color: white;
  font-weight: 700;
  letter-spacing: 0.05em;
  text-transform: uppercase;
  box-shadow: var(--shadow-md);
}

.hot-badge {
  background: linear-gradient(135deg, var(--danger-color), #dc2626);
  color: white;
  font-weight: 700;
  letter-spacing: 0.05em;
  text-transform: uppercase;
  box-shadow: var(--shadow-md);
  animation: pulse 2s infinite;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .card-content {
    padding: var(--spacing-md);
  }
  
  .card-title {
    font-size: var(--text-base);
  }
  
  .card-summary {
    font-size: var(--text-xs);
  }
  
  .card-meta {
    flex-direction: column;
    align-items: flex-start;
    gap: var(--spacing-sm);
  }
  
  .stats {
    width: 100%;
    justify-content: space-between;
  }
  
  .overlay-content {
    flex-direction: column;
    align-items: flex-start;
    gap: var(--spacing-sm);
  }
}

@media (max-width: 480px) {
  .card-small,
  .card-medium,
  .card-large {
    max-width: 100%;
  }
  
  .card-content {
    padding: var(--spacing-sm);
  }
  
  .featured-badge,
  .hot-badge {
    top: var(--spacing-xs);
    right: var(--spacing-xs);
    padding: var(--spacing-xs);
  }
}

/* 加载动画 */
.news-card {
  animation: fadeInUp var(--transition-slow) ease-out;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes pulse {
  0%, 100% {
    opacity: 1;
  }
  50% {
    opacity: 0.8;
  }
}

/* 深色模式适配 */
@media (prefers-color-scheme: dark) {
  .news-card {
    background: var(--bg-primary);
    border-color: var(--border-primary);
  }
  
  .card-image-placeholder {
    background: var(--bg-tertiary);
  }
  
  .category-badge {
    background: var(--primary-color);
  }
  
  .reading-time {
    background: rgba(255, 255, 255, 0.1);
  }
}
</style>
