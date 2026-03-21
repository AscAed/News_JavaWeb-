import request from '../request'
import type { ApiResponse } from '@/types'

/**
 * 获取系统统计概览
 */
export const getOverviewStatistics = () => {
  return request.get<any, ApiResponse<any>>('/statistics/overview')
}

/**
 * 获取新闻统计
 */
export const getNewsStatistics = () => {
  return request.get<any, ApiResponse<any>>('/statistics/news')
}

/**
 * 获取热词趋势 (ES 实时聚合)
 * @param size 返回词条数
 */
export const getTrendingKeywords = (size: number = 20) => {
  return request.get<any, ApiResponse<any>>('/statistics/trending', { params: { size } })
}
