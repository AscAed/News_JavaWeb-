<template>
  <div class="publish-container">
    <div class="publish-card">
      <div class="page-header">
        <h1 class="page-title">发布新闻</h1>
        <div class="header-actions">
          <el-button @click="handleCancel">取消</el-button>
          <el-button type="primary" :loading="submitting" @click="handlePublish">发布新闻</el-button>
        </div>
      </div>

      <el-form
        ref="formRef"
        :model="publishForm"
        :rules="rules"
        label-position="top"
        class="publish-form"
      >
        <div class="form-row">
          <el-form-item label="新闻标题" prop="title" class="flex-2">
            <el-input
              v-model="publishForm.title"
              placeholder="请输入吸引人的标题 (5-200字)"
              maxlength="200"
              show-word-limit
            />
          </el-form-item>

          <el-form-item label="新闻分类" prop="type" class="flex-1">
            <el-select v-model="publishForm.type" placeholder="请选择分类" class="w-full">
              <el-option
                v-for="item in categories"
                :key="item.tid"
                :label="item.tname"
                :value="item.tid"
              />
            </el-select>
          </el-form-item>
        </div>

        <el-form-item label="内容摘要" prop="summary">
          <el-input
            v-model="publishForm.summary"
            type="textarea"
            :rows="3"
            placeholder="请输入新闻摘要，让读者快速了解核心内容"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>

        <el-form-item label="封面图片" prop="coverImage">
          <el-upload
            class="cover-uploader"
            action="#"
            :show-file-list="false"
            :before-upload="beforeUpload"
            :http-request="uploadCover"
          >
            <div v-if="publishForm.coverImage" class="cover-preview">
              <img :src="publishForm.coverImage" alt="封面预览" />
              <div class="cover-action">
                <el-icon><Edit /></el-icon>
                <span>更换封面</span>
              </div>
            </div>
            <div v-else class="upload-placeholder">
              <el-icon class="upload-icon"><Plus /></el-icon>
              <span>上传封面图片</span>
              <p class="upload-tip">建议比例 16:9, 大小不超过 5MB</p>
            </div>
          </el-upload>
        </el-form-item>

        <el-form-item label="正文内容" prop="article">
          <div class="editor-wrapper">
            <Toolbar
              style="border-bottom: 1px solid var(--border-primary)"
              :editor="editorRef"
              :defaultConfig="toolbarConfig"
              :mode="mode"
            />
            <Editor
              style="height: 500px; overflow-y: hidden"
              v-model="publishForm.article"
              :defaultConfig="editorConfig"
              :mode="mode"
              @onCreated="handleCreated"
            />
          </div>
        </el-form-item>

        <el-form-item label="新闻标签" prop="tags">
          <el-input
            v-model="publishForm.tags"
            placeholder="添加标签，多个标签用英文逗号分隔"
          />
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, shallowRef, onBeforeUnmount, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Edit } from '@element-plus/icons-vue'
import { Editor, Toolbar } from '@wangeditor/editor-for-vue'
import '@wangeditor/editor/dist/css/style.css'
import { getNewsTypes, publishNews, uploadFile } from '@/api/modules/news'
import type { NewsType } from '@/types'

const router = useRouter()
const formRef = ref()
const submitting = ref(false)
const categories = ref<NewsType[]>([])

// 编辑器配置
const editorRef = shallowRef()
const mode = 'default'
const toolbarConfig = {}
const editorConfig = {
  placeholder: '开始编写新闻内容...',
  MENU_CONF: {
    uploadImage: {
      async customUpload(file: File, insertFn: any) {
        try {
          const res = await uploadFile(file)
          if (res.code === 200) {
            const url = typeof res.data === 'string' ? res.data : (res.data as any).url || (res.data as any).accessUrl
            if (url) {
              insertFn(url, file.name, url)
            } else {
              ElMessage.error('上传返回格式错误')
            }
          }
        } catch (error) {
          ElMessage.error('图片上传失败')
        }
      }
    }
  }
}

const publishForm = reactive({
  title: '',
  type: null as number | null,
  summary: '',
  article: '',
  coverImage: '',
  tags: ''
})

