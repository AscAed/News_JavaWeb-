<template>
  <div class="app-layout">
    <!-- 顶部导航栏 -->
    <AppHeader />

    <!-- 主要内容区域 -->
    <main class="main-content" :class="{ 'with-sidebar': showSidebar }">


    <!-- 页面标题区域 -->
      <div v-if="pageTitle" class="page-header">
        <div class="container">
          <div class="page-header-content">
            <div class="page-title-section">
              <h1 class="page-title">{{ pageTitle }}</h1>
              <p v-if="pageSubtitle" class="page-subtitle">{{ pageSubtitle }}</p>
            </div>
            <div v-if="$slots.pageActions" class="page-actions">
              <slot name="pageActions"></slot>
            </div>
          </div>
        </div>
      </div>

      <!-- 内容容器 -->
      <div class="content-container">
        <div class="container">
          <div class="content-wrapper" :class="contentWrapperClass">
            <!-- 主要内容 -->
            <div class="main-content-area" :class="mainContentClass">
              <slot></slot>
            </div>

            <!-- 侧边栏 -->
            <aside v-if="showSidebar" class="sidebar" :class="sidebarClass">
              <div class="sidebar-content">
                <slot name="sidebar"></slot>
              </div>
            </aside>
          </div>
        </div>
      </div>
    </main>

    <!-- 页脚 -->
    <AppFooter />
  </div>
</template>

<script setup lang="ts">
import {computed} from 'vue'
import {useRoute} from 'vue-router'
import AppHeader from './AppHeader.vue'
import AppFooter from './AppFooter.vue'

interface Props {
  showSidebar?: boolean
  showBreadcrumb?: boolean
  pageTitle?: string
  pageSubtitle?: string
  contentLayout?: 'default' | 'wide' | 'narrow' | 'centered'
  sidebarPosition?: 'right' | 'left'
  sidebarWidth?: 'narrow' | 'medium' | 'wide'
}

const props = withDefaults(defineProps<Props>(), {
  showSidebar: false,
  showBreadcrumb: true,
  pageTitle: '',
  pageSubtitle: '',
  contentLayout: 'default',
  sidebarPosition: 'right',
  sidebarWidth: 'medium'
})

const route = useRoute()

// 面包屑导航
const breadcrumbItems = computed(() => {
  const items = [
    { title: '首页', path: '/' }
  ]

  const pathSegments = route.path.split('/').filter(Boolean)
  let currentPath = ''

  pathSegments.forEach((segment, index) => {
    currentPath += `/${segment}`
    const title = getBreadcrumbTitle(segment, currentPath)
    if (title && index < pathSegments.length - 1) {
      items.push({ title, path: currentPath })
    } else if (title && index === pathSegments.length - 1) {
      items.push({ title, path: currentPath })
    }
  })

  return items
})

// 根据路径获取面包屑标题
const getBreadcrumbTitle = (segment: string, path: string): string => {
  const titleMap: Record<string, string> = {
    'news': '新闻',
    'videos': '视频',
    'favorites': '收藏',
    'profile': '个人中心',
    'admin': '管理后台',
    'login': '登录',
    'register': '注册',
    'search': '搜索',
    'trending': '热点',
    'live': '直播'
  }

  return titleMap[segment] || segment
}

// 内容区域样式类
const contentWrapperClass = computed(() => {
  const classes = []

  if (props.showSidebar) {
    classes.push('has-sidebar')
    classes.push(`sidebar-${props.sidebarPosition}`)
    classes.push(`sidebar-${props.sidebarWidth}`)
  }

  return classes.join(' ')
})

const mainContentClass = computed(() => {
  const classes = []

  if (props.showSidebar) {
    classes.push('with-sidebar-content')
  }

  switch (props.contentLayout) {
    case 'wide':
      classes.push('content-wide')
      break
    case 'narrow':
      classes.push('content-narrow')
      break
    case 'centered':
      classes.push('content-centered')
      break
    default:
      classes.push('content-default')
  }

  return classes.join(' ')
})

const sidebarClass = computed(() => {
  const classes = ['sidebar-panel']

  classes.push(`sidebar-${props.sidebarPosition}`)
  classes.push(`sidebar-${props.sidebarWidth}`)

  return classes.join(' ')
})
</script>

<style scoped>
@import '@/assets/design-system.css';

.app-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background: var(--bg-secondary);
}



/* 页面标题 */
.page-header {
  background: var(--bg-primary);
  border-bottom: 1px solid var(--border-primary);
  padding: var(--spacing-xl) 0;
}

.page-header-content {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: var(--spacing-xl);
}

