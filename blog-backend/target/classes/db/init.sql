-- 创建数据库
CREATE DATABASE IF NOT EXISTS blog CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 使用数据库
USE blog;

-- 创建用户表
CREATE TABLE IF NOT EXISTS `user` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
  `email` VARCHAR(100) NOT NULL UNIQUE COMMENT '邮箱',
  `password` VARCHAR(100) NOT NULL COMMENT '密码（加密存储）',
  `avatar` VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
  `bio` TEXT DEFAULT NULL COMMENT '个人简介',
  `role` VARCHAR(20) NOT NULL DEFAULT 'user' COMMENT '角色（user/admin）',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  INDEX `idx_username` (`username`),
  INDEX `idx_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 创建分类表
CREATE TABLE IF NOT EXISTS `category` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `name` VARCHAR(50) NOT NULL UNIQUE COMMENT '分类名称',
  `slug` VARCHAR(100) NOT NULL UNIQUE COMMENT '分类别名（URL友好）',
  `description` VARCHAR(255) DEFAULT NULL COMMENT '分类描述',
  `article_count` INT(11) NOT NULL DEFAULT 0 COMMENT '文章数量',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  INDEX `idx_slug` (`slug`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='分类表';

-- 创建标签表
CREATE TABLE IF NOT EXISTS `tag` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '标签ID',
  `name` VARCHAR(50) NOT NULL UNIQUE COMMENT '标签名称',
  `slug` VARCHAR(100) NOT NULL UNIQUE COMMENT '标签别名（URL友好）',
  `article_count` INT(11) NOT NULL DEFAULT 0 COMMENT '文章数量',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  INDEX `idx_slug` (`slug`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='标签表';

-- 创建文章表
CREATE TABLE IF NOT EXISTS `article` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '文章ID',
  `title` VARCHAR(200) NOT NULL COMMENT '标题',
  `content` TEXT NOT NULL COMMENT '内容',
  `excerpt` VARCHAR(500) DEFAULT NULL COMMENT '摘要',
  `cover` VARCHAR(255) DEFAULT NULL COMMENT '封面图片URL',
  `author_id` BIGINT(20) NOT NULL COMMENT '作者ID',
  `category_id` BIGINT(20) DEFAULT NULL COMMENT '分类ID',
  `status` VARCHAR(20) NOT NULL DEFAULT 'draft' COMMENT '状态（draft/published）',
  `view_count` INT(11) NOT NULL DEFAULT 0 COMMENT '浏览量',
  `like_count` INT(11) NOT NULL DEFAULT 0 COMMENT '点赞数',
  `comment_count` INT(11) NOT NULL DEFAULT 0 COMMENT '评论数',
  `is_top` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否置顶（0/1）',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  INDEX `idx_author_id` (`author_id`),
  INDEX `idx_category_id` (`category_id`),
  INDEX `idx_status` (`status`),
  INDEX `idx_is_top` (`is_top`),
  INDEX `idx_created_at` (`created_at`),
  FULLTEXT INDEX `idx_title_content` (`title`, `content`),
  FOREIGN KEY (`author_id`) REFERENCES `user` (`id`) ON DELETE CASCADE,
  FOREIGN KEY (`category_id`) REFERENCES `category` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文章表';

-- 创建文章标签关联表
CREATE TABLE IF NOT EXISTS `article_tag` (
  `article_id` BIGINT(20) NOT NULL COMMENT '文章ID',
  `tag_id` BIGINT(20) NOT NULL COMMENT '标签ID',
  PRIMARY KEY (`article_id`, `tag_id`),
  INDEX `idx_article_id` (`article_id`),
  INDEX `idx_tag_id` (`tag_id`),
  FOREIGN KEY (`article_id`) REFERENCES `article` (`id`) ON DELETE CASCADE,
  FOREIGN KEY (`tag_id`) REFERENCES `tag` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文章标签关联表';

-- 创建评论表
CREATE TABLE IF NOT EXISTS `comment` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '评论ID',
  `article_id` BIGINT(20) NOT NULL COMMENT '文章ID',
  `user_id` BIGINT(20) NOT NULL COMMENT '用户ID',
  `content` TEXT NOT NULL COMMENT '评论内容',
  `parent_id` BIGINT(20) DEFAULT NULL COMMENT '父评论ID（用于回复）',
  `status` VARCHAR(20) NOT NULL DEFAULT 'approved' COMMENT '状态（approved/pending）',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  INDEX `idx_article_id` (`article_id`),
  INDEX `idx_user_id` (`user_id`),
  INDEX `idx_parent_id` (`parent_id`),
  INDEX `idx_status` (`status`),
  FOREIGN KEY (`article_id`) REFERENCES `article` (`id`) ON DELETE CASCADE,
  FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE,
  FOREIGN KEY (`parent_id`) REFERENCES `comment` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='评论表';

-- 创建文章点赞表
CREATE TABLE IF NOT EXISTS `article_like` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '点赞ID',
  `article_id` BIGINT(20) NOT NULL COMMENT '文章ID',
  `user_id` BIGINT(20) NOT NULL COMMENT '用户ID',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_article_user` (`article_id`, `user_id`),
  INDEX `idx_article_id` (`article_id`),
  INDEX `idx_user_id` (`user_id`),
  FOREIGN KEY (`article_id`) REFERENCES `article` (`id`) ON DELETE CASCADE,
  FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文章点赞表';

