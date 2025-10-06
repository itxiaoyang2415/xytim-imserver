CREATE TABLE `im_user` (
  `id` BIGINT NOT NULL auto_increment PRIMARY key comment 'id',
  `user_name` VARCHAR(255) NOT NULL comment '用户名',
  `nick_name` VARCHAR(255) NOT NULL comment '用户昵称',
  `head_image` VARCHAR(255) DEFAULT '' comment '用户头像',
  `head_image_thumb` VARCHAR(255) DEFAULT '' comment '用户头像缩略图',
  `password` VARCHAR(255) NOT NULL comment '密码',
  `sex` tinyint (1) DEFAULT 0 comment '性别 0:男 1:女',
  `phone` VARCHAR(16) DEFAULT NULL comment '手机号码',
  `email` VARCHAR(32) DEFAULT NULL comment '邮箱',
  `company_id` BIGINT COMMENT '归属企业id',
  `company_name` VARCHAR(128) COMMENT '归属企业名称',
  `is_banned` tinyint (1) DEFAULT 0 comment '是否被封禁 0:否 1:是',
  `reason` VARCHAR(255) DEFAULT '' comment '被封禁原因',
  `type` SMALLINT DEFAULT 1 comment '用户类型 1:普通用户 2:审核账户',
  `signature` VARCHAR(1024) DEFAULT '' comment '个性签名',
  `is_manual_approve` tinyint (1) DEFAULT 0 comment '是否手动验证好友请求',
  `audio_tip` tinyint DEFAULT 1 comment '新消息语音提醒 bit-0:web端 bit-1:app端',
  `cid` VARCHAR(255) DEFAULT '' comment '客户端id,用于uni-push推送',
  `status` tinyint DEFAULT 0 comment '状态  0:正常  1:已注销',
  `last_login_time` datetime DEFAULT NULL comment '最后登录时间',
  `last_login_ip` VARCHAR(32) DEFAULT NULL comment '最后登陆ip',
  `created_time` datetime DEFAULT CURRENT_TIMESTAMP comment '创建时间',
  UNIQUE key `idx_user_name` (user_name),
  UNIQUE key `idx_phone` (phone),
  UNIQUE key `idx_email` (email),
  key `idx_nick_name` (nick_name)
) ENGINE = InnoDB CHARSET = utf8mb4 comment '用户';

CREATE TABLE `im_friend` (
  `id` BIGINT NOT NULL auto_increment PRIMARY key comment 'id',
  `user_id` BIGINT NOT NULL comment '用户id',
  `friend_id` BIGINT NOT NULL comment '好友id',
  `friend_nick_name` VARCHAR(255) NOT NULL comment '好友昵称',
  `friend_head_image` VARCHAR(255) DEFAULT '' comment '好友头像',
  `friend_company_name` VARCHAR(128) COMMENT '企业名称',
  `remark_nick_name` VARCHAR(255) DEFAULT '' comment '备注昵称',
  `is_dnd`  tinyint (1) DEFAULT 0 comment '免打扰标识(Do Not Disturb)  0:关闭   1:开启',
  `is_top` tinyint (1) DEFAULT 0 comment '是否置顶会话',
  `deleted` tinyint comment '删除标识  0：正常   1：已删除',
  `created_time` datetime DEFAULT CURRENT_TIMESTAMP comment '创建时间',
  key `idx_user_id` (`user_id`),
  key `idx_friend_id` (`friend_id`)
) ENGINE = InnoDB CHARSET = utf8mb4 comment '好友';


CREATE TABLE `im_friend_request` (
  `id` BIGINT NOT NULL auto_increment PRIMARY key comment 'id',
  `send_id` BIGINT NOT NULL comment '发起方用户ID',
  `send_nick_name` VARCHAR(255) NOT NULL comment '发起方昵称，冗余字段',
  `send_head_image` VARCHAR(255) DEFAULT NULL comment '发起方头像，冗余字段',
  `recv_id` BIGINT NOT NULL comment '接收方用户ID',
  `recv_nick_name` VARCHAR(255) NOT NULL comment '接收方昵称，冗余字段',
  `recv_head_image` VARCHAR(255) DEFAULT NULL comment '接收方头像，冗余字段',
  `remark`  VARCHAR(255) DEFAULT '' comment '申请备注',
  `status` tinyint DEFAULT 1 comment '状态  1:未处理 2:同意 3:拒绝 4:过期',
  `apply_time` datetime DEFAULT CURRENT_TIMESTAMP comment '申请时间',
  key `idx_send_id` (`send_id`),
  key `idx_recv_id` (`recv_id`),
  key `idx_apply_time` (`apply_time`)
) ENGINE = InnoDB CHARSET = utf8mb4 comment '好友申请列表';


