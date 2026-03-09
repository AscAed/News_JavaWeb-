package com.zhouyi.entity;

import com.zhouyi.common.enums.NewsSource;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 统一新闻内容接口，用于屏蔽不同数据来源的差异
 */
public interface UnifiedNewsContent {
    NewsSource getSourceType();

    String getSourceId();

    Integer getMysqlHeadlineId();

    LocalDateTime getPublishedAt();

    List<String> getTags();

    String getContent();

    String getCoverImage();
}
