/*
 Navicat Premium Dump SQL

 Source Server         : 本地数据库
 Source Server Type    : MySQL
 Source Server Version : 80043 (8.0.43)
 Source Host           : 127.0.0.1:3307
 Source Schema         : im_platform

 Target Server Type    : MySQL
 Target Server Version : 80043 (8.0.43)
 File Encoding         : 65001

 Date: 07/10/2025 07:04:28
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for im_company
-- ----------------------------
DROP TABLE IF EXISTS `im_company`;
CREATE TABLE `im_company`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '企业名称',
  `code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '统一社会信用代码',
  `license` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '营业执照',
  `biz_scope` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '业务范围',
  `contact_person` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '联系人姓名',
  `contact_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '联系电话',
  `deleted` tinyint NULL DEFAULT 0 COMMENT '删除标识  0：正常   1：已删除',
  `creator` bigint NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '企业信息' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of im_company
-- ----------------------------

-- ----------------------------
-- Table structure for im_file_info
-- ----------------------------
DROP TABLE IF EXISTS `im_file_info`;
CREATE TABLE `im_file_info`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `file_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '文件名',
  `file_path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '文件地址',
  `file_size` int NOT NULL COMMENT '文件大小',
  `file_type` tinyint NOT NULL COMMENT '0:普通文件 1:图片 2:视频',
  `compressed_path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '压缩文件路径',
  `cover_path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '封面文件路径，仅视频文件有效',
  `upload_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间',
  `is_permanent` tinyint NULL DEFAULT 0 COMMENT '是否永久文件',
  `md5` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '文件md5',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_md5`(`md5` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '文件' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of im_file_info
-- ----------------------------

-- ----------------------------
-- Table structure for im_friend
-- ----------------------------
DROP TABLE IF EXISTS `im_friend`;
CREATE TABLE `im_friend`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `user_id` bigint NOT NULL COMMENT '用户id',
  `friend_id` bigint NOT NULL COMMENT '好友id',
  `friend_nick_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '好友昵称',
  `friend_head_image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '好友头像',
  `friend_company_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '企业名称',
  `remark_nick_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '备注昵称',
  `is_dnd` tinyint(1) NULL DEFAULT 0 COMMENT '免打扰标识(Do Not Disturb)  0:关闭   1:开启',
  `is_top` tinyint(1) NULL DEFAULT 0 COMMENT '是否置顶会话',
  `deleted` tinyint NULL DEFAULT NULL COMMENT '删除标识  0：正常   1：已删除',
  `created_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_friend_id`(`friend_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '好友' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of im_friend
-- ----------------------------
INSERT INTO `im_friend` VALUES (1, 2, 1, 'user', '', NULL, '', 0, 0, 0, '2025-10-06 14:36:57');
INSERT INTO `im_friend` VALUES (2, 1, 2, 'user1', '', NULL, '', 0, 0, 0, '2025-10-06 14:36:57');
INSERT INTO `im_friend` VALUES (3, 4, 3, 'yt', '', NULL, '', 0, 0, 0, '2025-10-07 02:33:23');
INSERT INTO `im_friend` VALUES (4, 3, 4, 'ty', '', NULL, '', 0, 0, 0, '2025-10-07 02:33:23');
INSERT INTO `im_friend` VALUES (5, 5, 3, 'yt', '', NULL, '', 0, 0, 0, '2025-10-07 04:42:09');
INSERT INTO `im_friend` VALUES (6, 3, 5, 'td', '', NULL, '', 0, 0, 0, '2025-10-07 04:42:09');
INSERT INTO `im_friend` VALUES (7, 5, 4, 'ty', '', NULL, '', 0, 0, 0, '2025-10-07 04:42:18');
INSERT INTO `im_friend` VALUES (8, 4, 5, 'td', '', NULL, '', 0, 0, 0, '2025-10-07 04:42:18');

-- ----------------------------
-- Table structure for im_friend_request
-- ----------------------------
DROP TABLE IF EXISTS `im_friend_request`;
CREATE TABLE `im_friend_request`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `send_id` bigint NOT NULL COMMENT '发起方用户ID',
  `send_nick_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '发起方昵称，冗余字段',
  `send_head_image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '发起方头像，冗余字段',
  `recv_id` bigint NOT NULL COMMENT '接收方用户ID',
  `recv_nick_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '接收方昵称，冗余字段',
  `recv_head_image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '接收方头像，冗余字段',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '申请备注',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态  1:未处理 2:同意 3:拒绝 4:过期',
  `apply_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '申请时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_send_id`(`send_id` ASC) USING BTREE,
  INDEX `idx_recv_id`(`recv_id` ASC) USING BTREE,
  INDEX `idx_apply_time`(`apply_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '好友申请列表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of im_friend_request
-- ----------------------------

-- ----------------------------
-- Table structure for im_group
-- ----------------------------
DROP TABLE IF EXISTS `im_group`;
CREATE TABLE `im_group`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '群名字',
  `owner_id` bigint NOT NULL COMMENT '群主id',
  `head_image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '群头像',
  `head_image_thumb` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '群头像缩略图',
  `notice` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '群公告',
  `top_message_id` bigint NULL DEFAULT NULL COMMENT '置顶消息id',
  `is_all_muted` tinyint(1) NULL DEFAULT 0 COMMENT '是否开启全体禁言 0:否 1:是',
  `is_allow_invite` tinyint(1) NULL DEFAULT 1 COMMENT '是否允许普通成员邀请好友 0:否 1:是',
  `is_allow_share_card` tinyint(1) NULL DEFAULT 1 COMMENT '是否允许普通成员分享名片 0:否 1:是',
  `is_banned` tinyint(1) NULL DEFAULT 0 COMMENT '是否被封禁 0:否 1:是',
  `reason` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '被封禁原因',
  `dissolve` tinyint(1) NULL DEFAULT 0 COMMENT '是否已解散',
  `created_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '群' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of im_group
-- ----------------------------
INSERT INTO `im_group` VALUES (1, 'td创建的群聊', 5, '', '', '', NULL, 0, 1, 1, 0, '', 0, '2025-10-07 04:42:46');
INSERT INTO `im_group` VALUES (2, 'td创建的群聊', 5, '', '', '1', NULL, 0, 1, 1, 0, '', 1, '2025-10-07 04:42:59');
INSERT INTO `im_group` VALUES (3, 'td创建的群聊', 5, '', '', '1', NULL, 0, 1, 1, 0, '', 1, '2025-10-07 04:43:11');
INSERT INTO `im_group` VALUES (4, 'td创建的群聊', 5, '', '', '1', NULL, 0, 1, 1, 0, '', 1, '2025-10-07 04:47:56');
INSERT INTO `im_group` VALUES (5, 'td创建的群聊', 5, '', '', '1', NULL, 0, 1, 1, 0, '', 1, '2025-10-07 04:48:27');
INSERT INTO `im_group` VALUES (6, 'td创建的群聊', 5, '', '', '1', NULL, 0, 1, 1, 0, '', 1, '2025-10-07 04:49:23');
INSERT INTO `im_group` VALUES (7, 'td创建的群聊', 5, '', '', '1', NULL, 0, 1, 1, 0, '', 1, '2025-10-07 04:56:29');

-- ----------------------------
-- Table structure for im_group_member
-- ----------------------------
DROP TABLE IF EXISTS `im_group_member`;
CREATE TABLE `im_group_member`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `group_id` bigint NOT NULL COMMENT '群id',
  `user_id` bigint NOT NULL COMMENT '用户id',
  `user_nick_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '用户昵称',
  `remark_nick_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '显示昵称备注',
  `head_image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '用户头像',
  `company_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '企业名称',
  `remark_group_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '显示群名备注',
  `is_manager` tinyint(1) NULL DEFAULT 0 COMMENT '是否管理员 0:否 1:是',
  `is_muted` tinyint(1) NULL DEFAULT 0 COMMENT '是否被禁言 0:否 1:是',
  `is_dnd` tinyint NULL DEFAULT NULL COMMENT '免打扰标识(Do Not Disturb)  0:关闭   1:开启',
  `is_top_message` tinyint(1) NULL DEFAULT 0 COMMENT '是否显示置顶消息',
  `is_top` tinyint(1) NULL DEFAULT 0 COMMENT '是否置顶会话',
  `quit` tinyint(1) NULL DEFAULT 0 COMMENT '是否已退出',
  `quit_time` datetime NULL DEFAULT NULL COMMENT '退出时间',
  `created_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `version` int NULL DEFAULT 0 COMMENT '版本号',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_group_id`(`group_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '群成员' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of im_group_member
-- ----------------------------
INSERT INTO `im_group_member` VALUES (1, 1, 5, 'td', '', '', NULL, '', 0, 0, NULL, 0, 0, 0, NULL, '2025-10-07 04:42:46', 1);
INSERT INTO `im_group_member` VALUES (2, 2, 5, 'td', '1', '', NULL, '', 0, 0, NULL, 0, 0, 1, '2025-10-07 06:09:21', '2025-10-07 04:42:59', 3);
INSERT INTO `im_group_member` VALUES (3, 3, 5, 'td', '1', '', NULL, '', 0, 0, NULL, 0, 0, 1, '2025-10-07 06:09:14', '2025-10-07 04:43:11', 4);
INSERT INTO `im_group_member` VALUES (4, 4, 5, 'td', '1hjgjkh', '', NULL, '', 0, 0, NULL, 0, 0, 1, '2025-10-07 06:09:08', '2025-10-07 04:47:56', 5);
INSERT INTO `im_group_member` VALUES (5, 5, 5, 'td', '1hjgjkh', '', NULL, '', 0, 0, NULL, 0, 0, 1, '2025-10-07 06:09:03', '2025-10-07 04:48:27', 6);
INSERT INTO `im_group_member` VALUES (6, 6, 5, 'td', '业务群1', '', NULL, '', 0, 0, NULL, 0, 0, 1, '2025-10-07 06:08:57', '2025-10-07 04:49:23', 7);
INSERT INTO `im_group_member` VALUES (7, 7, 5, 'td', '1', '', NULL, '', 0, 0, NULL, 0, 0, 1, '2025-10-07 06:08:44', '2025-10-07 04:56:29', 9);
INSERT INTO `im_group_member` VALUES (8, 7, 3, 'yt', '', '', NULL, '', 0, 0, NULL, 0, 0, 1, '2025-10-07 06:08:44', '2025-10-07 04:56:39', 9);
INSERT INTO `im_group_member` VALUES (9, 7, 4, 'ty', '', '', NULL, '', 0, 0, NULL, 0, 0, 1, '2025-10-07 06:08:44', '2025-10-07 04:56:39', 9);
INSERT INTO `im_group_member` VALUES (10, 1, 3, 'yt', '', '', NULL, '', 0, 0, NULL, 0, 0, 0, NULL, '2025-10-07 06:09:28', 2);
INSERT INTO `im_group_member` VALUES (11, 1, 4, 'ty', '', '', NULL, '', 0, 0, NULL, 0, 0, 0, NULL, '2025-10-07 06:09:28', 2);

-- ----------------------------
-- Table structure for im_group_message
-- ----------------------------
DROP TABLE IF EXISTS `im_group_message`;
CREATE TABLE `im_group_message`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `tmp_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '临时id,由前端生成',
  `group_id` bigint NOT NULL COMMENT '群id',
  `send_id` bigint NOT NULL COMMENT '发送用户id',
  `send_nick_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '发送用户昵称',
  `recv_ids` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '接收用户id,逗号分隔，为空表示发给所有成员',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '发送内容',
  `at_user_ids` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '被@的用户id列表，逗号分隔',
  `receipt` tinyint NULL DEFAULT 0 COMMENT '是否回执消息',
  `receipt_ok` tinyint NULL DEFAULT 0 COMMENT '回执消息是否完成',
  `type` tinyint(1) NOT NULL COMMENT '消息类型 0:文字 1:图片 2:文件 3:语音 4:视频 21:提示',
  `quote_message_id` bigint NULL DEFAULT NULL COMMENT '引用消息id',
  `status` tinyint(1) NULL DEFAULT 0 COMMENT '状态 0:未发出  2:撤回 ',
  `send_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发送时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_group_id`(`group_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 17 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '群消息' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of im_group_message
-- ----------------------------
INSERT INTO `im_group_message` VALUES (1, NULL, 7, 5, 'td', '', '\'td\'邀请\'yt,ty\'加入了群聊', NULL, 0, 0, 21, NULL, 0, '2025-10-07 04:56:39');
INSERT INTO `im_group_message` VALUES (2, '1759784218217181', 7, 5, '1', '', '1', '', 0, 0, 0, NULL, 0, '2025-10-07 04:56:58');
INSERT INTO `im_group_message` VALUES (3, NULL, 7, 5, '1', '', 'RP1975304631353475072', NULL, 0, 0, 7, NULL, 0, '2025-10-07 04:58:18');
INSERT INTO `im_group_message` VALUES (4, '1759784585088622', 7, 3, 'yt', '', '哈哈', '', 0, 0, 0, NULL, 0, '2025-10-07 05:03:05');
INSERT INTO `im_group_message` VALUES (5, '1759784598056785', 7, 3, 'yt', '', '#憨笑;', '', 0, 0, 0, NULL, 0, '2025-10-07 05:03:18');
INSERT INTO `im_group_message` VALUES (6, '1759784608159847', 7, 3, 'yt', '', '#绿叶;', '', 0, 0, 0, NULL, 0, '2025-10-07 05:03:28');
INSERT INTO `im_group_message` VALUES (7, '1759784635944784', 7, 3, 'yt', '', '#媚眼;', '', 0, 0, 0, NULL, 0, '2025-10-07 05:03:56');
INSERT INTO `im_group_message` VALUES (8, NULL, 7, 5, '1', '', 'RP1975306822524993536', NULL, 0, 0, 7, NULL, 0, '2025-10-07 05:07:01');
INSERT INTO `im_group_message` VALUES (9, NULL, 7, 5, 'td', '', '\'td\'解散了群聊', NULL, 0, 0, 21, NULL, 0, '2025-10-07 06:08:44');
INSERT INTO `im_group_message` VALUES (10, NULL, 6, 5, 'td', '', '\'td\'解散了群聊', NULL, 0, 0, 21, NULL, 0, '2025-10-07 06:08:57');
INSERT INTO `im_group_message` VALUES (11, NULL, 5, 5, 'td', '', '\'td\'解散了群聊', NULL, 0, 0, 21, NULL, 0, '2025-10-07 06:09:03');
INSERT INTO `im_group_message` VALUES (12, NULL, 4, 5, 'td', '', '\'td\'解散了群聊', NULL, 0, 0, 21, NULL, 0, '2025-10-07 06:09:08');
INSERT INTO `im_group_message` VALUES (13, NULL, 3, 5, 'td', '', '\'td\'解散了群聊', NULL, 0, 0, 21, NULL, 0, '2025-10-07 06:09:14');
INSERT INTO `im_group_message` VALUES (14, NULL, 2, 5, 'td', '', '\'td\'解散了群聊', NULL, 0, 0, 21, NULL, 0, '2025-10-07 06:09:21');
INSERT INTO `im_group_message` VALUES (15, NULL, 1, 5, 'td', '', '\'td\'邀请\'yt,ty\'加入了群聊', NULL, 0, 0, 21, NULL, 0, '2025-10-07 06:09:28');
INSERT INTO `im_group_message` VALUES (16, NULL, 1, 5, 'td', '', 'RP1975322628782604288', NULL, 0, 0, 7, NULL, 0, '2025-10-07 06:09:49');

-- ----------------------------
-- Table structure for im_private_message
-- ----------------------------
DROP TABLE IF EXISTS `im_private_message`;
CREATE TABLE `im_private_message`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `tmp_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '临时id,由前端生成',
  `send_id` bigint NOT NULL COMMENT '发送用户id',
  `recv_id` bigint NOT NULL COMMENT '接收用户id',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '发送内容',
  `type` tinyint(1) NOT NULL COMMENT '消息类型 0:文字 1:图片 2:文件 3:语音 4:视频 21:提示',
  `quote_message_id` bigint NULL DEFAULT NULL COMMENT '引用消息id',
  `status` tinyint(1) NOT NULL COMMENT '状态 0:未读 1:已发送 2:撤回 3:已读',
  `send_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发送时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_send_id`(`send_id` ASC) USING BTREE,
  INDEX `idx_recv_id`(`recv_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 20 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '私聊消息' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of im_private_message
-- ----------------------------
INSERT INTO `im_private_message` VALUES (1, NULL, 2, 1, '你们已成为好友，现在可以开始聊天了', 21, NULL, 3, '2025-10-06 14:36:57');
INSERT INTO `im_private_message` VALUES (2, '1759732651708230', 1, 2, '你好', 0, NULL, 3, '2025-10-06 14:37:30');
INSERT INTO `im_private_message` VALUES (3, '1759732684831200', 1, 2, '111', 0, NULL, 3, '2025-10-06 14:38:04');
INSERT INTO `im_private_message` VALUES (4, '1759740471435304', 1, 2, '11', 0, NULL, 1, '2025-10-06 16:47:50');
INSERT INTO `im_private_message` VALUES (5, '1759740473688314', 2, 1, '22', 0, NULL, 3, '2025-10-06 16:47:52');
INSERT INTO `im_private_message` VALUES (6, '175974053735133', 2, 1, '11', 0, NULL, 3, '2025-10-06 16:48:56');
INSERT INTO `im_private_message` VALUES (7, '1759740759748771', 2, 1, '你好呀', 0, NULL, 3, '2025-10-06 16:52:38');
INSERT INTO `im_private_message` VALUES (8, '1759767734871664', 1, 2, '11', 0, NULL, 1, '2025-10-07 00:22:14');
INSERT INTO `im_private_message` VALUES (9, NULL, 4, 3, '你们已成为好友，现在可以开始聊天了', 21, NULL, 3, '2025-10-07 02:33:24');
INSERT INTO `im_private_message` VALUES (10, '1759775638252714', 3, 4, '1', 0, NULL, 3, '2025-10-07 02:33:58');
INSERT INTO `im_private_message` VALUES (11, '1759775719963594', 3, 4, '1', 0, NULL, 3, '2025-10-07 02:35:20');
INSERT INTO `im_private_message` VALUES (12, '1759775650252830', 4, 3, '1', 0, NULL, 3, '2025-10-07 02:35:39');
INSERT INTO `im_private_message` VALUES (13, NULL, 4, 3, 'RP1975271739199262720', 7, NULL, 3, '2025-10-07 02:47:36');
INSERT INTO `im_private_message` VALUES (14, NULL, 4, 3, 'RP1975283043989393408', 7, NULL, 3, '2025-10-07 03:32:31');
INSERT INTO `im_private_message` VALUES (15, NULL, 3, 4, 'TXN1975293205311590400', 8, NULL, 3, '2025-10-07 04:12:54');
INSERT INTO `im_private_message` VALUES (16, NULL, 5, 3, '你们已成为好友，现在可以开始聊天了', 21, NULL, 3, '2025-10-07 04:42:10');
INSERT INTO `im_private_message` VALUES (17, NULL, 5, 4, '你们已成为好友，现在可以开始聊天了', 21, NULL, 3, '2025-10-07 04:42:18');
INSERT INTO `im_private_message` VALUES (18, '1759783347143971', 5, 4, '1', 0, NULL, 3, '2025-10-07 04:42:27');
INSERT INTO `im_private_message` VALUES (19, '1759783353136729', 5, 3, '1', 0, NULL, 3, '2025-10-07 04:42:33');

-- ----------------------------
-- Table structure for im_sensitive_word
-- ----------------------------
DROP TABLE IF EXISTS `im_sensitive_word`;
CREATE TABLE `im_sensitive_word`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `content` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '敏感词内容',
  `enabled` tinyint NULL DEFAULT 0 COMMENT '是否启用 0:未启用 1:启用',
  `creator` bigint NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '敏感词' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of im_sensitive_word
-- ----------------------------

-- ----------------------------
-- Table structure for im_sm_push_task
-- ----------------------------
DROP TABLE IF EXISTS `im_sm_push_task`;
CREATE TABLE `im_sm_push_task`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `message_id` bigint NOT NULL COMMENT '系统消息id',
  `seq_no` bigint NULL DEFAULT NULL COMMENT '发送序列号',
  `send_time` datetime NULL DEFAULT NULL COMMENT '推送时间',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态 1:待发送 2:发送中 3:已发送 4:已取消',
  `send_to_all` tinyint NULL DEFAULT 1 COMMENT '是否发送给全体用户',
  `recv_ids` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '接收用户id,逗号分隔,send_to_all为false时有效',
  `deleted` tinyint NULL DEFAULT NULL COMMENT '删除标识  0：正常   1：已删除',
  `creator` bigint NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_seq_no`(`seq_no` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '系统消息推送任务' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of im_sm_push_task
-- ----------------------------

-- ----------------------------
-- Table structure for im_system_message
-- ----------------------------
DROP TABLE IF EXISTS `im_system_message`;
CREATE TABLE `im_system_message`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `title` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '标题',
  `cover_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '封面图片',
  `intro` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '简介',
  `content_type` tinyint(1) NULL DEFAULT 0 COMMENT '内容类型 0:富文本  1:外部链接',
  `rich_text` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '富文本内容，base64编码',
  `extern_link` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '外部链接',
  `deleted` tinyint NULL DEFAULT 0 COMMENT '删除标识  0：正常   1：已删除',
  `creator` bigint NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '系统消息' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of im_system_message
-- ----------------------------

-- ----------------------------
-- Table structure for im_user
-- ----------------------------
DROP TABLE IF EXISTS `im_user`;
CREATE TABLE `im_user`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `user_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户名',
  `nick_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户昵称',
  `head_image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '用户头像',
  `head_image_thumb` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '用户头像缩略图',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '密码',
  `sex` tinyint(1) NULL DEFAULT 0 COMMENT '性别 0:男 1:女',
  `phone` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '手机号码',
  `email` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '邮箱',
  `company_id` bigint NULL DEFAULT NULL COMMENT '归属企业id',
  `company_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '归属企业名称',
  `is_banned` tinyint(1) NULL DEFAULT 0 COMMENT '是否被封禁 0:否 1:是',
  `reason` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '被封禁原因',
  `type` smallint NULL DEFAULT 1 COMMENT '用户类型 1:普通用户 2:审核账户',
  `signature` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '个性签名',
  `is_manual_approve` tinyint(1) NULL DEFAULT 0 COMMENT '是否手动验证好友请求',
  `audio_tip` tinyint NULL DEFAULT 1 COMMENT '新消息语音提醒 bit-0:web端 bit-1:app端',
  `cid` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '客户端id,用于uni-push推送',
  `status` tinyint NULL DEFAULT 0 COMMENT '状态  0:正常  1:已注销',
  `last_login_time` datetime NULL DEFAULT NULL COMMENT '最后登录时间',
  `last_login_ip` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '最后登陆ip',
  `created_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_user_name`(`user_name` ASC) USING BTREE,
  UNIQUE INDEX `idx_phone`(`phone` ASC) USING BTREE,
  UNIQUE INDEX `idx_email`(`email` ASC) USING BTREE,
  INDEX `idx_nick_name`(`nick_name` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of im_user
-- ----------------------------
INSERT INTO `im_user` VALUES (1, 'user', 'user', '', '', '$2a$10$YFCPkByeAGdyTrT5GnJ84u0TzZMOt0O08DudKhzMHWgiU.3j5MIOa', 0, NULL, NULL, NULL, NULL, 0, '', 1, '', 0, 1, '', 0, '2025-10-07 00:27:42', '172.20.0.1', '2025-10-06 14:35:26');
INSERT INTO `im_user` VALUES (2, 'user1', 'user1', '', '', '$2a$10$OxKmffjMx7oX2OO9AN9ozO9fwNc3WbIht2hnHygpPK6iEzBuVsrGq', 0, NULL, NULL, NULL, NULL, 0, '', 1, '', 0, 1, '', 0, '2025-10-06 22:51:26', '172.20.0.1', '2025-10-06 14:36:43');
INSERT INTO `im_user` VALUES (3, 'yt', 'yt', '', '', '$2a$10$gJiHGhTj.3lnRXQOvqNDGe/2sWlpvCRoNQDn8j2KtwVd8J/yFYs7e', 0, NULL, NULL, NULL, NULL, 0, '', 1, '', 0, 1, '', 0, '2025-10-07 05:00:37', '172.20.0.1', '2025-10-07 02:32:02');
INSERT INTO `im_user` VALUES (4, 'ty', 'ty', '', '', '$2a$10$zUUB393s/VEZGdi.oNMH.uRot8wpdg9bL8nCCfNlwD55GDs31mC5S', 0, NULL, NULL, NULL, NULL, 0, '', 1, '', 0, 1, '', 0, '2025-10-07 04:59:55', '172.20.0.1', '2025-10-07 02:33:04');
INSERT INTO `im_user` VALUES (5, 'td', 'td', '', '', '$2a$10$Ptzwv1bQvaJtZSD.EVmgSOb/KrkaqoiLp3IV6ZbnYZsBjNEiKSxty', 0, NULL, NULL, NULL, NULL, 0, '', 1, '', 0, 1, '', 0, '2025-10-07 06:47:03', '172.20.0.1', '2025-10-07 04:41:55');

-- ----------------------------
-- Table structure for im_user_blacklist
-- ----------------------------
DROP TABLE IF EXISTS `im_user_blacklist`;
CREATE TABLE `im_user_blacklist`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `from_user_id` bigint NOT NULL COMMENT '拉黑用户id',
  `to_user_id` bigint NOT NULL COMMENT '被拉黑用户id',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_from_user_id`(`from_user_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户黑名单' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of im_user_blacklist
-- ----------------------------

-- ----------------------------
-- Table structure for im_user_complaint
-- ----------------------------
DROP TABLE IF EXISTS `im_user_complaint`;
CREATE TABLE `im_user_complaint`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `user_id` bigint NOT NULL COMMENT '用户id',
  `target_type` tinyint NOT NULL COMMENT '投诉对象类型 1:用户 2:群聊',
  `target_id` bigint NOT NULL COMMENT '投诉对象id',
  `target_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '投诉对象名称',
  `type` tinyint NOT NULL COMMENT '投诉原因类型 1:对我造成骚扰 2:疑似诈骗 3:传播不良内容 99:其他',
  `images` varchar(4096) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '图片列表,最多9张',
  `content` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '投诉内容',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态 1:未处理 2:已处理',
  `resolved_admin_id` bigint NULL DEFAULT NULL COMMENT '处理投诉的管理员id',
  `resolved_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '处理结果类型 1:已处理 2:不予处理 3:未涉及不正规行为 4:其他',
  `resolved_summary` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '处理结果摘要',
  `resolved_time` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '处理时间',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户投诉' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of im_user_complaint
-- ----------------------------

-- ----------------------------
-- Table structure for t_group_privacy_config
-- ----------------------------
DROP TABLE IF EXISTS `t_group_privacy_config`;
CREATE TABLE `t_group_privacy_config`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `group_id` bigint NOT NULL COMMENT '群组ID',
  `privacy_enabled` tinyint NOT NULL DEFAULT 0 COMMENT '隐私保护是否开启: 0-关闭, 1-开启',
  `admin_view_real` tinyint NOT NULL DEFAULT 1 COMMENT '管理员是否可见真实信息: 0-否, 1-是（预留）',
  `created_by` bigint NULL DEFAULT NULL COMMENT '创建人ID（群主）',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_group_id`(`group_id` ASC) USING BTREE,
  INDEX `idx_privacy_enabled`(`privacy_enabled` ASC) USING BTREE,
  INDEX `idx_created_by`(`created_by` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '群组隐私配置表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_group_privacy_config
-- ----------------------------
INSERT INTO `t_group_privacy_config` VALUES (1, 1, 0, 1, 5, '2025-10-07 04:42:46', '2025-10-07 04:42:46');
INSERT INTO `t_group_privacy_config` VALUES (2, 2, 0, 1, 5, '2025-10-07 04:42:59', '2025-10-07 04:42:59');
INSERT INTO `t_group_privacy_config` VALUES (3, 3, 0, 1, 5, '2025-10-07 04:43:11', '2025-10-07 04:43:11');
INSERT INTO `t_group_privacy_config` VALUES (4, 4, 0, 1, 5, '2025-10-07 04:47:56', '2025-10-07 04:47:56');
INSERT INTO `t_group_privacy_config` VALUES (5, 5, 0, 1, 5, '2025-10-07 04:48:27', '2025-10-07 04:48:27');
INSERT INTO `t_group_privacy_config` VALUES (6, 6, 0, 1, 5, '2025-10-07 04:49:23', '2025-10-07 04:49:23');
INSERT INTO `t_group_privacy_config` VALUES (7, 7, 0, 1, 5, '2025-10-07 04:56:29', '2025-10-07 04:56:29');

-- ----------------------------
-- Table structure for t_group_virtual_member
-- ----------------------------
DROP TABLE IF EXISTS `t_group_virtual_member`;
CREATE TABLE `t_group_virtual_member`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `group_id` bigint NOT NULL COMMENT '群组ID',
  `real_user_id` bigint NOT NULL COMMENT '真实用户ID',
  `virtual_user_id` bigint NOT NULL COMMENT '模拟用户ID（雪花算法生成）',
  `virtual_nick_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '模拟昵称',
  `display_order` int NOT NULL DEFAULT 0 COMMENT '显示序号（用于生成昵称）',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态: 1-正常, 2-已失效',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_group_user`(`group_id` ASC, `real_user_id` ASC) USING BTREE,
  INDEX `idx_virtual_user`(`virtual_user_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_display_order`(`group_id` ASC, `display_order` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '群组模拟用户信息表 - 仅存储虚拟ID和昵称' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_group_virtual_member
-- ----------------------------

-- ----------------------------
-- Table structure for t_red_packet
-- ----------------------------
DROP TABLE IF EXISTS `t_red_packet`;
CREATE TABLE `t_red_packet`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `packet_no` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '红包编号',
  `sender_id` bigint NOT NULL COMMENT '发送者ID',
  `packet_type` tinyint NOT NULL COMMENT '红包类型:1-普通红包,2-拼手气红包',
  `total_amount` decimal(15, 2) NOT NULL COMMENT '总金额',
  `total_count` int NOT NULL COMMENT '总个数',
  `remaining_amount` decimal(15, 2) NOT NULL COMMENT '剩余金额',
  `remaining_count` int NOT NULL COMMENT '剩余个数',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态:1-创建,2-发放中,3-已领完,4-已过期,5-已退款',
  `expire_time` timestamp NOT NULL COMMENT '过期时间',
  `message` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '祝福语',
  `chat_type` tinyint NOT NULL COMMENT '聊天类型:1-单聊,2-群聊',
  `target_id` bigint NOT NULL COMMENT '目标ID(用户ID或群ID)',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_packet_no`(`packet_no` ASC) USING BTREE,
  INDEX `idx_sender`(`sender_id` ASC) USING BTREE,
  INDEX `idx_target`(`target_id` ASC, `chat_type` ASC) USING BTREE,
  INDEX `idx_status_expire`(`status` ASC, `expire_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '红包表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_red_packet
-- ----------------------------
INSERT INTO `t_red_packet` VALUES (1, 'RP1975232670389731328', 1, 1, 10.00, 1, 10.00, 1, 2, '2025-10-08 00:12:21', '恭喜发财', 1, 2, '2025-10-07 00:12:21', '2025-10-07 00:12:21');
INSERT INTO `t_red_packet` VALUES (2, 'RP1975235920564289536', 1, 1, 12.00, 1, 12.00, 1, 2, '2025-10-08 00:25:16', '恭喜发财', 1, 2, '2025-10-07 00:25:16', '2025-10-07 00:25:16');
INSERT INTO `t_red_packet` VALUES (3, 'RP1975271739199262720', 4, 1, 100.00, 1, 0.00, 0, 3, '2025-10-08 02:47:36', '恭喜发财', 1, 3, '2025-10-07 02:47:36', '2025-10-07 03:42:02');
INSERT INTO `t_red_packet` VALUES (4, 'RP1975283043989393408', 4, 1, 20.00, 1, 0.00, 0, 3, '2025-10-08 03:32:31', '恭喜发财', 1, 3, '2025-10-07 03:32:31', '2025-10-07 03:42:07');
INSERT INTO `t_red_packet` VALUES (5, 'RP1975304631353475072', 5, 2, 100.00, 2, 0.00, 0, 3, '2025-10-08 04:58:18', '恭喜发财', 2, 7, '2025-10-07 04:58:18', '2025-10-07 05:00:43');
INSERT INTO `t_red_packet` VALUES (6, 'RP1975306822524993536', 5, 2, 200.00, 3, 200.00, 3, 2, '2025-10-08 05:07:01', '恭喜发财', 2, 7, '2025-10-07 05:07:00', '2025-10-07 05:07:00');
INSERT INTO `t_red_packet` VALUES (7, 'RP1975322628782604288', 5, 1, 10.00, 1, 10.00, 1, 2, '2025-10-08 06:09:49', '恭喜发财', 2, 1, '2025-10-07 06:09:49', '2025-10-07 06:09:49');

-- ----------------------------
-- Table structure for t_red_packet_receive
-- ----------------------------
DROP TABLE IF EXISTS `t_red_packet_receive`;
CREATE TABLE `t_red_packet_receive`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `packet_id` bigint NOT NULL COMMENT '红包ID',
  `receiver_id` bigint NOT NULL COMMENT '领取者ID',
  `amount` decimal(15, 2) NOT NULL COMMENT '领取金额',
  `is_best_luck` tinyint NULL DEFAULT 0 COMMENT '是否手气最佳:0-否,1-是',
  `receive_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '领取时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_packet_receiver`(`packet_id` ASC, `receiver_id` ASC) USING BTREE,
  INDEX `idx_packet_id`(`packet_id` ASC) USING BTREE,
  INDEX `idx_receiver`(`receiver_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '红包领取记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_red_packet_receive
-- ----------------------------
INSERT INTO `t_red_packet_receive` VALUES (1, 3, 3, 100.00, 1, '2025-10-07 03:42:03');
INSERT INTO `t_red_packet_receive` VALUES (2, 4, 3, 20.00, 1, '2025-10-07 03:42:08');
INSERT INTO `t_red_packet_receive` VALUES (3, 5, 4, 50.21, 1, '2025-10-07 05:00:21');
INSERT INTO `t_red_packet_receive` VALUES (4, 5, 3, 49.79, 0, '2025-10-07 05:00:44');

-- ----------------------------
-- Table structure for t_system_privacy_config
-- ----------------------------
DROP TABLE IF EXISTS `t_system_privacy_config`;
CREATE TABLE `t_system_privacy_config`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `config_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '配置键',
  `config_value` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '配置值',
  `description` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '配置描述',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_config_key`(`config_key` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '系统隐私配置表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_system_privacy_config
-- ----------------------------
INSERT INTO `t_system_privacy_config` VALUES (1, 'group_privacy_global_enabled', '1', '全局隐私保护功能开关: 0-关闭, 1-开启', '2025-10-06 05:13:22', '2025-10-06 05:13:22');
INSERT INTO `t_system_privacy_config` VALUES (2, 'virtual_nick_name_prefix', '群友', '模拟昵称前缀', '2025-10-06 05:13:22', '2025-10-06 05:13:22');

-- ----------------------------
-- Table structure for t_user_wallet
-- ----------------------------
DROP TABLE IF EXISTS `t_user_wallet`;
CREATE TABLE `t_user_wallet`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '用户ID，关联IM系统用户',
  `balance` decimal(15, 2) NOT NULL DEFAULT 0.00 COMMENT '可用余额',
  `frozen_balance` decimal(15, 2) NOT NULL DEFAULT 0.00 COMMENT '冻结金额',
  `currency` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'CNY' COMMENT '币种',
  `wallet_status` tinyint NOT NULL DEFAULT 1 COMMENT '钱包状态:1-正常,2-冻结,3-禁用',
  `security_level` tinyint NOT NULL DEFAULT 1 COMMENT '安全等级',
  `pay_password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '支付密码(加密存储)',
  `version` int NOT NULL DEFAULT 0 COMMENT '版本号(乐观锁)',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_status`(`wallet_status` ASC) USING BTREE,
  INDEX `idx_created_at`(`created_at` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户钱包表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_user_wallet
-- ----------------------------
INSERT INTO `t_user_wallet` VALUES (1, 1, 978.00, 22.00, 'USDT', 1, 1, '$2a$10$bA3RFZm64rjOTc6wScCtouY4EiUaZktnzeySTXrhahNiQDbWncyPS', 2, '2025-10-06 23:01:47', '2025-10-07 05:22:03');
INSERT INTO `t_user_wallet` VALUES (2, 3, 1168.79, 0.00, 'USDT', 1, 1, '$2a$10$3lBl0qTzGru03Ef0wcAG5.LzCJS.UMnk0C89uq/ar5X5y1CARqhAu', 4, '2025-10-07 02:32:14', '2025-10-07 05:22:01');
INSERT INTO `t_user_wallet` VALUES (3, 4, 931.21, 0.00, 'USDT', 1, 1, '$2a$10$RzgrYdGBVyc4/eCkFOnfB.Xkt1s8LgoPz3fyipDW4k1GWWHRPAP4e', 6, '2025-10-07 02:36:27', '2025-10-07 05:22:00');
INSERT INTO `t_user_wallet` VALUES (4, 5, 690.00, 210.00, 'USDT', 1, 1, '$2a$10$SPM9SLYpDGYqvXcoOYG5R.WDvT0y1Ht6/DmsVOi7w1E9lu5xGzSve', 5, '2025-10-07 04:45:07', '2025-10-07 06:09:49');

-- ----------------------------
-- Table structure for t_wallet_order
-- ----------------------------
DROP TABLE IF EXISTS `t_wallet_order`;
CREATE TABLE `t_wallet_order`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `order_no` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '订单号',
  `type` tinyint NOT NULL COMMENT '类型:1-充值,2-提现',
  `amount` decimal(15, 2) NOT NULL COMMENT '金额',
  `channel` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '支付渠道:alipay,wechat,bank',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态:1-处理中,2-成功,3-失败,4-已取消',
  `channel_order_no` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '渠道订单号',
  `notify_data` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '渠道回调数据',
  `completed_time` timestamp NULL DEFAULT NULL COMMENT '完成时间',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_order_no`(`order_no` ASC) USING BTREE,
  INDEX `idx_user_type`(`user_id` ASC, `type` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_channel_order`(`channel_order_no` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '充值提现表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_wallet_order
-- ----------------------------
INSERT INTO `t_wallet_order` VALUES (1, 1, 'ORD20251007000001', 1, 1000.00, 'alipay', 2, '2025100722001400001000000001', NULL, '2025-10-07 05:36:40', '2025-10-07 05:36:40', '2025-10-07 05:36:40');
INSERT INTO `t_wallet_order` VALUES (2, 3, 'ORD20251007000003', 1, 1000.00, 'wechat', 2, '4200001234567890000000000003', NULL, '2025-10-07 05:36:40', '2025-10-07 05:36:40', '2025-10-07 05:36:40');
INSERT INTO `t_wallet_order` VALUES (3, 4, 'ORD20251007000004', 1, 1000.00, 'bank', 2, 'BANK20251007000000000004', NULL, '2025-10-07 05:36:40', '2025-10-07 05:36:40', '2025-10-07 05:36:40');
INSERT INTO `t_wallet_order` VALUES (4, 5, 'ORD20251007000005', 1, 1000.00, 'alipay', 2, '2025100722001400001000000005', NULL, '2025-10-07 05:36:40', '2025-10-07 05:36:40', '2025-10-07 05:36:40');

-- ----------------------------
-- Table structure for t_wallet_transaction
-- ----------------------------
DROP TABLE IF EXISTS `t_wallet_transaction`;
CREATE TABLE `t_wallet_transaction`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `transaction_no` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '交易流水号',
  `wallet_id` bigint NULL DEFAULT NULL COMMENT '钱包ID',
  `from_user_id` bigint NOT NULL COMMENT '付款用户ID',
  `to_user_id` bigint NOT NULL COMMENT '收款用户ID',
  `amount` decimal(15, 2) NOT NULL COMMENT '交易金额',
  `transaction_type` tinyint NOT NULL COMMENT '交易类型:1-转账,2-红包,3-充值,4-提现,5-退款',
  `business_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '业务类型',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态:1-处理中,2-成功,3-失败,4-已退款',
  `remark` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '交易备注',
  `relation_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '关联业务ID(如红包ID、订单ID)',
  `fee` decimal(15, 2) NULL DEFAULT 0.00 COMMENT '手续费',
  `before_balance` decimal(15, 2) NULL DEFAULT NULL COMMENT '交易前余额',
  `after_balance` decimal(15, 2) NULL DEFAULT NULL COMMENT '交易后余额',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_transaction_no`(`transaction_no` ASC) USING BTREE,
  INDEX `idx_from_user`(`from_user_id` ASC, `created_at` ASC) USING BTREE,
  INDEX `idx_to_user`(`to_user_id` ASC, `created_at` ASC) USING BTREE,
  INDEX `idx_type_status`(`transaction_type` ASC, `status` ASC) USING BTREE,
  INDEX `idx_relation`(`relation_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 17 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '交易流水表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_wallet_transaction
-- ----------------------------
INSERT INTO `t_wallet_transaction` VALUES (1, 'TXN1975232670389731329', NULL, 1, 0, 10.00, 2, NULL, 2, '发红包', 'RP1975232670389731328', 0.00, 1000.00, 990.00, '2025-10-07 00:12:21', '2025-10-07 00:12:21');
INSERT INTO `t_wallet_transaction` VALUES (2, 'TXN1975235920564289537', NULL, 1, 0, 12.00, 2, NULL, 2, '发红包', 'RP1975235920564289536', 0.00, 990.00, 978.00, '2025-10-07 00:25:16', '2025-10-07 00:25:16');
INSERT INTO `t_wallet_transaction` VALUES (3, 'TXN1975271739203457024', NULL, 4, 0, 100.00, 2, NULL, 2, '发红包', 'RP1975271739199262720', 0.00, 1000.00, 900.00, '2025-10-07 02:47:36', '2025-10-07 02:47:36');
INSERT INTO `t_wallet_transaction` VALUES (4, 'TXN1975283043989393409', NULL, 4, 0, 20.00, 2, NULL, 2, '发红包', 'RP1975283043989393408', 0.00, 900.00, 880.00, '2025-10-07 03:32:31', '2025-10-07 03:32:31');
INSERT INTO `t_wallet_transaction` VALUES (5, 'TXN1975285440102993920', NULL, 4, 3, 100.00, 2, NULL, 1, '领取红包', 'RP1975271739199262720', 0.00, NULL, NULL, '2025-10-07 03:42:02', '2025-10-07 03:42:02');
INSERT INTO `t_wallet_transaction` VALUES (6, 'TXN1975285461191954432', NULL, 4, 3, 20.00, 2, NULL, 1, '领取红包', 'RP1975283043989393408', 0.00, NULL, NULL, '2025-10-07 03:42:07', '2025-10-07 03:42:07');
INSERT INTO `t_wallet_transaction` VALUES (7, 'TXN1975293205311590400', NULL, 3, 4, 1.00, 1, NULL, 2, '', NULL, 0.00, 1120.00, 1119.00, '2025-10-07 04:12:53', '2025-10-07 04:12:53');
INSERT INTO `t_wallet_transaction` VALUES (8, 'TXN1975304631353475073', NULL, 5, 0, 100.00, 2, NULL, 2, '发红包', 'RP1975304631353475072', 0.00, 1000.00, 900.00, '2025-10-07 04:58:18', '2025-10-07 04:58:18');
INSERT INTO `t_wallet_transaction` VALUES (9, 'TXN1975305147911372800', NULL, 5, 4, 50.21, 2, NULL, 1, '领取红包', 'RP1975304631353475072', 0.00, NULL, NULL, '2025-10-07 05:00:21', '2025-10-07 05:00:21');
INSERT INTO `t_wallet_transaction` VALUES (10, 'TXN1975305241750536192', NULL, 5, 3, 49.79, 2, NULL, 1, '领取红包', 'RP1975304631353475072', 0.00, NULL, NULL, '2025-10-07 05:00:43', '2025-10-07 05:00:43');
INSERT INTO `t_wallet_transaction` VALUES (11, 'TXN1975306822524993537', NULL, 5, 0, 200.00, 2, NULL, 2, '发红包', 'RP1975306822524993536', 0.00, 900.00, 700.00, '2025-10-07 05:07:00', '2025-10-07 05:07:00');
INSERT INTO `t_wallet_transaction` VALUES (12, 'TXN20251007000001', 1, 0, 1, 1000.00, 3, 'RECHARGE', 2, '初始化充值', 'ORD20251007000001', 0.00, 978.00, 1978.00, '2025-10-07 05:36:40', '2025-10-07 05:36:40');
INSERT INTO `t_wallet_transaction` VALUES (13, 'TXN20251007000003', 2, 0, 3, 1000.00, 3, 'RECHARGE', 2, '初始化充值', 'ORD20251007000003', 0.00, 1168.79, 2168.79, '2025-10-07 05:36:40', '2025-10-07 05:36:40');
INSERT INTO `t_wallet_transaction` VALUES (14, 'TXN20251007000004', 3, 0, 4, 1000.00, 3, 'RECHARGE', 2, '初始化充值', 'ORD20251007000004', 0.00, 931.21, 1931.21, '2025-10-07 05:36:40', '2025-10-07 05:36:40');
INSERT INTO `t_wallet_transaction` VALUES (15, 'TXN20251007000005', 4, 0, 5, 1000.00, 3, 'RECHARGE', 2, '初始化充值', 'ORD20251007000005', 0.00, 700.00, 1700.00, '2025-10-07 05:36:40', '2025-10-07 05:36:40');
INSERT INTO `t_wallet_transaction` VALUES (16, 'TXN1975322628782604289', NULL, 5, 0, 10.00, 2, NULL, 2, '发红包', 'RP1975322628782604288', 0.00, 700.00, 690.00, '2025-10-07 06:09:49', '2025-10-07 06:09:49');

SET FOREIGN_KEY_CHECKS = 1;
