-- RSS Subscriptions Table
CREATE TABLE IF NOT EXISTS `rss_subscriptions` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'Primary Key',
  `name` VARCHAR(255) NOT NULL COMMENT 'Subscription Name',
  `url` VARCHAR(1024) NOT NULL COMMENT 'RSS Feed URL',
  `description` TEXT COMMENT 'Description',
  `last_fetched_at` DATETIME COMMENT 'Last Fetch Time',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Creation Time',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update Time'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='RSS Subscriptions';

-- RSS Feed Items Table
CREATE TABLE IF NOT EXISTS `rss_feed_items` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'Primary Key',
  `subscription_id` BIGINT NOT NULL COMMENT 'Subscription ID',
  `title` VARCHAR(512) NOT NULL COMMENT 'Article Title',
  `link` VARCHAR(1024) NOT NULL COMMENT 'Article Link',
  `description` LONGTEXT COMMENT 'Article Description/Content',
  `pub_date` DATETIME COMMENT 'Publication Date',
  `guid` VARCHAR(512) COMMENT 'Unique Identifier',
  `author` VARCHAR(255) COMMENT 'Author',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Creation Time',
  FOREIGN KEY (`subscription_id`) REFERENCES `rss_subscriptions`(`id`) ON DELETE CASCADE,
  UNIQUE KEY `uk_link_guid` (`link`(255), `guid`(255))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='RSS Feed Items';

-- Initial Data
INSERT INTO `rss_subscriptions` (`name`, `url`, `description`) 
SELECT '联合早报', 'https://rsshub.app/zaobao/realtime/china', '《联合早报》-中港台-即时'
WHERE NOT EXISTS (SELECT 1 FROM `rss_subscriptions` WHERE `url` = 'https://rsshub.app/zaobao/realtime/china');
