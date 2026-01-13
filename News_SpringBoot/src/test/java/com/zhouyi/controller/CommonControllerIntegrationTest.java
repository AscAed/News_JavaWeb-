package com.zhouyi.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 通用控制器集成测试
 */
@SpringBootTest
@TestPropertySource(properties = {
        "file.upload.path=test-uploads",
        "file.access.url=http://localhost:8080/files"
})
class CommonControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Test
    void testUploadFileSuccess() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        // 创建测试图片文件
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test-image.jpg",
                "image/jpeg",
                "test image content".getBytes());

        // 执行文件上传请求
        mockMvc.perform(multipart("/api/v1/common/upload")
                .file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("上传成功"))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data")
                        .value(org.hamcrest.Matchers.containsString("http://localhost:8080/files/images/")));
    }

    @Test
    void testUploadInvalidFileType() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        // 创建无效文件类型的测试文件
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                "test text content".getBytes());

        // 执行文件上传请求
        mockMvc.perform(multipart("/api/v1/common/upload")
                .file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("不支持的文件类型")));
    }

    @Test
    void testUploadEmptyFile() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        // 创建空文件
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "empty.jpg",
                "image/jpeg",
                new byte[0]);

        // 执行文件上传请求
        mockMvc.perform(multipart("/api/v1/common/upload")
                .file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("文件不能为空")));
    }
}
