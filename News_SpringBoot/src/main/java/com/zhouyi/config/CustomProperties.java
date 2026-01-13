package com.zhouyi.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 自定义配置属性
 */
@Component
@ConfigurationProperties(prefix = "custom")
public class CustomProperties {

    /**
     * 文件上传配置
     */
    private FileUpload file = new FileUpload();

    /**
     * JWT配置
     */
    private Jwt jwt = new Jwt();

    /**
     * MinIO配置
     */
    private Minio minio = new Minio();

    // Getters and Setters
    public FileUpload getFile() {
        return file;
    }

    public void setFile(FileUpload file) {
        this.file = file;
    }

    public Jwt getJwt() {
        return jwt;
    }

    public void setJwt(Jwt jwt) {
        this.jwt = jwt;
    }

    public Minio getMinio() {
        return minio;
    }

    public void setMinio(Minio minio) {
        this.minio = minio;
    }

    /**
     * 文件上传配置内部类
     */
    public static class FileUpload {
        private Upload upload = new Upload();

        public Upload getUpload() {
            return upload;
        }

        public void setUpload(Upload upload) {
            this.upload = upload;
        }

        public static class Upload {
            private String path = "uploads";
            private String accessUrl = "http://localhost:8080/files";

            public String getPath() {
                return path;
            }

            public void setPath(String path) {
                this.path = path;
            }

            public String getAccessUrl() {
                return accessUrl;
            }

            public void setAccessUrl(String accessUrl) {
                this.accessUrl = accessUrl;
            }
        }
    }

    /**
     * JWT配置内部类
     */
    public static class Jwt {
        private String secret = "defaultSecret";
        private long expiration = 86400000L; // 24小时
        private Long refreshExpiration = 604800000L; // 7天

        public String getSecret() {
            return secret;
        }

        public void setSecret(String secret) {
            this.secret = secret;
        }

        public long getExpiration() {
            return expiration;
        }

        public void setExpiration(long expiration) {
            this.expiration = expiration;
        }
        
        public Long getRefreshExpiration() {
            return refreshExpiration;
        }

        public void setRefreshExpiration(Long refreshExpiration) {
            this.refreshExpiration = refreshExpiration;
        }
    }

    /**
     * MinIO配置内部类
     */
    public static class Minio {
        private String endpoint = "http://localhost:9000";
        private String accessKey = "minioadmin";
        private String secretKey = "minioadmin";
        private String bucketName = "news-storage";
        private String accessUrl = "http://localhost:9000";

        public String getEndpoint() {
            return endpoint;
        }

        public void setEndpoint(String endpoint) {
            this.endpoint = endpoint;
        }

        public String getAccessKey() {
            return accessKey;
        }

        public void setAccessKey(String accessKey) {
            this.accessKey = accessKey;
        }

        public String getSecretKey() {
            return secretKey;
        }

        public void setSecretKey(String secretKey) {
            this.secretKey = secretKey;
        }

        public String getBucketName() {
            return bucketName;
        }

        public void setBucketName(String bucketName) {
            this.bucketName = bucketName;
        }

        public String getAccessUrl() {
            return accessUrl;
        }

        public void setAccessUrl(String accessUrl) {
            this.accessUrl = accessUrl;
        }
    }
}
