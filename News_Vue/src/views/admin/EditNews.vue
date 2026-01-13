<template>
  <div class="edit-news-page">
    <div class="page-header">
      <h1>编辑新闻</h1>
      <div class="header-actions">
        <el-button @click="$router.go(-1)">返回</el-button>
        <el-button type="primary" @click="updateNews">保存修改</el-button>
      </div>
    </div>

    <div class="form-section" v-if="!loading">
      <el-form :model="newsForm" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="新闻标题" prop="title">
          <el-input v-model="newsForm.title" placeholder="请输入新闻标题" />
        </el-form-item>

        <el-form-item label="新闻分类" prop="type">
          <el-select v-model="newsForm.type" placeholder="选择分类">
            <el-option
              v-for="type in newsTypes"
              :key="type.tid"
              :label="type.tname"
              :value="type.tid"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="新闻摘要" prop="summary">
          <el-input
            v-model="newsForm.summary"
            type="textarea"
            :rows="3"
            placeholder="请输入新闻摘要"
          />
        </el-form-item>

        <el-form-item label="封面图片">
          <div class="upload-section">
            <el-upload
              class="cover-uploader"
              action="#"
              :show-file-list="false"
              :before-upload="beforeUpload"
              :http-request="uploadCover"
            >
              <img v-if="newsForm.coverImage" :src="newsForm.coverImage" class="cover-image" />
              <div v-else class="upload-placeholder">
                <el-icon class="upload-icon"><Plus /></el-icon>
                <div>上传封面图片</div>
              </div>
            </el-upload>
          </div>
        </el-form-item>

        <el-form-item label="新闻内容" prop="content">
          <div class="editor-container">
            <el-input
              v-model="newsForm.content"
              type="textarea"
              :rows="15"
              placeholder="请输入新闻内容"
            />
          </div>
        </el-form-item>

        <el-form-item label="标签">
          <el-input v-model="newsForm.tags" placeholder="多个标签用逗号分隔" />
        </el-form-item>

        <el-form-item label="状态">
          <el-radio-group v-model="newsForm.status">
            <el-radio :label="0">草稿</el-radio>
            <el-radio :label="1">发布</el-radio>
            <el-radio :label="2">下线</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
    </div>

    <div v-else class="loading-section">
      <el-loading-spinner />
      <p>加载中...</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import type { News, NewsType } from '@/types'

const router = useRouter()
const route = useRoute()
const formRef = ref()

const loading = ref(true)
const newsId = ref<number>(parseInt(route.params.hid as string))

const newsForm = ref({
  title: '',
  type: null as number | null,
  summary: '',
  content: '',
  coverImage: '',
  tags: '',
  status: 1
})

const newsTypes = ref<NewsType[]>([])

const rules = {
  title: [
    { required: true, message: '请输入新闻标题', trigger: 'blur' },
    { min: 5, max: 200, message: '标题长度在 5 到 200 个字符', trigger: 'blur' }
  ],
  type: [
    { required: true, message: '请选择新闻分类', trigger: 'change' }
  ],
  content: [
    { required: true, message: '请输入新闻内容', trigger: 'blur' },
    { min: 10, message: '内容至少 10 个字符', trigger: 'blur' }
  ]
}

const fetchNewsDetail = async () => {
  try {
    loading.value = true
    // 模拟数据，实际应该调用API获取新闻详情
    const newsData: News = {
      hid: newsId.value,
      title: 'Vue 3 Composition API 深度解析',
      summary: '详细介绍Vue 3的新特性和最佳实践',
      content: 'Vue 3带来了许多令人兴奋的新特性...',
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
    
    newsForm.value = {
      title: newsData.title,
      type: newsData.type,
      summary: newsData.summary || '',
      content: newsData.content || '',
      coverImage: newsData.coverImageUrl || '',
      tags: newsData.tags || '',
      status: newsData.status
    }
  } catch (error) {
    ElMessage.error('获取新闻详情失败')
  } finally {
    loading.value = false
  }
}

const fetchNewsTypes = async () => {
  try {
    // 模拟数据
    newsTypes.value = [
      { tid: 1, tname: '技术', sortOrder: 1, status: 1, createdTime: '', updatedTime: '' },
      { tid: 2, tname: '新闻', sortOrder: 2, status: 1, createdTime: '', updatedTime: '' }
    ]
  } catch (error) {
    ElMessage.error('获取分类列表失败')
  }
}

const beforeUpload = (file: File) => {
  const isImage = file.type.startsWith('image/')
  const isLt5M = file.size / 1024 / 1024 < 5

  if (!isImage) {
    ElMessage.error('只能上传图片文件!')
    return false
  }
  if (!isLt5M) {
    ElMessage.error('图片大小不能超过 5MB!')
    return false
  }
  return true
}

const uploadCover = async (options: any) => {
  const file = options.file
  try {
    // 模拟上传，实际应该调用上传API
    const url = URL.createObjectURL(file)
    newsForm.value.coverImage = url
    ElMessage.success('上传成功')
  } catch (error) {
    ElMessage.error('上传失败')
  }
}

const updateNews = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    
    // 调用更新API
    ElMessage.success('新闻更新成功')
    router.push('/admin/news')
  } catch (error) {
    ElMessage.error('更新失败')
  }
}

onMounted(() => {
  fetchNewsDetail()
  fetchNewsTypes()
})
</script>

<style scoped>
.edit-news-page {
  padding: var(--spacing-lg);
  max-width: 1000px;
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

.header-actions {
  display: flex;
  gap: var(--spacing-md);
}

.form-section {
  background: var(--bg-primary);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-sm);
  padding: var(--spacing-lg);
}

.upload-section {
  width: 100%;
}

.cover-uploader {
  border: 1px dashed var(--border-primary);
  border-radius: var(--radius-md);
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: var(--transition-normal);
}

.cover-uploader:hover {
  border-color: var(--primary-color);
}

.cover-image {
  width: 300px;
  height: 200px;
  object-fit: cover;
  display: block;
}

.upload-placeholder {
  width: 300px;
  height: 200px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: var(--text-secondary);
  background: var(--bg-secondary);
}

.upload-icon {
  font-size: 28px;
  margin-bottom: var(--spacing-sm);
}

.editor-container {
  width: 100%;
}

.loading-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: var(--spacing-xl);
  color: var(--text-secondary);
}
</style>
