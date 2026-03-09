package com.zhouyi.common.enums;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 新闻来源枚举
 */
public enum NewsSource {
    API("api", "系统发布"),
    RSS("rss", "RSS抓取");

    private final String code;
    private final String description;

    NewsSource(String code, String description) {
        this.code = code;
        this.description = description;
    }

    @JsonValue
    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
