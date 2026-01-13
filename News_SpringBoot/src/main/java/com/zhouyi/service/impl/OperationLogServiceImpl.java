package com.zhouyi.service.impl;

import com.zhouyi.dto.OperationLogDTO;
import com.zhouyi.entity.OperationLog;
import com.zhouyi.mapper.OperationLogMapper;
import com.zhouyi.service.OperationLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 操作日志服务实现
 */
@Service
public class OperationLogServiceImpl implements OperationLogService {

    @Autowired
    private OperationLogMapper operationLogMapper;

    @Override
    @Transactional
    public void logOperation(Integer userId, String username, String operationType, 
                           String resourceType, String resourceId, String description,
                           String ipAddress, String userAgent) {
        OperationLog log = new OperationLog();
        log.setUserId(userId);
        log.setUsername(username);
        log.setOperationType(operationType);
        log.setResourceType(resourceType);
        log.setResourceId(resourceId);
        log.setDescription(description);
        log.setIpAddress(ipAddress);
        log.setUserAgent(userAgent);
        log.setCreatedTime(LocalDateTime.now());

        operationLogMapper.insert(log);
    }

    @Override
    public com.zhouyi.dto.OperationLogDTO.PageResult getOperationLogs(Integer page, Integer pageSize, 
                                                     Integer userId, String action,
                                                     String dateFrom, String dateTo) {
        // 计算偏移量
        int offset = (page - 1) * pageSize;

        // 查询日志列表
        List<OperationLog> logs = operationLogMapper.selectByPage(userId, action, dateFrom, dateTo, offset, pageSize);

        // 查询总数
        Long total = operationLogMapper.countByCondition(userId, action, dateFrom, dateTo);

        // 转换为DTO
        List<OperationLogDTO> logDTOs = logs.stream().map(this::convertToDTO).collect(Collectors.toList());

        // 计算总页数
        int totalPages = (int) Math.ceil((double) total / pageSize);

        return new com.zhouyi.dto.OperationLogDTO.PageResult(
            total.intValue(),
            page,
            pageSize,
            totalPages,
            logDTOs
        );
    }

    @Override
    public List<OperationLogDTO> getOperationLogsByUserId(Integer userId, Integer limit) {
        List<OperationLog> logs = operationLogMapper.selectByUserId(userId, limit);
        return logs.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public int cleanExpiredLogs(Integer days) {
        String date = LocalDateTime.now().minusDays(days).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        return operationLogMapper.deleteByDate(date);
    }

    /**
     * 转换为DTO
     */
    private OperationLogDTO convertToDTO(OperationLog log) {
        OperationLogDTO dto = new OperationLogDTO();
        dto.setId(log.getId());
        dto.setAction(log.getOperationType());
        dto.setResource(getResourceTypeName(log.getResourceType()));
        dto.setResourceId(log.getResourceId());
        dto.setDescription(log.getDescription());
        dto.setIpAddress(log.getIpAddress());
        dto.setUserAgent(log.getUserAgent());
        dto.setCreatedTime(log.getCreatedTime());

        // 设置用户信息
        OperationLogDTO.UserDTO userDTO = new OperationLogDTO.UserDTO();
        userDTO.setId(log.getUserId());
        userDTO.setUsername(log.getUsername());
        dto.setUser(userDTO);

        return dto;
    }

    /**
     * 获取资源类型名称
     */
    private String getResourceTypeName(String resourceType) {
        switch (resourceType) {
            case "NEWS":
                return "新闻";
            case "USER":
                return "用户";
            case "FILE":
                return "文件";
            case "ROLE":
                return "角色";
            case "CONFIG":
                return "配置";
            default:
                return resourceType;
        }
    }
}
