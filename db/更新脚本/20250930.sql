 ALTER TABLE im_user ADD `last_login_ip` VARCHAR(32) DEFAULT NULL comment '最后登陆ip';

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

ALTER TABLE im_user ADD `company_id` BIGINT COMMENT '企业id';
ALTER TABLE im_user ADD `company_name` VARCHAR(128) COMMENT '企业名称';
ALTER TABLE im_friend ADD `friend_company_name` VARCHAR(128) COMMENT '企业名称';
ALTER TABLE im_group_member ADD `company_name` VARCHAR(128) COMMENT '企业名称';

ALTER TABLE im_group_member ADD `version` INT DEFAULT 0 comment '版本号';