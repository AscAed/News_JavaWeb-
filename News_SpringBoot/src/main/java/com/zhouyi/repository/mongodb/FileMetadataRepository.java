package com.zhouyi.repository.mongodb;

import com.zhouyi.entity.minio.FileMetadata;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 文件元数据MongoDB仓库
 */
@Repository
public interface FileMetadataRepository extends MongoRepository<FileMetadata, String> {
    
    /**
     * 根据文件ID查找元数据
     */
    Optional<FileMetadata> findByFileId(String fileId);
    
    /**
     * 根据上传用户ID查找文件
     */
    List<FileMetadata> findByUploaderIdOrderByUploadTimeDesc(Integer uploaderId);
    
    /**
     * 根据文件分类查找
     */
    List<FileMetadata> findByCategoryOrderByUploadTimeDesc(String category);
    
    /**
     * 根据状态查找
     */
    List<FileMetadata> findByStatusOrderByUploadTimeDesc(Integer status);
    
    /**
     * 根据用户ID和状态查找
     */
    List<FileMetadata> findByUploaderIdAndStatusOrderByUploadTimeDesc(Integer uploaderId, Integer status);
    
    /**
     * 根据MD5哈希查找（去重用）
     */
    Optional<FileMetadata> findByMd5Hash(String md5Hash);
    
    /**
     * 模糊搜索文件名
     */
    List<FileMetadata> findByOriginalNameContainingIgnoreCase(String fileName);
    
    /**
     * 模糊搜索描述
     */
    List<FileMetadata> findByDescriptionContainingIgnoreCase(String description);
    
    /**
     * 按标签搜索
     */
    @Query("{'tags': {$regex: ?0, $options: 'i'}}")
    List<FileMetadata> findByTags(String tag);
    
    /**
     * 统计用户文件数量
     */
    long countByUploaderIdAndStatus(Integer uploaderId, Integer status);
    
    /**
     * 统计分类文件数量
     */
    long countByCategoryAndStatus(String category, Integer status);
    
    /**
     * 查找指定时间后上传的文件
     */
    List<FileMetadata> findByUploadTimeAfterOrderByUploadTimeDesc(LocalDateTime dateTime);
    
    /**
     * 查找指定时间范围内的文件
     */
    List<FileMetadata> findByUploadTimeBetweenOrderByUploadTimeDesc(LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 查找热门文件（按下载次数排序）
     */
    List<FileMetadata> findByStatusOrderByDownloadCountDesc(Integer status);
    
    /**
     * 查找大文件
     */
    @Query("{'fileSize': {$gt: ?0}}")
    List<FileMetadata> findByFileSizeGreaterThan(Long size);
    
    /**
     * 全文搜索（文件名、描述、标签）
     */
    @Query("{$or: [" +
           "{'originalName': {$regex: ?0, $options: 'i'}}, " +
           "{'description': {$regex: ?0, $options: 'i'}}, " +
           "{'tags': {$regex: ?0, $options: 'i'}}" +
           "]}")
    List<FileMetadata> fullTextSearch(String keyword);
}
