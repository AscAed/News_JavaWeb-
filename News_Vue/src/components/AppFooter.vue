<template>
  <footer class="app-footer">
    <!-- 主要页脚内容 -->
    <div class="footer-main">
      <div class="container">
        <div class="footer-content">
          <!-- 公司信息 -->
          <div class="footer-section company-info">
            <div class="footer-brand">
              <div class="brand-logo">
                <el-icon><Document /></el-icon>
              </div>
              <div class="brand-text">
                <h3 class="brand-name">新闻头条</h3>
                <p class="brand-description">实时热点 · 深度报道 · 专业资讯</p>
              </div>
            </div>
            <p class="company-description">
              新闻头条致力于为用户提供及时、准确、全面的新闻资讯服务。我们汇聚了全球各地的优质新闻内容，通过智能推荐算法，为每位用户呈现最感兴趣的新闻报道。
            </p>
            <div class="social-links">
              <a href="#" class="social-link" title="微信">
                <el-icon><ChatDotRound /></el-icon>
              </a>
              <a href="#" class="social-link" title="微博">
                <el-icon><Share /></el-icon>
              </a>
              <a href="#" class="social-link" title="Twitter">
                <el-icon><Position /></el-icon>
              </a>
              <a href="#" class="social-link" title="Facebook">
                <el-icon><Connection /></el-icon>
              </a>
            </div>
          </div>

          <!-- 新闻分类 -->
          <div class="footer-section">
            <h4 class="footer-title">新闻分类</h4>
            <ul class="footer-links">
              <li><router-link to="/news?category=politics">时政要闻</router-link></li>
              <li><router-link to="/news?category=finance">财经资讯</router-link></li>
              <li><router-link to="/news?category=tech">科技前沿</router-link></li>
              <li><router-link to="/news?category=sports">体育竞技</router-link></li>
              <li><router-link to="/news?category=entertainment">娱乐八卦</router-link></li>
              <li><router-link to="/news?category=education">教育文化</router-link></li>
              <li><router-link to="/news?category=health">健康生活</router-link></li>
              <li><router-link to="/news?category=international">国际新闻</router-link></li>
            </ul>
          </div>

          <!-- 服务支持 -->
          <div class="footer-section">
            <h4 class="footer-title">服务支持</h4>
            <ul class="footer-links">
              <li><router-link to="/help">帮助中心</router-link></li>
              <li><router-link to="/feedback">意见反馈</router-link></li>
              <li><router-link to="/report">举报中心</router-link></li>
              <li><router-link to="/api">API文档</router-link></li>
              <li><router-link to="/mobile">移动应用</router-link></li>
              <li><router-link to="/rss">RSS订阅</router-link></li>
              <li><router-link to="/newsletter">邮件订阅</router-link></li>
              <li><router-link to="/bookmark">收藏本站</router-link></li>
            </ul>
          </div>

          <!-- 关于我们 -->
          <div class="footer-section">
            <h4 class="footer-title">关于我们</h4>
            <ul class="footer-links">
              <li><router-link to="/about">公司简介</router-link></li>
              <li><router-link to="/team">团队介绍</router-link></li>
              <li><router-link to="/contact">联系我们</router-link></li>
              <li><router-link to="/careers">加入我们</router-link></li>
              <li><router-link to="/partners">合作伙伴</router-link></li>
              <li><router-link to="/investor">投资者关系</router-link></li>
              <li><router-link to="/press">媒体资源</router-link></li>
              <li><router-link to="/blog">官方博客</router-link></li>
            </ul>
          </div>

          <!-- 法律信息 -->
          <div class="footer-section">
            <h4 class="footer-title">法律信息</h4>
            <ul class="footer-links">
              <li><router-link to="/terms">服务条款</router-link></li>
              <li><router-link to="/privacy">隐私政策</router-link></li>
              <li><router-link to="/copyright">版权声明</router-link></li>
              <li><router-link to="/disclaimer">免责声明</router-link></li>
              <li><router-link to="/advertising">广告服务</router-link></li>
              <li><router-link to="/sitemap">网站地图</router-link></li>
              <li><router-link to="/compliance">合规信息</router-link></li>
              <li><router-link to="/ethics">道德准则</router-link></li>
            </ul>
          </div>
        </div>
      </div>
    </div>

    <!-- 底部版权信息 -->
    <div class="footer-bottom">
      <div class="container">
        <div class="footer-bottom-content">
          <div class="copyright">
            <p>&copy; 2025 新闻头条. All rights reserved.</p>
            <p class="icp-info">
              <a href="https://beian.miit.gov.cn/" target="_blank" rel="noopener">
                京ICP备12345678号-1
              </a>
              <span>|</span>
              <a href="http://www.beian.gov.cn/" target="_blank" rel="noopener">
                京公网安备11010802012345号
              </a>
            </p>
          </div>
          
          <div class="footer-stats">
            <div class="stat-item">
              <span class="stat-number">{{ formatNumber(totalNews) }}</span>
              <span class="stat-label">新闻资讯</span>
            </div>
            <div class="stat-item">
              <span class="stat-number">{{ formatNumber(totalUsers) }}</span>
              <span class="stat-label">注册用户</span>
            </div>
            <div class="stat-item">
              <span class="stat-number">{{ formatNumber(dailyViews) }}</span>
              <span class="stat-label">日访问量</span>
            </div>
          </div>

          <div class="footer-links-bottom">
            <a href="#" class="footer-link">友情链接</a>
            <a href="#" class="footer-link">合作伙伴</a>
            <a href="#" class="footer-link">媒体报道</a>
            <a href="#" class="footer-link">商务合作</a>
          </div>
        </div>
      </div>
    </div>
  </footer>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { 
  Document, ChatDotRound, Share, Position, Connection 
} from '@element-plus/icons-vue'

