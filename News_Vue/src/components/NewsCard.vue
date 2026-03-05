<template>
  <div class="news-card" :class="[cardSize, { 'featured': isFeatured }]">
    <!-- 卡片内容 (左侧) -->
    <div class="card-content">
      <!-- 标题 -->
      <h3 class="card-title" @click="goToDetail">
        {{ news.title }}
      </h3>

      <!-- 摘要 -->
      <p v-if="news.summary" class="card-summary" @click="goToDetail">
        {{ news.summary }}
      </p>

      <!-- 标签 & 分类 (合并显示在摘要下方) -->
      <div class="news-tags-group">
        <el-tag v-if="news.typeName" class="news-tag type-tag" effect="plain" size="small"
                type="info">
          {{ news.typeName }}
        </el-tag>
        <template v-if="news.tags">
          <el-tag
            v-for="tag in getTagList(news.tags)"
            :key="tag"
            :type="getTagType(tag)"
            class="news-tag"
            effect="plain"
            size="small"
          >
            {{ tag }}
          </el-tag>
        </template>
      </div>

      <!-- 元信息 -->
      <div class="card-meta">
        <div class="meta-left">
          <span v-if="(news as any).sourceName"
                class="source-name">来自: {{ (news as any).sourceName }}</span>
          <span v-if="news.author" class="author-name">· {{ news.author }}</span>
          <span class="publish-time">· {{ formatTime(news.publishedTime) }}</span>
          <span v-if="(news as any).readingTime" class="reading-time">
            · {{ (news as any).readingTime }}分钟阅读
          </span>
        </div>
      </div>
    </div>

    <!-- 封面图片 (右侧) -->
    <div v-if="news.coverImageUrl" class="card-image-container" @click="goToDetail">
      <img
        :alt="news.title"
        :src="news.coverImageUrl"
        class="card-image"
        loading="lazy"
        @error="handleImageError"
      />
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
import {computed} from 'vue'
import {useRouter} from 'vue-router'
import type {News} from '@/types'
import {Trophy} from '@element-plus/icons-vue'

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
  if (props.news.sourceUrl) {
    window.open(props.news.sourceUrl, '_blank')
    return
  }
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
  border-bottom: 1px solid var(--border-primary);
  padding-bottom: var(--spacing-xl);
  transition: var(--transition-normal);
  cursor: pointer;
  position: relative;
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  gap: var(--spacing-xl);
  align-items: flex-start;
}

.news-card:hover {
  opacity: 0.8;
}

/* 覆盖组件的默认最大宽度以适应流式布局 */
.card-small, .card-medium, .card-large {
  max-width: 100%;
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
  flex-shrink: 0;
  width: 160px;
  height: 108px;
  overflow: hidden;
  border-radius: var(--radius-md);
  margin-top: var(--spacing-sm);
}

.card-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: var(--transition-normal);
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
  display: flex;
  flex-direction: column;
  gap: var(--spacing-xs);
  flex: 1;
}

.card-title {
  font-family: var(--font-family-heading);
  font-size: 1.1rem;
  font-weight: 500;
  color: var(--text-primary);
  line-height: 1.4;
  margin: 0;
  cursor: pointer;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
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
  line-height: 1.4;
  margin: 4px 0 0 0;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.news-tags-group {
  display: flex;
  flex-wrap: wrap;
  gap: var(--spacing-xs);
  margin-top: var(--spacing-xs);
}

.news-tag {
  font-size: var(--text-xs);
  border-radius: var(--radius-full);
}

.card-meta {
  display: flex;
  align-items: center;
  margin-top: var(--spacing-sm);
}

.meta-left {
  display: flex;
  align-items: center;
  gap: var(--spacing-xs);
  font-size: 0.75rem;
  color: var(--text-muted);
}

.source-name {
  color: var(--primary-color);
  font-weight: 500;
}

.author-name {
  font-weight: 500;
  color: var(--text-secondary);
}

.reading-time {
  color: var(--text-muted);
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
  .news-card {
    flex-direction: column;
    gap: var(--spacing-md);
  }

  .card-image-container {
    width: 100%;
    height: auto;
    padding-top: 56.25%; /* 16:9 on mobile */
  }

  .card-image {
    position: absolute;
    top: 0;
    left: 0;
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
