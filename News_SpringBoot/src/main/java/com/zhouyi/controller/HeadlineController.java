package com.zhouyi.controller;

import com.zhouyi.common.result.Result;
import com.zhouyi.common.utils.JwtUtil;
import com.zhouyi.dto.HeadlineDetailDTO;
import com.zhouyi.service.HeadlineService;
import com.zhouyi.dto.HeadlineQueryDTO;
import com.zhouyi.dto.HeadlinePublishDTO;
import com.zhouyi.dto.HeadlineUpdateDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

/**
 * 新闻头条控制器 - RESTful API
 */
@RestController
@RequestMapping("/api/v1/headlines")
public class HeadlineController {
    
    @Autowired
    private HeadlineService headlineService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    /**
     * 查询新闻列表 - RESTful标准GET方法
     * @param keywords 搜索关键词
     * @param typeId 新闻类别ID
     * @param status 新闻状态
     * @param sortBy 排序字段
     * @param sortOrder 排序方向
     * @param page 页码
     * @param pageSize 每页数量
     * @param dateFrom 开始日期
     * @param dateTo 结束日期
     * @return 分页结果
     */
    @GetMapping
    public Result<?> getHeadlines(
            @RequestParam(required = false) String keywords,
            @RequestParam(required = false) Integer typeId,
            @RequestParam(defaultValue = "1") Integer status,
            @RequestParam(defaultValue = "published_time") String sortBy,
            @RequestParam(defaultValue = "desc") String sortOrder,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String dateFrom,
            @RequestParam(required = false) String dateTo) {
        try {
            // 构建查询DTO
            HeadlineQueryDTO queryDTO = new HeadlineQueryDTO();
            queryDTO.setKeywords(keywords);
            queryDTO.setType(typeId);
            queryDTO.setStatus(status);
            queryDTO.setSortBy(sortBy);
            queryDTO.setSortOrder(sortOrder);
            queryDTO.setPageNum(page);
            queryDTO.setPageSize(pageSize);
            queryDTO.setDateFrom(dateFrom);
            queryDTO.setDateTo(dateTo);
            
            return headlineService.getHeadlinesByPage(queryDTO);
        } catch (Exception e) {
            return Result.error("查询失败：" + e.getMessage(), "/api/v1/headlines");
        }
    }
    
    /**
     * 查看新闻详情 - RESTful标准路径
     * @param id 新闻ID
     * @return 新闻详情
     */
    @GetMapping("/{id}")
    public Result<HeadlineDetailDTO> getHeadlineById(@PathVariable Integer id) {
        try {
            if (id == null || id <= 0) {
                return Result.error("新闻ID不能为空或小于等于0", "/api/v1/headlines/" + id);
            }
            return headlineService.getHeadlineById(id);
        } catch (Exception e) {
            return Result.error("查询失败：" + e.getMessage(), "/api/v1/headlines/" + id);
        }
    }
    
    /**
     * 创建新闻 - RESTful标准POST方法
     * @param publishDTO 发布信息
     * @param request HTTP请求
     * @return 创建结果
     */
    @PostMapping
    public Result<String> createHeadline(@Valid @RequestBody HeadlinePublishDTO publishDTO, 
                                       HttpServletRequest request) {
        try {
            // 从Authorization Bearer头中获取用户ID进行权限验证
            String authorizationHeader = request.getHeader("Authorization");
            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                return Result.error("请先登录", "/api/v1/headlines");
            }
            
            String token = authorizationHeader.substring(7);
            Integer userId = jwtUtil.getUserIdFromToken(token);
            if (userId == null) {
                return Result.error("Token无效，请重新登录", "/api/v1/headlines");
            }
            
            // 验证用户是否有发布权限（可选）
            // 这里可以添加角色权限验证逻辑
            
            return headlineService.publishHeadline(publishDTO, userId);
        } catch (Exception e) {
            return Result.error("创建失败：" + e.getMessage(), "/api/v1/headlines");
        }
    }
    
    /**
     * 更新新闻 - RESTful标准PUT方法
     * @param id 新闻ID
     * @param updateDTO 更新信息
     * @param request HTTP请求
     * @return 更新结果
     */
    @PutMapping("/{id}")
    public Result<String> updateHeadline(@PathVariable Integer id,
                                        @Valid @RequestBody HeadlineUpdateDTO updateDTO,
                                        HttpServletRequest request) {
        try {
            // 从Authorization Bearer头中获取用户信息进行权限验证
            String authorizationHeader = request.getHeader("Authorization");
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                // String token = authorizationHeader.substring(7);
                // Integer userId = jwtUtil.getUserIdFromToken(token);
                
                // TODO: 验证用户是否有权限修改该新闻（发布者或管理员）
                // 可以添加权限验证逻辑，例如：
                // if (!hasPermissionToUpdate(userId, id)) {
                //     return Result.error("无权限修改该新闻");
                // }
            }
            
            // 设置新闻ID到DTO中
            updateDTO.setId(id);  // API统一字段
            updateDTO.setHid(id);  // 数据库字段
            
            return headlineService.updateHeadline(updateDTO);
        } catch (Exception e) {
            return Result.error("更新失败：" + e.getMessage(), "/api/v1/headlines/" + id);
        }
    }
    
    /**
     * 删除新闻 - RESTful标准DELETE方法
     * @param id 新闻ID
     * @param request HTTP请求
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public Result<String> deleteHeadline(@PathVariable Integer id, HttpServletRequest request) {
        try {
            if (id == null || id <= 0) {
                return Result.error("新闻ID不能为空或小于等于0", "/api/v1/headlines/" + id);
            }
            
            // 从Authorization Bearer头中获取用户信息进行权限验证
            String authorizationHeader = request.getHeader("Authorization");
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                String token = authorizationHeader.substring(7);
                Integer userId = jwtUtil.getUserIdFromToken(token);
                
                // TODO: 验证用户是否有权限删除该新闻（发布者或管理员）
                // 可以添加权限验证逻辑
            }
            
            return headlineService.deleteHeadline(id);
        } catch (Exception e) {
            return Result.error("删除失败：" + e.getMessage(), "/api/v1/headlines/" + id);
        }
    }
}
