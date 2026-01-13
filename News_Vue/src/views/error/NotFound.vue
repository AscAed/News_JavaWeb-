<script setup lang="ts">
import { useRouter } from 'vue-router'
import { WarningFilled } from '@element-plus/icons-vue'

const router = useRouter()

const goHome = () => {
  router.push('/')
}
</script>

<template>
  <div class="min-h-screen flex items-center justify-center bg-gray-50 py-12 px-4 sm:px-6 lg:px-8">
    <div class="max-w-md w-full text-center">
      <div class="mb-8">
        <el-icon class="text-6xl text-gray-300"><WarningFilled /></el-icon>
      </div>
      
      <div class="space-y-4">
        <h1 class="text-3xl font-bold text-gray-900">404</h1>
        <p class="text-gray-600">抱歉，您访问的页面不存在</p>
        
        <el-button type="primary" @click="goHome">
          返回首页
        </el-button>
      </div>
    </div>
  </div>
</template>