CREATE TABLE `im_private_message` (
  `id` BIGINT NOT NULL auto_increment PRIMARY key comment 'id',
	`tmp_id` varchar(32)  comment '临时id,由前端生成',
  `send_id` BIGINT NOT NULL comment '发送用户id',
  `recv_id` BIGINT NOT NULL comment '接收用户id',
  `content` text CHARACTER SET utf8mb4 comment '发送内容',
  `type` tinyint (1) NOT NULL comment '消息类型 0:文字 1:图片 2:文件 3:语音 4:视频 21:提示',
  `quote_message_id`  BIGINT DEFAULT NULL comment '引用消息id',
  `status` tinyint (1) NOT NULL comment '状态 0:未读 1:已发送 2:撤回 3:已读',
  `send_time` datetime DEFAULT CURRENT_TIMESTAMP comment '发送时间',
  key `idx_send_id` (`send_id`),
  key `idx_recv_id` (`recv_id`)
) ENGINE = InnoDB CHARSET = utf8mb4 comment '私聊消息';

CREATE TABLE `im_group` (
  `id` BIGINT NOT NULL auto_increment PRIMARY key comment 'id',
  `name` VARCHAR(255) NOT NULL comment '群名字',
  `owner_id` BIGINT NOT NULL comment '群主id',
  `head_image` VARCHAR(255) DEFAULT '' comment '群头像',
  `head_image_thumb` VARCHAR(255) DEFAULT '' comment '群头像缩略图',
  `notice` VARCHAR(1024) DEFAULT '' comment '群公告',
  `top_message_id` BIGINT DEFAULT NULL  comment '置顶消息id',
  `is_all_muted` tinyint (1) DEFAULT 0 comment '是否开启全体禁言 0:否 1:是',
  `is_allow_invite` tinyint (1) DEFAULT 1 comment '是否允许普通成员邀请好友 0:否 1:是',
  `is_allow_share_card` tinyint (1) DEFAULT 1 comment '是否允许普通成员分享名片 0:否 1:是',
  `is_banned` tinyint (1) DEFAULT 0 comment '是否被封禁 0:否 1:是',
  `reason` VARCHAR(255) DEFAULT '' comment '被封禁原因',
  `dissolve` tinyint (1) DEFAULT 0 comment '是否已解散',
  `created_time` datetime DEFAULT CURRENT_TIMESTAMP comment '创建时间'
) ENGINE = InnoDB CHARSET = utf8mb4 comment '群';

CREATE TABLE `im_group_member` (
  `id` BIGINT NOT NULL auto_increment PRIMARY key comment 'id',
  `group_id` BIGINT NOT NULL comment '群id',
  `user_id` BIGINT NOT NULL comment '用户id',
  `user_nick_name` VARCHAR(255) DEFAULT '' comment '用户昵称',
  `remark_nick_name` VARCHAR(255) DEFAULT '' comment '显示昵称备注',
  `head_image` VARCHAR(255) DEFAULT '' comment '用户头像',
  `company_name` VARCHAR(128) COMMENT '企业名称',
  `remark_group_name` VARCHAR(255) DEFAULT '' comment '显示群名备注',
  `is_manager` tinyint (1) DEFAULT 0 comment '是否管理员 0:否 1:是',
  `is_muted` tinyint (1) DEFAULT 0 comment '是否被禁言 0:否 1:是',
  `is_dnd`  tinyint comment '免打扰标识(Do Not Disturb)  0:关闭   1:开启',
  `is_top_message` tinyint (1) DEFAULT 0 comment '是否显示置顶消息',
  `is_top` tinyint (1) DEFAULT 0 comment '是否置顶会话',
  `quit` tinyint (1) DEFAULT 0 comment '是否已退出',
  `quit_time` datetime DEFAULT NULL comment '退出时间',
  `created_time` datetime DEFAULT CURRENT_TIMESTAMP comment '创建时间',
  `version` INT DEFAULT 0 comment '版本号',
  key `idx_group_id` (`group_id`),
  key `idx_user_id` (`user_id`)
) ENGINE = InnoDB CHARSET = utf8mb4 comment '群成员';

