package com.zhouyi.dto;

import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * 新闻统计数据传输对象
 */
public class NewsStatisticsDTO {
    
    @NotNull(message = "总新闻数不能为空")
    private Integer totalNews;
    
    @NotNull(message = "今日新闻数不能为空")
    private Integer todayNews;
    
    @NotNull(message = "总浏览量不能为空")
    private Long totalViews;
    
    private List<CategoryStatisticsDTO> topCategories;

    // Getters and Setters
    public Integer getTotalNews() {
        return totalNews;
    }

    public void setTotalNews(Integer totalNews) {
        this.totalNews = totalNews;
    }

    public Integer getTodayNews() {
        return todayNews;
    }

    public void setTodayNews(Integer todayNews) {
        this.todayNews = todayNews;
    }

    public Long getTotalViews() {
        return totalViews;
    }

    public void setTotalViews(Long totalViews) {
        this.totalViews = totalViews;
    }

    public List<CategoryStatisticsDTO> getTopCategories() {
        return topCategories;
    }

    public void setTopCategories(List<CategoryStatisticsDTO> topCategories) {
        this.topCategories = topCategories;
    }
}
