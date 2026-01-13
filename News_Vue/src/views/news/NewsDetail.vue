<template>
  <div class="news-detail">
    <el-container v-loading="loading">
      <el-header>
        <el-button @click="goBack" type="text">
          <el-icon><ArrowLeft /></el-icon>
          返回
        </el-button>
      </el-header>
      
      <el-main v-if="newsDetail">
        <article class="news-article">
          <header class="article-header">
            <h1>{{ newsDetail.title }}</h1>
            <div class="article-meta">
              <span class="author">作者：{{ newsDetail.author }}</span>
              <span class="publish-time">发布时间：{{ formatTime(newsDetail.publishedTime) }}</span>
              <span class="category">分类：{{ newsDetail.typeName }}</span>
            </div>
            <div class="article-stats">
              <el-icon><View /></el-icon>
              <span>{{ newsDetail.pageViews }} 阅读</span>
              <el-icon><Star /></el-icon>
              <span>{{ newsDetail.likeCount }} 点赞</span>
              <el-icon><ChatDotRound /></el-icon>
              <span>{{ newsDetail.commentCount }} 评论</span>
            </div>
          </header>
          
          <div class="article-cover" v-if="newsDetail.coverImage">
            <img :src="newsDetail.coverImage" :alt="newsDetail.title" />
          </div>
          
          <div class="article-content" v-html="newsDetail.content"></div>
          
          <footer class="article-footer">
            <div class="article-tags" v-if="newsDetail.tags">
              <el-tag 
                v-for="tag in newsDetail.tags.split(',')" 
                :key="tag"
                size="small"
                style="margin-right: 8px"
              >
                {{ tag }}
              </el-tag>
            </div>
            
            <div class="article-actions">
              <el-button @click="toggleLike" :type="isLiked ? 'primary' : 'default'">
                <el-icon><Star /></el-icon>
                {{ isLiked ? '已点赞' : '点赞' }} ({{ newsDetail.likeCount }})
              </el-button>
              <el-button @click="toggleFavorite" :type="isFavorited ? 'primary' : 'default'">
                <el-icon><Collection /></el-icon>
                {{ isFavorited ? '已收藏' : '收藏' }}
              </el-button>
              <el-button @click="shareNews">
                <el-icon><Share /></el-icon>
                分享
              </el-button>
            </div>
          </footer>
        </article>
        
        <!-- 评论区 -->
        <section class="comments-section">
          <h3>评论 ({{ comments.length }})</h3>
          
          <!-- 发表评论 -->
          <div class="comment-form" v-if="isLoggedIn">
            <el-input
              v-model="newComment"
              type="textarea"
              :rows="3"
              placeholder="发表你的评论..."
              maxlength="500"
              show-word-limit
            />
            <el-button 
              type="primary" 
              @click="submitComment"
              :loading="submittingComment"
              style="margin-top: 10px"
            >
              发表评论
            </el-button>
          </div>
          <div v-else class="login-prompt">
            <el-button type="primary" @click="$router.push('/login')">登录后评论</el-button>
          </div>
          
          <!-- 评论列表 -->
          <div class="comments-list">
            <div 
              v-for="comment in comments" 
              :key="comment.id"
              class="comment-item"
            >
              <div class="comment-avatar">
                <el-avatar :src="comment.userAvatar" :alt="comment.username">
                  {{ comment.username?.charAt(0) }}
                </el-avatar>
              </div>
              <div class="comment-content">
                <div class="comment-header">
                  <span class="username">{{ comment.username }}</span>
                  <span class="comment-time">{{ formatTime(comment.createdTime) }}</span>
                </div>
                <div class="comment-text">{{ comment.content }}</div>
                <div class="comment-actions">
                  <el-button 
                    type="text" 
                    size="small"
                    @click="likeComment(comment.id)"
                    :disabled="!isLoggedIn"
                  >
                    <el-icon><Star /></el-icon>
                    {{ comment.likeCount }}
                  </el-button>
                  <el-button 
                    type="text" 
                    size="small"
                    @click="replyComment(comment)"
                    :disabled="!isLoggedIn"
                  >
                    回复
                  </el-button>
                </div>
              </div>
            </div>
          </div>
        </section>
      </el-main>
    </el-container>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ArrowLeft, View, Star, ChatDotRound, Collection, Share } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { Headline, Comment } from '@/types/headline'
import { getHeadlineById } from '@/api/headline'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

// 响应式数据
const loading = ref(false)
const newsDetail = ref<Headline | null>(null)
const comments = ref<Comment[]>([])
const newComment = ref('')
const submittingComment = ref(false)
const isLiked = ref(false)
const isFavorited = ref(false)

// 计算属性
const isLoggedIn = computed(() => userStore.isLoggedIn)

// 方法
const fetchNewsDetail = async () => {
  const hid = Number(route.params.hid)
  if (!hid) {
    ElMessage.error('新闻ID无效')
    return
  }
  
  loading.value = true
  try {
    const response = await getHeadlineById(hid)
    if (response.code === 200) {
      newsDetail.value = response.data
    } else {
      ElMessage.error(response.message || '获取新闻详情失败')
    }
  } catch (error) {
    console.error('获取新闻详情失败:', error)
    ElMessage.error('获取新闻详情失败')
  } finally {
    loading.value = false
  }
}

