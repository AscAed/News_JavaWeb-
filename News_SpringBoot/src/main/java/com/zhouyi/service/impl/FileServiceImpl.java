package com.zhouyi.service.impl;

import com.zhouyi.config.CustomProperties;
import com.zhouyi.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * 文件服务实现类
 */
@Service
public class FileServiceImpl implements FileService {

    // 支持的图片类型
    private static final List<String> ALLOWED_CONTENT_TYPES = Arrays.asList(
        "image/jpeg",
        "image/jpg", 
        "image/png",
        "image/gif",
        "image/webp"
    );

    // 支持的文件扩展名
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList(
        ".jpg", ".jpeg", ".png", ".gif", ".webp"
    );

    // 最大文件大小 (5MB)
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;

    @Autowired
    private CustomProperties customProperties;

    @Value("${server.port:8080}")
    private String serverPort;

    @Override
    public String uploadFile(MultipartFile file) throws Exception {
        // 1. 验证文件
        validateFile(file);

        // 2. 生成文件名
        String originalFilename = file.getOriginalFilename();
        String extension = getFileExtension(originalFilename);
        String newFilename = generateFilename(extension);

        // 3. 创建目录
        String datePath = getDatePath();
        String uploadPath = customProperties.getFile().getUpload().getPath();
        Path uploadDir = Paths.get(uploadPath, "images", datePath);
        Files.createDirectories(uploadDir);

        // 4. 保存文件
        Path filePath = uploadDir.resolve(newFilename);
        file.transferTo(filePath.toFile());

        // 5. 返回访问URL
        String accessUrl = customProperties.getFile().getUpload().getAccessUrl();
        return accessUrl + "/images/" + datePath + "/" + newFilename;
    }

    /**
     * 验证文件
     */
    private void validateFile(MultipartFile file) throws Exception {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("文件不能为空");
        }

        // 检查文件大小
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("文件大小不能超过5MB");
        }

        // 检查Content-Type
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase())) {
            throw new IllegalArgumentException("不支持的文件类型，仅支持 JPG、PNG、GIF、WebP 格式");
        }

        // 检查文件扩展名
        String originalFilename = file.getOriginalFilename();
        String extension = getFileExtension(originalFilename);
        if (!ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
            throw new IllegalArgumentException("不支持的文件扩展名，仅支持 jpg、jpeg、png、gif、webp");
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
        return lastDotIndex == -1 ? "" : filename.substring(lastDotIndex);
    }

    /**
     * 生成新文件名
     */
    private String generateFilename(String extension) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String uuid = UUID.randomUUID().toString().replace("-", "");
        return timestamp + "_" + uuid + extension;
    }

    /**
     * 获取日期路径
     */
    private String getDatePath() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM"));
    }
}
