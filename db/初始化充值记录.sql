-- =====================================================
-- 初始化充值记录SQL
-- 为每个已有钱包的用户生成1000元的充值记录
-- 执行前请确认数据库环境
-- =====================================================

USE `im-platform`;

-- 为用户1生成充值记录
INSERT INTO `t_wallet_order` (
    `user_id`, 
    `order_no`, 
    `type`, 
    `amount`, 
    `channel`, 
    `status`, 
    `channel_order_no`, 
    `completed_time`, 
    `created_at`, 
    `updated_at`
) VALUES (
    1,
    'ORD20251007000001',
    1,
    1000.00,
    'alipay',
    2,
    '2025100722001400001000000001',
    NOW(),
    NOW(),
    NOW()
);

INSERT INTO `t_wallet_transaction` (
    `transaction_no`,
    `wallet_id`,
    `from_user_id`,
    `to_user_id`,
    `amount`,
    `transaction_type`,
    `business_type`,
    `status`,
    `remark`,
    `relation_id`,
    `fee`,
    `before_balance`,
    `after_balance`,
    `created_at`,
    `updated_at`
) VALUES (
    'TXN20251007000001',
    1,
    0,
    1,
    1000.00,
    3,
    'RECHARGE',
    2,
    '初始化充值',
    'ORD20251007000001',
    0.00,
    978.00,
    1978.00,
    NOW(),
    NOW()
);

-- 为用户3生成充值记录
INSERT INTO `t_wallet_order` (
    `user_id`, 
    `order_no`, 
    `type`, 
    `amount`, 
    `channel`, 
    `status`, 
    `channel_order_no`, 
    `completed_time`, 
    `created_at`, 
    `updated_at`
) VALUES (
    3,
    'ORD20251007000003',
    1,
    1000.00,
    'wechat',
    2,
    '4200001234567890000000000003',
    NOW(),
    NOW(),
    NOW()
);

INSERT INTO `t_wallet_transaction` (
    `transaction_no`,
    `wallet_id`,
    `from_user_id`,
    `to_user_id`,
    `amount`,
    `transaction_type`,
    `business_type`,
    `status`,
    `remark`,
    `relation_id`,
    `fee`,
    `before_balance`,
    `after_balance`,
    `created_at`,
    `updated_at`
) VALUES (
    'TXN20251007000003',
    2,
    0,
    3,
    1000.00,
    3,
    'RECHARGE',
    2,
    '初始化充值',
    'ORD20251007000003',
    0.00,
    1168.79,
    2168.79,
    NOW(),
    NOW()
);

-- 为用户4生成充值记录
INSERT INTO `t_wallet_order` (
    `user_id`, 
    `order_no`, 
    `type`, 
    `amount`, 
    `channel`, 
    `status`, 
    `channel_order_no`, 
    `completed_time`, 
    `created_at`, 
    `updated_at`
) VALUES (
    4,
    'ORD20251007000004',
    1,
    1000.00,
    'bank',
    2,
    'BANK20251007000000000004',
    NOW(),
    NOW(),
    NOW()
);

INSERT INTO `t_wallet_transaction` (
    `transaction_no`,
    `wallet_id`,
    `from_user_id`,
    `to_user_id`,
    `amount`,
    `transaction_type`,
    `business_type`,
    `status`,
    `remark`,
    `relation_id`,
    `fee`,
    `before_balance`,
    `after_balance`,
    `created_at`,
    `updated_at`
) VALUES (
    'TXN20251007000004',
    3,
    0,
    4,
    1000.00,
    3,
    'RECHARGE',
    2,
    '初始化充值',
    'ORD20251007000004',
    0.00,
    931.21,
    1931.21,
    NOW(),
    NOW()
);

-- 为用户5生成充值记录
INSERT INTO `t_wallet_order` (
    `user_id`, 
    `order_no`, 
    `type`, 
    `amount`, 
    `channel`, 
    `status`, 
    `channel_order_no`, 
    `completed_time`, 
    `created_at`, 
    `updated_at`
) VALUES (
    5,
    'ORD20251007000005',
    1,
    1000.00,
    'alipay',
    2,
    '2025100722001400001000000005',
    NOW(),
    NOW(),
    NOW()
);

INSERT INTO `t_wallet_transaction` (
    `transaction_no`,
    `wallet_id`,
    `from_user_id`,
    `to_user_id`,
    `amount`,
    `transaction_type`,
    `business_type`,
    `status`,
    `remark`,
    `relation_id`,
    `fee`,
    `before_balance`,
    `after_balance`,
    `created_at`,
    `updated_at`
) VALUES (
    'TXN20251007000005',
    4,
    0,
    5,
    1000.00,
    3,
    'RECHARGE',
    2,
    '初始化充值',
    'ORD20251007000005',
    0.00,
    700.00,
    1700.00,
    NOW(),
    NOW()
);

-- =====================================================
-- 执行完成
-- 统计：为4个用户生成了充值记录
-- - 用户1: 1000元 (支付宝)
-- - 用户3: 1000元 (微信支付)
-- - 用户4: 1000元 (银行卡)
-- - 用户5: 1000元 (支付宝)
-- =====================================================

-- 查询验证
SELECT 
    wo.id AS '订单ID',
    wo.user_id AS '用户ID',
    wo.order_no AS '订单号',
    wo.amount AS '金额',
    wo.channel AS '支付渠道',
    wo.status AS '状态',
    wo.created_at AS '创建时间'
FROM t_wallet_order wo
WHERE wo.order_no IN (
    'ORD20251007000001',
    'ORD20251007000003',
    'ORD20251007000004',
    'ORD20251007000005'
);

SELECT 
    wt.id AS '流水ID',
    wt.transaction_no AS '流水号',
    wt.to_user_id AS '用户ID',
    wt.amount AS '金额',
    wt.transaction_type AS '类型',
    wt.status AS '状态',
    wt.created_at AS '创建时间'
FROM t_wallet_transaction wt
WHERE wt.transaction_no IN (
    'TXN20251007000001',
    'TXN20251007000003',
    'TXN20251007000004',
    'TXN20251007000005'
);

