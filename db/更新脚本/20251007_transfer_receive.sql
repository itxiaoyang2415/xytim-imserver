-- 转账需要领取功能 - 添加转账表
-- 执行时间：2025-10-07

USE im_platform;

-- 转账表
CREATE TABLE IF NOT EXISTS t_transfer (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    transfer_no VARCHAR(32) NOT NULL COMMENT '转账编号',
    from_user_id BIGINT NOT NULL COMMENT '转账方用户ID',
    to_user_id BIGINT NOT NULL COMMENT '收款方用户ID',
    amount DECIMAL(15,2) NOT NULL COMMENT '转账金额',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态:1-待领取,2-已领取,3-已过期,4-已退款',
    remark VARCHAR(200) COMMENT '转账备注',
    expire_time TIMESTAMP NOT NULL COMMENT '过期时间(24小时)',
    receive_time TIMESTAMP NULL COMMENT '领取时间',
    refund_time TIMESTAMP NULL COMMENT '退款时间',
    transaction_no VARCHAR(32) COMMENT '关联的交易流水号',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_transfer_no (transfer_no),
    INDEX idx_from_user (from_user_id),
    INDEX idx_to_user (to_user_id),
    INDEX idx_status_expire (status, expire_time),
    INDEX idx_transaction (transaction_no)
) COMMENT='转账表';