// 响应式数据
const totalNews = ref(125890)
const totalUsers = ref(3456789)
const dailyViews = ref(12345678)

// 方法
const formatNumber = (num: number): string => {
  if (num >= 100000000) {
    return (num / 100000000).toFixed(1) + '亿'
  } else if (num >= 10000) {
    return (num / 10000).toFixed(1) + '万'
  }
  return num.toString()
}

// 生命周期
onMounted(() => {
  // 模拟数据更新
  setInterval(() => {
    dailyViews.value += Math.floor(Math.random() * 100)
  }, 5000)
})
</script>

<style scoped>
@import '@/assets/design-system.css';

.app-footer {
  background: var(--bg-primary);
  border-top: 1px solid var(--border-primary);
  color: var(--text-secondary);
  margin-top: auto;
}

/* 主要页脚内容 */
.footer-main {
  background: var(--bg-secondary);
  padding: var(--spacing-2xl) 0;
}

.footer-content {
  display: grid;
  grid-template-columns: 2fr 1fr 1fr 1fr 1fr;
  gap: var(--spacing-2xl);
  align-items: start;
}

/* 页脚区块 */
.footer-section {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-lg);
}

.footer-title {
  font-size: var(--text-lg);
  font-weight: 600;
  color: var(--text-primary);
  margin: 0 0 var(--spacing-md) 0;
}

/* 公司信息 */
.company-info {
  max-width: 320px;
}

.footer-brand {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  margin-bottom: var(--spacing-lg);
}

.brand-logo {
  width: 48px;
  height: 48px;
  background: linear-gradient(135deg, var(--primary-color), var(--primary-light));
  border-radius: var(--radius-lg);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 20px;
}

.brand-text {
  flex: 1;
}

.brand-name {
  font-size: var(--text-xl);
  font-weight: 700;
  color: var(--text-primary);
  margin: 0 0 var(--spacing-xs) 0;
  line-height: var(--leading-tight);
}

.brand-description {
  font-size: var(--text-sm);
  color: var(--text-muted);
  margin: 0;
  line-height: var(--leading-tight);
}

.company-description {
  font-size: var(--text-sm);
  line-height: var(--leading-relaxed);
  color: var(--text-secondary);
  margin-bottom: var(--spacing-lg);
}

.social-links {
  display: flex;
  gap: var(--spacing-md);
}

.social-link {
  width: 40px;
  height: 40px;
  background: var(--bg-tertiary);
  border-radius: var(--radius-full);
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--text-secondary);
  text-decoration: none;
  transition: var(--transition-normal);
}

.social-link:hover {
  background: var(--primary-color);
  color: white;
  transform: translateY(-2px);
}

/* 页脚链接 */
.footer-links {
  list-style: none;
  padding: 0;
  margin: 0;
  display: flex;
  flex-direction: column;
  gap: var(--spacing-sm);
}

.footer-links li {
  margin: 0;
}

