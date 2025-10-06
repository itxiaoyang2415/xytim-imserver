
UPDATE im_group_member a
JOIN im_user b ON a.user_id = b.id
SET a.head_image = b.head_image_thumb
WHERE a.head_image != '';


UPDATE im_friend a
JOIN im_user b ON a.friend_id = b.id
SET a.friend_head_image = b.head_image_thumb
WHERE a.friend_head_image != '';

ALTER TABLE im_group CHANGE is_muted is_all_muted tinyint (1) DEFAULT 0 comment '是否开启全体禁言 0:否 1:是';
ALTER TABLE im_group ADD `is_allow_invite` tinyint (1) DEFAULT 1 comment '是否允许普通成员邀请好友 0:否 1:是';
ALTER TABLE im_group ADD `is_allow_share_card` tinyint (1) DEFAULT 1 comment '是否允许普通成员分享名片 0:否 1:是';
