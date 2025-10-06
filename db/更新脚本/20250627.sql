ALTER TABLE im_friend ADD COLUMN `is_dnd`  tinyint comment '免打扰标识(Do Not Disturb)  0:关闭   1:开启';

ALTER TABLE im_group_member ADD COLUMN `is_dnd`  tinyint comment '免打扰标识(Do Not Disturb)  0:关闭   1:开启';

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