package com.zhouyi.entity.mongodb;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

/**
 * 用户信息实体类（评论中的冗余信息）
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfo {
    
    private String username;              // 用户名
    private String avatarUrl;             // 头像URL
}

/**
 * 用户提及实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
class Mention {
    
    private Integer userId;               // 被提及用户ID
    private String username;              // 被提及用户名
}

/**
 * 媒体文件实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
class Media {
    
    private List<String> images;          // 图片列表
    private List<String> videos;          // 视频列表
}

/**
 * 地理位置实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
class Location {
    
    private String country;               // 国家
    private String city;                  // 城市
    private String ipAddress;             // IP地址
}

/**
 * 设备信息实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
class DeviceInfo {
    
    private String userAgent;             // 用户代理
    private String platform;              // 平台：web, mobile, tablet
    private String browser;               // 浏览器
    private String screenResolution;       // 屏幕分辨率
}
