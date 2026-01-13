package com.zhouyi.service.impl;

import com.zhouyi.config.CustomProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.mock.web.MockMultipartFile;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * 文件服务测试类
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class FileServiceImplTest {

    @InjectMocks
    private FileServiceImpl fileService;

    @Mock
    private CustomProperties customProperties;

    private MockMultipartFile validImageFile;
    private MockMultipartFile invalidTypeFile;
    private MockMultipartFile largeFile;
    private MockMultipartFile emptyFile;

    @BeforeEach
    void setUp() {
        // 配置CustomProperties Mock
        CustomProperties.FileUpload fileUpload = new CustomProperties.FileUpload();
        CustomProperties.FileUpload.Upload upload = new CustomProperties.FileUpload.Upload();
        upload.setPath("uploads/images");
        upload.setAccessUrl("http://localhost:8080/files/images/");
        fileUpload.setUpload(upload);
        
        when(customProperties.getFile()).thenReturn(fileUpload);
        
        // 有效的图片文件
        validImageFile = new MockMultipartFile(
            "file", 
            "test.jpg", 
            "image/jpeg", 
            "test image content".getBytes()
        );

        // 无效类型的文件
        invalidTypeFile = new MockMultipartFile(
            "file", 
            "test.txt", 
            "text/plain", 
            "test text content".getBytes()
        );

        // 大文件 (模拟超过5MB)
        byte[] largeContent = new byte[6 * 1024 * 1024]; // 6MB
        largeFile = new MockMultipartFile(
            "file", 
            "large.jpg", 
            "image/jpeg", 
            largeContent
        );

        // 空文件
        emptyFile = new MockMultipartFile(
            "file", 
            "empty.jpg", 
            "image/jpeg", 
            new byte[0]
        );
    }

    @Test
    void testUploadValidFile() throws Exception {
        // 测试上传有效文件
        String result = fileService.uploadFile(validImageFile);
        
        assertNotNull(result);
        assertTrue(result.contains("http://localhost:8080/files/images/"));
        assertTrue(result.endsWith(".jpg"));
    }

    @Test
    void testUploadInvalidFileType() {
        // 测试上传无效文件类型
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            fileService.uploadFile(invalidTypeFile);
        });
        
        assertTrue(exception.getMessage().contains("不支持的文件类型"));
    }

    @Test
    void testUploadLargeFile() {
        // 测试上传超大文件
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            fileService.uploadFile(largeFile);
        });
        
        assertTrue(exception.getMessage().contains("文件大小不能超过5MB"));
    }

    @Test
    void testUploadEmptyFile() {
        // 测试上传空文件
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            fileService.uploadFile(emptyFile);
        });
        
        assertTrue(exception.getMessage().contains("文件不能为空"));
    }

    @Test
    void testUploadNullFile() {
        // 测试上传null文件
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            fileService.uploadFile(null);
        });
        
        assertTrue(exception.getMessage().contains("文件不能为空"));
    }

    @Test
    void testUploadValidPngFile() throws Exception {
        // 测试上传PNG文件
        MockMultipartFile pngFile = new MockMultipartFile(
            "file", 
            "test.png", 
            "image/png", 
            "test png content".getBytes()
        );
        
        String result = fileService.uploadFile(pngFile);
        
        assertNotNull(result);
        assertTrue(result.contains("http://localhost:8080/files/images/"));
        assertTrue(result.endsWith(".png"));
    }

    @Test
    void testUploadValidGifFile() throws Exception {
        // 测试上传GIF文件
        MockMultipartFile gifFile = new MockMultipartFile(
            "file", 
            "test.gif", 
            "image/gif", 
            "test gif content".getBytes()
        );
        
        String result = fileService.uploadFile(gifFile);
        
        assertNotNull(result);
        assertTrue(result.contains("http://localhost:8080/files/images/"));
        assertTrue(result.endsWith(".gif"));
    }
}
