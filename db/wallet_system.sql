-- 钱包系统表结构
-- 使用数据库：im_platform

USE im_platform;

-- 用户钱包表
CREATE TABLE IF NOT EXISTS t_user_wallet (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL COMMENT '用户ID，关联IM系统用户',
  balance DECIMAL(15,2) NOT NULL DEFAULT 0.00 COMMENT '可用余额',
  frozen_balance DECIMAL(15,2) NOT NULL DEFAULT 0.00 COMMENT '冻结金额',
  currency VARCHAR(10) NOT NULL DEFAULT 'CNY' COMMENT '币种',
  wallet_status TINYINT NOT NULL DEFAULT 1 COMMENT '钱包状态:1-正常,2-冻结,3-禁用',
  security_level TINYINT NOT NULL DEFAULT 1 COMMENT '安全等级',
  pay_password VARCHAR(100) COMMENT '支付密码(加密存储)',
  version INT NOT NULL DEFAULT 0 COMMENT '版本号(乐观锁)',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_user_id (user_id),
  INDEX idx_status (wallet_status),
  INDEX idx_created_at (created_at)
) COMMENT='用户钱包表';

-- 交易流水表
CREATE TABLE IF NOT EXISTS t_wallet_transaction (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    transaction_no VARCHAR(32) NOT NULL COMMENT '交易流水号',
    wallet_id BIGINT NULL COMMENT '钱包ID',
    from_user_id BIGINT NOT NULL COMMENT '付款用户ID',
    to_user_id BIGINT NOT NULL COMMENT '收款用户ID',
    amount DECIMAL(15,2) NOT NULL COMMENT '交易金额',
    transaction_type TINYINT NOT NULL COMMENT '交易类型:1-转账,2-红包,3-充值,4-提现,5-退款',
    business_type VARCHAR(50) COMMENT '业务类型',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态:1-处理中,2-成功,3-失败,4-已退款',
    remark VARCHAR(200) COMMENT '交易备注',
    relation_id VARCHAR(50) COMMENT '关联业务ID(如红包ID、订单ID)',
    fee DECIMAL(15,2) DEFAULT 0.00 COMMENT '手续费',
    before_balance DECIMAL(15,2) COMMENT '交易前余额',
    after_balance DECIMAL(15,2) COMMENT '交易后余额',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_transaction_no (transaction_no),
    INDEX idx_from_user (from_user_id, created_at),
    INDEX idx_to_user (to_user_id, created_at),
    INDEX idx_type_status (transaction_type, status),
    INDEX idx_relation (relation_id)
) COMMENT='交易流水表';

-- 充值提现表
CREATE TABLE IF NOT EXISTS t_wallet_order (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    order_no VARCHAR(32) NOT NULL COMMENT '订单号',
    type TINYINT NOT NULL COMMENT '类型:1-充值,2-提现',
    amount DECIMAL(15,2) NOT NULL COMMENT '金额',
    channel VARCHAR(50) NOT NULL COMMENT '支付渠道:alipay,wechat,bank',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态:1-处理中,2-成功,3-失败,4-已取消',
    channel_order_no VARCHAR(100) COMMENT '渠道订单号',
    notify_data TEXT COMMENT '渠道回调数据',
    completed_time TIMESTAMP NULL COMMENT '完成时间',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_order_no (order_no),
    INDEX idx_user_type (user_id, type),
    INDEX idx_status (status),
    INDEX idx_channel_order (channel_order_no)
) COMMENT='充值提现表';

-- 红包表
CREATE TABLE IF NOT EXISTS t_red_packet (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    packet_no VARCHAR(32) NOT NULL COMMENT '红包编号',
    sender_id BIGINT NOT NULL COMMENT '发送者ID',
    packet_type TINYINT NOT NULL COMMENT '红包类型:1-普通红包,2-拼手气红包',
    total_amount DECIMAL(15,2) NOT NULL COMMENT '总金额',
    total_count INT NOT NULL COMMENT '总个数',
    remaining_amount DECIMAL(15,2) NOT NULL COMMENT '剩余金额',
    remaining_count INT NOT NULL COMMENT '剩余个数',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态:1-已创建,2-发放中,3-已领完,4-已过期,5-已退款',
    expire_time TIMESTAMP NOT NULL COMMENT '过期时间',
    message VARCHAR(100) COMMENT '祝福语',
    chat_type TINYINT NOT NULL COMMENT '聊天类型:1-单聊,2-群聊',
    target_id BIGINT NOT NULL COMMENT '目标ID(用户ID或群ID)',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_packet_no (packet_no),
    INDEX idx_sender (sender_id),
    INDEX idx_target (target_id, chat_type),
    INDEX idx_status_expire (status, expire_time)
) COMMENT='红包表';

-- 红包领取记录表
CREATE TABLE IF NOT EXISTS t_red_packet_receive (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    packet_id BIGINT NOT NULL COMMENT '红包ID',
    receiver_id BIGINT NOT NULL COMMENT '领取者ID',
    amount DECIMAL(15,2) NOT NULL COMMENT '领取金额',
    is_best_luck TINYINT DEFAULT 0 COMMENT '是否手气最佳:0-否,1-是',
    receive_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '领取时间',
    INDEX idx_packet_id (packet_id),
    INDEX idx_receiver (receiver_id),
    UNIQUE KEY uk_packet_receiver (packet_id, receiver_id)
) COMMENT='红包领取记录表';

