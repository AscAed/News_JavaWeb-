package com.zhouyi.service.impl;

import com.zhouyi.config.MinioConfig;
import com.zhouyi.common.result.Result;
import com.zhouyi.service.MinioFileService;
import io.minio.*;
import io.minio.http.Method;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * MinIO文件服务实现类
 */
@Slf4j
@Service
public class MinioFileServiceImpl implements MinioFileService {

    @Autowired
    private MinioClient minioClient;

    @Autowired
    private MinioConfig minioConfig;

    /**
     * 确保存储桶和目录结构存在
     */
    private void ensureBucketExists() {
        try {
            boolean bucketExists = minioClient.bucketExists(
                    BucketExistsArgs.builder().bucket(minioConfig.getBucketName()).build());
            if (!bucketExists) {
                minioClient.makeBucket(
                        MakeBucketArgs.builder().bucket(minioConfig.getBucketName()).build());
                log.info("创建MinIO存储桶: {}", minioConfig.getBucketName());

                // 创建API文档要求的目录结构
                initializeBucketStructure();
            }
        } catch (Exception e) {
            log.error("检查/创建存储桶失败: {}", e.getMessage());
            throw new RuntimeException("存储桶操作失败", e);
        }
    }

    /**
     * 初始化存储桶目录结构
     */
    private void initializeBucketStructure() {
        try {
            String[] directories = {"images/", "videos/", "documents/", "avatars/"};

            for (String dir : directories) {
                // 创建空对象作为目录标记
                String dirMarker = dir + ".gitkeep";
                minioClient.putObject(
                        PutObjectArgs.builder()
                                .bucket(minioConfig.getBucketName())
                                .object(dirMarker)
                                .stream(new java.io.ByteArrayInputStream(new byte[0]), 0, -1)
                                .contentType("application/octet-stream")
                                .build());
                log.info("创建目录: {}", dir);
            }
        } catch (Exception e) {
            log.warn("初始化存储桶目录结构失败: {}", e.getMessage());
            // 目录创建失败不影响主要功能，只记录警告
        }
    }

    /**
     * 生成文件路径
     */
    private String generateFilePath(String category, String originalFilename) {
        // 验证和映射category
        String normalizedCategory = validateAndNormalizeCategory(category);

        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String fileExtension = getFileExtension(originalFilename);
        String uuid = UUID.randomUUID().toString().replace("-", "");
        return String.format("%s/%s/%s.%s", normalizedCategory, dateStr, uuid, fileExtension);
    }

