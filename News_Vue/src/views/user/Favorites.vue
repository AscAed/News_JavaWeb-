<template>
  <div class="favorites-page">
    <div class="page-header">
      <h1>我的收藏</h1>
      <p>您收藏的所有新闻</p>
    </div>

    <div class="filter-section">
      <el-input
        v-model="searchKeyword"
        placeholder="搜索收藏的新闻..."
        @keyup.enter="fetchFavorites"
        style="max-width: 400px;"
      >
        <template #prefix>
          <el-icon><Search /></el-icon>
        </template>
      </el-input>
    </div>

    <div class="favorites-list" v-loading="loading">
      <div
        v-for="favorite in favoritesList"
        :key="favorite.id"
        class="favorite-item"
        @click="favorite.news && viewNews(favorite.news)"
      >
        <div class="item-image">
          <img
            v-if="favorite.news?.coverImageUrl"
            :src="favorite.news.coverImageUrl"
            :alt="favorite.news?.title"
          />
          <div v-else class="image-placeholder">
            <el-icon><Picture /></el-icon>
          </div>
        </div>

        <div class="item-content">
          <h3 class="item-title">{{ favorite.news?.title }}</h3>
          <p class="item-summary" v-if="favorite.news?.summary">
            {{ favorite.news.summary }}
          </p>

          <div class="item-meta">
            <div class="meta-left">
              <span class="author">{{ favorite.news?.author }}</span>
              <span class="type">{{ favorite.news?.typeName }}</span>
            </div>
            <div class="meta-right">
              <span class="favorite-time">{{ formatTime(favorite.favoriteTime) }}</span>
              <el-button
                size="small"
                type="danger"
                @click.stop="removeFavorite(favorite)"
              >
                取消收藏
              </el-button>
            </div>
          </div>
        </div>
      </div>

      <div v-if="!loading && favoritesList.length === 0" class="empty-state">
        <el-icon class="empty-icon"><Star /></el-icon>
        <p>暂无收藏的新闻</p>
        <el-button type="primary" @click="$router.push('/news')">
          去浏览新闻
        </el-button>
      </div>
    </div>

    <div class="pagination" v-if="total > pageSize">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :total="total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="fetchFavorites"
        @current-change="fetchFavorites"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Picture, Star } from '@element-plus/icons-vue'
import type { Favorite, News } from '@/types'

const router = useRouter()

const searchKeyword = ref('')
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(20)
const total = ref(0)

const favoritesList = ref<Favorite[]>([])

const formatTime = (time: string) => {
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

const fetchFavorites = async () => {
  loading.value = true
  try {
    // 模拟数据，实际应该调用API
    const mockNews: News = {
      hid: 1,
      title: 'Vue 3 Composition API 深度解析',
      summary: '详细介绍Vue 3的新特性和最佳实践，包括响应式系统、组合式API等核心概念',
      type: 1,
      typeName: '技术',
      publisher: 1,
      author: '张三',
      pageViews: 1234,
      likeCount: 56,
      commentCount: 12,
      favoriteCount: 8,
      isTop: false,
      isHot: false,
      status: 1,
      coverImageUrl: 'https://example.com/cover.jpg',
      tags: 'Vue,JavaScript,前端',
      createdTime: '2025-12-31 10:30:00',
      updatedTime: '2025-12-31 10:30:00'
    }

    favoritesList.value = [
      {
        id: 1,
        hid: 1,
        userId: 1,
        favoriteTime: '2025-12-30 15:30:00',
        news: mockNews
      },
      {
        id: 2,
        hid: 2,
        userId: 1,
        favoriteTime: '2025-12-29 10:20:00',
        news: {
          ...mockNews,
          hid: 2,
          title: 'Spring Boot 最佳实践指南',
          summary: '深入探讨Spring Boot开发中的最佳实践和常见陷阱'
        }
      }
    ]
    total.value = 2
  } catch (error) {
    ElMessage.error('获取收藏列表失败')
  } finally {
    loading.value = false
  }
}

const viewNews = (news: News) => {
  router.push(`/news/${news.hid}`)
}

const removeFavorite = async (favorite: Favorite) => {
  try {
    await ElMessageBox.confirm('确定要取消收藏这篇新闻吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    // 调用取消收藏API
    ElMessage.success('已取消收藏')
    fetchFavorites()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('取消收藏失败')
    }
  }
}

onMounted(() => {
  fetchFavorites()
})
</script>

<style scoped>
.favorites-page {
  padding: var(--spacing-lg);
  max-width: 1000px;
  margin: 0 auto;
}

.page-header {
  margin-bottom: var(--spacing-lg);
}

.page-header h1 {
  color: var(--text-primary);
  margin-bottom: var(--spacing-sm);
}

.page-header p {
  color: var(--text-secondary);
  margin: 0;
}

.filter-section {
  margin-bottom: var(--spacing-lg);
}

.favorites-list {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-lg);
}

.favorite-item {
  background: var(--bg-primary);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-sm);
  overflow: hidden;
  cursor: pointer;
  transition: var(--transition-normal);
  display: flex;
}

.favorite-item:hover {
  box-shadow: var(--shadow-md);
  transform: translateY(-2px);
}

.item-image {
  width: 200px;
  height: 150px;
  flex-shrink: 0;
}

.item-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.image-placeholder {
  width: 100%;
  height: 100%;
  background: var(--bg-secondary);
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--text-secondary);
  font-size: 2rem;
}

.item-content {
  flex: 1;
  padding: var(--spacing-lg);
  display: flex;
  flex-direction: column;
}

.item-title {
  color: var(--text-primary);
  margin-bottom: var(--spacing-sm);
  font-size: var(--text-lg);
  font-weight: 600;
  line-height: var(--leading-tight);
}

.item-summary {
  color: var(--text-secondary);
  margin-bottom: var(--spacing-md);
  line-height: var(--leading-normal);
  flex: 1;
}

.item-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: var(--text-sm);
}

.meta-left {
  display: flex;
  gap: var(--spacing-md);
}

.author, .type {
  color: var(--text-secondary);
}

.meta-right {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
}

.favorite-time {
  color: var(--text-secondary);
}

.empty-state {
  text-align: center;
  padding: var(--spacing-xl);
  color: var(--text-secondary);
}

.empty-icon {
  font-size: 4rem;
  margin-bottom: var(--spacing-lg);
  color: var(--text-tertiary);
}

.pagination {
  margin-top: var(--spacing-xl);
  text-align: center;
}

@media (max-width: 768px) {
  .favorite-item {
    flex-direction: column;
  }
  
  .item-image {
    width: 100%;
    height: 200px;
  }
  
  .item-meta {
    flex-direction: column;
    align-items: flex-start;
    gap: var(--spacing-sm);
  }
}
</style>
