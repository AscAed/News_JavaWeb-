import {defineStore} from 'pinia'
import {computed, ref} from 'vue'
import {getNewsList, getNewsTypes, getSources} from '@/api/modules/news'
import type {News, NewsType} from '@/types'

export const useNewsStore = defineStore('news', () => {
  // 状态
  const newsList = ref<News[]>([])
  const newsTypes = ref<NewsType[]>([])
  const sources = ref<any[]>([])
  const currentNews = ref<News | null>(null)
  const currentSource = ref<any | null>(null)
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

  const fetchNewsList = async (params: {
    page?: number
    size?: number
    type?: number | string
    keyword?: string
  } = {}) => {
    loading.value = true
    try {
      const mergedParams: any = {
        page: params.page || currentPage.value,
        size: params.size || pageSize.value,
        ...params
      }

      // 处理 '全部' 分类 (type为0) 的情况，以及字符串的情况
      if (mergedParams.type === 0 || mergedParams.type === '0') {
        delete mergedParams.type; // 不传type参数，查询该源下的所有新闻
      } else if (typeof mergedParams.type === 'string') {
        mergedParams.section = mergedParams.type;
        delete mergedParams.type;
      }

      // 全局搜索：如果有 keyword，则不限制 sourceType 和 sourceId
      if (!mergedParams.keyword) {
        if (currentSource.value && currentSource.value.type !== 'original' && currentSource.value.id !== -1) {
          mergedParams.sourceType = currentSource.value.type
          mergedParams.sourceId = String(currentSource.value.id)
        } else {
          mergedParams.sourceType = 'original'
        }
      }

      const response = await getNewsList(mergedParams)

      const { data } = response
      // Backend returns { items: [], total: number, page: number, ... }
      newsList.value = (data.items || []).map((item: any) => {
        let mappedSourceName = '原创频道'
        if (item.sourceType && item.sourceType !== 'original') {
          const foundSource = sources.value.find(s => String(s.id) === String(item.sourceId) && s.type === item.sourceType)
          if (foundSource) mappedSourceName = foundSource.name
        } else if (item.source_type && item.source_type !== 'original') {
          const foundSource = sources.value.find(s => String(s.id) === String(item.source_id) && s.type === item.source_type)
          if (foundSource) mappedSourceName = foundSource.name
        }

        return {
          ...item,
          sourceName: mappedSourceName,
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
        }
      })
      total.value = data.total || 0
      currentPage.value = data.page || 1
    } catch (error) {
      console.error('获取新闻列表失败:', error)
    } finally {
      loading.value = false
    }
  }

  const fetchNewsTypes = async () => {
    try {
      const params: any = {}
      if (currentSource.value && currentSource.value.type !== 'original' && currentSource.value.id !== -1) {
        params.sourceType = currentSource.value.type
        params.sourceId = String(currentSource.value.id)
      } else {
        params.sourceType = 'original'
      }
      const response = await getNewsTypes(params)
      // Backend returns Result<Map<String, Object>> with "items" key containing list
      const backendCategories = response.data?.items || []

      // Map backend fields to frontend interface (id -> tid, typeName -> tname)
      const mappedCategories = backendCategories.map((item: any) => ({
        tid: item.id,
        tname: item.typeName,
        description: item.description,
        iconUrl: item.iconUrl,
        sortOrder: item.sortOrder,
        status: item.status,
        createdTime: item.createdTime,
        updatedTime: item.updatedTime
      }))
      
      // 始终在最前面插入"全部新闻"选项
      newsTypes.value = [
        { tid: 0, tname: '全部', sortOrder: 0 } as any,
        ...mappedCategories
      ]
    } catch (error) {
      console.error('获取新闻分类失败:', error)
    }
  }

  // 获取新闻来源
  const fetchSources = async () => {
    try {
      const response = await getSources()
      sources.value = response.data || []

      if (sources.value.length > 0 && !currentSource.value) {
        const originalSource = sources.value.find((s: any) => s.type === 'original' || s.name === '原创' || s.id === -1)
        currentSource.value = originalSource || sources.value[0]
      }
    } catch (error) {
      console.error('获取新闻来源失败:', error)
    }
  }

  // 设置当前新闻
  const setCurrentNews = (news: News | null) => {
    currentNews.value = news
  }

  // 设置当前新闻来源
  const setCurrentSource = (source: any) => {
    currentSource.value = source
  }

  // 重新排序新闻来源
  const reorderSources = (oldIndex: number, newIndex: number) => {
    const item = sources.value.splice(oldIndex, 1)[0]
    if (item) {
      sources.value.splice(newIndex, 0, item)
    }
  }

  // 重新排序新闻分类
  const reorderNewsTypes = (oldIndex: number, newIndex: number) => {
    const item = newsTypes.value.splice(oldIndex, 1)[0]
    if (item) {
      newsTypes.value.splice(newIndex, 0, item)
    }
  }

  // 重置状态
  const resetNewsState = () => {
    newsList.value = []
    currentNews.value = null
    currentSource.value = null
    total.value = 0
    currentPage.value = 1
    loading.value = false
  }

  return {
    // 状态
    newsList,
    newsTypes,
    sources,
    currentNews,
    currentSource,
    loading,
    total,
    currentPage,
    pageSize,

    // 计算属性
    defaultCategoryId,

    // 方法
    fetchNewsList,
    fetchNewsTypes,
    fetchSources,
    setCurrentNews,
    setCurrentSource,
    reorderSources,
    reorderNewsTypes,
    resetNewsState,
  }
})