CREATE TABLE `im_group_message` (
  `id` BIGINT NOT NULL auto_increment PRIMARY key comment 'id',
	`tmp_id` varchar(32)  comment '临时id,由前端生成',
  `group_id` BIGINT NOT NULL comment '群id',
  `send_id` BIGINT NOT NULL comment '发送用户id',
  `send_nick_name` VARCHAR(255) DEFAULT '' comment '发送用户昵称',
  `recv_ids` VARCHAR(1024) DEFAULT '' comment '接收用户id,逗号分隔，为空表示发给所有成员',
  `content` text CHARACTER SET utf8mb4 COMMENT '发送内容',
  `at_user_ids` VARCHAR(1024) comment '被@的用户id列表，逗号分隔',
  `receipt` tinyint DEFAULT 0 comment '是否回执消息',
  `receipt_ok` tinyint DEFAULT 0 comment '回执消息是否完成',
  `type` tinyint (1) NOT NULL comment '消息类型 0:文字 1:图片 2:文件 3:语音 4:视频 21:提示',
  `quote_message_id`  BIGINT DEFAULT NULL comment '引用消息id',
  `status` tinyint (1) DEFAULT 0 comment '状态 0:未发出  2:撤回 ',
  `send_time` datetime DEFAULT CURRENT_TIMESTAMP comment '发送时间',
  key `idx_group_id` (group_id)
) ENGINE = InnoDB CHARSET = utf8mb4 comment '群消息';

CREATE TABLE `im_system_message` (
  `id` BIGINT NOT NULL auto_increment PRIMARY key comment 'id',
  `title` VARCHAR(64) NOT NULL comment '标题',
  `cover_url` VARCHAR(255) comment '封面图片',
  `intro` VARCHAR(1024) NOT NULL comment '简介',
  `content_type` tinyint (1) DEFAULT 0 comment '内容类型 0:富文本  1:外部链接',
  `rich_text` text comment '富文本内容，base64编码',
  `extern_link` VARCHAR(255) comment '外部链接',
  `deleted` tinyint DEFAULT 0 comment '删除标识  0：正常   1：已删除',
  `creator` BIGINT comment '创建者',
  `create_time` datetime comment '创建时间'
) ENGINE = InnoDB CHARSET = utf8mb4 comment '系统消息';

CREATE TABLE `im_sm_push_task` (
  `id` BIGINT NOT NULL auto_increment PRIMARY key comment 'id',
  `message_id` BIGINT NOT NULL comment '系统消息id',
  `seq_no` BIGINT comment '发送序列号',
  `send_time` datetime comment '推送时间',
  `status` tinyint DEFAULT 1 comment '状态 1:待发送 2:发送中 3:已发送 4:已取消',
  `send_to_all` tinyint DEFAULT 1 comment '是否发送给全体用户',
  `recv_ids` VARCHAR(1024) comment '接收用户id,逗号分隔,send_to_all为false时有效',
  `deleted` tinyint comment '删除标识  0：正常   1：已删除',
  `creator` BIGINT comment '创建者',
  `create_time` datetime comment '创建时间',
  UNIQUE KEY `idx_seq_no` (seq_no)
) ENGINE = InnoDB CHARSET = utf8mb4 comment '系统消息推送任务';

CREATE TABLE `im_sensitive_word` (
  `id` BIGINT NOT NULL auto_increment PRIMARY key comment 'id',
  `content` VARCHAR(64) NOT NULL comment '敏感词内容',
  `enabled` tinyint DEFAULT 0 comment '是否启用 0:未启用 1:启用',
  `creator` BIGINT DEFAULT NULL comment '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP comment '创建时间'
) ENGINE = InnoDB CHARSET = utf8mb4 comment '敏感词';

