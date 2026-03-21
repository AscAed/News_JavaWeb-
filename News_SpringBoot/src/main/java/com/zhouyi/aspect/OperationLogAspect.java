package com.zhouyi.aspect;

import com.zhouyi.annotation.LogOperation;
import com.zhouyi.common.security.CustomUserDetails;
import com.zhouyi.service.OperationLogService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;

/**
 * 操作日志切面 - 自动拦截并记录带有 @LogOperation 注解的方法
 */
@Aspect
@Component
@Slf4j
public class OperationLogAspect {

    @Autowired
    private OperationLogService operationLogService;

    @Around("@annotation(logOperation)")
    public Object around(ProceedingJoinPoint joinPoint, LogOperation logOperation) throws Throwable {
        long startTime = System.currentTimeMillis();
        
        // 执行目标方法
        Object result = joinPoint.proceed();
        
        // 异步或同步记录日志 (根据需要)
        try {
            recordLog(joinPoint, logOperation, result, System.currentTimeMillis() - startTime);
        } catch (Exception e) {
            log.error("Failed to record operation log: {}", e.getMessage());
        }
        
        return result;
    }

    private void recordLog(ProceedingJoinPoint joinPoint, LogOperation logOperation, Object result, long executionTime) {
        // 1. 获取 Request 对象
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) return;
        HttpServletRequest request = attributes.getRequest();

        // 2. 获取当前登录用户信息
        Integer userId = -1;
        String username = "unknown";
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
            userId = userDetails.getUserId();
            username = userDetails.getUsername();
        }

        // 3. 提取资源ID (如果参数中有ID)
        String resourceId = extractResourceId(joinPoint);

        // 4. 构建描述
        String description = logOperation.description();
        if (logOperation.includeArgs()) {
            description += " | Args: " + Arrays.toString(joinPoint.getArgs());
        }
        description += " | ExecutionTime: " + executionTime + "ms";

        // 5. 调用服务记录日志
        operationLogService.logOperation(
                userId,
                username,
                logOperation.operationType(),
                logOperation.resourceType(),
                resourceId,
                description,
                getClientIpAddress(request),
                request.getHeader("User-Agent")
        );
    }

    private String extractResourceId(ProceedingJoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        if (args.length > 0) {
            // 简单逻辑：假设第一个参数可能是ID (Integer, Long, String)
            Object firstArg = args[0];
            if (firstArg instanceof Integer || firstArg instanceof Long || firstArg instanceof String) {
                return String.valueOf(firstArg);
            }
        }
        return null;
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }

        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }

        return request.getRemoteAddr();
    }
}
