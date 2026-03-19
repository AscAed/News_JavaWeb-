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
                v-for="tag in (Array.isArray(newsDetail.tags) ? newsDetail.tags : String(newsDetail.tags).split(','))"
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
        <section class="comments-section" id="comments">
          <h3>评论 ({{ totalComments }})</h3>

          <!-- 发表评论 -->
          <div class="comment-form" v-if="isLoggedIn">
            <div v-if="replyingTo" class="reply-indicator">
              <span>正在回复 @{{ replyingTo.author.username }}</span>
              <el-button type="text" size="small" @click="cancelReply">取消回复</el-button>
            </div>
            <el-input
              v-model="newComment"
              type="textarea"
              :rows="3"
              :placeholder="replyingTo ? '发表回复...' : '发表你的评论...'"
              maxlength="500"
              show-word-limit
            />
            <el-button
              type="primary"
              @click="submitComment"
              :loading="submittingComment"
              style="margin-top: 10px"
            >
              {{ replyingTo ? '发表回复' : '发表评论' }}
            </el-button>
          </div>
          <div v-else class="login-prompt">
            <el-button type="primary" @click="$router.push('/login')">登录后评论</el-button>
          </div>

          <!-- 评论列表 -->
          <div class="comments-list">
            <template v-if="comments.length > 0">
              <CommentItem
                v-for="comment in comments"
                :key="comment.id"
                :comment="comment"
                :current-user="userStore.userInfo?.username"
                @like="handleLike"
                @reply="handleReply"
                @delete="handleDelete"
              />
            </template>
            <el-empty v-else description="暂无评论，快来抢沙发吧~" />
          </div>
        </section>
      </el-main>
    </el-container>
  </div>
</template>

<script setup lang="ts">
import {computed, onMounted, ref, nextTick} from 'vue'
import {useRoute, useRouter} from 'vue-router'
import {ArrowLeft, ChatDotRound, Collection, Share, Star, View} from '@element-plus/icons-vue'
import {ElMessage, ElMessageBox} from 'element-plus'
import type {Comment, Headline} from '@/types/headline'
import {getHeadlineById} from '@/api/headline'
import {useUserStore} from '@/stores/user'
import CommentItem from '@/components/CommentItem.vue'
import {getComments, addComment, likeComment as likeCommentApi, deleteComment as deleteCommentApi} from '@/api/modules/interaction'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

// 响应式数据
const loading = ref(false)
const newsDetail = ref<Headline | null>(null)
const comments = ref<Comment[]>([])
const submittingComment = ref(false)
const isLiked = ref(false)
const isFavorited = ref(false)
const totalComments = ref(0)
const newComment = ref('')
const replyingTo = ref<Comment | null>(null)

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

const fetchCommentsList = async () => {
  const hid = Number(route.params.hid)
  if (!hid) return
  try {
    const res = await getComments(hid, { page: 1, page_size: 100 })
    if (res.code === 200) {
      comments.value = res.data.items || []
      totalComments.value = res.data.total || comments.value.length
    }
  } catch (err) {
    console.error('获取评论失败', err)
  }
}

const cancelReply = () => {
  replyingTo.value = null
  newComment.value = ''
}

const submitComment = async () => {
  if (!newComment.value.trim()) {
    ElMessage.warning('请输入评论内容')
    return
  }

  const hid = Number(route.params.hid)
  submittingComment.value = true
  try {
    const res = await addComment({
      headlineId: hid,
      content: newComment.value.trim(),
      parentId: replyingTo.value ? replyingTo.value.id : undefined
    })

    if (res.code === 200) {
      ElMessage.success(replyingTo.value ? '回复成功' : '评论发表成功')
      newComment.value = ''
      replyingTo.value = null
      await fetchCommentsList() // 刷新评论列表
      // 更新顶部统计数据
      if (newsDetail.value) {
        newsDetail.value.commentCount = (newsDetail.value.commentCount || 0) + 1
      }
    } else {
      ElMessage.error(res.message || '评论失败')
    }
  } catch (error) {
    ElMessage.error('评论发表失败')
  } finally {
    submittingComment.value = false
  }
}

const handleLike = async (comment: Comment) => {
  if (!isLoggedIn.value) {
    ElMessage.warning('请先登录')
    return
  }
  try {
    // 这里简单的认为是切换like，实际应用需要记录是否已经点赞过
    const res = await likeCommentApi(comment.id, 'like')
    if (res.code === 200) {
      // 简单地在本地更新数字，或重新拉取全量评论
      comment.like_count = res.data.like_count
      ElMessage.success('操作成功')
    }
  } catch (error) {
    ElMessage.error('点赞失败')
  }
}

const handleReply = (comment: Comment) => {
  if (!isLoggedIn.value) {
    ElMessage.warning('请先登录')
    return
  }
  replyingTo.value = comment
  // 滚动到评论框
  nextTick(() => {
    document.getElementById('comments')?.scrollIntoView({ behavior: 'smooth' })
  })
}

const handleDelete = async (comment: Comment) => {
  try {
    await ElMessageBox.confirm('确定要删除这条评论吗？', '提示', {
      type: 'warning'
    })
    const res = await deleteCommentApi(comment.id)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      await fetchCommentsList() // 刷新评论列表
    } else {
      ElMessage.error(res.message || '删除失败')
    }
  } catch (e) {
    // user cancelled
  }
}

// 生命周期
onMounted(() => {
  fetchNewsDetail()
  if (route.params.hid) {
    fetchCommentsList()
  }
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
