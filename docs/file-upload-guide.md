# 文件上传功能说明

## 功能概述

本模块实现了文件上传功能，支持新闻封面、用户头像等图片文件的上传。

## API 接口

### 上传文件

**请求地址**: `POST /api/v1/common/upload`

**请求参数**:

| 参数名    | 类型            | 必填 | 描述      |
|--------|---------------|----|---------|
| `file` | MultipartFile | 是  | 上传的文件对象 |

**请求示例**:

```bash
curl -X POST \
  http://localhost:8080/api/v1/common/upload \
  -H 'Content-Type: multipart/form-data' \
  -F 'file=@/path/to/your/image.jpg'
```

**响应示例**:

```json
{
  "code": 200,
  "message": "上传成功",
  "data": "http://localhost:8080/files/images/2023/11/20231121_163022_abc123def456.jpg"
}
```

## 支持的文件类型

- **图片格式**: JPG, JPEG, PNG, GIF, WebP
- **文件大小**: 最大 5MB
- **Content-Type**: image/jpeg, image/jpg, image/png, image/gif, image/webp

## 文件存储规则

### 存储路径

- 基础路径: `uploads/images/`
- 目录结构: `uploads/images/yyyy/MM/`
- 文件命名: `yyyyMMdd_HHmmss_uuid.extension`

### 示例

```text
uploads/images/2023/11/20231121_163022_abc123def456.jpg
```

### 访问URL

```text
http://localhost:8080/files/images/2023/11/20231121_163022_abc123def456.jpg
```

## 配置说明

在 `application.yml` 中可以配置以下参数:

```yaml
# Spring Boot 文件上传配置
spring:
  servlet:
    multipart:
      max-file-size: 5MB
      max-request-size: 5MB
      enabled: true

# 自定义文件上传配置
file:
  upload:
    path: uploads                    # 文件存储路径
    access:
      url: http://localhost:8080/files # 文件访问URL前缀
```

## 错误处理

| 错误场景    | 错误信息                                  |
|---------|---------------------------------------|
| 文件为空    | "文件不能为空"                              |
| 文件过大    | "文件大小不能超过5MB"                         |
| 不支持的类型  | "不支持的文件类型，仅支持 JPG、PNG、GIF、WebP 格式"    |
| 不支持的扩展名 | "不支持的文件扩展名，仅支持 jpg、jpeg、png、gif、webp" |

## 安全特性

1. **文件类型验证**: 同时检查 Content-Type 和文件扩展名
2. **文件大小限制**: 防止上传过大文件占用服务器资源
3. **文件名重命名**: 使用 UUID 避免文件名冲突和安全问题
4. **目录隔离**: 按月份组织文件，便于管理和清理

## 使用示例

### 前端 JavaScript 示例

```javascript
// 文件上传函数
async function uploadFile(file) {
  const formData = new FormData();
  formData.append('file', file);
  
  try {
    const response = await fetch('/api/v1/common/upload', {
      method: 'POST',
      body: formData
    });
    
    const result = await response.json();
    
    if (result.code === 200) {
      console.log('上传成功:', result.data);
      return result.data;
    } else {
      console.error('上传失败:', result.message);
      throw new Error(result.message);
    }
  } catch (error) {
    console.error('上传异常:', error);
    throw error;
  }
}

// 使用示例
const fileInput = document.getElementById('fileInput');
fileInput.addEventListener('change', async (event) => {
  const file = event.target.files[0];
  if (file) {
    try {
      const fileUrl = await uploadFile(file);
      // 处理上传成功后的逻辑
      console.log('文件访问URL:', fileUrl);
    } catch (error) {
      // 处理错误
      alert('文件上传失败: ' + error.message);
    }
  }
});
```

### Vue.js 示例

```vue
<template>
  <div>
    <input type="file" @change="handleFileChange" accept="image/*">
    <button @click="uploadFile" :disabled="!selectedFile">上传</button>
  </div>
</template>

<script>
export default {
  data() {
    return {
      selectedFile: null
    }
  },
  methods: {
    handleFileChange(event) {
      this.selectedFile = event.target.files[0];
    },
    async uploadFile() {
      if (!this.selectedFile) return;
      
      const formData = new FormData();
      formData.append('file', this.selectedFile);
      
      try {
        const response = await this.$axios.post('/api/v1/common/upload', formData);
        if (response.data.code === 200) {
          this.$message.success('上传成功');
          this.$emit('upload-success', response.data.data);
        } else {
          this.$message.error(response.data.message);
        }
      } catch (error) {
        this.$message.error('上传失败');
      }
    }
  }
}
</script>
```

## 测试

项目包含完整的单元测试和集成测试：

- **单元测试**: `FileServiceImplTest.java`
- **集成测试**: `CommonControllerIntegrationTest.java`

运行测试命令:

```bash
mvn test
```

## 注意事项

1. 确保 `uploads` 目录有足够的磁盘空间
2. 定期清理过期文件，避免磁盘空间不足
3. 在生产环境中，建议使用 CDN 或对象存储服务
4. 可以根据需要调整文件大小限制和支持的文件类型
