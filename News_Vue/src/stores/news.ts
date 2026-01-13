import { defineStore } from 'pinia'
import { ref } from 'vue'
import { getNewsList, getNewsTypes } from '@/api/modules/news'
import type { News, NewsType, PageResult } from '@/types'

export const useNewsStore = defineStore('news', () => {
  // 状态
  const newsList = ref<News[]>([])
  const newsTypes = ref<NewsType[]>([])
  const currentNews = ref<News | null>(null)
  const loading = ref(false)
  const total = ref(0)
  const currentPage = ref(1)
  const pageSize = ref(10)

  // 获取新闻列表
  const fetchNewsList = async (params: {
    page?: number
    size?: number
    type?: number
    keyword?: string
  } = {}) => {
    loading.value = true
    try {
      const response = await getNewsList({
        page: params.page || currentPage.value,
        size: params.size || pageSize.value,
        ...params
      })
      
      const { data } = response
      newsList.value = data.records || []
      total.value = data.total || 0
      currentPage.value = data.current || 1
    } catch (error) {
      console.error('获取新闻列表失败:', error)
    } finally {
      loading.value = false
    }
  }

  // 获取新闻分类
  const fetchNewsTypes = async () => {
    try {
      const response = await getNewsTypes()
      newsTypes.value = response.data || []
    } catch (error) {
      console.error('获取新闻分类失败:', error)
    }
  }

  // 设置当前新闻
  const setCurrentNews = (news: News | null) => {
    currentNews.value = news
  }

  // 重置状态
  const resetNewsState = () => {
    newsList.value = []
    currentNews.value = null
    total.value = 0
    currentPage.value = 1
    loading.value = false
  }

  return {
    // 状态
    newsList,
    newsTypes,
    currentNews,
    loading,
    total,
    currentPage,
    pageSize,
    
    // 方法
    fetchNewsList,
    fetchNewsTypes,
    setCurrentNews,
    resetNewsState,
  }
})