CREATE TABLE `im_user_blacklist` (
  `id` BIGINT NOT NULL auto_increment PRIMARY key comment 'id',
  `from_user_id` BIGINT NOT NULL comment '拉黑用户id',
  `to_user_id` BIGINT NOT NULL comment '被拉黑用户id',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP comment '创建时间',
  key `idx_from_user_id` (from_user_id)
) ENGINE = InnoDB CHARSET = utf8mb4 comment '用户黑名单';

CREATE TABLE `im_file_info` (
  `id` BIGINT NOT NULL auto_increment PRIMARY key comment 'id',
  `file_name` VARCHAR(255) NOT NULL comment '文件名',
  `file_path` VARCHAR(255) NOT NULL comment '文件地址',
  `file_size` INTEGER NOT NULL comment '文件大小',
  `file_type` tinyint NOT NULL comment '0:普通文件 1:图片 2:视频',
  `compressed_path` VARCHAR(255) DEFAULT NULL comment '压缩文件路径',
  `cover_path` VARCHAR(255) DEFAULT NULL comment '封面文件路径，仅视频文件有效',
  `upload_time` datetime DEFAULT CURRENT_TIMESTAMP comment '上传时间',
  `is_permanent` tinyint DEFAULT 0 comment '是否永久文件',
  `md5` VARCHAR(64) NOT NULL comment '文件md5',
  KEY `idx_md5` (md5)
) ENGINE = InnoDB CHARSET = utf8mb4 comment '文件';


CREATE TABLE `im_user_complaint` (
  `id` BIGINT NOT NULL auto_increment PRIMARY key comment 'id',
	`user_id` BIGINT NOT NULL comment '用户id',
  `target_type` tinyint NOT NULL comment '投诉对象类型 1:用户 2:群聊',
  `target_id` BIGINT NOT NULL comment '投诉对象id',
  `target_name` VARCHAR(255) NOT NULL comment '投诉对象名称',
  `type`  tinyint NOT NULL comment '投诉原因类型 1:对我造成骚扰 2:疑似诈骗 3:传播不良内容 99:其他',
  `images` VARCHAR(4096) DEFAULT '' comment '图片列表,最多9张',
  `content` VARCHAR(1024) DEFAULT '' comment '投诉内容',
  `status` tinyint DEFAULT 1 comment '状态 1:未处理 2:已处理',
  `resolved_admin_id` BIGINT DEFAULT NULL comment '处理投诉的管理员id',
  `resolved_type` VARCHAR(255) DEFAULT NULL comment '处理结果类型 1:已处理 2:不予处理 3:未涉及不正规行为 4:其他',
  `resolved_summary` VARCHAR(255) DEFAULT NULL comment '处理结果摘要',
  `resolved_time` VARCHAR(255) DEFAULT NULL comment '处理时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP comment '创建时间',
   key `idx_user_id` (user_id)
) ENGINE = InnoDB CHARSET = utf8mb4 comment '用户投诉';

CREATE TABLE im_company (
  `id` BIGINT NOT NULL auto_increment PRIMARY key comment 'id',
  `name` VARCHAR(128) NOT NULL COMMENT '企业名称',
  `code` VARCHAR(64) NOT NULL COMMENT '统一社会信用代码',
  `license` VARCHAR(256) COMMENT '营业执照',
	`biz_scope` VARCHAR(256) COMMENT '业务范围',
  `contact_person` VARCHAR(32) COMMENT '联系人姓名',
  `contact_phone` VARCHAR(20) COMMENT '联系电话',
  `deleted` tinyint DEFAULT 0 comment '删除标识  0：正常   1：已删除',
  `creator` BIGINT comment '创建者',
  `create_time` datetime comment '创建时间'
) ENGINE = InnoDB CHARSET = utf8mb4 comment '企业信息';