.footer-links a {
  color: var(--text-secondary);
  text-decoration: none;
  font-size: var(--text-sm);
  line-height: var(--leading-normal);
  transition: var(--transition-normal);
  padding: var(--spacing-xs) 0;
  border-radius: var(--radius-sm);
  display: inline-block;
}

.footer-links a:hover {
  color: var(--primary-color);
  background: var(--bg-tertiary);
  transform: translateX(4px);
}

/* 底部版权信息 */
.footer-bottom {
  background: var(--bg-primary);
  padding: var(--spacing-xl) 0;
  border-top: 1px solid var(--border-primary);
}

.footer-bottom-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: var(--spacing-xl);
}

.copyright {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-xs);
}

.copyright p {
  margin: 0;
  font-size: var(--text-sm);
  color: var(--text-muted);
  line-height: var(--leading-normal);
}

.icp-info {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  flex-wrap: wrap;
}

.icp-info a {
  color: var(--text-muted);
  text-decoration: none;
  font-size: var(--text-xs);
  transition: var(--transition-normal);
}

.icp-info a:hover {
  color: var(--primary-color);
}

/* 统计信息 */
.footer-stats {
  display: flex;
  gap: var(--spacing-xl);
}

.stat-item {
  text-align: center;
}

.stat-number {
  display: block;
  font-size: var(--text-xl);
  font-weight: 700;
  color: var(--primary-color);
  line-height: var(--leading-tight);
}

.stat-label {
  font-size: var(--text-xs);
  color: var(--text-muted);
  line-height: var(--leading-normal);
}

/* 底部链接 */
.footer-links-bottom {
  display: flex;
  gap: var(--spacing-lg);
}

.footer-link {
  color: var(--text-muted);
  text-decoration: none;
  font-size: var(--text-sm);
  transition: var(--transition-normal);
  position: relative;
}

.footer-link:hover {
  color: var(--primary-color);
}

.footer-link::after {
  content: '';
  position: absolute;
  bottom: -2px;
  left: 0;
  width: 0;
  height: 1px;
  background: var(--primary-color);
  transition: var(--transition-normal);
}

.footer-link:hover::after {
  width: 100%;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .footer-content {
    grid-template-columns: 1fr 1fr 1fr;
    gap: var(--spacing-xl);
  }
  
  .company-info {
    grid-column: 1 / -1;
    max-width: none;
  }
  
  .footer-bottom-content {
    flex-direction: column;
    align-items: flex-start;
    gap: var(--spacing-lg);
  }
  
  .footer-stats {
    order: -1;
  }
}

@media (max-width: 768px) {
  .container {
    padding: 0 var(--spacing-md);
  }
  
  .footer-main {
    padding: var(--spacing-xl) 0;
  }
  
  .footer-content {
    grid-template-columns: 1fr;
    gap: var(--spacing-lg);
  }
  
  .footer-brand {
    flex-direction: column;
    text-align: center;
    gap: var(--spacing-sm);
  }
  
  .brand-logo {
    width: 40px;
    height: 40px;
    font-size: 16px;
  }
  
  .footer-stats {
    justify-content: space-around;
    width: 100%;
  }
  
  .footer-links-bottom {
    flex-wrap: wrap;
    gap: var(--spacing-md);
    justify-content: center;
  }
  
  .social-links {
    justify-content: center;
  }
}

@media (max-width: 480px) {
  .footer-bottom {
    padding: var(--spacing-lg) 0;
  }
  
  .footer-stats {
    flex-direction: column;
    gap: var(--spacing-md);
    align-items: center;
  }
  
  .stat-item {
    display: flex;
    align-items: center;
    gap: var(--spacing-sm);
  }
  
  .stat-number {
    font-size: var(--text-lg);
  }
  
  .footer-links-bottom {
    flex-direction: column;
    align-items: center;
    gap: var(--spacing-sm);
  }
}

/* 动画效果 */
.footer-section {
  animation: fadeInUp var(--transition-slow) ease-out;
}

.footer-section:nth-child(1) { animation-delay: 0.1s; }
.footer-section:nth-child(2) { animation-delay: 0.2s; }
.footer-section:nth-child(3) { animation-delay: 0.3s; }
.footer-section:nth-child(4) { animation-delay: 0.4s; }
.footer-section:nth-child(5) { animation-delay: 0.5s; }

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

/* 深色模式适配 */
@media (prefers-color-scheme: dark) {
  .app-footer {
    background: var(--bg-primary);
  }
  
  .footer-main {
    background: var(--bg-secondary);
  }
}
</style>
