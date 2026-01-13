# 新闻头条项目 - 数据库部署指南

## 📋 概述

本项目采用多数据源架构，包含 MySQL、MongoDB、MinIO 三种存储系统。本文档提供完整的数据库部署和初始化指南。

## 🏗️ 架构总览

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   MySQL 8.3     │    │   MongoDB 8.2   │    │   MinIO         │
│                 │    │                 │    │                 │
│ • 用户管理       │    │ • 新闻内容       │    │ • 图片文件       │
│ • 角色权限       │    │ • 评论数据       │    │ • 视频文件       │
│ • 新闻基础信息   │◄──►│ • 操作日志       │◄──►│ • 文档文件       │
│ • 系统配置       │    │ • 文件元数据     │    │ • 缩略图         │
│ • 收藏关系       │    │ • 用户行为       │    │                 │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

## 🚀 快速部署

### 前置要求

- Docker & Docker Compose
- Java 17+
- 至少 4GB 可用内存
- 至少 10GB 可用磁盘空间

### 一键部署

```bash
# 克隆项目
git clone <repository-url>
cd News_JavaWeb/News_SpringBoot

# 启动所有数据库服务
docker-compose -f docker/docker-compose.yml up -d

# 初始化数据库
./scripts/init-databases.sh
```

## 📁 目录结构

```
database/
├── mysql/
│   ├── 00-complete-database-init.sql  # MySQL 完整初始化脚本
│   └── README.md                       # MySQL 部署说明
├── mongodb/
│   ├── 00-complete-mongodb-init.js  # MongoDB 完整初始化脚本
│   └── README.md                     # MongoDB 部署说明
├── minio/
│   ├── init-buckets.sh           # MinIO 存储桶初始化
│   └── README.md                 # MinIO 部署说明
├── scripts/
│   ├── init-databases.sh         # 数据库初始化脚本
│   ├── backup-databases.sh       # 数据库备份脚本
│   └── restore-databases.sh      # 数据库恢复脚本
├── docker/
│   ├── docker-compose.yml        # Docker Compose 配置
│   ├── mysql/
│   │   ├── my.cnf               # MySQL 配置
│   │   └── init/                # MySQL 初始化脚本
│   ├── mongodb/
│   │   ├── mongod.conf          # MongoDB 配置
│   │   └── init/                # MongoDB 初始化脚本
│   └── minio/
│       ├── config/              # MinIO 配置
│       └── data/                # MinIO 数据目录
└── README.md                    # 本文档
```

## 🗄️ MySQL 部署

### 手动部署

#### 1. 安装 MySQL 8.3+

```bash
# Ubuntu/Debian
sudo apt update
sudo apt install mysql-server-8.3

# CentOS/RHEL
sudo yum install mysql-community-server-8.3

# macOS (使用 Homebrew)
brew install mysql@8.3
```

#### 2. 创建数据库和用户

```sql
-- 登录 MySQL
mysql -u root -p

-- 创建数据库
CREATE DATABASE News_DB CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 创建应用用户
CREATE USER 'news_app'@'%' IDENTIFIED BY 'your_password';
GRANT ALL PRIVILEGES ON News_DB.* TO 'news_app'@'%';
FLUSH PRIVILEGES;
```

#### 3. 执行初始化脚本

```bash
# 一键执行完整数据库初始化
mysql -u news_app -p News_DB < database/mysql/00-complete-database-init.sql
```

#### 4. 验证部署

```sql
-- 检查表结构
USE News_DB;
SHOW TABLES;

-- 检查数据
SELECT COUNT(*) FROM users;
SELECT COUNT(*) FROM news_types;
SELECT COUNT(*) FROM headlines;
```

### Docker 部署

```bash
# 启动 MySQL 容器
docker run -d \
  --name news-mysql \
  -p 3306:3306 \
  -e MYSQL_ROOT_PASSWORD=root \
  -e MYSQL_DATABASE=News_DB \
  -e MYSQL_USER=news_app \
  -e MYSQL_PASSWORD=your_password \
  -v $(pwd)/database/mysql/00-complete-database-init.sql:/docker-entrypoint-initdb.d/init.sql \
  mysql:8.3
```

