ALTER TABLE im_file_info DROP INDEX `idx_md5`, ADD INDEX `idx_md5`(md5);

alter table im_private_message add `tmp_id` varchar(32)  comment '临时id,由前端生成';

alter table im_group_message add `tmp_id` varchar(32)  comment '临时id,由前端生成';

alter table im_group_member add `is_top` tinyint (1) DEFAULT 0 comment '是否置顶会话';

alter table im_friend add `is_top` tinyint (1) DEFAULT 0 comment '是否置顶会话';

alter table im_user add `audio_tip` tinyint DEFAULT 1 comment '新消息语音提醒 bit-0:web端 bit-1:app端';