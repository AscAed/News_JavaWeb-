<template>
  <div class="home-page">
    <!-- 使用NewsCard组件展示新闻 -->
    <div class="news-grid">
      <NewsCard
        v-for="news in filteredNewsList"
        :key="news.hid"
        :news="news"
        :size="'medium'"
        @click="viewNewsDetail(news)"
      />
    </div>

    <!-- 加载更多 -->
    <div class="load-more-section" v-if="!newsStore.loading && newsStore.newsList.length < newsStore.total">
      <el-button
        @click="loadMore"
        :loading="newsStore.loading"
        size="large"
      >
        加载更多新闻
      </el-button>
    </div>

    <!-- 错误状态 -->
    <div v-if="error" class="error-state">
      <el-icon class="error-icon"><WarningFilled /></el-icon>
      <h3>加载失败</h3>
      <p>{{ error }}</p>
      <el-button @click="retryLoad" type="primary" :loading="retrying">
        <el-icon><Refresh /></el-icon>
        重新加载
      </el-button>
    </div>

    <!-- 空状态 -->
    <div v-if="!newsStore.loading && !error && filteredNewsList.length === 0" class="empty-state">
      <el-icon class="empty-icon"><DocumentRemove /></el-icon>
      <h3>暂无新闻</h3>
      <p>没有找到符合条件的新闻</p>
    </div>

    <!-- 加载状态 -->
    <div v-if="newsStore.loading && newsStore.newsList.length === 0" class="loading-state">
      <el-loading-spinner size="large" />
      <p>正在加载新闻...</p>
      <div class="loading-skeleton">
        <div v-for="i in 6" :key="i" class="skeleton-card">
          <div class="skeleton-image"></div>
          <div class="skeleton-content">
            <div class="skeleton-title"></div>
            <div class="skeleton-summary"></div>
            <div class="skeleton-meta"></div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useNewsStore } from '@/stores/news'
import { ElMessage } from 'element-plus'
import { DocumentRemove, WarningFilled, Refresh } from '@element-plus/icons-vue'
import NewsCard from '@/components/NewsCard.vue'
import type { News } from '@/types'

const router = useRouter()
const newsStore = useNewsStore()

// 响应式数据
const keyword = ref('')
const selectedType = ref<number | null>(null)
const error = ref<string | null>(null)
const retrying = ref(false)

// 计算属性
const filteredNewsList = computed(() => {
  let list = newsStore.newsList
  
  if (selectedType.value) {
    list = list.filter(news => news.type === selectedType.value)
  }
  
  if (keyword.value) {
    list = list.filter(news => 
      news.title.toLowerCase().includes(keyword.value.toLowerCase()) ||
      news.summary?.toLowerCase().includes(keyword.value.toLowerCase())
    )
  }
  
  return list
})

// 方法
const handleSearch = (searchKeyword: string) => {
  keyword.value = searchKeyword
}

const handleTypeChange = (typeId: number | null) => {
  selectedType.value = typeId
}

const viewNewsDetail = (news: News) => {
  router.push(`/news/${news.hid}`)
}

const loadMore = async () => {
  try {
    await newsStore.fetchNewsList({
      page: newsStore.currentPage + 1,
      size: newsStore.pageSize
    })
  } catch (err) {
    ElMessage.error('加载更多新闻失败')
  }
}

const retryLoad = async () => {
  retrying.value = true
  error.value = null
  
  try {
    await newsStore.fetchNewsList({
      page: 1,
      size: newsStore.pageSize
    })
    ElMessage.success('重新加载成功')
  } catch (err) {
    error.value = '重新加载失败，请稍后再试'
    ElMessage.error('重新加载失败')
  } finally {
    retrying.value = false
  }
}

// 生命周期
onMounted(async () => {
  // 监听来自AppHeader的搜索事件
  window.addEventListener('search', (event: any) => {
    keyword.value = event.detail.keyword
    handleSearch(keyword.value)
  })

  // 监听来自AppHeader的分类切换事件
  window.addEventListener('typeChange', (event: any) => {
    selectedType.value = event.detail.type
    handleTypeChange(event.detail.type)
  })

  // 初始加载数据
  try {
    await Promise.all([
      newsStore.fetchNewsList(),
      newsStore.fetchNewsTypes()
    ])
  } catch (error) {
    ElMessage.error('加载数据失败')
  }
})

// 暴露方法给父组件使用
defineExpose({
  handleSearch,
  handleTypeChange
})
</script>

<style scoped>
.home-page {
  padding: var(--spacing-lg);
  max-width: var(--container-xl);
  margin: 0 auto;
}

.news-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(380px, 1fr));
  gap: var(--spacing-xl);
  margin-bottom: var(--spacing-xl);
}

.load-more-section {
  text-align: center;
  padding: var(--spacing-xl);
  margin: var(--spacing-xl) 0;
}

.error-state {
  text-align: center;
  padding: var(--spacing-2xl);
  color: var(--text-secondary);
}

