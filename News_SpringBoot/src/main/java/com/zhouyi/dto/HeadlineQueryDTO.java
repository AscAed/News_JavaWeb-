package com.zhouyi.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 新闻头条查询DTO - 支持RESTful查询参数
 */
@Data
public class HeadlineQueryDTO {

    /**
     * 当前页码，从1开始
     */
    @NotNull(message = "页码不能为空")
    @Min(value = 1, message = "页码必须大于0")
    private Integer pageNum = 1;

    /**
     * 每页显示条数
     */
    @NotNull(message = "每页条数不能为空")
    @Min(value = 1, message = "每页条数必须大于0")
    private Integer pageSize = 10;

    /**
     * 新闻类型ID，可选
     */
    private Integer type;

    /**
     * 搜索关键词，可选
     */
    private String keywords;

    /**
     * 发布者ID，可选
     */
    private Integer publisher;

    /**
     * 新闻状态，可选
     */
    private Integer status;

    /**
     * 排序字段，可选
     */
    private String sortBy;

    /**
     * 排序方向，可选
     */
    private String sortOrder;

    /**
     * 开始日期，可选
     */
    private String dateFrom;

    /**
     * 结束日期，可选
     */
    private String dateTo;

    /**
     * Language code filtering (zh or en), optional
     */
    private String lang;
}
