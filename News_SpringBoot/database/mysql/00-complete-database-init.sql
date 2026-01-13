-- =====================================================
-- 新闻头条项目 - MySQL 完整数据库初始化脚本
-- 版本: v1.0
-- 创建时间: 2025-11-24
-- 说明: 完整的数据库初始化，包括创建、修改和数据初始化
-- 特性: 完全幂等，可重复执行
-- =====================================================

-- =====================================================
-- 1. 数据库创建和基础表结构
-- =====================================================

-- 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS news_db 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_0900_ai_ci;

USE news_db;

-- =====================================================
-- 1.1 用户表
-- =====================================================
CREATE TABLE IF NOT EXISTS users (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码（加密存储）',
    email VARCHAR(100) UNIQUE COMMENT '邮箱',
    phone VARCHAR(20) COMMENT '手机号',
    avatar_url VARCHAR(500) COMMENT '头像URL',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    last_login_time TIMESTAMP NULL COMMENT '最后登录时间',
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_status (status),
    INDEX idx_created_time (created_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户表';

-- =====================================================
-- 1.2 角色表
-- =====================================================
CREATE TABLE IF NOT EXISTS roles (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT '角色ID',
    role_name VARCHAR(50) NOT NULL UNIQUE COMMENT '角色名称，如：admin, user',
    description VARCHAR(255) COMMENT '角色描述',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    INDEX idx_role_name (role_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='角色表';

-- =====================================================
-- 1.3 用户角色关联表
-- =====================================================
CREATE TABLE IF NOT EXISTS user_roles (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT '关联ID',
    user_id INT NOT NULL COMMENT '用户ID',
    role_id INT NOT NULL COMMENT '角色ID',
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_user_role (user_id, role_id),
    INDEX idx_user_id (user_id),
    INDEX idx_role_id (role_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户角色关联表';

-- =====================================================
-- 1.4 新闻表
-- =====================================================
CREATE TABLE IF NOT EXISTS headlines (
    hid INT PRIMARY KEY AUTO_INCREMENT COMMENT '新闻ID',
    title VARCHAR(255) NOT NULL COMMENT '新闻标题',
    content LONGTEXT COMMENT '新闻内容',
    cover_image_url VARCHAR(500) COMMENT '封面图片URL',
    type INT COMMENT '新闻类型ID',
    publisher INT COMMENT '发布者ID',
    source VARCHAR(100) COMMENT '新闻来源',
    tags VARCHAR(255) COMMENT '标签，逗号分隔',
    summary VARCHAR(500) COMMENT '新闻摘要',
    page_views INT DEFAULT 0 COMMENT '浏览量',
    like_count INT DEFAULT 0 COMMENT '点赞数',
    comment_count INT DEFAULT 0 COMMENT '评论数',
    favorite_count INT DEFAULT 0 COMMENT '收藏数',
    is_top TINYINT DEFAULT 0 COMMENT '是否置顶：0-否，1-是',
    is_hot TINYINT DEFAULT 0 COMMENT '是否热门：0-否，1-是',
    status TINYINT DEFAULT 1 COMMENT '状态：0-草稿，1-发布，2-下架',
    published_time TIMESTAMP NULL COMMENT '发布时间',
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_title (title),
    INDEX idx_type (type),
    INDEX idx_publisher (publisher),
    INDEX idx_status (status),
    INDEX idx_published_time (published_time),
    INDEX idx_is_top (is_top),
    INDEX idx_is_hot (is_hot),
    INDEX idx_page_views (page_views),
    INDEX idx_like_count (like_count),
    FOREIGN KEY (publisher) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='新闻表';

-- =====================================================
-- 1.5 评论表
-- =====================================================
CREATE TABLE IF NOT EXISTS comments (
    cid INT PRIMARY KEY AUTO_INCREMENT COMMENT '评论ID',
    hid INT NOT NULL COMMENT '新闻ID',
    user_id INT NOT NULL COMMENT '用户ID',
    parent_id INT DEFAULT 0 COMMENT '父评论ID，0表示顶级评论',
    content TEXT NOT NULL COMMENT '评论内容',
    like_count INT DEFAULT 0 COMMENT '点赞数',
    status TINYINT DEFAULT 1 COMMENT '状态：0-删除，1-正常',
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_hid (hid),
    INDEX idx_user_id (user_id),
    INDEX idx_parent_id (parent_id),
    INDEX idx_status (status),
    INDEX idx_created_time (created_time),
    FOREIGN KEY (hid) REFERENCES headlines(hid) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (parent_id) REFERENCES comments(cid) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='评论表';

-- =====================================================
-- 1.6 收藏表
-- =====================================================
CREATE TABLE IF NOT EXISTS favorites (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT '收藏ID',
    hid INT NOT NULL COMMENT '新闻ID',
    user_id INT NOT NULL COMMENT '用户ID',
    favorite_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
    UNIQUE KEY uk_user_news (user_id, hid),
    INDEX idx_hid (hid),
    INDEX idx_user_id (user_id),
    INDEX idx_favorite_time (favorite_time),
    FOREIGN KEY (hid) REFERENCES headlines(hid) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='收藏表';

-- =====================================================
-- 2. 基础数据初始化
-- =====================================================

-- =====================================================
-- 2.1 角色基础数据
-- =====================================================
INSERT IGNORE INTO roles (id, role_name, description, status) VALUES 
(1, 'admin', '管理员，拥有所有权限', 1),
(2, 'user', '普通用户，可以浏览、评论、收藏', 1);

-- =====================================================
-- 2.2 用户基础数据
-- =====================================================
INSERT IGNORE INTO users (id, username, password, email, status, created_time, updated_time) VALUES 
(1, 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8ioctKi0pT2cR.CNmlkoFHsZfiI9K', 'admin@news.com', 1, '2025-10-19 00:46:51', '2025-10-19 00:46:51'),
(2, 'user1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8ioctKi0pT2cR.CNmlkoFHsZfiI9K', 'user1@news.com', 1, '2025-10-19 00:46:51', '2025-10-19 00:46:51');

-- =====================================================
-- 2.3 用户角色关联数据
-- =====================================================
INSERT IGNORE INTO user_roles (user_id, role_id) VALUES 
(1, 1),
(2, 2);

-- =====================================================
-- 3. 数据库结构扩展
-- =====================================================

-- =====================================================
-- 3.1 新闻类别表
-- =====================================================
CREATE TABLE IF NOT EXISTS news_types (
    tid INT PRIMARY KEY AUTO_INCREMENT COMMENT '类别ID',
    tname VARCHAR(50) NOT NULL COMMENT '类别名称',
    description VARCHAR(255) COMMENT '类别描述',
    icon_url VARCHAR(500) COMMENT '类别图标URL',
    sort_order INT DEFAULT 0 COMMENT '排序顺序',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    INDEX idx_tname (tname),
    INDEX idx_sort_order (sort_order),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='新闻类别表';

-- =====================================================
-- 3.2 系统配置表
-- =====================================================
CREATE TABLE IF NOT EXISTS system_config (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT '配置ID',
    config_key VARCHAR(100) NOT NULL UNIQUE COMMENT '配置键',
    config_value TEXT COMMENT '配置值',
    config_type VARCHAR(20) DEFAULT 'STRING' COMMENT '配置类型：STRING,NUMBER,BOOLEAN,JSON',
    description VARCHAR(255) COMMENT '配置描述',
    is_system TINYINT DEFAULT 0 COMMENT '是否系统配置：0-否，1-是',
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_config_key (config_key),
    INDEX idx_config_type (config_type),
    INDEX idx_is_system (is_system)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统配置表';

-- =====================================================
-- 3.3 新闻统计表
-- =====================================================
CREATE TABLE IF NOT EXISTS news_statistics (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT '统计ID',
    news_id INT NOT NULL COMMENT '新闻ID',
    statistic_date DATE NOT NULL COMMENT '统计日期',
    daily_views INT DEFAULT 0 COMMENT '日浏览量',
    weekly_views INT DEFAULT 0 COMMENT '周浏览量',
    monthly_views INT DEFAULT 0 COMMENT '月浏览量',
    total_views INT DEFAULT 0 COMMENT '总浏览量',
    daily_likes INT DEFAULT 0 COMMENT '日点赞数',
    weekly_likes INT DEFAULT 0 COMMENT '周点赞数',
    monthly_likes INT DEFAULT 0 COMMENT '月点赞数',
    total_likes INT DEFAULT 0 COMMENT '总点赞数',
    daily_comments INT DEFAULT 0 COMMENT '日评论数',
    weekly_comments INT DEFAULT 0 COMMENT '周评论数',
    monthly_comments INT DEFAULT 0 COMMENT '月评论数',
    total_comments INT DEFAULT 0 COMMENT '总评论数',
    daily_favorites INT DEFAULT 0 COMMENT '日收藏数',
    weekly_favorites INT DEFAULT 0 COMMENT '周收藏数',
    monthly_favorites INT DEFAULT 0 COMMENT '月收藏数',
    total_favorites INT DEFAULT 0 COMMENT '总收藏数',
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_news_date (news_id, statistic_date),
    INDEX idx_news_id (news_id),
    INDEX idx_statistic_date (statistic_date),
    INDEX idx_total_views (total_views),
    INDEX idx_total_likes (total_likes),
    FOREIGN KEY (news_id) REFERENCES headlines(hid) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='新闻统计表';

-- =====================================================
-- 3.4 添加用户表缺失字段（安全添加方式）
-- =====================================================

DELIMITER //

CREATE PROCEDURE safe_add_columns()
BEGIN
    -- 检查并添加 avatar_url 字段
    IF NOT EXISTS (SELECT * FROM information_schema.COLUMNS 
                   WHERE TABLE_SCHEMA = 'news_db' 
                   AND TABLE_NAME = 'users' 
                   AND COLUMN_NAME = 'avatar_url') THEN
        ALTER TABLE users ADD COLUMN avatar_url VARCHAR(500) COMMENT '头像URL';
    END IF;
    
    -- 检查并添加 last_login_time 字段
    IF NOT EXISTS (SELECT * FROM information_schema.COLUMNS 
                   WHERE TABLE_SCHEMA = 'news_db' 
                   AND TABLE_NAME = 'users' 
                   AND COLUMN_NAME = 'last_login_time') THEN
        ALTER TABLE users ADD COLUMN last_login_time TIMESTAMP NULL COMMENT '最后登录时间';
    END IF;
    
    -- 检查并添加 created_time 字段
    IF NOT EXISTS (SELECT * FROM information_schema.COLUMNS 
                   WHERE TABLE_SCHEMA = 'news_db' 
                   AND TABLE_NAME = 'users' 
                   AND COLUMN_NAME = 'created_time') THEN
        ALTER TABLE users ADD COLUMN created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间';
    END IF;
    
    -- 检查并添加 updated_time 字段
    IF NOT EXISTS (SELECT * FROM information_schema.COLUMNS 
                   WHERE TABLE_SCHEMA = 'news_db' 
                   AND TABLE_NAME = 'users' 
                   AND COLUMN_NAME = 'updated_time') THEN
        ALTER TABLE users ADD COLUMN updated_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间';
    END IF;
    
    -- 检查并添加索引 idx_status
    IF NOT EXISTS (SELECT * FROM information_schema.STATISTICS 
                   WHERE TABLE_SCHEMA = 'news_db' 
                   AND TABLE_NAME = 'users' 
                   AND INDEX_NAME = 'idx_status') THEN
        ALTER TABLE users ADD INDEX idx_status (status);
    END IF;
    
    -- 检查并添加索引 idx_created_time
    IF NOT EXISTS (SELECT * FROM information_schema.STATISTICS 
                   WHERE TABLE_SCHEMA = 'news_db' 
                   AND TABLE_NAME = 'users' 
                   AND INDEX_NAME = 'idx_created_time') THEN
        ALTER TABLE users ADD INDEX idx_created_time (created_time);
    END IF;
END//

DELIMITER ;

-- 执行安全添加存储过程
CALL safe_add_columns();

-- 删除临时存储过程
DROP PROCEDURE IF EXISTS safe_add_columns;

-- =====================================================
-- 4. 创建视图
-- =====================================================

-- 新闻详情视图（关联基础信息）
CREATE OR REPLACE VIEW news_detail_view AS
SELECT 
    h.hid,
    h.title,
    h.content,
    h.cover_image_url,
    h.type,
    h.publisher,
    h.source,
    h.tags,
    h.summary,
    h.page_views,
    h.like_count,
    h.comment_count,
    h.favorite_count,
    h.is_top,
    h.is_hot,
    h.status,
    h.published_time,
    h.created_time,
    h.updated_time,
    t.tname AS type_name,
    u.username AS publisher_name,
    u.avatar_url AS publisher_avatar
FROM headlines h
LEFT JOIN news_types t ON h.type = t.tid
LEFT JOIN users u ON h.publisher = u.id;

-- 用户详情视图（包含角色信息）
CREATE OR REPLACE VIEW user_detail_view AS
SELECT 
    u.id,
    u.username,
    u.email,
    u.phone,
    u.avatar_url,
    u.status,
    u.last_login_time,
    u.created_time,
    u.updated_time,
    GROUP_CONCAT(r.role_name) AS roles,
    GROUP_CONCAT(r.description) AS role_descriptions
FROM users u
LEFT JOIN user_roles ur ON u.id = ur.user_id
LEFT JOIN roles r ON ur.role_id = r.id
WHERE u.status = 1
GROUP BY u.id;

-- =====================================================
-- 5. 创建存储过程
-- =====================================================

-- 删除已存在的存储过程，然后重新创建
DROP PROCEDURE IF EXISTS sp_update_news_statistics;

DELIMITER //

-- 更新新闻统计的存储过程
CREATE PROCEDURE sp_update_news_statistics(
    IN p_news_id INT,
    IN p_views_increment INT,
    IN p_likes_increment INT,
    IN p_comments_increment INT,
    IN p_favorites_increment INT
)
BEGIN
    DECLARE v_exists INT DEFAULT 0;
    
    -- 处理NULL值，设置默认值
    IF p_views_increment IS NULL THEN
        SET p_views_increment = 0;
    END IF;
    IF p_likes_increment IS NULL THEN
        SET p_likes_increment = 0;
    END IF;
    IF p_comments_increment IS NULL THEN
        SET p_comments_increment = 0;
    END IF;
    IF p_favorites_increment IS NULL THEN
        SET p_favorites_increment = 0;
    END IF;
    
    -- 检查统计记录是否存在
    SELECT COUNT(*) INTO v_exists 
    FROM news_statistics 
    WHERE news_id = p_news_id AND statistic_date = CURDATE();
    
    IF v_exists > 0 THEN
        -- 更新现有记录
        UPDATE news_statistics 
        SET 
            daily_views = daily_views + p_views_increment,
            weekly_views = weekly_views + p_views_increment,
            monthly_views = monthly_views + p_views_increment,
            total_views = total_views + p_views_increment,
            daily_likes = daily_likes + p_likes_increment,
            weekly_likes = weekly_likes + p_likes_increment,
            monthly_likes = monthly_likes + p_likes_increment,
            total_likes = total_likes + p_likes_increment,
            daily_comments = daily_comments + p_comments_increment,
            weekly_comments = weekly_comments + p_comments_increment,
            monthly_comments = monthly_comments + p_comments_increment,
            total_comments = total_comments + p_comments_increment,
            daily_favorites = daily_favorites + p_favorites_increment,
            weekly_favorites = weekly_favorites + p_favorites_increment,
            monthly_favorites = monthly_favorites + p_favorites_increment,
            total_favorites = total_favorites + p_favorites_increment,
            updated_time = CURRENT_TIMESTAMP
        WHERE news_id = p_news_id AND statistic_date = CURDATE();
    ELSE
        -- 插入新记录
        INSERT INTO news_statistics (
            news_id, daily_views, weekly_views, monthly_views, total_views,
            daily_likes, weekly_likes, monthly_likes, total_likes,
            daily_comments, weekly_comments, monthly_comments, total_comments,
            daily_favorites, weekly_favorites, monthly_favorites, total_favorites,
            statistic_date
        ) VALUES (
            p_news_id, p_views_increment, p_views_increment, p_views_increment, p_views_increment,
            p_likes_increment, p_likes_increment, p_likes_increment, p_likes_increment,
            p_comments_increment, p_comments_increment, p_comments_increment, p_comments_increment,
            p_favorites_increment, p_favorites_increment, p_favorites_increment, p_favorites_increment,
            CURDATE()
        );
    END IF;
END//

DELIMITER ;

-- 删除已存在的存储过程
DROP PROCEDURE IF EXISTS sp_get_hot_news;

DELIMITER //

-- 获取热门新闻的存储过程
CREATE PROCEDURE sp_get_hot_news(
    IN p_limit INT,
    IN p_days INT
)
BEGIN
    -- 处理NULL值，设置默认值
    IF p_limit IS NULL THEN
        SET p_limit = 10;
    END IF;
    IF p_days IS NULL THEN
        SET p_days = 7;
    END IF;
    
    SELECT 
        h.hid,
        h.title,
        h.cover_image_url,
        h.type,
        h.publisher,
        h.page_views,
        h.like_count,
        h.comment_count,
        h.favorite_count,
        h.published_time,
        t.tname AS type_name,
        u.username AS publisher_name,
        (h.page_views * 0.4 + h.like_count * 0.3 + h.comment_count * 0.2 + h.favorite_count * 0.1) AS hot_score
    FROM headlines h
    LEFT JOIN news_types t ON h.type = t.tid
    LEFT JOIN users u ON h.publisher = u.id
    WHERE h.status = 1 
        AND h.published_time >= DATE_SUB(CURDATE(), INTERVAL p_days DAY)
    ORDER BY hot_score DESC, h.published_time DESC
    LIMIT p_limit;
END//

DELIMITER ;

-- =====================================================
-- 6. 创建触发器
-- =====================================================

-- 删除已存在的触发器
DROP TRIGGER IF EXISTS tr_update_favorite_count;
DROP TRIGGER IF EXISTS tr_delete_favorite_count;

DELIMITER //

-- 收藏数更新触发器
CREATE TRIGGER tr_update_favorite_count
AFTER INSERT ON favorites
FOR EACH ROW
BEGIN
    UPDATE headlines 
    SET favorite_count = favorite_count + 1,
        updated_time = CURRENT_TIMESTAMP
    WHERE hid = NEW.hid;
END//

-- 删除收藏时减少收藏数
CREATE TRIGGER tr_delete_favorite_count
AFTER DELETE ON favorites
FOR EACH ROW
BEGIN
    UPDATE headlines 
    SET favorite_count = favorite_count - 1,
        updated_time = CURRENT_TIMESTAMP
    WHERE hid = OLD.hid;
END//

DELIMITER ;

-- =====================================================
-- 7. 创建事件（可选）
-- =====================================================

-- 清理过期统计数据的事件
CREATE EVENT IF NOT EXISTS ev_cleanup_old_statistics
ON SCHEDULE EVERY 1 DAY
STARTS CURRENT_TIMESTAMP + INTERVAL 1 HOUR
DO
BEGIN
    -- 删除30天前的统计数据
    DELETE FROM news_statistics 
    WHERE statistic_date < DATE_SUB(CURDATE(), INTERVAL 30 DAY);
    
    -- 重置过期的日统计数据
    UPDATE news_statistics 
    SET 
        daily_views = 0,
        daily_likes = 0,
        daily_comments = 0,
        daily_favorites = 0
    WHERE statistic_date < CURDATE();
END//

-- 启用事件调度器
SET GLOBAL event_scheduler = ON;

-- =====================================================
-- 8. 完整数据初始化
-- =====================================================

-- =====================================================
-- 8.1 新闻类别数据初始化
-- =====================================================
INSERT IGNORE INTO news_types (tid, tname, description, icon_url, sort_order, status) VALUES 
(1, '推荐', '首页推荐内容', 'https://example.com/icons/recommend.png', 1, 1),
(2, '科技', '科技新闻和资讯', 'https://example.com/icons/tech.png', 2, 1),
(3, '体育', '体育新闻和赛事', 'https://example.com/icons/sports.png', 3, 1),
(4, '娱乐', '娱乐新闻和八卦', 'https://example.com/icons/entertainment.png', 4, 1),
(5, '财经', '财经新闻和市场分析', 'https://example.com/icons/finance.png', 5, 1),
(6, '国际', '国际新闻和时事', 'https://example.com/icons/international.png', 6, 1),
(7, '社会', '社会新闻和热点', 'https://example.com/icons/society.png', 7, 1),
(8, '健康', '健康资讯和养生', 'https://example.com/icons/health.png', 8, 1);

-- =====================================================
-- 8.2 系统配置数据初始化
-- =====================================================
INSERT IGNORE INTO system_config (config_key, config_value, config_type, description, is_system) VALUES 
('site.title', '新闻头条', 'STRING', '网站标题', 1),
('site.description', '为您提供最新最热的新闻资讯', 'STRING', '网站描述', 1),
('site.keywords', '新闻,资讯,头条,热点', 'STRING', '网站关键词', 1),
('site.logo_url', 'https://example.com/logo.png', 'STRING', '网站Logo', 1),
('site.favicon_url', 'https://example.com/favicon.ico', 'STRING', '网站图标', 1),

('upload.max_file_size', '5', 'NUMBER', '最大文件上传大小(MB)', 1),
('upload.allowed_image_types', 'jpg,jpeg,png,gif,webp', 'STRING', '允许的图片格式', 1),
('upload.allowed_video_types', 'mp4,avi,mov,wmv', 'STRING', '允许的视频格式', 1),
('upload.allowed_document_types', 'pdf,doc,docx,xls,xlsx,ppt,pptx', 'STRING', '允许的文档格式', 1),

('comment.auto_publish', 'true', 'BOOLEAN', '评论是否自动发布', 0),
('comment.max_length', '500', 'NUMBER', '评论最大字数', 0),
('comment.check_sensitive_words', 'true', 'BOOLEAN', '是否检查敏感词', 0),

('news.hot_views_threshold', '1000', 'NUMBER', '热门新闻浏览量阈值', 0),
('news.recommend_count', '10', 'NUMBER', '首页推荐新闻数量', 0),
('news.cache_expire_time', '300', 'NUMBER', '新闻缓存过期时间(秒)', 0),

('user.default_avatar', 'https://example.com/avatar/default.jpg', 'STRING', '默认头像URL', 1),
('user.register_enabled', 'true', 'BOOLEAN', '是否允许用户注册', 0),

('email.smtp_host', 'smtp.example.com', 'STRING', 'SMTP服务器地址', 0),
('email.smtp_port', '587', 'NUMBER', 'SMTP端口', 0),
('email.from_address', 'noreply@news.com', 'STRING', '发件人邮箱', 0);

-- =====================================================
-- 8.3 示例新闻数据
-- =====================================================
INSERT IGNORE INTO headlines (hid, title, cover_image_url, type, publisher, page_views, like_count, comment_count, favorite_count, is_top, is_hot, status, published_time) VALUES 
(1001, 'Spring Boot 3.0 正式发布，带来革命性改进', 'https://example.com/images/spring-boot-3.jpg', 2, 1, 1580, 89, 23, 45, 1, 1, 1, '2025-11-20 10:00:00'),
(1002, 'Vue 3.5 新特性详解：性能提升与开发体验优化', 'https://example.com/images/vue-3-5.jpg', 2, 2, 920, 56, 18, 32, 0, 1, 1, '2025-11-21 14:30:00'),
(1003, '2025年世界杯：精彩瞬间回顾', 'https://example.com/images/world-cup-2025.jpg', 3, 1, 2340, 167, 45, 78, 0, 1, 1, '2025-11-22 09:15:00'),
(1004, '全球股市震荡：投资者应如何应对', 'https://example.com/images/stock-market.jpg', 5, 2, 680, 34, 12, 21, 0, 0, 1, '2025-11-23 16:45:00'),
(1005, 'AI技术突破：新一代大模型即将发布', 'https://example.com/images/ai-breakthrough.jpg', 2, 1, 3120, 234, 67, 123, 1, 1, 1, '2025-11-24 08:00:00');

-- =====================================================
-- 8.4 示例收藏数据
-- =====================================================
INSERT IGNORE INTO favorites (hid, user_id, favorite_time) VALUES 
(1001, 1, '2025-11-20 10:30:00'),
(1002, 1, '2025-11-21 15:00:00'),
(1003, 1, '2025-11-22 10:00:00'),
(1005, 1, '2025-11-24 09:00:00'),
(1001, 2, '2025-11-20 11:00:00'),
(1003, 2, '2025-11-22 11:30:00'),
(1005, 2, '2025-11-24 08:30:00');

-- =====================================================
-- 8.5 新闻统计初始数据
-- =====================================================
INSERT IGNORE INTO news_statistics (news_id, daily_views, weekly_views, monthly_views, total_views, daily_likes, weekly_likes, monthly_likes, total_likes, daily_comments, weekly_comments, monthly_comments, total_comments, daily_favorites, weekly_favorites, monthly_favorites, total_favorites, statistic_date) VALUES 
(1001, 120, 850, 1580, 1580, 8, 67, 89, 89, 3, 18, 23, 23, 6, 38, 45, 45, CURDATE()),
(1002, 85, 620, 920, 920, 5, 48, 56, 56, 2, 14, 18, 18, 4, 25, 32, 32, CURDATE()),
(1003, 180, 1560, 2340, 2340, 12, 145, 167, 167, 6, 38, 45, 45, 9, 65, 78, 78, CURDATE()),
(1004, 65, 480, 680, 680, 4, 28, 34, 34, 1, 8, 12, 12, 3, 16, 21, 21, CURDATE()),
(1005, 220, 1890, 3120, 3120, 18, 198, 234, 234, 8, 56, 67, 67, 15, 98, 123, 123, CURDATE());

-- =====================================================
-- 8.6 角色和用户数据扩展
-- =====================================================

-- 添加媒体角色
INSERT IGNORE INTO roles (id, role_name, description, status) VALUES 
(3, 'media', '媒体用户，可以发布新闻', 1);

-- 创建媒体用户账号
INSERT IGNORE INTO users (id, username, password, email, status, created_time, updated_time) VALUES 
(3, 'media_user', '$2a$10$example.hash.for.media.user', 'media@news.com', 1, NOW(), NOW());

-- 为媒体用户分配媒体角色
INSERT IGNORE INTO user_roles (user_id, role_id) VALUES 
(3, 3);

-- =====================================================
-- 8.7 更新现有用户数据
-- =====================================================

-- 为现有用户添加头像和登录时间（只在字段为空时更新）
UPDATE users SET 
    avatar_url = IF(avatar_url IS NULL OR avatar_url = '', 'https://example.com/avatar/admin.jpg', avatar_url),
    last_login_time = IF(last_login_time IS NULL, NOW(), last_login_time),
    created_time = IF(created_time IS NULL, '2025-10-19 00:46:51', created_time),
    updated_time = NOW()
WHERE id = 1;

UPDATE users SET 
    avatar_url = IF(avatar_url IS NULL OR avatar_url = '', 'https://example.com/avatar/user1.jpg', avatar_url),
    last_login_time = IF(last_login_time IS NULL, DATE_SUB(NOW(), INTERVAL 2 HOUR), last_login_time),
    created_time = IF(created_time IS NULL, '2025-10-19 00:46:51', created_time),
    updated_time = NOW()
WHERE id = 2;

UPDATE users SET 
    avatar_url = IF(avatar_url IS NULL OR avatar_url = '', 'https://example.com/avatar/media.jpg', avatar_url),
    last_login_time = IF(last_login_time IS NULL, DATE_SUB(NOW(), INTERVAL 1 HOUR), last_login_time),
    created_time = IF(created_time IS NULL, '2025-10-19 00:46:51', created_time),
    updated_time = NOW()
WHERE id = 3;

-- =====================================================
-- 9. 验证数据完整性
-- =====================================================

-- 检查表结构
SELECT 
    TABLE_NAME as '表名',
    TABLE_ROWS as '记录数',
    DATA_LENGTH/1024/1024 as '数据大小(MB)',
    INDEX_LENGTH/1024/1024 as '索引大小(MB)'
FROM information_schema.TABLES 
WHERE TABLE_SCHEMA = 'news_db' 
ORDER BY TABLE_NAME;

-- 检查外键约束
SELECT 
    TABLE_NAME,
    COLUMN_NAME,
    CONSTRAINT_NAME,
    REFERENCED_TABLE_NAME,
    REFERENCED_COLUMN_NAME
FROM information_schema.KEY_COLUMN_USAGE
WHERE TABLE_SCHEMA = 'news_db' 
    AND REFERENCED_TABLE_NAME IS NOT NULL;

-- 显示执行完成信息
SELECT '数据库初始化脚本执行完成！' AS message;
