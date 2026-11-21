-- =====================================================================
-- GameLive 游戏直播网站数据库初始化脚本
-- DB: MySQL 8.0    Charset: utf8mb4
-- 用法: mysql -u root -p < gamelive.sql
-- =====================================================================

CREATE DATABASE IF NOT EXISTS gamelive
    DEFAULT CHARACTER SET utf8mb4
    COLLATE utf8mb4_general_ci;

USE gamelive;

-- ---------------------------------------------------------------------
-- 用户表
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
    `id`            BIGINT       NOT NULL AUTO_INCREMENT COMMENT '用户 ID',
    `username`      VARCHAR(20)  NOT NULL COMMENT '登录账号',
    `password`      VARCHAR(255) NOT NULL COMMENT '密码（应存储加密摘要）',
    `nickname`      VARCHAR(30)  DEFAULT NULL COMMENT '昵称',
    `avatar`        VARCHAR(255) DEFAULT NULL COMMENT '头像地址',
    `signature`     VARCHAR(255) DEFAULT NULL COMMENT '个性签名',
    `role`          VARCHAR(10)  NOT NULL DEFAULT 'USER' COMMENT 'USER-观众 / ANCHOR-主播 / ADMIN-管理员',
    `status`        VARCHAR(10)  NOT NULL DEFAULT 'NORMAL' COMMENT 'NORMAL-正常 / BANNED-封禁',
    `fans_count`    INT          NOT NULL DEFAULT 0 COMMENT '粉丝数',
    `follow_count`  INT          NOT NULL DEFAULT 0 COMMENT '关注数',
    `create_time`   DATETIME     DEFAULT NULL,
    `update_time`   DATETIME     DEFAULT NULL,
    `deleted`       TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '用户表';

-- ---------------------------------------------------------------------
-- 游戏分类表
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS `category`;
CREATE TABLE `category` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT,
    `name`        VARCHAR(30)  NOT NULL COMMENT '分类名称',
    `icon`        VARCHAR(255) DEFAULT NULL COMMENT '分类图标',
    `sort`        INT          NOT NULL DEFAULT 0 COMMENT '排序值',
    `status`      VARCHAR(10)  NOT NULL DEFAULT 'ENABLE' COMMENT 'ENABLE-启用 / DISABLE-停用',
    `create_time` DATETIME     DEFAULT NULL,
    `update_time` DATETIME     DEFAULT NULL,
    `deleted`     TINYINT      NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '游戏分类表';

