package com.zhouyi.entity.mongodb;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 评论实体类（MongoDB存储）
 * 对应comments集合
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "comments")
public class Comment {
    
    @Id
    private String id;                    // MongoDB主键
    
    @Field("news_id")
    private Integer newsId;               // 关联的新闻ID
    
    @Field("user_id")
    private Integer userId;               // 评论用户ID
    
    @Field("parent_id")
    private String parentId;              // 父评论ID（null表示顶级评论）
    
    @Field("content")
    private String content;               // 评论内容
    
    @Field("like_count")
    private Integer likeCount;            // 点赞数
    
    @Field("reply_count")
    private Integer replyCount;           // 回复数
    
    @Field("is_deleted")
    private Boolean isDeleted;            // 是否已删除
    
    @Field("user_info")
    private UserInfo userInfo;            // 冗余用户信息
    
    @Field("mentions")
    private List<Mention> mentions;       // @用户提及
    
    @Field("media")
    private Media media;                  // 评论中的媒体文件
    
    @Field("location")
    private Location location;            // 地理位置
    
    @Field("device_info")
    private DeviceInfo deviceInfo;        // 设备信息
    
    @Field("created_at")
    private LocalDateTime createdAt;       // 创建时间
    
    @Field("updated_at")
    private LocalDateTime updatedAt;       // 更新时间
    
    @Field("status")
    private Integer status;                // 状态：0-正常，1-待审核，2-已删除
    
    // ==================== 内部类定义 ====================
    
    /**
     * 用户信息实体类（评论中的冗余信息）
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInfo {
        private String username;              // 用户名
        private String avatarUrl;             // 头像URL
    }
    
    /**
     * 用户提及实体类
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Mention {
        private Integer userId;               // 被提及用户ID
        private String username;              // 被提及用户名
    }
    
    /**
     * 媒体文件实体类
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Media {
        private List<String> images;          // 图片列表
        private List<String> videos;          // 视频列表
    }
    
    /**
     * 地理位置实体类
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Location {
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
    public static class DeviceInfo {
        private String userAgent;             // 用户代理
        private String platform;              // 平台：web, mobile, tablet
        private String browser;               // 浏览器
        private String screenResolution;       // 屏幕分辨率
    }
}
