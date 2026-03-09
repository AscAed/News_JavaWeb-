package com.zhouyi.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 创建RSS订阅源DTO
 */
@Data
public class RssSubscriptionCreateDTO {

    @NotBlank(message = "订阅源名称不能为空")
    private String name;

    @NotBlank(message = "订阅源URL不能为空")
    private String url;

    private String description;
    private String category;
    private String language;
}
