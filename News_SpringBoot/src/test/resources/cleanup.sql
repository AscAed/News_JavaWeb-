-- H2 Database Cleanup Script for News Spring Boot Application Tests
-- This file cleans up the database after tests

-- Drop tables in reverse order of foreign key dependencies
DROP TABLE IF EXISTS favorites;
DROP TABLE IF EXISTS comments;
DROP TABLE IF EXISTS headlines;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS news_types;