    /**
     * 验证和标准化文件分类
     */
    private String validateAndNormalizeCategory(String category) {
        if (category == null || category.trim().isEmpty()) {
            return "default";
        }

        // 映射常见的分类到标准分类
        String normalized = category.toLowerCase().trim();
        switch (normalized) {
            case "image":
            case "img":
            case "picture":
            case "photo":
                return "images";
            case "video":
            case "vid":
            case "movie":
                return "videos";
            case "document":
            case "doc":
            case "pdf":
            case "file":
                return "documents";
            case "avatar":
            case "profile":
            case "userpic":
                return "avatars";
            default:
                // 检查是否为已知的有效分类
                String[] validCategories = {"images", "videos", "documents", "avatars"};
                for (String valid : validCategories) {
                    if (valid.equals(normalized)) {
                        return valid;
                    }
                }
                return "default";
        }
    }

    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String filename) {
        if (filename == null || filename.isEmpty()) {
            return "";
        }
        int lastDotIndex = filename.lastIndexOf('.');
        return lastDotIndex == -1 ? "" : filename.substring(lastDotIndex + 1).toLowerCase();
    }

    @Override
    public Result<Map<String, Object>> uploadFile(MultipartFile file, String category, String description) {
        try {
            // 参数校验
            if (file == null || file.isEmpty()) {
                return Result.error("文件不能为空");
            }

            // 确保存储桶存在
            ensureBucketExists();

            // 生成文件路径
            String originalFilename = file.getOriginalFilename();
            String filePath = generateFilePath(category != null ? category : "default", originalFilename);

            // 上传文件
            try (InputStream inputStream = file.getInputStream()) {
                minioClient.putObject(
                        PutObjectArgs.builder()
                                .bucket(minioConfig.getBucketName())
                                .object(filePath)
                                .stream(inputStream, file.getSize(), -1)
                                .contentType(file.getContentType())
                                .build());
            }

            // 构建返回结果
            Map<String, Object> fileInfo = new HashMap<>();
            fileInfo.put("fileId", filePath);
            fileInfo.put("fileName", originalFilename);
            fileInfo.put("fileSize", file.getSize());
            fileInfo.put("fileType", file.getContentType());
            fileInfo.put("category", category);
            fileInfo.put("accessUrl", getFileUrl(filePath));
            fileInfo.put("uploadTime", LocalDateTime.now());
            fileInfo.put("description", description);

            log.info("文件上传成功: {}", filePath);
            return Result.successWithMessageAndData("文件上传成功", fileInfo);

        } catch (Exception e) {
            log.error("文件上传失败: {}", e.getMessage());
            return Result.error("文件上传失败: " + e.getMessage());
        }
    }

    @Override
    public Result<String> deleteFile(String fileId) {
        try {
            if (!fileExists(fileId)) {
                return Result.error("文件不存在");
            }

            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(minioConfig.getBucketName())
                            .object(fileId)
                            .build());

            log.info("文件删除成功: {}", fileId);
            return Result.success("文件删除成功");

        } catch (Exception e) {
            log.error("文件删除失败: {}", e.getMessage());
            return Result.error("文件删除失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Map<String, Object>> getFileInfo(String fileId) {
        try {
            if (!fileExists(fileId)) {
                return Result.error("文件不存在");
            }

            StatObjectResponse statResponse = minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(minioConfig.getBucketName())
                            .object(fileId)
                            .build());

            Map<String, Object> fileInfo = new HashMap<>();
            fileInfo.put("fileId", fileId);
            fileInfo.put("fileName", getFileNameFromPath(fileId));
            fileInfo.put("fileSize", statResponse.size());
            fileInfo.put("fileType", statResponse.contentType());
            fileInfo.put("lastModified", statResponse.lastModified());
            fileInfo.put("accessUrl", getFileUrl(fileId));

            return Result.successWithMessageAndData("获取文件信息成功", fileInfo);

        } catch (Exception e) {
            log.error("获取文件信息失败: {}", e.getMessage());
            return Result.error("获取文件信息失败: " + e.getMessage());
        }
    }

    @Override
    public String getFileUrl(String fileId) {
        try {
            // 生成预签名URL，有效期7天
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(minioConfig.getBucketName())
                            .object(fileId)
                            .expiry(7, TimeUnit.DAYS)
                            .build());
        } catch (Exception e) {
            log.error("获取文件URL失败: {}", e.getMessage());
            return minioConfig.getAccessUrl() + "/" + minioConfig.getBucketName() + "/" + fileId;
        }
    }

    @Override
    public boolean fileExists(String fileId) {
        try {
            minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(minioConfig.getBucketName())
                            .object(fileId)
                            .build());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public byte[] downloadFile(String fileId) {
        try {
            try (InputStream inputStream = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(minioConfig.getBucketName())
                            .object(fileId)
                            .build())) {
                return inputStream.readAllBytes();
            }
        } catch (Exception e) {
            log.error("文件下载失败: {}", e.getMessage());
            throw new RuntimeException("文件下载失败", e);
        }
    }

    /**
     * 从文件路径中提取文件名
     */
    private String getFileNameFromPath(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            return "";
        }
        int lastSlashIndex = filePath.lastIndexOf('/');
        return lastSlashIndex == -1 ? filePath : filePath.substring(lastSlashIndex + 1);
    }
}
