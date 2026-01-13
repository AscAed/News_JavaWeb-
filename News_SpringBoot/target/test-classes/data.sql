-- H2 Database Sample Data for News Spring Boot Application Tests
-- This file inserts sample data for testing

-- Insert news categories first (required by foreign keys)
MERGE INTO news_types (tid, tname) KEY(tid) VALUES 
(1, '新闻'),
(2, '科技'),
(3, '体育'),
(4, '娱乐'),
(5, '财经');

-- Insert test users with BCrypt-encoded passwords
-- Password: Password123 (BCrypt encoded: $2a$10$nnBQ4SdqD3ScCVvfIeONGuY7IFewTM1KUaENsg7ibjCdIpx013eaO)
MERGE INTO users (id, phone, password, username, email, status) KEY(id) VALUES 
(1, '13800138000', '$2a$10$nnBQ4SdqD3ScCVvfIeONGuY7IFewTM1KUaENsg7ibjCdIpx013eaO', '管理员用户', 'admin@example.com', 1),
(2, '13800138001', '$2a$10$nnBQ4SdqD3ScCVvfIeONGuY7IFewTM1KUaENsg7ibjCdIpx013eaO', '测试用户', 'test@example.com', 1);

-- Insert sample news articles
MERGE INTO headlines (hid, title, article, type, page_views, publisher) KEY(hid) VALUES 
(1001, 'Spring Boot 3.0 发布资讯', '<p>Spring Boot 3.0 带来了许多新特性，包括对 Java 17 的原生支持、改进的可观测性等。</p>', 2, 520, 1),
(1002, 'Vue3 实战技巧分享', '<p>Vue3 的 Composition API 为开发者提供了更灵活的代码组织方式。</p>', 2, 300, 2),
(1003, '体育新闻：世界杯精彩瞬间', '<p>本届世界杯带来了许多令人难忘的比赛瞬间。</p>', 3, 150, 1);

-- Insert sample comments
MERGE INTO comments (coid, hid, content, user_id) KEY(coid) VALUES 
(1, 1001, '这篇新闻写得很好！学到了很多。', 2),
(2, 1002, 'Vue3 确实比 Vue2 好用很多。', 1),
(3, 1003, '期待下一届世界杯！', 2);

-- Insert sample favorites
MERGE INTO news_favorites (fid, headline_id, user_id) KEY(fid) VALUES 
(1, 1001, 2),
(2, 1002, 1),
(3, 1003, 2);

-- Insert roles
MERGE INTO roles (id, role_name, description) KEY(id) VALUES 
(1, 'admin', '管理员'),
(2, 'user', '普通用户');

-- Insert user-role associations
MERGE INTO user_roles (user_id, role_id) KEY(user_id, role_id) VALUES 
(1, 1),
(2, 2);
