<template>
  <header class="app-header">
    <div class="header-container">
      <!-- 品牌 Logo 区域 -->
      <div class="brand-logo-container hover-lift" @click="router.push('/')">
        <div class="brand-icon-box">
          <span class="brand-icon-character">易</span>
        </div>
        <span class="brand-text">易闻趣事</span>
      </div>

      <!-- 搜索区域 -->
      <div class="search-section">
        <div class="search-box">
          <el-input
            v-model="searchKeyword"
            placeholder="搜索新闻..."
            class="search-input"
            @keyup.enter="handleSearch"
            clearable
          >
            <template #append>
              <el-button @click="handleSearch" :icon="Search" />
            </template>
          </el-input>
        </div>

        <!-- 搜索建议下拉框 -->
        <div v-if="showSuggestions && searchSuggestions.length > 0" class="search-suggestions">
          <div
            v-for="(suggestion, index) in searchSuggestions"
            :key="index"
            class="suggestion-item"
            @click="selectSuggestion(suggestion)"
          >
            <el-icon><Search /></el-icon>
            <span>{{ suggestion }}</span>
          </div>
        </div>
      </div>

      <!-- 用户操作区域 (重新加回) -->
      <div class="user-section">
        <!-- 发布新闻按钮 (仅媒体用户可见) -->
        <el-button
          v-if="isLoggedIn && userInfo?.role_name === 'media'"
          type="primary"
          class="publish-btn hover-lift"
          @click="router.push('/publish')"
        >
          发布新闻
        </el-button>
        
        <!-- 后台管理按钮 (仅管理员可见) -->
        <el-button
          v-if="isLoggedIn && userInfo?.id === 1"
          type="warning"
          plain
          class="admin-btn hover-lift"
          @click="router.push('/admin')"
        >
          <el-icon><Management /></el-icon>
          后台管理
        </el-button>

        <!-- 主题切换按钮 -->
        <el-button
          circle
          class="theme-toggle-btn hover-lift"
          @click="toggleTheme"
          :title="isDark ? '切换到浅色模式' : '切换到深色模式'"
        >
          <el-icon v-if="isDark"><Sunny /></el-icon>
          <el-icon v-else><Moon /></el-icon>
        </el-button>

        <template v-if="isLoggedIn">
          <el-dropdown @command="handleUserCommand">
            <div class="user-info">
              <el-avatar :size="32" :src="userInfo?.avatarUrl">
                <el-icon><User /></el-icon>
              </el-avatar>
              <span class="username">{{ userInfo?.username }}</span>
              <el-icon class="dropdown-icon"><ArrowDown /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">
                  <el-icon><User /></el-icon>
                  个人资料
                </el-dropdown-item>
                <el-dropdown-item command="favorites">
                  <el-icon><Star /></el-icon>
                  我的收藏
                </el-dropdown-item>
                <el-dropdown-item command="settings">
                  <el-icon><Setting /></el-icon>
                  设置
                </el-dropdown-item>
                <el-dropdown-item divided command="logout">
                  <el-icon><SwitchButton /></el-icon>
                  退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </template>
        <template v-else>
          <el-button type="primary" @click="router.push('/login')">登录</el-button>
        </template>
      </div>
    </div>
  </header>
</template>

<script setup lang="ts">
import { computed, onMounted, onUnmounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  ArrowDown,
  Search,
  Setting,
  Star,
  SwitchButton,
  User,
  Sunny,
  Moon,
  Management,
} from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

// 搜索相关状态
const searchKeyword = ref('')
const showSuggestions = ref(false)
const searchSuggestions = ref<string[]>([])

// 计算属性
const isLoggedIn = computed(() => userStore.isLoggedIn)
const userInfo = computed(() => userStore.userInfo)

// 主题切换相关状态和逻辑
const isDark = ref(false)

const initTheme = () => {
  const savedTheme = localStorage.getItem('news-theme')
  if (
    savedTheme === 'dark' ||
    (!savedTheme && window.matchMedia('(prefers-color-scheme: dark)').matches)
  ) {
    isDark.value = true
    document.documentElement.classList.add('dark')
  } else {
    isDark.value = false
    document.documentElement.classList.remove('dark')
  }
}

