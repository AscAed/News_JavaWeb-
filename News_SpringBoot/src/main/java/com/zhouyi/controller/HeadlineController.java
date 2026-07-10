package com.zhouyi.controller;

import com.zhouyi.common.result.Result;
import com.zhouyi.common.security.CustomUserDetails;
import com.zhouyi.dto.HeadlineDetailDTO;
import com.zhouyi.dto.HeadlinePublishDTO;
import com.zhouyi.dto.HeadlineQueryDTO;
import com.zhouyi.dto.HeadlineUpdateDTO;
import com.zhouyi.service.HeadlineService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * 新闻头条控制器 - RESTful API
 */
@RestController
@RequestMapping("/api/v1/headlines")
public class HeadlineController {

    @Autowired
    private HeadlineService headlineService;

    /**
     * 查询新闻列表 - RESTful标准GET方法
     *
     * @param keywords  搜索关键词
     * @param typeId    新闻类别ID
     * @param status    新闻状态
     * @param sortBy    排序字段
     * @param sortOrder 排序方向
     * @param page      页码
     * @param pageSize  每页数量
     * @param dateFrom  开始日期
     * @param dateTo    结束日期
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
            @RequestParam(required = false) String dateTo,
            @RequestParam(required = false) String sourceType,
            @RequestParam(required = false) String sourceId,
            @RequestParam(required = false) String section) {
        
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
        queryDTO.setSourceType(sourceType);
        queryDTO.setSourceId(sourceId);
        queryDTO.setSection(section);

        return headlineService.getHeadlinesByPage(queryDTO);
    }

    /**
     * 查看新闻详情 - RESTful标准路径
     * 
     * @param id 新闻ID
     * @return 新闻详情
     */
    @GetMapping("/{id}")
    public Result<HeadlineDetailDTO> getHeadlineById(@PathVariable Integer id) {
        if (id == null || id <= 0) {
            return Result.error("新闻ID不能为空或小于等于0");
        }
        // 记录浏览（异步写入Redis缓冲）
        headlineService.incrementViewCount(id);
        
        return headlineService.getHeadlineById(id);
    }

    /**
     * 创建新闻 - RESTful标准POST方法
     * 
     * @param publishDTO 发布信息
     * @return 创建结果
     */
    @PostMapping
    public Result<String> createHeadline(@Valid @RequestBody HeadlinePublishDTO publishDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails)) {
            return Result.error(401, "请先登录");
        }
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        return headlineService.publishHeadline(publishDTO, userDetails.getUserId());
    }

    /**
     * 更新新闻 - RESTful标准PUT方法
     *
     * @param id        新闻ID
     * @param updateDTO 更新信息
     * @return 更新结果
     */
    @PutMapping("/{id}")
    public Result<String> updateHeadline(@PathVariable Integer id,
                                         @Valid @RequestBody HeadlineUpdateDTO updateDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails)) {
            return Result.error(401, "请先登录");
        }
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        // 设置新闻ID到DTO中
        updateDTO.setId(id); // API统一字段
        updateDTO.setHid(id); // 数据库字段

        return headlineService.updateHeadline(updateDTO, userDetails.getUserId());
    }

    /**
     * 删除新闻 - RESTful标准DELETE方法
     *
     * @param id      新闻ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public Result<String> deleteHeadline(@PathVariable Integer id) {
        if (id == null || id <= 0) {
            return Result.error("新闻ID不能为空或小于等于0");
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails)) {
            return Result.error(401, "请先登录");
        }
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        return headlineService.deleteHeadline(id, userDetails.getUserId());
    }
}
