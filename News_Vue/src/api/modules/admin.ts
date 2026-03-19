import request from '../request'
import type { ApiResponse } from '@/types'

/**
 * 获取系统统计概览
 */
export const getStatisticsOverview = () => {
  return request.get('/statistics/overview')
}

/**
 * 获取最新新闻（管理员视图）
 */
export const getAdminRecentNews = (limit: number = 5) => {
  return request.get('/headlines', {
    params: {
      pageSize: limit,
      page: 1
    }
  })
}

/**
 * 更新新闻状态（审核、下线等）
 * @param id 新闻HID
 * @param status 1-发布/通过, 2-下线/拒绝
 */
export const updateNewsStatus = (id: number | string, status: number) => {
  return request.patch(`/admin/news/${id}/status`, null, {
    params: { status }
  })
}

/**
 * 获取操作日志
 */
export const getOperationLogs = (params: any) => {
  return request.get('/admin/logs', { params })
}
