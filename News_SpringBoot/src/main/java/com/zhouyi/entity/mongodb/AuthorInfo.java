package com.zhouyi.entity.mongodb;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 作者信息实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthorInfo {
    
    private String bio;                    // 作者简介
    private SocialLinks socialLinks;       // 社交链接
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SocialLinks {
        private String github;             // GitHub链接
        private String twitter;            // Twitter链接
        private String linkedin;           // LinkedIn链接
    }
}
