<template>
  <div id="app" class="app-container">
    <component :is="layoutComponent">
      <!-- 主应用头部 -->
      <template #header>
        <AppHeader />
      </template>

      <!-- 主应用内容 -->
      <template #default>
        <RouterView />
      </template>

      <!-- 主应用侧边栏 -->
      <template #sidebar>
        <SidebarNavigation />
      </template>

      <!-- 主应用页脚 -->
      <template #footer>
        <AppFooter />
      </template>
    </component>
  </div>
</template>

<script setup lang="ts">
import {computed, onMounted} from 'vue'
import {RouterView, useRoute} from 'vue-router'
import AppLayout from '@/components/AppLayout.vue'
import AppHeader from '@/components/AppHeader.vue'
import AppFooter from '@/components/AppFooter.vue'
import SidebarNavigation from '@/components/SidebarNavigation.vue'
import {useUserStore} from '@/stores/user'

const route = useRoute()
const userStore = useUserStore()

onMounted(() => {
  userStore.initUserInfo()
  if (userStore.token) {
    userStore.fetchUserInfo()
  }
})

// 根据路由 meta 动态选择布局组件
// 如果 route.meta.layout === 'empty'，则直接渲染 RouterView (通过一个简单的 wrapper)
// 否则渲染 AppLayout
const layoutComponent = computed(() => {
  if (route.meta?.layout === 'empty') {
    return 'div' // 简单包装，或者可以直接返回 RouterView 的 wrapper
  }
  return AppLayout
})
</script>

<style>
/* 全局样式重置 */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

html,
body {
  height: 100%;
  font-family: var(--font-family-base);
  background-color: var(--bg-primary);
  color: var(--text-primary);
  line-height: var(--leading-normal);
}

#app {
  height: 100vh;
  display: flex;
  flex-direction: column;
}

.app-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

/* 滚动条样式 */
::-webkit-scrollbar {
  width: 6px;
  height: 6px;
}

::-webkit-scrollbar-track {
  background: var(--bg-tertiary);
}

::-webkit-scrollbar-thumb {
  background: var(--border-secondary);
  border-radius: 3px;
}

::-webkit-scrollbar-thumb:hover {
  background: var(--border-primary);
}

/* 选择文本样式 */
::selection {
  background-color: var(--primary-color);
  color: white;
}

/* 焦点样式 */
:focus {
  outline: 2px solid var(--primary-color);
  outline-offset: 2px;
}

:focus:not(:focus-visible) {
  outline: none;
}

/* 链接样式 - 增强交互 */
a {
  color: var(--primary-color);
  text-decoration: none;
  transition: var(--transition-normal);
  position: relative;
  display: inline-block;
}

a::after {
  content: '';
  position: absolute;
  bottom: -2px;
  left: 0;
  width: 0;
  height: 2px;
  background: var(--primary-color);
  transition: width var(--transition-normal);
}

a:hover {
  color: var(--primary-hover);
}

a:hover::after {
  width: 100%;
}

a:focus {
  outline: 2px solid var(--primary-color);
  outline-offset: 2px;
}

/* 按钮基础样式 - 增强交互 */
button {
  cursor: pointer;
  border: none;
  background: none;
  font-family: inherit;
  transition: var(--transition-normal);
  position: relative;
  overflow: hidden;
}

button::before {
  content: '';
  position: absolute;
  top: 50%;
  left: 50%;
  width: 0;
  height: 0;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
  transform: translate(-50%, -50%);
  transition:
    width var(--transition-fast),
    height var(--transition-fast);
}

button:hover::before {
  width: 100%;
  height: 100%;
}

button:focus {
  outline: 2px solid var(--primary-color);
  outline-offset: 2px;
}

button:disabled {
  cursor: not-allowed;
  opacity: 0.6;
}

button:disabled::before {
  display: none;
}

/* 输入框基础样式 - 增强交互 */
input,
textarea,
select {
  font-family: inherit;
  border: 1px solid var(--border-primary);
  border-radius: var(--radius-md);
  background-color: var(--bg-primary);
  color: var(--text-primary);
  transition: var(--transition-normal);
  position: relative;
}

input:focus,
textarea:focus,
select:focus {
  outline: none;
  border-color: var(--primary-color);
  box-shadow:
    0 0 0 3px rgba(26, 54, 93, 0.15),
    0 0 0 6px rgba(26, 54, 93, 0.08);
  transform: translateY(-1px);
}

input:hover,
textarea:hover,
select:hover {
  border-color: var(--border-secondary);
}

