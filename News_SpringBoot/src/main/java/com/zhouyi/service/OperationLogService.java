package com.zhouyi.service;

import com.zhouyi.dto.OperationLogDTO;
import java.util.List;

/**
 * 操作日志服务接口
 */
public interface OperationLogService {

    /**
     * 记录操作日志
     * @param userId 用户ID
     * @param username 用户名
     * @param operationType 操作类型
     * @param resourceType 资源类型
     * @param resourceId 资源ID
     * @param description 描述
     * @param ipAddress IP地址
     * @param userAgent 用户代理
     */
    void logOperation(Integer userId, String username, String operationType, 
                     String resourceType, String resourceId, String description,
                     String ipAddress, String userAgent);

    /**
     * 分页查询操作日志
     * @param page 页码
     * @param pageSize 每页数量
     * @param userId 用户ID筛选
     * @param action 操作类型筛选
     * @param dateFrom 开始日期
     * @param dateTo 结束日期
     * @return 分页结果
     */
    com.zhouyi.dto.OperationLogDTO.PageResult getOperationLogs(Integer page, Integer pageSize, 
                                               Integer userId, String action,
                                               String dateFrom, String dateTo);

    /**
     * 根据用户ID查询操作日志
     * @param userId 用户ID
     * @param limit 限制数量
     * @return 操作日志列表
     */
    List<OperationLogDTO> getOperationLogsByUserId(Integer userId, Integer limit);

    /**
     * 清理过期日志
     * @param days 保留天数
     * @return 清理数量
     */
    int cleanExpiredLogs(Integer days);
}
