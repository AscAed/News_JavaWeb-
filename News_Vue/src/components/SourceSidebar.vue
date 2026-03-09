<template>
  <aside class="source-sidebar">
    <div class="sidebar-section">
      <h3 class="sidebar-title">
        <el-icon>
          <Menu/>
        </el-icon>
        新闻源
      </h3>
      <div class="source-list">
        <div :class="{ active: !selectedSource }" class="source-item" @click="goToSource('')">
          <el-icon>
            <Document/>
          </el-icon>
          全部新闻
        </div>
        <div
          v-for="source in sources"
          :key="source.id"
          :class="{ active: selectedSource === String(source.id) }"
          class="source-item"
          @click="goToSource(source.id)"
        >
          <el-icon>
            <Link/>
          </el-icon>
          {{ source.name }}
        </div>
      </div>
    </div>
  </aside>
</template>

<script lang="ts" setup>
import {onMounted, ref} from 'vue'
import {useRoute, useRouter} from 'vue-router'
import {Document, Link, Menu} from '@element-plus/icons-vue'
import request from '@/api/request'

const router = useRouter()
const route = useRoute()

const sources = ref<any[]>([])
const selectedSource = ref<string | null>(null)

const fetchSources = async () => {
  try {
    const res = await request.get('/api/v1/rss-subscriptions/list')
    if (res.data && res.data.code === 200) {
      sources.value = res.data.data || []
    }
  } catch (error) {
    console.error('Failed to fetch sources:', error)
  }
}

const goToSource = (sourceId: string | number) => {
  selectedSource.value = sourceId ? String(sourceId) : null
  const query = {...route.query}
  if (sourceId) {
    query.source_id = String(sourceId)
    query.source_type = 'rss'
  } else {
    delete query.source_id
    delete query.source_type
  }
  router.push({
    path: '/',
    query
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
  gap: 20px;
}

.sidebar-section {
  background: var(--bg-primary, #ffffff);
  border-radius: var(--radius-lg, 8px);
  box-shadow: var(--shadow-md, 0 4px 6px -1px rgba(0, 0, 0, 0.1));
  padding: 20px;
}

.sidebar-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 16px;
  border-bottom: 1px solid var(--border-primary, #ebeef5);
  padding-bottom: 12px;
  color: var(--text-primary, #303133);
}

.sidebar-title .el-icon {
  color: var(--primary-color, #409eff);
}

.source-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.source-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 12px;
  border-radius: 6px;
  cursor: pointer;
  color: var(--text-secondary, #606266);
  transition: all 0.3s;
}

.source-item:hover, .source-item.active {
  background: rgba(64, 158, 255, 0.08);
  color: var(--primary-color, #409eff);
  font-weight: 500;
}
</style>