.page-title-section {
  flex: 1;
}

.page-title {
  font-size: var(--text-3xl);
  font-weight: 700;
  color: var(--text-primary);
  line-height: var(--leading-tight);
  margin: 0 0 var(--spacing-sm) 0;
}

.page-subtitle {
  font-size: var(--text-lg);
  color: var(--text-secondary);
  line-height: var(--leading-normal);
  margin: 0;
}

.page-actions {
  flex-shrink: 0;
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
}

/* 内容容器 */
.content-container {
  flex: 1;
  padding: var(--spacing-xl) 0;
}

.content-wrapper {
  display: grid;
  gap: var(--spacing-xl);
}

/* 默认布局 */
.content-wrapper:not(.has-sidebar) {
  grid-template-columns: 1fr;
}

/* 有侧边栏布局 */
.content-wrapper.has-sidebar {
  grid-template-columns: 1fr;
}

.content-wrapper.sidebar-right.sidebar-medium {
  grid-template-columns: 1fr 320px;
}

.content-wrapper.sidebar-left.sidebar-medium {
  grid-template-columns: 320px 1fr;
}

.content-wrapper.sidebar-right.sidebar-wide {
  grid-template-columns: 1fr 400px;
}

.content-wrapper.sidebar-left.sidebar-wide {
  grid-template-columns: 400px 1fr;
}

.content-wrapper.sidebar-right.sidebar-narrow {
  grid-template-columns: 1fr 280px;
}

.content-wrapper.sidebar-left.sidebar-narrow {
  grid-template-columns: 280px 1fr;
}

/* 主内容区域 */
.main-content-area {
  min-height: 400px;
}

.main-content-area.content-default {
  max-width: none;
}

.main-content-area.content-wide {
  max-width: none;
}

.main-content-area.content-narrow {
  max-width: 800px;
  margin: 0 auto;
}

.main-content-area.content-centered {
  max-width: 1200px;
  margin: 0 auto;
}

.main-content-area.with-sidebar-content {
  /* 有侧边栏时的主内容样式 */
  padding-right: var(--spacing-lg);
}

/* 侧边栏 */
.sidebar {
  position: sticky;
  top: calc(72px + 56px + 20px); /* header height + nav height + padding */
  height: fit-content;
  max-height: calc(100vh - 72px - 56px - 40px);
  overflow-y: auto;
}

.sidebar-content {
  background: var(--bg-primary);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-md);
  padding: var(--spacing-lg);
}

/* 侧边栏位置 */
.sidebar.sidebar-right {
  justify-self: end;
}

.sidebar.sidebar-left {
  justify-self: start;
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .content-wrapper.sidebar-right.sidebar-medium,
  .content-wrapper.sidebar-left.sidebar-medium,
  .content-wrapper.sidebar-right.sidebar-wide,
  .content-wrapper.sidebar-left.sidebar-wide,
  .content-wrapper.sidebar-right.sidebar-narrow,
  .content-wrapper.sidebar-left.sidebar-narrow {
    grid-template-columns: 1fr;
    gap: var(--spacing-lg);
  }

  .sidebar {
    position: static;
    max-height: none;
    order: -1;
  }

  .page-header-content {
    flex-direction: column;
    align-items: flex-start;
    gap: var(--spacing-lg);
  }

  .page-actions {
    width: 100%;
    justify-content: flex-start;
  }
}

@media (max-width: 768px) {
  .container {
    padding: 0 var(--spacing-md);
  }

  .page-title {
    font-size: var(--text-2xl);
  }

  .page-subtitle {
    font-size: var(--text-base);
  }

  .content-container {
    padding: var(--spacing-lg) 0;
  }

  .sidebar-content {
    padding: var(--spacing-md);
  }
}

/* 滚动条样式 */
.sidebar::-webkit-scrollbar {
  width: 6px;
}

.sidebar::-webkit-scrollbar-track {
  background: var(--bg-secondary);
  border-radius: var(--radius-full);
}

.sidebar::-webkit-scrollbar-thumb {
  background: var(--border-secondary);
  border-radius: var(--radius-full);
}

.sidebar::-webkit-scrollbar-thumb:hover {
  background: var(--border-primary);
}

/* 内容区域动画 */
.main-content-area {
  animation: fadeInUp var(--transition-normal) ease-out;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 加载状态 */
.main-content-area.loading {
  opacity: 0.6;
  pointer-events: none;
}

/* 空状态样式 */
.main-content-area.empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 300px;
  color: var(--text-muted);
}

.main-content-area.empty .el-icon {
  font-size: 48px;
  margin-bottom: var(--spacing-md);
}
</style>
