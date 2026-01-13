-- =====================================================
-- Verification Tokens Table Migration
-- Purpose: Store email and phone verification tokens
-- =====================================================

USE news_db;

-- Create verification_tokens table
CREATE TABLE IF NOT EXISTS verification_tokens (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT 'Token ID',
    user_id INT NOT NULL COMMENT 'User ID',
    token_type VARCHAR(20) NOT NULL COMMENT 'Token type: EMAIL or PHONE',
    new_value VARCHAR(100) NOT NULL COMMENT 'New email or phone value',
    token VARCHAR(10) NOT NULL COMMENT 'Verification token (6-digit code)',
    expires_at TIMESTAMP NOT NULL COMMENT 'Token expiration time',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'Creation time',
    UNIQUE KEY uk_user_token_type (user_id, token_type),
    INDEX idx_user_id (user_id),
    INDEX idx_token (token),
    INDEX idx_expires_at (expires_at),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='Verification tokens table';

-- Display completion message
SELECT 'Verification tokens table created successfully!' AS message;
