-- H2 Database Schema for News Spring Boot Application
-- This file creates the database structure for testing

-- Create users table
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    phone VARCHAR(11) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    username VARCHAR(50) NOT NULL,
    email VARCHAR(100),
    avatar_url VARCHAR(255),
    status INT DEFAULT 1 COMMENT '0:禁用, 1:启用',
    last_login_time TIMESTAMP NULL,
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Create news_types table for categories
CREATE TABLE IF NOT EXISTS news_types (
    tid INT AUTO_INCREMENT PRIMARY KEY,
    tname VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255),
    icon_url VARCHAR(255),
    sort_order INT DEFAULT 0,
    color VARCHAR(20),
    status INT DEFAULT 1,
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Create headlines table for news articles
CREATE TABLE IF NOT EXISTS headlines (
    hid INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    article TEXT,
    type INT,
    summary VARCHAR(500),
    cover_image_url VARCHAR(255),
    tags VARCHAR(255),
    is_top INT DEFAULT 0,
    published_time TIMESTAMP,
    page_views INT DEFAULT 0,
    like_count INT DEFAULT 0,
    comment_count INT DEFAULT 0,
    status INT DEFAULT 1,
    publisher INT,
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (type) REFERENCES news_types(tid),
    FOREIGN KEY (publisher) REFERENCES users(id)
);

-- Create comments table
CREATE TABLE IF NOT EXISTS comments (
    coid INT AUTO_INCREMENT PRIMARY KEY,
    hid INT NOT NULL,
    content TEXT NOT NULL,
    user_id INT NOT NULL,
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (hid) REFERENCES headlines(hid),
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Create news_favorites table
CREATE TABLE IF NOT EXISTS news_favorites (
    fid INT AUTO_INCREMENT PRIMARY KEY,
    headline_id INT NOT NULL,
    user_id INT NOT NULL,
    note VARCHAR(255),
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT unique_favorite UNIQUE (headline_id, user_id),
    FOREIGN KEY (headline_id) REFERENCES headlines(hid),
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Create roles table
CREATE TABLE IF NOT EXISTS roles (
    id INT AUTO_INCREMENT PRIMARY KEY,
    role_name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255),
    status INT DEFAULT 1 COMMENT '0:禁用, 1:启用'
);

-- Create user_roles table
CREATE TABLE IF NOT EXISTS user_roles (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    role_id INT NOT NULL,
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (role_id) REFERENCES roles(id)
);

-- Reset auto-increment sequences after creation and before data.sql (if needed)
ALTER TABLE news_types ALTER COLUMN tid RESTART WITH 100;
ALTER TABLE headlines ALTER COLUMN hid RESTART WITH 2000;
ALTER TABLE news_favorites ALTER COLUMN fid RESTART WITH 100;

-- News categories will be inserted in data.sql
