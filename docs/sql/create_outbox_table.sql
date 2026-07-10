-- Use the application's database
USE news_db;

-- Create the outbox_message table for reliable event synchronization
CREATE TABLE IF NOT EXISTS outbox_message (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    category VARCHAR(50) NOT NULL COMMENT 'Message category, e.g., HEADLINE_ES_SYNC',
    payload TEXT NOT NULL COMMENT 'JSON payload of the message',
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT 'Message status: PENDING, SENT, FAILED',
    retry_count INT DEFAULT 0 COMMENT 'Number of processing attempts',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_status_category (status, category)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
