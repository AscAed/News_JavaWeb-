package com.zhouyi.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 操作日志响应DTO
 */
public class OperationLogDTO {
    
    private Integer id;
    private UserDTO user;
    private String action;
    private String resource;
    private String resourceId;
    private String description;
    private String ipAddress;
    private String userAgent;
    private LocalDateTime createdTime;

    // Constructors
    public OperationLogDTO() {}

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }

    /**
     * 用户信息DTO
     */
    public static class UserDTO {
        private Integer id;
        private String username;

        public UserDTO() {}

        public UserDTO(Integer id, String username) {
            this.id = id;
            this.username = username;
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }
    }

    /**
     * 分页结果类
     */
    public static class PageResult {
        private Integer total;
        private Integer page;
        private Integer pageSize;
        private Integer totalPages;
        private List<OperationLogDTO> items;

        public PageResult() {}

        public PageResult(Integer total, Integer page, Integer pageSize, Integer totalPages, List<OperationLogDTO> items) {
            this.total = total;
            this.page = page;
            this.pageSize = pageSize;
            this.totalPages = totalPages;
            this.items = items;
        }

        // Getters and Setters
        public Integer getTotal() {
            return total;
        }

        public void setTotal(Integer total) {
            this.total = total;
        }

        public Integer getPage() {
            return page;
        }

        public void setPage(Integer page) {
            this.page = page;
        }

        public Integer getPageSize() {
            return pageSize;
        }

        public void setPageSize(Integer pageSize) {
            this.pageSize = pageSize;
        }

        public Integer getTotalPages() {
            return totalPages;
        }

        public void setTotalPages(Integer totalPages) {
            this.totalPages = totalPages;
        }

        public List<OperationLogDTO> getItems() {
            return items;
        }

        public void setItems(List<OperationLogDTO> items) {
            this.items = items;
        }
    }
}
