<template>
  <div class="dashboard-page">
    <div class="dashboard-header">
      <h1>管理仪表盘</h1>
      <p>欢迎回来，{{ userStore.userInfo?.username }}</p>
    </div>

    <div class="dashboard-stats">
      <div class="stat-card">
        <div class="stat-icon news">
          <el-icon><Document /></el-icon>
        </div>
        <div class="stat-content">
          <h3>{{ stats.totalNews }}</h3>
          <p>新闻总数</p>
        </div>
      </div>

      <div class="stat-card">
        <div class="stat-icon user">
          <el-icon><User /></el-icon>
        </div>
        <div class="stat-content">
          <h3>{{ stats.totalUsers }}</h3>
          <p>用户总数</p>
        </div>
      </div>

      <div class="stat-card">
        <div class="stat-icon governance">
          <el-icon><Monitor /></el-icon>
        </div>
        <div class="stat-content">
          <h3>{{ governanceStats.rateLimitHits }}</h3>
          <p>限流拦截总数</p>
        </div>
        <div class="stat-badge danger">Lua 分布式</div>
      </div>

      <div class="stat-card">
        <div class="stat-icon security">
          <el-icon><Lock /></el-icon>
        </div>
        <div class="stat-content">
          <h3>{{ governanceStats.blacklistedSessions }}</h3>
          <p>已封禁会话</p>
        </div>
        <div class="stat-badge warning">JTI 审计</div>
      </div>
    </div>

    <div class="dashboard-content">
      <div class="content-left">
        <!-- 限流触发日志 - 服务治理核心展现 -->
        <div class="content-section governance-logs">
          <div class="section-header">
            <h2>
              <el-icon><Monitor /></el-icon>
              无侵入式服务治理：高频访问拦截审计
            </h2>
            <div class="header-actions">
              <el-button type="danger" link @click="handleClearLogs">
                <el-icon><Delete /></el-icon> 清空记录
              </el-button>
              <el-button type="primary" link @click="fetchGovernanceData" title="刷新数据" aria-label="刷新数据">
                <el-icon><Refresh /></el-icon>
              </el-button>
            </div>
          </div>
          <el-table :data="rateLimitLogs" style="width: 100%" max-height="400" v-loading="loading">
            <el-table-column label="触发时间" width="180">
              <template #default="scope">
                <span class="timestamp-column">{{ formatTime(scope.row.timestamp) }}</span>
              </template>
            </el-table-column>
            <el-table-column label="来源 IP / 标识" width="180">
              <template #default="scope">
                <div class="ip-ident">
                   <el-tag size="small" type="info" effect="plain">{{ scope.row.ip }}</el-tag>
                   <span v-if="scope.row.userId !== -1" class="user-id">UID: {{ scope.row.userId }}</span>
                </div>
              </template>
            </el-table-column>
            <el-table-column prop="key" label="拦截资源" show-overflow-tooltip>
               <template #default="scope">
                 <code class="resource-code">{{ scope.row.key }}</code>
               </template>
            </el-table-column>
            <el-table-column label="限流阈值" width="120">
              <template #default="scope">
                <span class="policy-tag">{{ scope.row.limit }}次/{{ scope.row.period }}s</span>
              </template>
            </el-table-column>
            <el-table-column label="状态" width="100" align="center">
              <template #default>
                <el-tag type="danger" size="small" effect="dark" round>已拦截</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="100" align="center">
              <template #default="scope">
                <el-tooltip
                  v-if="scope.row.jti"
                  content="一键封禁该会话"
                  placement="top"
                >
                  <el-button 
                    type="danger" 
                    icon="Lock" 
                    circle 
                    size="small"
                    aria-label="一键封禁该会话"
                    @click="handleFastBlock(scope.row.jti)"
                  />
                </el-tooltip>
                <span v-else class="no-action">-</span>
              </template>
            </el-table-column>
          </el-table>
        </div>

        <div class="content-section">
          <div class="section-header">
            <h2>近期发布动态</h2>
            <el-button type="primary" link @click="$router.push('/admin/news')">管理新闻</el-button>
          </div>
          <el-table :data="recentNews" style="width: 100%" v-loading="loading">
            <el-table-column prop="title" label="标题" min-width="200" show-overflow-tooltip />
            <el-table-column prop="author" label="作者" width="100" />
            <el-table-column prop="pageViews" label="浏览" width="80" align="center" />
            <el-table-column prop="status" label="状态" width="100" align="center">
              <template #default="scope">
                <el-tag :type="scope.row.status === 1 ? 'success' : 'info'" size="small">
                  {{ scope.row.status === 1 ? '已发布' : '草稿' }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </div>

      <div class="content-right">
        <!-- 一键干预管控 -->
        <div class="content-section control-panel">
          <div class="section-header">
            <h2>
              <el-icon><Lock /></el-icon>
              一键干预管控展现
            </h2>
          </div>
          <div class="control-form">
            <p class="control-desc">强制切断违规会话 (JWT Blacklist)</p>
            <el-input 
              v-model="targetJti" 
              placeholder="输入 JTI 唯一识别码" 
              class="jti-input"
            >
              <template #prefix>
                <el-icon><Key /></el-icon>
              </template>
            </el-input>
            <el-button 
              type="danger" 
              class="w-full mt-4" 
              @click="handleBlacklistJti"
              :loading="blacklisting"
            >
              撤回会话授权
            </el-button>
            <div class="security-tips">
              <el-alert
                title="基于 JTI 的原子化撤回机制，瞬间切断其会话连续性。"
                type="info"
                :closable="false"
                show-icon
              />
            </div>
          </div>
        </div>

        <div class="content-section trending-section">
          <div class="section-header">
            <h2>舆情热词聚合 (ES)</h2>
            <el-icon class="header-icon"><TrendCharts /></el-icon>
          </div>
          <div class="trending-tags" v-loading="loading">
            <el-tag
              v-for="(item, index) in trendingKeywords"
              :key="index"
              :type="getTagType(index)"
              effect="light"
              round
              class="trending-tag"
              :style="{ fontSize: getFontSize(item.value) }"
            >
              {{ item.name }}
              <span class="tag-count">{{ item.value }}</span>
            </el-tag>
            <el-empty v-if="!trendingKeywords.length" description="暂无舆情数据" :image-size="60" />
          </div>
        </div>

        <div class="content-section status-section">
          <div class="section-header">
            <h2>底层防线状态</h2>
          </div>
          <div class="status-list">
            <div class="status-item">
              <span class="label">Redis 集群连接:</span>
              <el-tag type="success" size="small">Active</el-tag>
            </div>
            <div class="status-item">
              <span class="label">RateLimit 引擎:</span>
              <el-tag type="success" size="small">Lua v2.0</el-tag>
            </div>
            <div class="status-item">
              <span class="label">JWT 审计模式:</span>
              <el-tag type="warning" size="small">实时拦截</el-tag>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  Document, User, Lock, Warning, 
  TrendCharts, Monitor, Refresh,
  Delete, Key
} from '@element-plus/icons-vue'
import type { News } from '@/types'
import { getOverviewStatistics, getTrendingKeywords } from '@/api/modules/statistics'
import { getNewsList } from '@/api/modules/news'
import { 
  getGovernanceStats, getRateLimitLogs, 
  clearRateLimitLogs, addToBlacklist 
} from '@/api/modules/governance'
import dayjs from 'dayjs'
import { getStatisticsOverview, getAdminRecentNews } from '@/api/modules/admin'

