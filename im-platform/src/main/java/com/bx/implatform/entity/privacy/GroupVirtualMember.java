package com.bx.implatform.entity.privacy;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

/**
 * 群组模拟用户信息实体类
 *
 * @author blue
 * @since 2025-10-06
 */
@Data
@TableName("t_group_virtual_member")
public class GroupVirtualMember {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 群组ID
     */
    private Long groupId;

    /**
     * 真实用户ID
     */
    private Long realUserId;

    /**
     * 模拟用户ID（雪花算法生成）
     */
    private Long virtualUserId;

    /**
     * 模拟昵称
     */
    private String virtualNickName;

    /**
     * 显示序号（用于生成昵称）
     */
    private Integer displayOrder;

    /**
     * 状态: 1-正常, 2-已失效
     */
    private Integer status;

    /**
     * 创建时间
     */
    @TableField("created_at")
    private Date createdAt;

    /**
     * 更新时间
     */
    @TableField("updated_at")
    private Date updatedAt;
}

