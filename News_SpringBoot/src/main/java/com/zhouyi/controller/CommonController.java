package com.zhouyi.controller;

import com.zhouyi.common.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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
            // 使用 MinioFileService 进行上传，指定分类为 images
            Result<java.util.Map<String, Object>> uploadResult = minioFileService.uploadFile(file, "images", "Uploaded via CommonController");
            
            if (uploadResult.getCode() == 200 && uploadResult.getData() != null) {
                String fileUrl = (String) uploadResult.getData().get("accessUrl");
                return Result.success("上传成功", fileUrl, null);
            } else {
                return Result.error("文件上传失败：" + uploadResult.getMessage());
            }
        } catch (Exception e) {
            return Result.error("文件上传失败：" + e.getMessage());
        }
    }
}
