<template>
  <div class="news-list">
    <el-container>
      <el-header>
        <h1>新闻列表</h1>
        <el-input
          v-model="searchQuery"
          placeholder="搜索新闻..."
          style="width: 300px"
          @keyup.enter="searchNews"
        >
          <template #append>
            <el-button @click="searchNews">
              <el-icon><Search /></el-icon>
            </el-button>
          </template>
        </el-input>
      </el-header>
      
      <el-main>
        <el-row :gutter="20">
          <el-col :span="6">
            <el-card>
              <h3>分类筛选</h3>
              <el-menu
                :default-active="selectedCategory"
                @select="handleCategorySelect"
              >
                <el-menu-item index="">全部新闻</el-menu-item>
                <el-menu-item 
                  v-for="category in categories" 
                  :key="category.id"
                  :index="category.id.toString()"
                >
                  {{ category.name }}
                </el-menu-item>
              </el-menu>
            </el-card>
          </el-col>
          
          <el-col :span="18">
            <el-row :gutter="20" v-loading="loading">
              <el-col 
                :span="8" 
                v-for="news in newsList" 
                :key="news.hid"
                style="margin-bottom: 20px"
              >
                <el-card 
                  :body-style="{ padding: '0px' }"
                  shadow="hover"
                  @click="goToDetail(news.hid)"
                  class="news-card"
                >
                  <img 
                    :src="news.coverImage || '/placeholder.jpg'" 
                    class="news-image"
                    :alt="news.title"
                  />
                  <div class="news-content">
                    <h3>{{ news.title }}</h3>
                    <p class="news-summary">{{ news.summary }}</p>
                    <div class="news-meta">
                      <span class="news-author">{{ news.author }}</span>
                      <span class="news-time">{{ formatTime(news.publishedTime) }}</span>
                    </div>
                    <div class="news-stats">
                      <el-icon><View /></el-icon>
                      <span>{{ news.pageViews }}</span>
                      <el-icon><Star /></el-icon>
                      <span>{{ news.likeCount }}</span>
                    </div>
                  </div>
                </el-card>
              </el-col>
            </el-row>
            
            <el-pagination
              v-model:current-page="currentPage"
              v-model:page-size="pageSize"
              :page-sizes="[10, 20, 50, 100]"
              :total="total"
              layout="total, sizes, prev, pager, next, jumper"
              @size-change="handleSizeChange"
              @current-change="handleCurrentChange"
            />
          </el-col>
        </el-row>
      </el-main>
    </el-container>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Search, View, Star } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import type { Headline } from '@/types/headline'
import { getHeadlines } from '@/api/headline'

const router = useRouter()

// 响应式数据
const loading = ref(false)
const newsList = ref<Headline[]>([])
const categories = ref([
  { id: 1, name: '科技' },
  { id: 2, name: '体育' },
  { id: 3, name: '娱乐' },
  { id: 4, name: '财经' },
  { id: 5, name: '教育' }
])
const searchQuery = ref('')
const selectedCategory = ref('')
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 方法
const fetchNews = async () => {
  loading.value = true
  try {
    const params = {
      page: currentPage.value,
      pageSize: pageSize.value,
      keywords: searchQuery.value,
      type: selectedCategory.value ? Number(selectedCategory.value) : undefined
    }
    
    const response = await getHeadlines(params)
    if (response.code === 200) {
      newsList.value = response.data.items || []
      total.value = response.data.total || 0
    } else {
      ElMessage.error(response.message || '获取新闻列表失败')
    }
  } catch (error) {
    console.error('获取新闻列表失败:', error)
    ElMessage.error('获取新闻列表失败')
  } finally {
    loading.value = false
  }
}

const searchNews = () => {
  currentPage.value = 1
  fetchNews()
}

const handleCategorySelect = (categoryIndex: string) => {
  selectedCategory.value = categoryIndex
  currentPage.value = 1
  fetchNews()
}

const handleSizeChange = (newSize: number) => {
  pageSize.value = newSize
  currentPage.value = 1
  fetchNews()
}

const handleCurrentChange = (newPage: number) => {
  currentPage.value = newPage
  fetchNews()
}

const goToDetail = (hid: number) => {
  router.push(`/news/${hid}`)
}

const formatTime = (time?: string) => {
  if (!time) return ''
  return new Date(time).toLocaleDateString()
}

// 生命周期
onMounted(() => {
  fetchNews()
})
</script>

<style scoped>
.news-list {
  padding: 20px;
}

.el-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 60px;
  padding: 0 20px;
  background: #fff;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.news-card {
  cursor: pointer;
  transition: transform 0.2s;
}

.news-card:hover {
  transform: translateY(-2px);
}

.news-image {
  width: 100%;
  height: 200px;
  object-fit: cover;
}

.news-content {
  padding: 15px;
}

.news-content h3 {
  margin: 0 0 10px 0;
  font-size: 16px;
  font-weight: bold;
  line-height: 1.4;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.news-summary {
  margin: 0 0 10px 0;
  font-size: 14px;
  color: #666;
  line-height: 1.5;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
}

.news-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
  font-size: 12px;
  color: #999;
}

.news-stats {
  display: flex;
  align-items: center;
  gap: 15px;
  font-size: 12px;
  color: #666;
}

.news-stats .el-icon {
  margin-right: 4px;
}

.el-pagination {
  margin-top: 20px;
  text-align: center;
}
</style>