const userStore = useUserStore()
const loading = ref(false)
const targetJti = ref('')
const blacklisting = ref(false)

const stats = ref({
  totalNews: 0,
  totalUsers: 0,
  totalViews: 0,
  totalLikes: 0
})

const governanceStats = ref({
  rateLimitHits: 0,
  blacklistedSessions: 0,
  systemStatus: 'Healthy'
})

const recentNews = ref<News[]>([])
const trendingKeywords = ref<{ name: string, value: number }[]>([])
const rateLimitLogs = ref<any[]>([])

// 格式化时间
const formatTime = (timestamp: number) => {
  return dayjs(timestamp).format('YYYY-MM-DD HH:mm:ss')
}

// 获取标签类型
const getTagType = (index: number) => {
  const types: any[] = ['', 'success', 'info', 'warning', 'danger']
  return types[index % 5]
}

// 动态计算字号
const getFontSize = (count: number) => {
  const max = trendingKeywords.value[0]?.value || 1
  const size = 12 + (count / max) * 8
  return `${size}px`
}

const handleFastBlock = async (jti: string) => {
  targetJti.value = jti
  await handleBlacklistJti()
}

const handleBlacklistJti = async () => {
  if (!targetJti.value) {
    ElMessage.warning('请输入 JTI 识别码')
    return
  }

  try {
    await ElMessageBox.confirm('确定要强制吊销该会话吗？被封禁后用户将瞬间失去访问权限。', '安全警告', {
      confirmButtonText: '确定吊销',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    blacklisting.value = true
    const res = await addToBlacklist({ jti: targetJti.value }) as any
    if (res.code === 200) {
      ElMessage.success('会话已成功撤回')
      targetJti.value = ''
      await fetchGovernanceData()
    }
  } catch (error) {
    // Cancelled
  } finally {
    blacklisting.value = false
  }
}

const fetchGovernanceData = async () => {
  try {
    const statsRes = await getGovernanceStats() as any
    if (statsRes.code === 200) {
      governanceStats.value = statsRes.data
    }

    const logsRes = await getRateLimitLogs() as any
    if (logsRes.code === 200) {
      rateLimitLogs.value = (logsRes.data || []).map((item: string) => {
        try {
          return JSON.parse(item)
        } catch (e) {
          return { key: item, timestamp: Date.now(), ip: 'Unknown', limit: '?', period: '?' }
        }
      })
    }
  } catch (error) {
    console.error('Governance data error:', error)
  }
}

const handleClearLogs = async () => {
  try {
    await ElMessageBox.confirm('确定要清空所有的限流审计日志吗？', '操作确认')
    const res = await clearRateLimitLogs() as any
    if (res.code === 200) {
      ElMessage.success('日志已清空')
      rateLimitLogs.value = []
      governanceStats.value.rateLimitHits = 0
    }
  } catch (error) {
    // Cancelled
  }
}

const fetchDashboardData = async () => {
  loading.value = true
  try {
    // 1. 获取统计概览
    const statsRes = await getOverviewStatistics() as any
    if (statsRes.code === 200) {
      const data = statsRes.data
      stats.value = {
        totalNews: data.newsStatistics?.totalNews || 0,
        totalUsers: data.userStatistics?.totalUsers || 0,
        totalViews: data.newsStatistics?.totalViews || 0,
        totalLikes: data.systemStatistics?.totalFavorites || 0
      }
    }

    // 2. 获取最新新闻
    const newsRes = await getNewsList({ page: 1, size: 5 }) as any
    if (newsRes.code === 200) {
      recentNews.value = newsRes.data.records || []
    }

    // 3. 获取热词趋势
    const trendingRes = await getTrendingKeywords(15) as any
    if (trendingRes.code === 200) {
      trendingKeywords.value = trendingRes.data || []
    }

    // 4. 获取治理数据
    await fetchGovernanceData()
  } catch (error) {
    console.error('Fetch dashboard error:', error)
    console.error('Fetch dashboard data error:', error)
    ElMessage.error('获取仪表盘数据失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchDashboardData()
})
</script>

<style scoped>
.dashboard-page {
  padding: 24px;
  max-width: 1600px;
  margin: 0 auto;
  background-color: #f8fafc;
  min-height: 100vh;
}

.dashboard-header {
  margin-bottom: 32px;
  border-left: 4px solid #409eff;
  padding-left: 20px;
}

.dashboard-header h1 {
  color: #1e293b;
  margin: 0 0 8px;
  font-size: 1.85rem;
  font-weight: 800;
  letter-spacing: -0.025em;
}

.dashboard-header p {
  color: #64748b;
  font-size: 0.95rem;
}

/* Stats Cards Section */
.dashboard-stats {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 24px;
  margin-bottom: 32px;
}

.stat-card {
  background: white;
  border-radius: 20px;
  padding: 24px;
  display: flex;
  position: relative;
  align-items: center;
  gap: 20px;
  box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.05), 0 4px 6px -2px rgba(0, 0, 0, 0.02);
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  overflow: hidden;
  border: 1px solid rgba(226, 232, 240, 0.8);
}

.stat-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04);
  border-color: #409eff;
}

