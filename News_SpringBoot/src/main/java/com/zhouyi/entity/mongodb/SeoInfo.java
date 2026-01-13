package com.zhouyi.entity.mongodb;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * SEO信息实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SeoInfo {

    private String metaDescription; // Meta描述
    private String metaKeywords; // Meta关键词
    private String ogImage; // Open Graph图片
    private String canonicalUrl; // 规范URL

}