## 🍃 MongoDB 部署

### 手动部署

#### 1. 安装 MongoDB 8.2+

```bash
# Ubuntu/Debian
wget -qO - https://www.mongodb.org/static/pgp/server-8.2.asc | sudo apt-key add -
echo "deb [ arch=amd64,arm64 ] https://repo.mongodb.org/apt/ubuntu focal/mongodb-org/8.2 multiverse" | sudo tee /etc/apt/sources.list.d/mongodb-org-8.2.list
sudo apt update
sudo apt install -y mongodb-org=8.2

# CentOS/RHEL
sudo yum install -y mongodb-org-8.2

# macOS (使用 Homebrew)
brew tap mongodb/brew
brew install mongodb-community@8.2
```

#### 2. 启动 MongoDB 服务

```bash
# 启动服务
sudo systemctl start mongod
sudo systemctl enable mongod

# 检查状态
sudo systemctl status mongod
```

#### 3. 初始化数据库

```bash
# 连接到 MongoDB
mongosh mongodb://localhost:27017

# 一键执行完整数据库初始化
mongosh News_MongoDB database/mongodb/00-complete-mongodb-init.js
```

#### 4. 验证部署

```javascript
// 连接到 MongoDB
mongosh News_MongoDB

// 检查集合
show collections

// 检查数据
db.news.countDocuments()
db.comments.countDocuments()
db.user_behavior.countDocuments()
db.file_metadata.countDocuments()
db.system_cache.countDocuments()
```

### Docker 部署

```bash
# 启动 MongoDB 容器
docker run -d \
  --name news-mongodb \
  -p 27017:27017 \
  -v $(pwd)/database/mongodb/00-complete-mongodb-init.js:/docker-entrypoint-initdb.d/init.js \
  mongo:8.2
```

## 🗃️ MinIO 部署

### 手动部署

#### 1. 下载 MinIO

```bash
# Linux (amd64)
wget https://dl.min.io/server/minio/release/linux-amd64/minio
chmod +x minio
sudo mv minio /usr/local/bin/

# macOS
brew install minio/stable/minio
```

#### 2. 启动 MinIO 服务

```bash
# 创建数据目录
mkdir -p /data/minio

# 启动服务
MINIO_ROOT_USER=minioadmin \
MINIO_ROOT_PASSWORD=minioadmin \
minio server /data/minio --console-address ":9001"
```

#### 3. 初始化存储桶

```bash
# 执行初始化脚本
chmod +x database/minio/init-buckets.sh
./database/minio/init-buckets.sh
```

#### 4. 验证部署

- 访问 Web 控制台: http://localhost:9001
- 用户名: `minioadmin`
- 密码: `minioadmin`

### Docker 部署

```bash
# 启动 MinIO 容器
docker run -d \
  --name news-minio \
  -p 9000:9000 \
  -p 9001:9001 \
  -e MINIO_ROOT_USER=minioadmin \
  -e MINIO_ROOT_PASSWORD=minioadmin \
  -v $(pwd)/data/minio:/data \
  minio/minio server /data --console-address ":9001"
```

## 🔧 配置说明

### 应用配置

更新 `application.yml` 中的数据库连接配置：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/News_DB?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai
    username: news_app
    password: your_password
    
  data:
    mongodb:
      uri: mongodb://localhost:27017
      database: News_MongoDB

custom:
  minio:
    endpoint: http://localhost:9000
    access-key: minioadmin
    secret-key: minioadmin
    bucket-name: news-storage
```

### 性能优化

#### MySQL 优化

```ini
# /etc/mysql/mysql.conf.d/mysqld.cnf
[mysqld]
# 内存配置
innodb_buffer_pool_size = 2G
innodb_log_file_size = 256M

# 连接配置
max_connections = 200
max_connect_errors = 1000

# 查询缓存
query_cache_type = 1
query_cache_size = 128M

# 字符集
character-set-server = utf8mb4
collation-server = utf8mb4_unicode_ci
```

#### MongoDB 优化

```yaml
# /etc/mongod.conf
storage:
  dbPath: /var/lib/mongodb
  journal:
    enabled: true
  wiredTiger:
    engineConfig:
      cacheSizeGB: 2
    collectionConfig:
      blockCompressor: snappy

