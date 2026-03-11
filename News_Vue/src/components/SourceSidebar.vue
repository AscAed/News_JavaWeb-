<template>
  <aside class="source-sidebar">
    <div class="sidebar-section">
      <h3 class="sidebar-title">
        <el-icon>
          <Menu />
        </el-icon>
        新闻源
      </h3>
      <div class="source-list">
        <div :class="{ active: !selectedSource }" class="source-item" @click="goToSource('')">
          <div class="source-abbr">
            <el-icon><Document /></el-icon>
          </div>
          <span class="source-text">全部新闻</span>
        </div>
        <div
          v-for="source in sources"
          :key="source.id"
          :class="{ active: selectedSource === String(source.id) }"
          class="source-item"
          @click="goToSource(source.id)"
        >
          <div class="source-abbr">{{ source.name.charAt(0) }}</div>
          <span class="source-text">{{ source.name }}</span>
        </div>
      </div>
    </div>
  </aside>
</template>

<script lang="ts" setup>
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Document, Link, Menu } from '@element-plus/icons-vue'
import request from '@/api/request'
import { useNewsStore } from '@/stores/news'

const router = useRouter()
const route = useRoute()
const newsStore = useNewsStore()

const sources = ref<any[]>([])
const selectedSource = ref<string | null>(null)

const fetchSources = async () => {
  try {
    const res: any = await request.get('/rss-subscriptions/list')
    if (res.code === 200) {
      sources.value = res.data || []

      // Initialize newsStore currentSource if URL has source_id on mount
      if (route.query.source_id && route.query.source_type === 'rss') {
        const sourceObj = sources.value.find((s) => String(s.id) === String(route.query.source_id))
        if (sourceObj) {
          newsStore.setCurrentSource({ ...sourceObj, type: 'rss' })
        }
      } else if (!newsStore.currentSource) {
        newsStore.setCurrentSource({ id: -1, name: '原创', type: 'original' })
      }
    }
  } catch (error) {
    console.error('Failed to fetch sources:', error)
  }
}

const goToSource = (sourceId: string | number) => {
  selectedSource.value = sourceId ? String(sourceId) : null
  const query = { ...route.query }

  const sourceObj = sourceId ? sources.value.find((s) => String(s.id) === String(sourceId)) : null
  const storeSource = sourceObj
    ? { ...sourceObj, type: 'rss' }
    : { id: -1, name: '原创', type: 'original' }
  newsStore.setCurrentSource(storeSource)

  if (sourceId) {
    query.source_id = String(sourceId)
    query.source_type = 'rss'
  } else {
    delete query.source_id
    delete query.source_type
  }

  window.dispatchEvent(new CustomEvent('sourceChange', { detail: { source: storeSource } }))

  router.push({
    path: '/',
    query,
  })
}

onMounted(() => {
  fetchSources()
  if (route.query.source_id) {
    selectedSource.value = String(route.query.source_id)
  }
})
</script>

<style scoped>
.source-sidebar {
  display: flex;
  flex-direction: column;
  padding: var(--spacing-md) 0;
  background: var(--bg-glass);
  backdrop-filter: var(--blur-md);
  -webkit-backdrop-filter: var(--blur-md);
  border: 1px solid var(--border-primary);
  border-radius: var(--radius-xl);
  box-shadow: var(--shadow-lg);
  width: 64px;
  transition: width 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  overflow: hidden;
  white-space: nowrap;
}

.source-sidebar:hover {
  width: 200px; /* Expand on hover */
}

.sidebar-section {
  background: transparent;
  box-shadow: none;
  border: none;
  padding: 0 8px; /* Matched to fit the 28px abbr icon cleanly */
  margin-bottom: 0;
}

.sidebar-title {
  display: none; /* Hide title for a cleaner floating widget look */
}

.source-list {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-xs);
}

.source-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px;
  border-radius: var(--radius-full);
  cursor: pointer;
  color: var(--text-secondary);
  font-size: var(--text-sm);
  font-weight: 500;
  transition: all var(--transition-fast);
}

.source-item:hover {
  background: var(--bg-tertiary);
  color: var(--text-primary);
}

.source-item.active {
  background: var(--primary-light);
  color: var(--primary-dark);
  font-weight: 600;
}

/* 缩写圆圈 */
.source-abbr {
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  background: var(--bg-secondary);
  color: var(--text-muted);
  font-weight: 600;
  font-size: 14px;
  flex-shrink: 0;
  transition: all var(--transition-fast);
}

.source-item:hover .source-abbr {
  color: var(--text-primary);
  background: var(--bg-primary);
}

.source-item.active .source-abbr {
  background: var(--primary-color);
  color: white;
}

/* 文字部分设置透明度过渡，在 hover sidebar 时显示 */
.source-text {
  opacity: 0;
  transition: opacity 0.2s ease-out;
  pointer-events: none;
}

.source-sidebar:hover .source-text {
  opacity: 1;
  pointer-events: auto;
}
</style>