.stat-icon {
  width: 64px;
  height: 64px;
  border-radius: 18px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 28px;
  flex-shrink: 0;
}

.stat-icon.news { background: linear-gradient(135deg, #3b82f6 0%, #2dd4bf 100%); }
.stat-icon.user { background: linear-gradient(135deg, #6366f1 0%, #a855f7 100%); }
.stat-icon.governance { background: linear-gradient(135deg, #f43f5e 0%, #fb923c 100%); }
.stat-icon.security { background: linear-gradient(135deg, #ec4899 0%, #8b5cf6 100%); }

.stat-content h3 {
  font-size: 2rem;
  color: #0f172a;
  margin: 0 0 2px;
  font-weight: 800;
}

.stat-content p {
  color: #64748b;
  margin: 0;
  font-size: 0.875rem;
  font-weight: 500;
}

.stat-badge {
  position: absolute;
  top: 12px;
  right: 12px;
  font-size: 10px;
  padding: 2px 8px;
  border-radius: 10px;
  font-weight: 600;
  text-transform: uppercase;
}
.stat-badge.danger { background: #fee2e2; color: #ef4444; }
.stat-badge.warning { background: #fef3c7; color: #d97706; }

/* Layout Blocks */
.dashboard-content {
  display: flex;
  gap: 28px;
}

.content-left {
  flex: 3;
  display: flex;
  flex-direction: column;
  gap: 28px;
}

.content-right {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 28px;
}

.content-section {
  background: white;
  border-radius: 20px;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.05);
  padding: 24px;
  border: 1px solid #f1f5f9;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.section-header h2 {
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
}

.status-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.status-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 0.9rem;
}

.status-item .label {
  color: var(--text-secondary);
}

@media (max-width: 1200px) {
  .dashboard-stats {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .dashboard-content {
    flex-direction: column;
  }
}

@media (max-width: 768px) {
  .dashboard-stats {
    grid-template-columns: 1fr;
  }
}
</style>