-- ---------------------------------------------------------------------
-- 直播间表
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS `live_room`;
CREATE TABLE `live_room` (
    `id`           BIGINT       NOT NULL AUTO_INCREMENT,
    `anchor_id`    BIGINT       NOT NULL COMMENT '主播用户 ID',
    `title`        VARCHAR(60)  NOT NULL COMMENT '直播间标题',
    `category_id`  BIGINT       DEFAULT NULL COMMENT '游戏分类 ID',
    `cover`        VARCHAR(255) DEFAULT NULL COMMENT '封面图地址',
    `stream_key`   VARCHAR(64)  NOT NULL COMMENT '推流密钥',
    `status`       VARCHAR(10)  NOT NULL DEFAULT 'NOT_LIVING' COMMENT 'NOT_LIVING-未开播 / LIVING-直播中 / REPLAY-回放',
    `notice`       VARCHAR(255) DEFAULT NULL COMMENT '直播间公告',
    `viewer_count` INT          NOT NULL DEFAULT 0 COMMENT '在线人数',
    `like_count`   INT          NOT NULL DEFAULT 0 COMMENT '点赞数',
    `replay_url`   VARCHAR(255) DEFAULT NULL COMMENT '回放视频地址',
    `audit_status` VARCHAR(10)  NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING-待审核 / APPROVED-通过 / REJECTED-驳回',
    `create_time`  DATETIME     DEFAULT NULL,
    `update_time`  DATETIME     DEFAULT NULL,
    `deleted`      TINYINT      NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`),
    KEY `idx_anchor` (`anchor_id`),
    KEY `idx_category` (`category_id`),
    KEY `idx_status` (`status`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '直播间表';

-- ---------------------------------------------------------------------
-- 关注关系表
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS `follow`;
CREATE TABLE `follow` (
    `id`         BIGINT   NOT NULL AUTO_INCREMENT,
    `user_id`    BIGINT   NOT NULL COMMENT '关注者用户 ID',
    `anchor_id`  BIGINT   NOT NULL COMMENT '被关注主播 ID',
    `create_time` DATETIME DEFAULT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_anchor` (`user_id`, `anchor_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '关注关系表';

-- ---------------------------------------------------------------------
-- 弹幕表
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS `danmu`;
CREATE TABLE `danmu` (
    `id`         BIGINT      NOT NULL AUTO_INCREMENT,
    `room_id`    BIGINT      NOT NULL COMMENT '直播间 ID',
    `user_id`    BIGINT      NOT NULL COMMENT '发送者用户 ID',
    `content`    VARCHAR(200) NOT NULL COMMENT '弹幕内容',
    `create_time` DATETIME   DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `idx_room` (`room_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '弹幕表';

-- ---------------------------------------------------------------------
-- 礼物字典表
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS `gift`;
CREATE TABLE `gift` (
    `id`    BIGINT       NOT NULL AUTO_INCREMENT,
    `name`  VARCHAR(30)  NOT NULL COMMENT '礼物名称',
    `icon`  VARCHAR(255) DEFAULT NULL COMMENT '礼物图标',
    `price` DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '单价',
    `sort`  INT          NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '礼物字典表';

-- ---------------------------------------------------------------------
-- 礼物打赏记录表
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS `gift_record`;
CREATE TABLE `gift_record` (
    `id`          BIGINT   NOT NULL AUTO_INCREMENT,
    `room_id`     BIGINT   NOT NULL COMMENT '直播间 ID',
    `from_user_id` BIGINT  NOT NULL COMMENT '打赏者用户 ID',
    `anchor_id`   BIGINT   NOT NULL COMMENT '接收主播 ID',
    `gift_id`     BIGINT   NOT NULL COMMENT '礼物 ID',
    `gift_count`  INT      NOT NULL DEFAULT 1 COMMENT '赠送数量',
    `create_time` DATETIME DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `idx_room` (`room_id`),
    KEY `idx_from` (`from_user_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '礼物打赏记录表';

-- ---------------------------------------------------------------------
-- 禁言 / 封禁记录表
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS `ban_record`;
CREATE TABLE `ban_record` (
    `id`           BIGINT      NOT NULL AUTO_INCREMENT,
    `room_id`      BIGINT      DEFAULT NULL COMMENT '直播间 ID，空为平台级处罚',
    `operator_id`  BIGINT      NOT NULL COMMENT '操作人 ID',
    `target_user_id` BIGINT    NOT NULL COMMENT '被处罚用户 ID',
    `type`         VARCHAR(10) NOT NULL COMMENT 'MUTE-禁言 / BAN-封禁',
    `reason`       VARCHAR(255) DEFAULT NULL COMMENT '处罚原因',
    `expire_time`  DATETIME    DEFAULT NULL COMMENT '解禁时间，空为永久',
    `create_time`  DATETIME    DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `idx_target` (`target_user_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '禁言封禁记录表';

-- =====================================================================
-- 初始化演示数据（骨架阶段密码为明文，正式环境务必改为加密存储）
-- =====================================================================
INSERT INTO `user` (`username`, `password`, `nickname`, `role`, `status`, `create_time`, `update_time`)
VALUES
    ('admin',  '123456', '平台管理员', 'ADMIN',  'NORMAL', NOW(), NOW()),
    ('anchor1','123456', '技术流主播', 'ANCHOR', 'NORMAL', NOW(), NOW()),
    ('viewer1','123456', '普通观众',   'USER',   'NORMAL', NOW(), NOW());

INSERT INTO `category` (`name`, `icon`, `sort`, `status`, `create_time`, `update_time`)
VALUES
    ('英雄联盟', NULL, 1, 'ENABLE', NOW(), NOW()),
    ('原神',     NULL, 2, 'ENABLE', NOW(), NOW()),
    ('CS2',      NULL, 3, 'ENABLE', NOW(), NOW()),
    ('王者荣耀', NULL, 4, 'ENABLE', NOW(), NOW());

INSERT INTO `gift` (`name`, `icon`, `price`, `sort`)
VALUES
    ('小心心', NULL, 0.10, 1),
    ('啤酒',   NULL, 1.00, 2),
    ('火箭',   NULL, 99.00, 3),
    ('嘉年华', NULL, 520.00, 4);