const toggleTheme = () => {
  isDark.value = !isDark.value
  if (isDark.value) {
    document.documentElement.classList.add('dark')
    localStorage.setItem('news-theme', 'dark')
  } else {
    document.documentElement.classList.remove('dark')
    localStorage.setItem('news-theme', 'light')
  }
}

// 监听系统主题变化
window.matchMedia('(prefers-color-scheme: dark)').addEventListener('change', (e) => {
  if (!localStorage.getItem('news-theme')) {
    isDark.value = e.matches
    if (e.matches) {
      document.documentElement.classList.add('dark')
    } else {
      document.documentElement.classList.remove('dark')
    }
  }
})

// 搜索功能
const handleSearch = () => {
  if (searchKeyword.value.trim()) {
    if (route.path !== '/') {
      const targetQuery = { ...route.query, k: searchKeyword.value.trim() }
      router.push({ path: '/', query: targetQuery })
    } else {
      // 触发搜索事件，通知HomeView组件
      window.dispatchEvent(
        new CustomEvent('search', {
          detail: { keyword: searchKeyword.value.trim() },
        }),
      )
    }
    showSuggestions.value = false
  }
}

// 选择搜索建议
const selectSuggestion = (suggestion: string) => {
  searchKeyword.value = suggestion
  handleSearch()
}

// 用户下拉菜单命令处理
const handleUserCommand = (command: string) => {
  switch (command) {
    case 'profile':
      router.push('/profile')
      break
    case 'favorites':
      router.push('/favorites')
      break
    case 'settings':
      router.push('/settings')
      break
    case 'logout':
      handleLogout()
      break
  }
}

// 退出登录
const handleLogout = async () => {
  try {
    await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })

    await userStore.logout()
    ElMessage.success('退出登录成功')
    router.push('/')
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error('退出登录失败')
    }
  }
}

// 搜索建议功能
const fetchSearchSuggestions = async (keyword: string) => {
  if (keyword.trim().length < 2) {
    searchSuggestions.value = []
    showSuggestions.value = false
    return
  }

  try {
    // 模拟搜索建议
    searchSuggestions.value = [`${keyword} 相关新闻`, `${keyword} 最新动态`, `${keyword} 深度分析`]
    showSuggestions.value = true
  } catch (error) {
    searchSuggestions.value = []
    showSuggestions.value = false
  }
}

// 监听搜索关键词变化
watch(searchKeyword, (newKeyword) => {
  if (newKeyword.trim()) {
    fetchSearchSuggestions(newKeyword)
  } else {
    searchSuggestions.value = []
    showSuggestions.value = false
  }
})

// 监听路由改变同步状态
watch(
  () => route.query,
  (query) => {
    if (query.k) {
      searchKeyword.value = query.k as string
    } else {
      searchKeyword.value = ''
    }
  },
  { immediate: true },
)

// 点击外部关闭搜索建议
const handleClickOutside = (event: MouseEvent) => {
  const target = event.target as HTMLElement
  if (!target.closest('.search-section')) {
    showSuggestions.value = false
  }
}

// 生命周期
onMounted(() => {
  initTheme()
  // 添加全局点击事件监听
  document.addEventListener('click', handleClickOutside)
})

onUnmounted(() => {
  // 移除全局点击事件监听
  document.removeEventListener('click', handleClickOutside)
})
</script>

<style scoped>
.app-header {
  background: var(--bg-primary);
  border-bottom: 1px solid var(--border-primary);
  box-shadow: var(--shadow-sm);
  position: sticky;
  top: 0;
  z-index: 1000;
  backdrop-filter: blur(10px);
}

.header-container {
  max-width: var(--container-2xl);
  margin: 0 auto;
  padding: 0 var(--spacing-lg);
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 72px;
  gap: var(--spacing-xl);
}

/* 搜索区域 */
.search-section {
  flex: 1;
  max-width: 600px;
  position: relative;
}

