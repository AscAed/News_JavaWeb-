<template>
  <div class="comment-item" :class="{ 'is-child': depth > 0 }">
    <div class="comment-main">
      <div class="comment-avatar">
        <el-avatar :size="depth === 0 ? 40 : 32" :src="comment.userInfo?.avatarUrl">
          {{ comment.userInfo?.username?.charAt(0) }}
        </el-avatar>
      </div>
      <div class="comment-content">
        <div class="comment-header">
          <span class="username">{{ comment.userInfo?.username }}</span>
          <span v-if="depth > 0" class="reply-label">回复了</span>
          <span class="comment-time">{{ formatTime(comment.createdAt) }}</span>
        </div>
        <div class="comment-text" v-text="comment.content"></div>
        <div class="comment-actions">
          <el-button type="text" size="small" @click="$emit('like', comment.id)">
            <el-icon><Star /></el-icon>
            {{ comment.likeCount || 0 }}
          </el-button>
          <el-button type="text" size="small" @click="$emit('reply', comment)"> 回复 </el-button>
        </div>
      </div>
    </div>

    <!-- 递归渲染子评论 -->
    <div v-if="comment.children && comment.children.length > 0" class="comment-children">
      <CommentItem
        v-for="child in comment.children"
        :key="child.id"
        :comment="child"
        :depth="depth + 1"
        @reply="(c) => $emit('reply', c)"
        @like="(id) => $emit('like', id)"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { Star } from '@element-plus/icons-vue'
import type { Comment } from '@/types/headline'

const props = defineProps<{
  comment: Comment
  depth: number
}>()

defineEmits(['reply', 'like'])

const formatTime = (time?: string) => {
  if (!time) return ''
  return new Date(time).toLocaleString()
}
</script>

<style scoped>
.comment-item {
  margin-top: 15px;
  transition: all 0.3s ease;
}

.is-child {
  margin-left: 40px;
  border-left: 2px solid #f0f0f0;
  padding-left: 15px;
}

.comment-main {
  display: flex;
  gap: 12px;
}

.comment-avatar {
  flex-shrink: 0;
}

.comment-content {
  flex: 1;
  border-bottom: 1px solid #f8f8f8;
  padding-bottom: 10px;
}

.comment-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 5px;
}

.username {
  font-weight: 600;
  font-size: 14px;
  color: #409eff;
}

.reply-label {
  font-size: 12px;
  color: #999;
}

.comment-time {
  font-size: 12px;
  color: #999;
  margin-left: auto;
}

.comment-text {
  font-size: 14px;
  line-height: 1.6;
  color: #444;
  margin: 8px 0;
  white-space: pre-wrap;
  word-break: break-all;
}

.comment-actions {
  display: flex;
  gap: 15px;
}

.comment-children {
  margin-top: 5px;
}

/* 针对深层嵌套的视觉优化 */
:deep(.is-child .is-child .is-child) {
  margin-left: 20px;
}
</style>