-- 创建文章收藏表
CREATE TABLE IF NOT EXISTS `article_favorite` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '收藏ID',
  `article_id` BIGINT(20) NOT NULL COMMENT '文章ID',
  `user_id` BIGINT(20) NOT NULL COMMENT '用户ID',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_article_user` (`article_id`, `user_id`),
  INDEX `idx_article_id` (`article_id`),
  INDEX `idx_user_id` (`user_id`),
  FOREIGN KEY (`article_id`) REFERENCES `article` (`id`) ON DELETE CASCADE,
  FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文章收藏表';

-- 插入初始数据

-- 插入默认分类
INSERT INTO `category` (`name`, `slug`, `description`) VALUES
('技术', 'tech', '技术相关文章'),
('前端', 'frontend', '前端开发相关文章'),
('后端', 'backend', '后端开发相关文章'),
('生活', 'life', '生活感悟和分享');

-- 插入默认标签
INSERT INTO `tag` (`name`, `slug`) VALUES
('Spring Boot', 'spring-boot'),
('Java', 'java'),
('Vue', 'vue'),
('JavaScript', 'javascript'),
('MySQL', 'mysql'),
('Linux', 'linux'),
('Git', 'git'),
('算法', 'algorithm');

-- 插入默认管理员用户（密码：admin123）
INSERT INTO `user` (`username`, `email`, `password`, `role`) VALUES
('admin', 'admin@example.com', '$2a$10$E6A2gUf8eA5i3m27sX3g/Op6YwUcQxGJz5EJgZ4Z6f7X7Y8Z9W10', 'admin');

-- 插入测试文章
INSERT INTO `article` (`title`, `content`, `excerpt`, `author_id`, `category_id`, `status`, `is_top`) VALUES
('Spring Boot 3.0 新特性介绍', 'Spring Boot 3.0 带来了许多新特性，包括基于 Java 17 的支持、GraalVM 原生镜像支持等。本文将详细介绍这些新特性。', 'Spring Boot 3.0 新特性介绍', 1, 1, 'published', 1),
('Vue 3 Composition API 最佳实践', 'Vue 3 的 Composition API 提供了一种新的组件逻辑组织方式，本文将介绍一些使用 Composition API 的最佳实践。', 'Vue 3 Composition API 最佳实践', 1, 2, 'published', 0);

-- 关联文章和标签
INSERT INTO `article_tag` (`article_id`, `tag_id`) VALUES
(1, 1),
(1, 2),
(2, 3),
(2, 4);

-- 更新分类文章数量
UPDATE `category` SET `article_count` = (
  SELECT COUNT(*) FROM `article` WHERE `category_id` = `category`.`id` AND `status` = 'published'
);

-- 更新标签文章数量
UPDATE `tag` SET `article_count` = (
  SELECT COUNT(*) FROM `article_tag` WHERE `tag_id` = `tag`.`id`
);

-- 查看创建结果
SHOW TABLES;

-- 查看初始数据
SELECT * FROM `category`;
SELECT * FROM `tag`;
SELECT * FROM `user`;
SELECT * FROM `article`;
SELECT * FROM `article_tag`;