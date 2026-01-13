package com.zhouyi.controller;

import com.zhouyi.common.result.Result;
import com.zhouyi.service.MinioFileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * 文件管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/files")
@Tag(name = "文件管理", description = "文件上传、下载、删除等操作")
public class FileController {
    
    @Autowired
    private MinioFileService minioFileService;
    
    /**
     * 文件上传
     */
    @PostMapping("/upload")
    @Operation(summary = "文件上传", description = "上传文件到MinIO对象存储")
    public Result<Map<String, Object>> uploadFile(
            @Parameter(description = "上传的文件", required = true)
            @RequestParam("file") MultipartFile file,
            
            @Parameter(description = "文件分类（image、video、document）")
            @RequestParam(value = "category", required = false, defaultValue = "default") String category,
            
            @Parameter(description = "文件描述")
            @RequestParam(value = "description", required = false) String description) {
        
        return minioFileService.uploadFile(file, category, description);
    }
    
    /**
     * 文件删除
     */
    @DeleteMapping("/{fileId}")
    @Operation(summary = "文件删除", description = "从MinIO删除指定文件")
    public Result<String> deleteFile(
            @Parameter(description = "文件唯一标识", required = true)
            @PathVariable String fileId) {
        
        return minioFileService.deleteFile(fileId);
    }
    
    /**
     * 获取文件信息
     */
    @GetMapping("/{fileId}")
    @Operation(summary = "获取文件信息", description = "获取文件的详细信息")
    public Result<Map<String, Object>> getFileInfo(
            @Parameter(description = "文件唯一标识", required = true)
            @PathVariable String fileId) {
        
        return minioFileService.getFileInfo(fileId);
    }
    
    /**
     * 文件下载
     */
    @GetMapping("/download/{fileId}")
    @Operation(summary = "文件下载", description = "下载指定文件")
    public ResponseEntity<byte[]> downloadFile(
            @Parameter(description = "文件唯一标识", required = true)
            @PathVariable String fileId) {
        
        try {
            byte[] fileData = minioFileService.downloadFile(fileId);
            Map<String, Object> fileInfo = minioFileService.getFileInfo(fileId).getData();
            
            String fileName = (String) fileInfo.get("fileName");
            String contentType = (String) fileInfo.get("fileType");
            
            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=\"" + fileName + "\"")
                    .header("Content-Type", contentType != null ? contentType : "application/octet-stream")
                    .body(fileData);
                    
        } catch (Exception e) {
            log.error("文件下载失败: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * 获取文件访问URL
     */
    @GetMapping("/url/{fileId}")
    @Operation(summary = "获取文件URL", description = "获取文件的访问链接")
    public Result<String> getFileUrl(
            @Parameter(description = "文件唯一标识", required = true)
            @PathVariable String fileId) {
        
        if (!minioFileService.fileExists(fileId)) {
            return Result.error("文件不存在");
        }
        
        String url = minioFileService.getFileUrl(fileId);
        return Result.success("获取文件URL成功", url);
    }
    
    /**
     * 检查文件是否存在
     */
    @GetMapping("/exists/{fileId}")
    @Operation(summary = "检查文件存在", description = "检查指定文件是否存在")
    public Result<Boolean> checkFileExists(
            @Parameter(description = "文件唯一标识", required = true)
            @PathVariable String fileId) {
        
        boolean exists = minioFileService.fileExists(fileId);
        return Result.success("检查完成", exists);
    }
}
