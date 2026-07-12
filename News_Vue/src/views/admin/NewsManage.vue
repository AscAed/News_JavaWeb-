<template>
  <div class="news-manage-page">
    <div class="page-header">
      <h1>新闻管理</h1>
      <el-button type="primary" @click="$router.push('/admin/news/create')">
        <el-icon><Plus /></el-icon>
        创建新闻
      </el-button>
    </div>

    <div class="filter-section">
      <el-row :gutter="20">
        <el-col :span="8">
          <el-input
            v-model="searchKeyword"
            placeholder="搜索新闻标题..."
            @keyup.enter="fetchNews"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
        </el-col>
        <el-col :span="6">
          <el-select v-model="selectedStatus" placeholder="选择状态" clearable>
            <el-option label="已发布" :value="1" />
            <el-option label="草稿" :value="0" />
            <el-option label="已下线" :value="2" />
          </el-select>
        </el-col>
        <el-col :span="6">
          <el-select v-model="selectedType" placeholder="选择分类" clearable>
            <el-option
              v-for="type in newsTypes"
              :key="type.tid"
              :label="type.tname"
              :value="type.tid"
            />
          </el-select>
        </el-col>
        <el-col :span="4">
          <el-button @click="fetchNews">搜索</el-button>
        </el-col>
      </el-row>
    </div>

    <div class="table-section">
      <el-table :data="newsList" v-loading="loading" style="width: 100%">
        <el-table-column prop="title" label="标题" min-width="200">
          <template #default="scope">
            <div class="news-title">
              {{ scope.row.title }}
              <el-tag v-if="scope.row.isTop" type="danger" size="small">置顶</el-tag>
              <el-tag v-if="scope.row.isHot" type="warning" size="small">热门</el-tag>
            </div>
          </template>
        </el-table-column>
        
        <el-table-column prop="typeName" label="分类" width="120" />
        
        <el-table-column prop="author" label="作者" width="100" />
        
        <el-table-column prop="pageViews" label="浏览量" width="100" />
        
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        
        <el-table-column prop="createdTime" label="创建时间" width="180" />
        
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <el-button 
              v-if="scope.row.status === 0" 
              size="small" 
              type="success" 
              @click="handleReview(scope.row, 1)"
            >通过</el-button>
            <el-button 
              v-if="scope.row.status === 0" 
              size="small" 
              type="warning" 
              @click="handleReview(scope.row, 2)"
            >拒绝</el-button>
            <el-button size="small" @click="editNews(scope.row)">编辑</el-button>
            <el-button size="small" type="danger" @click="deleteNews(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :total="total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="fetchNews"
          @current-change="fetchNews"
        />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search, Check, Close } from '@element-plus/icons-vue'
import type { News, NewsType } from '@/types'
import { getNewsList, getNewsTypes, deleteNews as deleteNewsApi } from '@/api/modules/news'
import { updateNewsStatus } from '@/api/modules/admin'

const router = useRouter()

const searchKeyword = ref('')
const selectedStatus = ref<number | null>(null)
const selectedType = ref<number | null>(null)
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(20)
const total = ref(0)

const newsList = ref<News[]>([])
const newsTypes = ref<NewsType[]>([])

const getStatusType = (status: number) => {
  switch (status) {
    case 0: return 'info'
    case 1: return 'success'
    case 2: return 'warning'
    default: return 'info'
  }
}

const getStatusText = (status: number) => {
  switch (status) {
    case 0: return '草稿'
    case 1: return '已发布'
    case 2: return '已下线'
    default: return '未知'
  }
}

const fetchNews = async () => {
  loading.value = true
  try {
    const params: any = {
      page: currentPage.value,
      size: pageSize.value,
      type: selectedType.value || undefined,
      keyword: searchKeyword.value,
    }
    const res = await getNewsList(params) as any
    if (res.code === 200) {
      newsList.value = res.data.items || []
      total.value = res.data.total || 0
    }
  } catch (error) {
    ElMessage.error('获取新闻列表失败')
  } finally {
    loading.value = false
  }
}

const fetchNewsTypes = async () => {
  try {
    const res = await getNewsTypes() as any
    if (res.code === 200) {
      newsTypes.value = res.data
    }
  } catch (error) {
    ElMessage.error('获取分类列表失败')
  }
}

const handleReview = async (news: News, status: number) => {
  const action = status === 1 ? '通过' : '拒绝'
  try {
    await ElMessageBox.confirm(`确定要${action}这篇新闻吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: status === 1 ? 'success' : 'warning'
    })
    
    const res = await updateNewsStatus(news.hid, status) as any
    if (res.code === 200) {
      ElMessage.success(`${action}操作成功`)
      fetchNews()
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(`${action}操作失败`)
    }
  }
}

const editNews = (news: News) => {
  router.push(`/admin/news/${news.hid}/edit`)
}

const deleteNews = async (news: News) => {
  try {
    await ElMessageBox.confirm('确定要删除这篇新闻吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const res = await deleteNewsApi(Number(news.hid)) as any
    if (res.code === 200) {
      ElMessage.success('刪除成功')
      fetchNews()
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

onMounted(() => {
  fetchNews()
  fetchNewsTypes()
})
</script>

<style scoped>
.news-manage-page {
  padding: var(--spacing-lg);
  max-width: 1200px;
  margin: 0 auto;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--spacing-lg);
}

.page-header h1 {
  color: var(--text-primary);
  margin: 0;
}

.filter-section {
  background: var(--bg-primary);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-sm);
  padding: var(--spacing-lg);
  margin-bottom: var(--spacing-lg);
}

.table-section {
  background: var(--bg-primary);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-sm);
  overflow: hidden;
}

.news-title {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
}

.pagination {
  padding: var(--spacing-lg);
  text-align: right;
}
</style>
