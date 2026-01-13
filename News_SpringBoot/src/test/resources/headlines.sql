-- 新闻头条模块数据库表结构

-- 1. 新闻类别表
CREATE TABLE types (
    tid INT PRIMARY KEY AUTO_INCREMENT COMMENT '类别ID',
    tname VARCHAR(50) NOT NULL COMMENT '类别名称',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_tname (tname)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='新闻类别表';

-- 2. 新闻头条表
CREATE TABLE headlines (
    hid INT PRIMARY KEY AUTO_INCREMENT COMMENT '头条ID',
    title VARCHAR(200) NOT NULL COMMENT '新闻标题',
    type INT NOT NULL COMMENT '新闻类型ID',
    summary VARCHAR(500) COMMENT '新闻摘要',
    cover_image VARCHAR(500) COMMENT '封面图片URL',
    tags VARCHAR(200) COMMENT '标签，多个标签用逗号分隔',
    page_views INT DEFAULT 0 COMMENT '浏览量',
    publisher INT NOT NULL COMMENT '发布者ID',
    author VARCHAR(50) COMMENT '发布者姓名',
    status INT DEFAULT 1 COMMENT '新闻状态：0-草稿，1-已发布，2-已下线',
    is_top INT DEFAULT 0 COMMENT '是否置顶：0-否，1-是',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    published_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
    INDEX idx_type (type),
    INDEX idx_publisher (publisher),
    INDEX idx_status (status),
    INDEX idx_published_time (published_time),
    INDEX idx_is_top (is_top),
    INDEX idx_title (title),
    FOREIGN KEY (type) REFERENCES types(tid) ON DELETE CASCADE,
    FOREIGN KEY (publisher) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='新闻头条表';

-- 3. 初始化类别数据
INSERT INTO types (tid, tname) VALUES
(1, '推荐'),
(2, '科技'),
(3, '体育'),
(4, '娱乐'),
(5, '财经');