const rules = {
  title: [
    { required: true, message: '请输入新闻标题', trigger: 'blur' },
    { min: 5, max: 200, message: '标题长度在 5 到 200 个字符', trigger: 'blur' }
  ],
  type: [
    { required: true, message: '请选择新闻分类', trigger: 'change' }
  ],
  article: [
    { required: true, message: '请输入新闻内容', trigger: 'blur' },
    {
      validator: (rule: any, value: string, callback: any) => {
        if (value.replace(/<[^>]+>/g, '').trim().length < 10) {
          callback(new Error('内容至少包含 10 个有效字符'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

const handleCreated = (editor: any) => {
  editorRef.value = editor
}

const fetchCategories = async () => {
  try {
    const res = await getNewsTypes({ sourceType: 'local' })
    if (res.code === 200) {
      categories.value = res.data
    }
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
  try {
    const res = await uploadFile(options.file)
    if (res.code === 200) {
      const url = typeof res.data === 'string' ? res.data : (res.data as any).url || (res.data as any).accessUrl
      publishForm.coverImage = url
      ElMessage.success('封面上传成功')
    }
  } catch (error) {
    ElMessage.error('封面上传失败')
  }
}

const handlePublish = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    submitting.value = true
    
    const submitData = {
      ...publishForm,
      type: publishForm.type as number // 确保是数字
    }
    
    const res = await publishNews(submitData)
    if (res.code === 200) {
      ElMessage.success('新闻发布成功，正在跳转...')
      setTimeout(() => {
        router.push('/')
      }, 1500)
    }
  } catch (error: any) {
    if (error?.message) {
      ElMessage.error('发布失败: ' + error.message)
    }
  } finally {
    submitting.value = false
  }
}

const handleCancel = () => {
  ElMessageBox.confirm('确定要放弃当前的编辑内容吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    router.back()
  }).catch(() => {})
}

onMounted(() => {
  fetchCategories()
})

onBeforeUnmount(() => {
  const editor = editorRef.value
  if (editor == null) return
  editor.destroy()
})
</script>

<style scoped>
.publish-container {
  padding: var(--spacing-xl) var(--spacing-lg);
  background-color: var(--bg-secondary);
  min-height: calc(100vh - 72px);
}

.publish-card {
  max-width: 1100px;
  margin: 0 auto;
  background-color: var(--bg-primary);
  border-radius: var(--radius-xl);
  box-shadow: var(--shadow-md);
  padding: var(--spacing-xl);
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--spacing-xl);
  border-bottom: 2px solid var(--border-primary);
  padding-bottom: var(--spacing-md);
}

.page-title {
  font-family: var(--font-family-header);
  font-size: var(--heading-size-md);
  color: var(--text-primary);
  margin: 0;
  position: relative;
}

.page-title::after {
  content: '';
  position: absolute;
  bottom: -10px;
  left: 0;
  width: 60px;
  height: 4px;
  background: var(--primary-color);
  border-radius: var(--radius-full);
}

.publish-form {
  margin-top: var(--spacing-lg);
}

.form-row {
  display: flex;
  gap: var(--spacing-lg);
}

.flex-2 { flex: 2; }
.flex-1 { flex: 1; }
.w-full { width: 100%; }

.cover-uploader {
  border: 1px dashed var(--border-primary);
  border-radius: var(--radius-lg);
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: all 0.3s ease;
  background: var(--bg-tertiary);
  width: fit-content;
}

.cover-uploader:hover {
  border-color: var(--primary-color);
  background: var(--bg-light);
}

.upload-placeholder {
  width: 320px;
  height: 180px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: var(--text-secondary);
}

.upload-icon {
  font-size: 32px;
  margin-bottom: var(--spacing-sm);
}

.upload-tip {
  font-size: 12px;
  margin-top: var(--spacing-xs);
  opacity: 0.7;
}

.cover-preview {
  width: 320px;
  height: 180px;
  position: relative;
}

.cover-preview img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.cover-action {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: white;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.cover-preview:hover .cover-action {
  opacity: 1;
}

.editor-wrapper {
  border: 1px solid var(--border-primary);
  border-radius: var(--radius-md);
  overflow: hidden;
  margin-bottom: var(--spacing-md);
}

:deep(.w-e-text-container) {
  background-color: var(--bg-primary) !important;
  color: var(--text-primary) !important;
}

:deep(.w-e-toolbar) {
  background-color: var(--bg-tertiary) !important;
  border-bottom: 1px solid var(--border-primary) !important;
}

:deep(.w-e-text-placeholder) {
  color: var(--text-secondary) !important;
}

@media (max-width: 768px) {
  .form-row {
    flex-direction: column;
    gap: 0;
  }
  
  .upload-placeholder, .cover-preview {
    width: 100%;
    aspect-ratio: 16/9;
  }
}
</style>
