<template>
  <div class="app-layout">
    <!-- 主要内容区域 -->
    <main :class="{ 'has-global-sidebar': showSidebar }" class="main-wrapper">
      <!-- 最左侧边栏 -->
      <aside v-if="showSidebar" class="global-sidebar">
        <div class="sidebar-inner">
          <slot name="sidebar"></slot>
        </div>
      </aside>

      <!-- 右侧主要内容区 -->
      <div class="main-body">
        <!-- 顶部导航栏 (现移动到右侧主内容区上方) -->
        <AppHeader/>

        <!-- 吸顶辅助导航 (如分类栏) -->
        <div class="sub-header-wrapper">
          <slot name="sub-header"></slot>
        </div>
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
            <div class="content-wrapper">
              <!-- 主要内容 -->
              <div :class="mainContentClass" class="main-content-area">
                <slot></slot>
              </div>
            </div>
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
  showSidebar: true,
  showBreadcrumb: true,
  pageTitle: '',
  pageSubtitle: '',
  contentLayout: 'default',
  sidebarPosition: 'left',
  sidebarWidth: 'narrow'
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
  display: block;
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

/* 全局侧边栏布局 */
.main-wrapper {
  flex: 1;
  display: flex;
  width: 100%;
}

.global-sidebar {
  width: 250px;
  flex-shrink: 0;
  background: var(--bg-primary);
  position: sticky;
  top: 0;
  height: 100vh;
  overflow-y: auto;
  z-index: 10;
  border-right: none; /* 移除全局边框，改用伪元素 */
}

.global-sidebar::after {
  content: '';
  position: absolute;
  top: 72px; /* 从Logo下方开始显示线 */
  right: 0;
  bottom: 0;
  width: 1px;
  background-color: var(--border-primary);
}

.sidebar-inner {
  padding: 0;
}

.main-body {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
}

.sub-header-wrapper {
  position: sticky;
  top: 72px;
  z-index: 900;
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .main-wrapper.has-global-sidebar {
    flex-direction: column;
  }

  .global-sidebar {
    width: 100%;
    position: static;
    height: auto;
    border-right: none;
    border-bottom: 1px solid var(--border-primary);
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
.global-sidebar::-webkit-scrollbar {
  width: 6px;
}

.global-sidebar::-webkit-scrollbar-track {
  background: var(--bg-secondary);
  border-radius: var(--radius-full);
}

.global-sidebar::-webkit-scrollbar-thumb {
  background: var(--border-secondary);
  border-radius: var(--radius-full);
}

.global-sidebar::-webkit-scrollbar-thumb:hover {
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
