package com.zhouyi.service;

import org.springframework.web.multipart.MultipartFile;
import com.zhouyi.common.result.Result;

import java.util.Map;

/**
 * MinIO文件服务接口
 */
public interface MinioFileService {
    
    /**
     * 上传文件
     * @param file 文件对象
     * @param category 文件分类（image、video、document）
     * @param description 文件描述
     * @return 上传结果
     */
    Result<Map<String, Object>> uploadFile(MultipartFile file, String category, String description);
    
    /**
     * 删除文件
     * @param fileId 文件ID
     * @return 删除结果
     */
    Result<String> deleteFile(String fileId);
    
    /**
     * 获取文件信息
     * @param fileId 文件ID
     * @return 文件信息
     */
    Result<Map<String, Object>> getFileInfo(String fileId);
    
    /**
     * 获取文件访问URL
     * @param fileId 文件ID
     * @return 访问URL
     */
    String getFileUrl(String fileId);
    
    /**
     * 检查文件是否存在
     * @param fileId 文件ID
     * @return 是否存在
     */
    boolean fileExists(String fileId);
    
    /**
     * 下载文件
     * @param fileId 文件ID
     * @return 文件字节数组
     */
    byte[] downloadFile(String fileId);
}