/* 输入框动画效果 */
input:focus::placeholder,
textarea:focus::placeholder {
  opacity: 0.7;
  transform: translateY(-2px);
  transition: all var(--transition-fast);
}

/* 图片样式 */
img {
  max-width: 100%;
  height: auto;
  display: block;
}

/* 列表样式 */
ul,
ol {
  list-style: none;
}

/* 表格样式 */
table {
  border-collapse: collapse;
  width: 100%;
}

th,
td {
  text-align: left;
  padding: var(--spacing-sm);
  border-bottom: 1px solid var(--border-primary);
}

/* 响应式字体大小 */
@media (max-width: 768px) {
  html {
    font-size: 14px;
  }
}

@media (max-width: 480px) {
  html {
    font-size: 13px;
  }
}

/* 工具类 */
.sr-only {
  position: absolute;
  width: 1px;
  height: 1px;
  padding: 0;
  margin: -1px;
  overflow: hidden;
  clip: rect(0, 0, 0, 0);
  white-space: nowrap;
  border: 0;
}

.text-center {
  text-align: center;
}

.text-left {
  text-align: left;
}

.text-right {
  text-align: right;
}

.flex {
  display: flex;
}

.flex-col {
  flex-direction: column;
}

.items-center {
  align-items: center;
}

.justify-center {
  justify-content: center;
}

.justify-between {
  justify-content: space-between;
}

.gap-2 {
  gap: var(--spacing-sm);
}

.gap-4 {
  gap: var(--spacing-md);
}

.gap-6 {
  gap: var(--spacing-lg);
}

.p-4 {
  padding: var(--spacing-md);
}

.p-6 {
  padding: var(--spacing-lg);
}

.m-4 {
  margin: var(--spacing-md);
}

.m-6 {
  margin: var(--spacing-lg);
}

.mt-4 {
  margin-top: var(--spacing-md);
}

.mb-4 {
  margin-bottom: var(--spacing-md);
}

.w-full {
  width: 100%;
}

.h-full {
  height: 100%;
}

/* 动画类 - 增强版本 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity var(--transition-normal);
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

.slide-up-enter-active,
.slide-up-leave-active {
  transition: all var(--transition-normal);
}

.slide-up-enter-from {
  opacity: 0;
  transform: translateY(20px);
}

.slide-up-leave-to {
  opacity: 0;
  transform: translateY(-20px);
}

/* 新增动画效果 */
.scale-enter-active,
.scale-leave-active {
  transition: all var(--transition-normal);
}

.scale-enter-from {
  opacity: 0;
  transform: scale(0.9);
}

.scale-leave-to {
  opacity: 0;
  transform: scale(1.1);
}

.bounce-enter-active {
  transition: all var(--transition-slow);
}

.bounce-enter-from {
  opacity: 0;
  transform: scale(0.3);
}

.bounce-enter-to {
  opacity: 1;
  transform: scale(1);
}

/* 全局悬停效果 */
.hover-lift {
  transition: var(--transition-normal);
}

.hover-lift:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-lg);
}

/* 加载状态动画 */
.loading-dots {
  display: inline-flex;
  gap: 4px;
}

.loading-dots span {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: var(--primary-color);
  animation: loading-bounce 1.4s ease-in-out infinite both;
}

.loading-dots span:nth-child(1) {
  animation-delay: -0.32s;
}
.loading-dots span:nth-child(2) {
  animation-delay: -0.16s;
}
.loading-dots span:nth-child(3) {
  animation-delay: 0s;
}

@keyframes loading-bounce {
  0%,
  80%,
  100% {
    transform: scale(0);
  }
  40% {
    transform: scale(1);
  }
}

/* 打印样式 */
@media print {
  .no-print {
    display: none !important;
  }

  body {
    background: white !important;
    color: black !important;
  }

  a {
    color: black !important;
    text-decoration: underline !important;
  }
}

/* 高对比度模式支持 */
@media (prefers-contrast: high) {
  :root {
    --border-primary: #000000;
    --text-primary: #000000;
    --text-secondary: #333333;
    --bg-primary: #ffffff;
    --bg-secondary: #f5f5f5;
  }
}

/* 减少动画模式支持 */
@media (prefers-reduced-motion: reduce) {
  *,
  *::before,
  *::after {
    animation-duration: 0.01ms !important;
    animation-iteration-count: 1 !important;
    transition-duration: 0.01ms !important;
    scroll-behavior: auto !important;
  }
}
</style>