.error-icon {
  font-size: 4rem;
  margin-bottom: var(--spacing-lg);
  color: var(--danger-color);
  animation: error-pulse var(--transition-slow) ease-in-out infinite;
}

.error-state h3 {
  margin-bottom: var(--spacing-md);
  color: var(--text-primary);
  font-family: var(--font-family-heading);
  font-size: var(--headline-size-md);
  font-weight: 600;
}

.error-state p {
  margin-bottom: var(--spacing-lg);
  color: var(--text-secondary);
  font-family: var(--font-family-body);
  font-size: var(--body-size-md);
  line-height: var(--leading-body);
}

.empty-state {
  text-align: center;
  padding: var(--spacing-2xl);
  color: var(--text-secondary);
}

.empty-icon {
  font-size: 4rem;
  margin-bottom: var(--spacing-lg);
  color: var(--text-tertiary);
  animation: empty-float var(--transition-slow) ease-in-out infinite;
}

.empty-state h3 {
  margin-bottom: var(--spacing-md);
  color: var(--text-primary);
  font-family: var(--font-family-heading);
  font-size: var(--headline-size-md);
  font-weight: 600;
}

.loading-state {
  text-align: center;
  padding: var(--spacing-2xl);
  color: var(--text-secondary);
}

.loading-state p {
  margin-top: var(--spacing-md);
  margin-bottom: var(--spacing-xl);
  color: var(--text-secondary);
  font-family: var(--font-family-body);
  font-size: var(--body-size-md);
}

/* 骨架屏样式 */
.loading-skeleton {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: var(--spacing-xl);
  margin-top: var(--spacing-xl);
}

.skeleton-card {
  background: var(--bg-primary);
  border: 1px solid var(--border-primary);
  border-radius: var(--radius-lg);
  overflow: hidden;
  position: relative;
  animation: skeleton-fade-in var(--transition-slow) ease-out;
}

.skeleton-image {
  width: 100%;
  height: 200px;
  background: linear-gradient(90deg, var(--bg-tertiary) 25%, var(--bg-secondary) 50%, var(--bg-tertiary) 75%);
  background-size: 200% 100%;
  animation: skeleton-shimmer var(--transition-slow) ease-in-out infinite;
  border-radius: var(--radius-md);
}

.skeleton-content {
  padding: var(--spacing-lg);
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
}

.skeleton-title {
  height: 20px;
  background: linear-gradient(90deg, var(--bg-tertiary) 25%, var(--bg-secondary) 50%, var(--bg-tertiary) 75%);
  background-size: 200% 100%;
  animation: skeleton-shimmer var(--transition-slow) ease-in-out infinite;
  border-radius: var(--radius-sm);
  width: 60%;
}

.skeleton-summary {
  height: 16px;
  background: linear-gradient(90deg, var(--bg-tertiary) 25%, var(--bg-secondary) 50%, var(--bg-tertiary) 75%);
  background-size: 200% 100%;
  animation: skeleton-shimmer var(--transition-slow) ease-in-out infinite;
  border-radius: var(--radius-sm);
  width: 80%;
}

.skeleton-meta {
  height: 14px;
  width: 60%;
  background: linear-gradient(90deg, var(--bg-tertiary) 25%, var(--bg-secondary) 50%, var(--bg-tertiary) 75%);
  background-size: 200% 100%;
  animation: skeleton-shimmer var(--transition-slow) ease-in-out infinite;
  border-radius: var(--radius-sm);
}

@keyframes skeleton-loading {
  0% { opacity: 0.7; }
  50% { opacity: 1; }
  100% { opacity: 0.7; }
}

@keyframes skeleton-shimmer {
  0% { background-position: -200% 0; }
  100% { background-position: 200% 0; }
}

@keyframes skeleton-fade-in {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes error-pulse {
  0%, 100% {
    transform: scale(1);
    opacity: 1;
  }
  50% {
    transform: scale(1.05);
    opacity: 0.8;
  }
}

@keyframes empty-float {
  0%, 100% {
    transform: translateY(0);
  }
  25% {
    transform: translateY(-3px);
  }
  75% {
    transform: translateY(0);
  }
}

/* 响应式设计 - 移动优先 */
@media (max-width: 1200px) {
  .news-grid {
    grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
    gap: var(--spacing-lg);
  }
}

@media (max-width: 1024px) {
  .news-grid {
    grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
    gap: var(--spacing-md);
  }
  
  .home-page {
    padding: var(--spacing-md);
  }
}

@media (max-width: 768px) {
  .news-grid {
    grid-template-columns: 1fr;
    gap: var(--spacing-md);
  }
  
  .home-page {
    padding: var(--spacing-sm);
  }
  
  .load-more-section {
    padding: var(--spacing-md);
    margin: var(--spacing-lg) 0;
  }
}

@media (max-width: 480px) {
  .news-grid {
    grid-template-columns: 1fr;
    gap: var(--spacing-sm);
  }
  
  .home-page {
    padding: var(--spacing-xs);
  }
  
  .load-more-section {
    padding: var(--spacing-sm);
    margin: var(--spacing-md) 0;
  }
}
</style>
