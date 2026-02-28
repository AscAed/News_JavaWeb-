<template>
  <header class="app-header">
    <div class="header-container">
      <!-- Logo区域 -->
      <div class="logo-section">
        <router-link to="/" class="logo">
          <h1>易闻趣事</h1>
        </router-link>
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

    <!-- 纯文本水平导航区 -->
    <div class="nav-section">
      <div class="nav-container">
        <button
          :class="{ active: selectedCategory === null }"
          class="nav-item"
          @click="handleCategoryChange(null)"
        >
          全部分类
        </button>
        <button
          :class="{ active: selectedCategory === 'domestic' }"
          class="nav-item"
          @click="handleCategoryChange('domestic')"
        >
          国内新闻
        </button>
        <button
          :class="{ active: selectedCategory === 'international' }"
          class="nav-item"
          @click="handleCategoryChange('international')"
        >
          国际新闻
        </button>
        <button
          v-for="category in categories"
          :key="category.tid"
          :class="{ active: selectedCategory === category.tid }"
          class="nav-item"
          @click="handleCategoryChange(category.tid)"
        >
          {{ category.tname }}
        </button>
      </div>
    </div>
  </header>
</template>

<script setup lang="ts">
import {computed, onMounted, onUnmounted, ref, watch} from 'vue'
import {useRoute, useRouter} from 'vue-router'
import {useUserStore} from '@/stores/user'
import {useNewsStore} from '@/stores/news'
import {ElMessage, ElMessageBox} from 'element-plus'
import {ArrowDown, Search, Setting, Star, SwitchButton, User,} from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const newsStore = useNewsStore()

// 搜索相关状态
const searchKeyword = ref('')
const showSuggestions = ref(false)
const searchSuggestions = ref<string[]>([])

// 分类相关状态
const selectedCategory = ref<number | string | null>(null)

// 用户相关状态
// 移除对话框相关状态，现在使用专门的登录/注册页面

// 计算属性
const isLoggedIn = computed(() => userStore.isLoggedIn)
const userInfo = computed(() => userStore.userInfo)
const categories = computed(() => newsStore.newsTypes)

// 搜索功能
const handleSearch = () => {
  if (searchKeyword.value.trim()) {
    if (route.path !== '/') {
      const targetQuery: Record<string, any> = {...route.query, k: searchKeyword.value.trim()}
      if (selectedCategory.value) targetQuery.t = selectedCategory.value as any
      router.push({path: '/', query: targetQuery})
    } else {
      // 触发搜索事件，通知HomeView组件
      window.dispatchEvent(
        new CustomEvent('search', {
          detail: {keyword: searchKeyword.value.trim()},
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

// 分类切换
const handleCategoryChange = (categoryId: number | string | null) => {
  selectedCategory.value = categoryId
  if (route.path !== '/') {
    const targetQuery: any = {...route.query}
    if (categoryId) {
      targetQuery.t = categoryId
    } else {
      delete targetQuery.t
    }
    router.push({path: '/', query: targetQuery})
  } else {
    // 触发分类切换事件，通知HomeView组件
    window.dispatchEvent(
      new CustomEvent('typeChange', {
        detail: {type: categoryId},
      }),
    )
  }
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

// 移除 handleLogin 和 handleRegister，由跳转后的页面处理

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

// 移除对话框关闭处理

// 搜索建议功能
const fetchSearchSuggestions = async (keyword: string) => {
  if (keyword.trim().length < 2) {
    searchSuggestions.value = []
    showSuggestions.value = false
    return
  }

  try {
    // 这里可以调用搜索建议API
    // const suggestions = await getSearchSuggestions(keyword)
    // searchSuggestions.value = suggestions

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
watch(() => route.query, (query) => {
  if (query.t) {
    selectedCategory.value = isNaN(Number(query.t)) ? query.t as string : Number(query.t)
  }
  if (query.k) {
    searchKeyword.value = query.k as string
  } else {
    searchKeyword.value = ''
  }
}, {immediate: true})

// 点击外部关闭搜索建议
const handleClickOutside = (event: MouseEvent) => {
  const target = event.target as HTMLElement
  if (!target.closest('.search-section')) {
    showSuggestions.value = false
  }
}

// 生命周期
onMounted(async () => {
  // 获取分类数据
  try {
    await newsStore.fetchNewsTypes()

    // 设置默认分类
    if (newsStore.defaultCategoryId) {
      selectedCategory.value = newsStore.defaultCategoryId

      // 触发初始分类切换事件，通知HomeView组件
      window.dispatchEvent(
        new CustomEvent('typeChange', {
          detail: {type: newsStore.defaultCategoryId},
        }),
      )
    }
  } catch (error) {
    console.error('获取分类数据失败:', error)
  }

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

/* Logo区域 */
.logo-section {
  flex-shrink: 0;
}

.logo {
  text-decoration: none;
  color: var(--primary-color);
}

.logo h1 {
  font-family: var(--font-family-heading);
  font-size: var(--headline-size-md);
  font-weight: 700;
  margin: 0;
  transition: color 0.3s ease;
  letter-spacing: -0.02em;
}

.logo:hover h1 {
  color: var(--primary-hover);
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

.search-input {
  width: 100%;
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

/* 水平导航区 */
.nav-section {
  border-top: 1px solid var(--border-primary);
  background-color: var(--bg-primary);
  overflow-x: auto;
  white-space: nowrap;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05); /* 更轻的阴影，贴近实体线 */
  /* 隐藏滚动条但保留滚动能力 */
  -ms-overflow-style: none;
  scrollbar-width: none;
}

.nav-section::-webkit-scrollbar {
  display: none;
}

.nav-container {
  max-width: var(--container-2xl);
  margin: 0 auto;
  padding: 0 var(--spacing-lg);
  display: flex;
  align-items: center;
  gap: var(--spacing-xl);
  height: 48px;
}

.nav-item {
  font-family: var(--font-family-ui);
  font-size: var(--body-size-md);
  color: var(--text-secondary);
  background: transparent;
  border: none;
  padding: 0;
  cursor: pointer;
  position: relative;
  height: 100%;
  display: flex;
  align-items: center;
  transition: color 0.2s ease;
}

.nav-item:hover,
.nav-item.active {
  color: var(--primary-color);
  font-weight: 500;
}

.nav-item::before {
  display: none;
}

.nav-item.active::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 3px;
  background-color: var(--primary-color);
  border-radius: 3px 3px 0 0;
}

/* 用户操作区域 */
.user-section {
  flex-shrink: 0;
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
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

  .nav-container {
    padding: 0 var(--spacing-md);
    gap: var(--spacing-md);
  }

  .logo h1 {
    font-size: var(--headline-size-xs);
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

  .logo h1 {
    font-size: var(--headline-size-xs);
  }

  .logo-section {
    order: 1;
    flex-shrink: 0;
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
