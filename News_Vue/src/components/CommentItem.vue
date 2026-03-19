<template>
  <div class="comment-item">
    <div class="comment-avatar">
      <el-avatar :src="comment.author.avatarUrl" :alt="comment.author.username">
        {{ comment.author.username?.charAt(0) }}
      </el-avatar>
    </div>
    <div class="comment-content">
      <div class="comment-header">
        <span class="username">{{ comment.author.username }}</span>
        <span class="comment-time">{{ formatTime(comment.created_time) }}</span>
      </div>
      <div class="comment-text">
        <span v-if="replyToUser" class="reply-target">回复 @{{ replyToUser }}: </span>
        {{ comment.content }}
      </div>
      <div class="comment-actions">
        <el-button
          type="text"
          size="small"
          @click="$emit('like', comment)"
        >
          <el-icon><Star /></el-icon>
          {{ comment.like_count || 0 }}
        </el-button>
        <el-button
          type="text"
          size="small"
          @click="$emit('reply', comment)"
        >
          回复
        </el-button>
        <el-button
          v-if="isAuthor"
          type="text"
          size="small"
          class="delete-btn"
          @click="$emit('delete', comment)"
        >
          删除
        </el-button>
      </div>

      <!-- 嵌套子评论 -->
      <div class="nested-comments" v-if="comment.replies && comment.replies.length > 0">
        <CommentItem
          v-for="reply in comment.replies"
          :key="reply.id"
          :comment="reply"
          :reply-to-user="comment.author.username"
          :current-user="currentUser"
          @like="$emit('like', $event)"
          @reply="$emit('reply', $event)"
          @delete="$emit('delete', $event)"
        />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { Star } from '@element-plus/icons-vue'
import type { Comment } from '@/types/headline'

const props = defineProps<{
  comment: Comment
  replyToUser?: string
  currentUser?: string
}>()

defineEmits<{
  (e: 'like', comment: Comment): void
  (e: 'reply', comment: Comment): void
  (e: 'delete', comment: Comment): void
}>()

const isAuthor = computed(() => {
  return props.currentUser && props.currentUser === props.comment.author.username
})

const formatTime = (time?: string) => {
  if (!time) return ''
  return new Date(time).toLocaleString()
}
</script>

<style scoped>
.comment-item {
  display: flex;
  gap: 15px;
  padding: 15px 0;
  border-bottom: 1px solid #eee;
}

.nested-comments .comment-item {
  border-bottom: none;
  border-top: 1px solid #f5f5f5;
  padding-bottom: 0;
  margin-top: 10px;
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
  margin-bottom: 5px;
}

.username {
  font-weight: bold;
  color: #333;
  font-size: 14px;
}

.comment-time {
  font-size: 12px;
  color: #999;
}

.comment-text {
  font-size: 14px;
  line-height: 1.6;
  color: #333;
  margin-bottom: 8px;
}

.reply-target {
  color: #409eff;
  margin-right: 5px;
}

.comment-actions {
  display: flex;
  gap: 15px;
}

.delete-btn {
  color: #f56c6c;
}
</style>