systemLog:
  destination: file
  path: /var/log/mongodb/mongod.log
  logAppend: true

net:
  port: 27017
  bindIp: 0.0.0.0
```

## 🔒 安全配置

### MySQL 安全

```sql
-- 删除默认用户
DELETE FROM mysql.user WHERE User='';

-- 禁用 root 远程登录
DELETE FROM mysql.user WHERE User='root' AND Host NOT IN ('localhost', '127.0.0.1', '::1');

-- 创建专用数据库用户
CREATE USER 'news_app'@'localhost' IDENTIFIED BY 'strong_password';
GRANT SELECT, INSERT, UPDATE, DELETE ON News_DB.* TO 'news_app'@'localhost';

-- 刷新权限
FLUSH PRIVILEGES;
```

### MongoDB 安全

```javascript
// 创建管理员用户
use admin
db.createUser({
  user: "admin",
  pwd: "strong_password",
  roles: ["userAdminAnyDatabase", "dbAdminAnyDatabase"]
});

// 创建应用用户
use News_MongoDB
db.createUser({
  user: "news_app",
  pwd: "strong_password",
  roles: ["readWrite"]
})

// 启用认证
// 在 mongod.conf 中添加:
// security:
//   authorization: enabled
```

### MinIO 安全

```bash
# 修改默认密码
mc alias set local http://localhost:9000 minioadmin minioadmin
mc admin user add local news_app strong_password
mc admin policy add local news_app readwrite
mc admin user policy set local news_app readwrite
```

## 📊 监控和维护

### 数据库监控

```bash
# MySQL 监控
mysql -u news_app -p -e "SHOW PROCESSLIST;"
mysql -u news_app -p -e "SHOW ENGINE INNODB STATUS;"

# MongoDB 监控
mongosh --eval "db.serverStatus()"
mongosh --eval "db.stats()"

# MinIO 监控
mc admin info local
```

### 备份策略

```bash
# 执行备份
./scripts/backup-databases.sh

# 恢复数据
./scripts/restore-databases.sh <backup_date>
```

### 定期维护

```bash
# MySQL 优化
mysql -u news_app -p -e "OPTIMIZE TABLE users, headlines, news_types;"

# MongoDB 索引重建
mongosh News_MongoDB --eval "db.news.reIndex(); db.comments.reIndex();"

# 日志清理
find /var/log -name "*.log" -mtime +30 -delete
```

## 🚨 故障排除

### 常见问题

1. **MySQL 连接失败**
   - 检查服务状态: `sudo systemctl status mysql`
   - 检查端口: `netstat -tlnp | grep 3306`
   - 检查防火墙: `sudo ufw status`

2. **MongoDB 连接失败**
   - 检查服务状态: `sudo systemctl status mongod`
   - 检查配置文件: `cat /etc/mongod.conf`
   - 检查日志: `tail -f /var/log/mongodb/mongod.log`

3. **MinIO 访问失败**
   - 检查服务状态: `ps aux | grep minio`
   - 检查端口占用: `lsof -i :9000`
   - 检查权限: `ls -la /data/minio`

### 性能问题

1. **MySQL 慢查询**
   ```sql
   -- 启用慢查询日志
   SET GLOBAL slow_query_log = 'ON';
   SET GLOBAL long_query_time = 1;
   
   -- 查看慢查询
   SELECT * FROM mysql.slow_log ORDER BY start_time DESC LIMIT 10;
   ```

2. **MongoDB 慢查询**
   ```javascript
   // 启用性能分析
   db.setProfilingLevel(2);
   
   // 查看慢查询
   db.system.profile.find().sort({ts: -1}).limit(5);
   ```

## 📚 更多资源

- [MySQL 官方文档](https://dev.mysql.com/doc/)
- [MongoDB 官方文档](https://docs.mongodb.com/)
- [MinIO 官方文档](https://docs.min.io/)
- [Docker 官方文档](https://docs.docker.com/)

---

**注意**: 在生产环境中，请务必修改默认密码，启用 SSL/TLS 加密，并配置适当的防火墙规则。
