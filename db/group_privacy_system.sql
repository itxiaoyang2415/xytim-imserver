-- 群组隐私保护系统表结构
-- 使用数据库：im_platform

USE im_platform;

-- 1. 系统隐私配置表（全局开关）
CREATE TABLE IF NOT EXISTS t_system_privacy_config (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
  config_key VARCHAR(100) NOT NULL COMMENT '配置键',
  config_value VARCHAR(500) COMMENT '配置值',
  description VARCHAR(200) COMMENT '配置描述',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  UNIQUE KEY uk_config_key (config_key)
) COMMENT='系统隐私配置表';

-- 插入默认配置
INSERT INTO t_system_privacy_config (config_key, config_value, description) VALUES
('group_privacy_global_enabled', '1', '全局隐私保护功能开关: 0-关闭, 1-开启'),
('virtual_nick_name_prefix', '群友', '模拟昵称前缀');

-- 2. 群组隐私配置表（群级开关）
CREATE TABLE IF NOT EXISTS t_group_privacy_config (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
  group_id BIGINT NOT NULL COMMENT '群组ID',
  privacy_enabled TINYINT NOT NULL DEFAULT 0 COMMENT '隐私保护是否开启: 0-关闭, 1-开启',
  admin_view_real TINYINT NOT NULL DEFAULT 1 COMMENT '管理员是否可见真实信息: 0-否, 1-是（预留）',
  created_by BIGINT COMMENT '创建人ID（群主）',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  UNIQUE KEY uk_group_id (group_id),
  INDEX idx_privacy_enabled (privacy_enabled),
  INDEX idx_created_by (created_by)
) COMMENT='群组隐私配置表';

-- 3. 群组模拟用户信息表（只存储虚拟ID和昵称）
CREATE TABLE IF NOT EXISTS t_group_virtual_member (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
  group_id BIGINT NOT NULL COMMENT '群组ID',
  real_user_id BIGINT NOT NULL COMMENT '真实用户ID',
  virtual_user_id BIGINT NOT NULL COMMENT '模拟用户ID（雪花算法生成）',
  virtual_nick_name VARCHAR(50) NOT NULL COMMENT '模拟昵称',
  display_order INT NOT NULL DEFAULT 0 COMMENT '显示序号（用于生成昵称）',
  status TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 1-正常, 2-已失效',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  UNIQUE KEY uk_group_user (group_id, real_user_id),
  INDEX idx_virtual_user (virtual_user_id),
  INDEX idx_status (status),
  INDEX idx_display_order (group_id, display_order)
) COMMENT='群组模拟用户信息表 - 仅存储虚拟ID和昵称';

