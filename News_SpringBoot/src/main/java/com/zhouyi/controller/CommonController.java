package com.zhouyi.controller;

import com.zhouyi.common.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * 通用控制器，处理文件上传等通用功能
 */
@RestController
@RequestMapping("/api/v1/common")
public class CommonController {

    @Autowired
    private com.zhouyi.service.MinioFileService minioFileService;

    /**
     * 文件上传接口
     * 用于上传新闻封面、用户头像等图片文件
     *
     * @param file 上传的文件对象
     * @return 文件访问路径
     */
    @PostMapping("/upload")
    public Result<String> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            // Use MinIO for storage with "images" category as required by the scheme
            Result<Map<String, Object>> uploadResult = minioFileService.uploadFile(file, "images", "上传的图片");
            
            if (uploadResult.getCode() == 200 && uploadResult.getData() != null) {
                String accessUrl = (String) uploadResult.getData().get("accessUrl");
                return Result.success("上传成功", accessUrl, null);
            } else {
                return Result.error("文件上传失败：" + uploadResult.getMessage());
            }
        } catch (Exception e) {
            return Result.error("文件上传失败：" + e.getMessage());
        }
    }
}