const goBack = () => {
  router.go(-1)
}

const formatTime = (time?: string) => {
  if (!time) return ''
  return new Date(time).toLocaleString()
}

const toggleLike = () => {
  if (!isLoggedIn.value) {
    ElMessage.warning('请先登录')
    return
  }
  
  // TODO: 实现点赞API调用
  isLiked.value = !isLiked.value
  if (newsDetail.value && newsDetail.value.likeCount !== undefined) {
    newsDetail.value.likeCount += isLiked.value ? 1 : -1
  }
  ElMessage.success(isLiked.value ? '点赞成功' : '取消点赞')
}

const toggleFavorite = () => {
  if (!isLoggedIn.value) {
    ElMessage.warning('请先登录')
    return
  }
  
  // TODO: 实现收藏API调用
  isFavorited.value = !isFavorited.value
  ElMessage.success(isFavorited.value ? '收藏成功' : '取消收藏')
}

const shareNews = () => {
  const url = window.location.href
  if (navigator.share) {
    navigator.share({
      title: newsDetail.value?.title,
      text: newsDetail.value?.summary,
      url: url
    })
  } else {
    navigator.clipboard.writeText(url)
    ElMessage.success('链接已复制到剪贴板')
  }
}

const submitComment = async () => {
  if (!newComment.value.trim()) {
    ElMessage.warning('请输入评论内容')
    return
  }
  
  submittingComment.value = true
  try {
    // TODO: 实现评论API调用
    await new Promise(resolve => setTimeout(resolve, 1000)) // 模拟API调用
    
    comments.value.unshift({
      id: Date.now(),
      newsId: Number(route.params.hid),
      userId: userStore.userInfo?.id || 0,
      username: userStore.userInfo?.username || '匿名用户',
      userAvatar: userStore.userInfo?.avatarUrl,
      content: newComment.value,
      likeCount: 0,
      createdTime: new Date().toISOString()
    })
    
    newComment.value = ''
    ElMessage.success('评论发表成功')
  } catch (error) {
    ElMessage.error('评论发表失败')
  } finally {
    submittingComment.value = false
  }
}

const likeComment = (commentId: number) => {
  // TODO: 实现评论点赞API调用
  const comment = comments.value.find(c => c.id === commentId)
  if (comment) {
    comment.likeCount += 1
  }
}

const replyComment = (comment: any) => {
  newComment.value = `@${comment.username} `
  // TODO: 聚焦到评论输入框
}

// 生命周期
onMounted(() => {
  fetchNewsDetail()
})
</script>

<style scoped>
.news-detail {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.el-header {
  height: 60px;
  display: flex;
  align-items: center;
}

.news-article {
  background: #fff;
  border-radius: 8px;
  padding: 30px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  margin-bottom: 30px;
}

.article-header h1 {
  font-size: 28px;
  font-weight: bold;
  line-height: 1.4;
  margin: 0 0 20px 0;
  color: #333;
}

.article-meta {
  display: flex;
  gap: 20px;
  margin-bottom: 15px;
  font-size: 14px;
  color: #666;
}

.article-stats {
  display: flex;
  gap: 20px;
  font-size: 14px;
  color: #666;
  margin-bottom: 20px;
}

.article-stats .el-icon {
  margin-right: 4px;
}

.article-cover {
  margin-bottom: 30px;
}

.article-cover img {
  width: 100%;
  max-height: 400px;
  object-fit: cover;
  border-radius: 8px;
}

.article-content {
  font-size: 16px;
  line-height: 1.8;
  color: #333;
  margin-bottom: 30px;
}

.article-content :deep(img) {
  max-width: 100%;
  height: auto;
  border-radius: 4px;
}

.article-footer {
  border-top: 1px solid #eee;
  padding-top: 20px;
}

.article-tags {
  margin-bottom: 20px;
}

.article-actions {
  display: flex;
  gap: 15px;
}

.comments-section {
  background: #fff;
  border-radius: 8px;
  padding: 30px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}

.comments-section h3 {
  margin: 0 0 20px 0;
  font-size: 20px;
  color: #333;
}

.comment-form {
  margin-bottom: 30px;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 8px;
}

.login-prompt {
  text-align: center;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 8px;
  margin-bottom: 30px;
}

.comments-list {
  margin-top: 20px;
}

.comment-item {
  display: flex;
  gap: 15px;
  padding: 20px 0;
  border-bottom: 1px solid #eee;
}

.comment-item:last-child {
  border-bottom: none;
}

.comment-avatar {
  flex-shrink: 0;
}

.comment-content {
  flex: 1;
}

.comment-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.username {
  font-weight: bold;
  color: #333;
}

.comment-time {
  font-size: 12px;
  color: #999;
}

.comment-text {
  font-size: 14px;
  line-height: 1.6;
  color: #333;
  margin-bottom: 10px;
}

.comment-actions {
  display: flex;
  gap: 15px;
}
</style>