CREATE TABLE `t_red_packet` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `packet_no` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '红包编号',
  `sender_id` bigint NOT NULL COMMENT '发送者ID',
  `packet_type` tinyint NOT NULL COMMENT '红包类型:1-普通红包,2-拼手气红包',
  `total_amount` decimal(15,2) NOT NULL COMMENT '总金额',
  `total_count` int NOT NULL COMMENT '总个数',
  `remaining_amount` decimal(15,2) NOT NULL COMMENT '剩余金额',
  `remaining_count` int NOT NULL COMMENT '剩余个数',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态:1-创建,2-发放中,3-已领完,4-已过期,5-已退款',
  `expire_time` timestamp NOT NULL COMMENT '过期时间',
  `message` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '祝福语',
  `chat_type` tinyint NOT NULL COMMENT '聊天类型:1-单聊,2-群聊',
  `target_id` bigint NOT NULL COMMENT '目标ID(用户ID或群ID)',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_packet_no` (`packet_no`),
  KEY `idx_sender` (`sender_id`),
  KEY `idx_target` (`target_id`,`chat_type`),
  KEY `idx_status_expire` (`status`,`expire_time`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='红包表';

CREATE TABLE `t_red_packet_receive` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `packet_id` bigint NOT NULL COMMENT '红包ID',
  `receiver_id` bigint NOT NULL COMMENT '领取者ID',
  `amount` decimal(15,2) NOT NULL COMMENT '领取金额',
  `is_best_luck` tinyint DEFAULT '0' COMMENT '是否手气最佳:0-否,1-是',
  `receive_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '领取时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_packet_receiver` (`packet_id`,`receiver_id`),
  KEY `idx_packet_id` (`packet_id`),
  KEY `idx_receiver` (`receiver_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='红包领取记录表';

CREATE TABLE `t_user_wallet` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '用户ID，关联IM系统用户',
  `balance` decimal(15,2) NOT NULL DEFAULT '0.00' COMMENT '可用余额',
  `frozen_balance` decimal(15,2) NOT NULL DEFAULT '0.00' COMMENT '冻结金额',
  `currency` varchar(10) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'CNY' COMMENT '币种',
  `wallet_status` tinyint NOT NULL DEFAULT '1' COMMENT '钱包状态:1-正常,2-冻结,3-禁用',
  `security_level` tinyint NOT NULL DEFAULT '1' COMMENT '安全等级',
  `pay_password` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '支付密码(加密存储)',
  `version` int NOT NULL DEFAULT '0' COMMENT '版本号(乐观锁)',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_id` (`user_id`),
  KEY `idx_status` (`wallet_status`),
  KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户钱包表';




CREATE TABLE `t_wallet_order` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `order_no` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '订单号',
  `type` tinyint NOT NULL COMMENT '类型:1-充值,2-提现',
  `amount` decimal(15,2) NOT NULL COMMENT '金额',
  `channel` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '支付渠道:alipay,wechat,bank',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态:1-处理中,2-成功,3-失败,4-已取消',
  `channel_order_no` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '渠道订单号',
  `notify_data` text COLLATE utf8mb4_unicode_ci COMMENT '渠道回调数据',
  `completed_time` timestamp NULL DEFAULT NULL COMMENT '完成时间',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_no` (`order_no`),
  KEY `idx_user_type` (`user_id`,`type`),
  KEY `idx_status` (`status`),
  KEY `idx_channel_order` (`channel_order_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='充值提现表';


CREATE TABLE `t_wallet_transaction` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `transaction_no` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '交易流水号',
  `wallet_id` bigint DEFAULT NULL COMMENT '钱包ID',
  `from_user_id` bigint NOT NULL COMMENT '付款用户ID',
  `to_user_id` bigint NOT NULL COMMENT '收款用户ID',
  `amount` decimal(15,2) NOT NULL COMMENT '交易金额',
  `transaction_type` tinyint NOT NULL COMMENT '交易类型:1-转账,2-红包,3-充值,4-提现,5-退款',
  `business_type` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '业务类型',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态:1-处理中,2-成功,3-失败,4-已退款',
  `remark` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '交易备注',
  `relation_id` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '关联业务ID(如红包ID、订单ID)',
  `fee` decimal(15,2) DEFAULT '0.00' COMMENT '手续费',
  `before_balance` decimal(15,2) DEFAULT NULL COMMENT '交易前余额',
  `after_balance` decimal(15,2) DEFAULT NULL COMMENT '交易后余额',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_transaction_no` (`transaction_no`),
  KEY `idx_from_user` (`from_user_id`,`created_at`),
  KEY `idx_to_user` (`to_user_id`,`created_at`),
  KEY `idx_type_status` (`transaction_type`,`status`),
  KEY `idx_relation` (`relation_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='交易流水表';

