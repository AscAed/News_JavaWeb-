package com.zhouyi.entity.minio;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import java.time.LocalDateTime;

/**
 * 文件元数据实体类（MongoDB存储）
 * 对应file_metadata集合
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "file_metadata")
public class FileMetadata {
    
    @Id
    private String id;                    // MongoDB主键
    
    @Field("file_id")
    private String fileId;               // MinIO中的文件ID（对象路径）
    
    @Field("original_name")
    private String originalName;         // 原始文件名
    
    @Field("file_size")
    private Long fileSize;               // 文件大小（字节）
    
    @Field("file_type")
    private String fileType;             // 文件MIME类型
    
    @Field("category")
    private String category;             // 文件分类（image、video、document）
    
    @Field("description")
    private String description;           // 文件描述
    
    @Field("uploader_id")
    private Integer uploaderId;          // 上传用户ID
    
    @Field("uploader_name")
    private String uploaderName;          // 上传用户名
    
    @Field("access_url")
    private String accessUrl;             // 访问URL
    
    @Field("upload_time")
    private LocalDateTime uploadTime;    // 上传时间
    
    @Field("last_access_time")
    private LocalDateTime lastAccessTime; // 最后访问时间
    
    @Field("download_count")
    private Integer downloadCount;        // 下载次数
    
    @Field("status")
    private Integer status;               // 状态：0-正常，1-已删除
    
    @Field("tags")
    private String tags;                  // 标签，逗号分隔
    
    @Field("md5_hash")
    private String md5Hash;              // 文件MD5哈希值（用于去重）
}
