import {defineStore} from 'pinia'
import {computed, ref} from 'vue'
import {getNewsList, getNewsTypes} from '@/api/modules/news'
import type {News, NewsType} from '@/types'

export const useNewsStore = defineStore('news', () => {
  // 状态
  const newsList = ref<News[]>([])
  const newsTypes = ref<NewsType[]>([])
  const currentNews = ref<News | null>(null)
  const loading = ref(false)
  const total = ref(0)
  const currentPage = ref(1)
  const pageSize = ref(10)

  // 计算默认分类ID
  const defaultCategoryId = computed(() => {
    if (newsTypes.value.length === 0) return null

    // 优先查找"推荐"分类
    const recommendedCategory = newsTypes.value.find(type => type.tname === '推荐')
    if (recommendedCategory) return recommendedCategory.tid

    // 如果没有"推荐"分类，返回第一个分类
    return newsTypes.value[0]?.tid || null
  })

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
      // Backend returns { items: [], total: number, page: number, ... }
      newsList.value = (data.items || []).map((item: any) => ({
        ...item,
        hid: item.id || item.hid,
        coverImageUrl: item.cover_image || item.coverImage || item.coverImageUrl,
        type: item.type_id || item.type,
        typeName: item.type_name || item.typeName,
        pageViews: item.page_views || item.pageViews,
        likeCount: item.like_count || item.likeCount,
        commentCount: item.comment_count || item.commentCount,
        publisher: item.author_id || item.publisher,
        isTop: item.is_top === 1 || item.is_top === true || item.isTop === true,
        publishedTime: item.published_time || item.publishedTime,
        createdTime: item.created_time || item.createdTime,
        updatedTime: item.updated_time || item.updatedTime
      }))
      total.value = data.total || 0
      currentPage.value = data.page || 1
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
      // Backend returns Result<Map<String, Object>> with "items" key containing list
      const backendCategories = response.data?.items || []

      // Map backend fields to frontend interface (id -> tid, typeName -> tname)
      newsTypes.value = backendCategories.map((item: any) => ({
        tid: item.id,
        tname: item.typeName,
        description: item.description,
        iconUrl: item.iconUrl,
        sortOrder: item.sortOrder,
        status: item.status,
        createdTime: item.createdTime,
        updatedTime: item.updatedTime
      }))
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

    // 计算属性
    defaultCategoryId,

    // 方法
    fetchNewsList,
    fetchNewsTypes,
    setCurrentNews,
    resetNewsState,
  }
})
