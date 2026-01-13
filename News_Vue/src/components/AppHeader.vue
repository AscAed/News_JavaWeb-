<template>
  <header class="app-header">
    <div class="header-container">
      <!-- Logo区域 -->
      <div class="logo-section">
        <router-link to="/" class="logo">
          <h1>新闻头条</h1>
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

      <!-- 分类筛选区域 -->
      <div class="category-section">
        <el-select
          v-model="selectedCategory"
          placeholder="选择分类"
          class="category-select"
          @change="handleCategoryChange"
          clearable
        >
          <el-option
            v-for="category in categories"
            :key="category.tid"
            :label="category.tname"
            :value="category.tid"
          />
        </el-select>
      </div>

      <!-- 用户操作区域 -->
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
          <el-button @click="showLoginDialog = true" type="primary">登录</el-button>
          <el-button @click="showRegisterDialog = true">注册</el-button>
        </template>
      </div>
    </div>

    <!-- 登录对话框 -->
    <el-dialog
      v-model="showLoginDialog"
      title="用户登录"
      width="400px"
      :before-close="handleLoginDialogClose"
    >
      <el-form
        ref="loginFormRef"
        :model="loginForm"
        :rules="loginRules"
        label-width="80px"
      >
        <el-form-item label="手机号" prop="phone">
          <el-input
            v-model="loginForm.phone"
            placeholder="请输入手机号"
            :prefix-icon="Phone"
          />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="请输入密码"
            :prefix-icon="Lock"
            show-password
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showLoginDialog = false">取消</el-button>
          <el-button type="primary" @click="handleLogin" :loading="loginLoading">
            登录
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 注册对话框 -->
    <el-dialog
      v-model="showRegisterDialog"
      title="用户注册"
      width="400px"
      :before-close="handleRegisterDialogClose"
    >
      <el-form
        ref="registerFormRef"
        :model="registerForm"
        :rules="registerRules"
        label-width="80px"
      >
        <el-form-item label="用户名" prop="username">
          <el-input
            v-model="registerForm.username"
            placeholder="请输入用户名"
            :prefix-icon="User"
          />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input
            v-model="registerForm.phone"
            placeholder="请输入手机号"
            :prefix-icon="Phone"
          />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input
            v-model="registerForm.password"
            type="password"
            placeholder="请输入密码"
            :prefix-icon="Lock"
            show-password
          />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input
            v-model="registerForm.confirmPassword"
            type="password"
            placeholder="请再次输入密码"
            :prefix-icon="Lock"
            show-password
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showRegisterDialog = false">取消</el-button>
          <el-button type="primary" @click="handleRegister" :loading="registerLoading">
            注册
          </el-button>
        </span>
      </template>
    </el-dialog>
  </header>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { useNewsStore } from '@/stores/news'
import { ElMessage, ElMessageBox, type FormInstance } from 'element-plus'
import {
  Search,
  User,
  Phone,
  Lock,
  ArrowDown,
  Star,
  Setting,
  SwitchButton
} from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()
const newsStore = useNewsStore()

// 搜索相关状态
const searchKeyword = ref('')
const showSuggestions = ref(false)
const searchSuggestions = ref<string[]>([])

// 分类相关状态
const selectedCategory = ref<number | null>(null)

// 用户相关状态
const showLoginDialog = ref(false)
const showRegisterDialog = ref(false)
const loginLoading = ref(false)
const registerLoading = ref(false)

// 表单引用
const loginFormRef = ref<FormInstance>()
const registerFormRef = ref<FormInstance>()

// 登录表单
const loginForm = ref({
  phone: '',
  password: ''
})

// 注册表单
const registerForm = ref({
  username: '',
  phone: '',
  password: '',
  confirmPassword: ''
})

// 表单验证规则
const loginRules = {
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ]
}

const registerRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 2, max: 20, message: '用户名长度在2到20个字符', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    {
      validator: (rule: any, value: string, callback: Function) => {
        if (value !== registerForm.value.password) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

// 计算属性
const isLoggedIn = computed(() => userStore.isLoggedIn)
const userInfo = computed(() => userStore.userInfo)
const categories = computed(() => newsStore.newsTypes)

// 搜索功能
const handleSearch = () => {
  if (searchKeyword.value.trim()) {
    // 触发搜索事件，通知HomeView组件
    window.dispatchEvent(new CustomEvent('search', {
      detail: { keyword: searchKeyword.value.trim() }
    }))
    showSuggestions.value = false
  }
}

// 选择搜索建议
const selectSuggestion = (suggestion: string) => {
  searchKeyword.value = suggestion
  handleSearch()
}

// 分类切换
const handleCategoryChange = (categoryId: number | null) => {
  // 触发分类切换事件，通知HomeView组件
  window.dispatchEvent(new CustomEvent('typeChange', {
    detail: { type: categoryId }
  }))
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

// 登录处理
const handleLogin = async () => {
  if (!loginFormRef.value) return
  
  try {
    await loginFormRef.value.validate()
    loginLoading.value = true
    
    await userStore.login({
      phone: loginForm.value.phone,
      password: loginForm.value.password
    })
    
    ElMessage.success('登录成功')
    showLoginDialog.value = false
    loginForm.value = { phone: '', password: '' }
  } catch (error: any) {
    ElMessage.error(error.message || '登录失败')
  } finally {
    loginLoading.value = false
  }
}

// 注册处理
const handleRegister = async () => {
  if (!registerFormRef.value) return
  
  try {
    await registerFormRef.value.validate()
    registerLoading.value = true
    
    await userStore.register({
      username: registerForm.value.username,
      phone: registerForm.value.phone,
      password: registerForm.value.password
    })
    
    ElMessage.success('注册成功，请登录')
    showRegisterDialog.value = false
    registerForm.value = { username: '', phone: '', password: '', confirmPassword: '' }
  } catch (error: any) {
    ElMessage.error(error.message || '注册失败')
  } finally {
    registerLoading.value = false
  }
}

// 退出登录
const handleLogout = async () => {
  try {
    await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
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

// 对话框关闭处理
const handleLoginDialogClose = () => {
  loginForm.value = { phone: '', password: '' }
  if (loginFormRef.value) {
    loginFormRef.value.clearValidate()
  }
}

const handleRegisterDialogClose = () => {
  registerForm.value = { username: '', phone: '', password: '', confirmPassword: '' }
  if (registerFormRef.value) {
    registerFormRef.value.clearValidate()
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
    // 这里可以调用搜索建议API
    // const suggestions = await getSearchSuggestions(keyword)
    // searchSuggestions.value = suggestions
    
    // 模拟搜索建议
    searchSuggestions.value = [
      `${keyword} 相关新闻`,
      `${keyword} 最新动态`,
      `${keyword} 深度分析`
    ]
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

/* 分类筛选区域 */
.category-section {
  flex-shrink: 0;
}

.category-select {
  width: 140px;
  font-family: var(--font-family-ui);
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
  
  .logo h1 {
    font-size: var(--headline-size-sm);
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
  
  .category-section {
    flex-shrink: 0;
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
  
  .category-section {
    display: none;
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