.search-box {
  width: 100%;
}

.search-input :deep(.el-input__wrapper) {
  border-radius: var(--radius-full);
  padding: 4px 16px;
  background: var(--bg-tertiary);
  box-shadow: none;
  border: 1px solid transparent;
  transition: all var(--transition-normal);
}

.search-input :deep(.el-input__wrapper.is-focus) {
  background: var(--bg-primary);
  box-shadow: 0 1px 6px rgba(32, 33, 36, 0.28);
  border-color: transparent;
}

.search-input :deep(.el-input-group__append) {
  border-radius: 0 var(--radius-full) var(--radius-full) 0;
  background: transparent;
  border: none;
  box-shadow: none;
}

.search-input :deep(input) {
  font-family: var(--font-family-body);
  color: var(--text-primary);
}

.search-suggestions {
  position: absolute;
  top: 100%;
  left: 0;
  right: 0;
  background: var(--bg-primary);
  border: 1px solid var(--border-primary);
  border-top: none;
  border-radius: 0 0 8px 8px;
  box-shadow: var(--shadow-lg);
  max-height: 300px;
  overflow-y: auto;
  z-index: 1001;
  animation: slideDown 0.2s ease-out;
}

.suggestion-item {
  display: flex;
  align-items: center;
  padding: var(--spacing-sm) var(--spacing-md);
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.suggestion-item:hover {
  background-color: var(--bg-light);
}

.suggestion-item .el-icon {
  margin-right: var(--spacing-sm);
  color: var(--text-secondary);
}

.suggestion-item span {
  color: var(--text-primary);
}

.user-section {
  flex-shrink: 0;
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
}

.theme-toggle-btn {
  border: none;
  background: var(--bg-tertiary);
  color: var(--text-secondary);
  font-size: 16px;
  width: 36px;
  height: 36px;
}

.theme-toggle-btn:hover,
.theme-toggle-btn:focus {
  background: var(--border-primary);
  color: var(--primary-color);
}

.user-info {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  cursor: pointer;
  padding: var(--spacing-sm) var(--spacing-md);
  border-radius: var(--radius-lg);
  transition: all 0.3s ease;
  border: 1px solid transparent;
}

.user-info:hover {
  background-color: var(--bg-light);
}

.username {
  font-family: var(--font-family-body);
  font-size: var(--body-size-sm);
  font-weight: 600;
  color: var(--text-primary);
  max-width: 120px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  letter-spacing: 0.01em;
}

.dropdown-icon {
  font-size: 12px;
  color: var(--text-secondary);
  transition: transform 0.2s ease;
}

.user-info:hover .dropdown-icon {
  transform: rotate(180deg);
}

/* 对话框样式 */
.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: var(--spacing-sm);
}

/* 响应式设计 - 增强版本 */
@media (max-width: 1200px) {
  .header-container {
    padding: 0 var(--spacing-lg);
    gap: var(--spacing-lg);
  }

  .search-section {
    max-width: 500px;
  }

  .nav-container {
    padding: 0 var(--spacing-lg);
    gap: var(--spacing-lg);
  }
}

@media (max-width: 1024px) {
  .header-container {
    padding: 0 var(--spacing-md);
    gap: var(--spacing-md);
  }

  .search-section {
    max-width: 400px;
  }
}

@media (max-width: 768px) {
  .header-container {
    padding: 0 var(--spacing-md);
    gap: var(--spacing-md);
    flex-wrap: wrap;
  }

  .search-section {
    max-width: 100%;
    flex: 1;
    order: 2;
  }

  .user-section {
    order: 3;
    flex-shrink: 0;
  }

  .username {
    display: none;
  }
}

@media (max-width: 480px) {
  .header-container {
    padding: 0 var(--spacing-sm);
    gap: var(--spacing-sm);
  }

  .search-section {
    max-width: 100%;
  }

  .user-section .el-button {
    padding: var(--spacing-xs) var(--spacing-xs);
    font-size: 11px;
  }

  .user-section {
    margin-top: var(--spacing-sm);
  }
}
</style>
