package com.zhouyi.controller;

import com.zhouyi.common.result.Result;
import com.zhouyi.dto.NewsTypeCreateDTO;
import com.zhouyi.dto.NewsTypeStatusDTO;
import com.zhouyi.dto.NewsTypeUpdateDTO;
import com.zhouyi.service.NewsTypeService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 新闻分类管理控制器
 */
@RestController
@RequestMapping("/api/v1")
public class NewsTypeController {

    @Autowired
    private NewsTypeService newsTypeService;

    /**
     * 获取分类列表
     */
    @GetMapping("/categories")
    public Result<?> getCategories(
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "sort_order") String sort_by,
            @RequestParam(defaultValue = "asc") String sort_order,
            @RequestParam(required = false) String sourceType,
            @RequestParam(required = false) String sourceId) {

        return newsTypeService.getCategories(status, sort_by, sort_order, sourceType, sourceId);
    }

    /**
     * 获取分类详情
     */
    @GetMapping("/categories/{id}")
    public Result<?> getCategoryById(@PathVariable("id") Integer id) {
        return newsTypeService.getCategoryById(id);
    }

    /**
     * 创建分类
     */
    @PostMapping("/categories")
    public Result<?> createCategory(@Valid @RequestBody NewsTypeCreateDTO createDTO,
                                    HttpServletRequest request) {

        // 这里可以添加权限验证，检查用户是否有创建分类的权限
        return newsTypeService.createCategory(createDTO);
    }

    /**
     * 更新分类
     */
    @PutMapping("/categories/{id}")
    public Result<?> updateCategory(@PathVariable("id") Integer id,
                                    @Valid @RequestBody NewsTypeUpdateDTO updateDTO,
                                    HttpServletRequest request) {

        // 这里可以添加权限验证，检查用户是否有更新分类的权限
        return newsTypeService.updateCategory(id, updateDTO);
    }

    /**
     * 更新分类状态
     */
    @PatchMapping("/categories/{id}/status")
    public Result<?> updateCategoryStatus(@PathVariable("id") Integer id,
                                          @Valid @RequestBody NewsTypeStatusDTO statusDTO,
                                          HttpServletRequest request) {

        // 这里可以添加权限验证，检查用户是否有管理分类状态的权限
        return newsTypeService.updateCategoryStatus(id, statusDTO);
    }

    /**
     * 删除分类
     */
    @DeleteMapping("/categories/{id}")
    public Result<?> deleteCategory(@PathVariable("id") Integer id,
                                    HttpServletRequest request) {

        // 这里可以添加权限验证，检查用户是否有删除分类的权限
        return newsTypeService.deleteCategory(id);
    }

    /**
     * 获取分类统计信息
     */
    @GetMapping("/categories/{id}/statistics")
    public Result<?> getCategoryStatistics(@PathVariable("id") Integer id,
                                           @RequestParam(required = false) String date_from,
                                           @RequestParam(required = false) String date_to,
                                           HttpServletRequest request) {

        // 这里可以添加权限验证，检查用户是否有查看统计的权限
        return newsTypeService.getCategoryStatistics(id, date_from, date_to);
    }
}
