import request from '@/api/request'

/**
 * 获取服务治理统计
 */
export const getGovernanceStats = () => {
  return request({
    url: '/admin/governance/stats',
    method: 'get',
  })
}

/**
 * 获取限流触发日志
 */
export const getRateLimitLogs = () => {
  return request({
    url: '/admin/governance/ratelimit/logs',
    method: 'get',
  })
}

/**
 * 清除限流日志
 */
export const clearRateLimitLogs = () => {
  return request({
    url: '/admin/governance/ratelimit/logs',
    method: 'delete',
  })
}

/**
 * 强制将用户会话加入黑名单 (踢出登录)
 * @param data { jti?: string, token?: string }
 */
export const addToBlacklist = (data: { jti?: string; token?: string }) => {
  return request({
    url: '/admin/governance/blacklist',
    method: 'post',
    data,
  })
}
