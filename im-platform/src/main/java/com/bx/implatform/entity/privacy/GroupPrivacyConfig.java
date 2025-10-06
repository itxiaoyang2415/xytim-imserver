package com.bx.implatform.entity.privacy;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

/**
 * 群组隐私配置实体类
 *
 * @author blue
 * @since 2025-10-06
 */
@Data
@TableName("t_group_privacy_config")
public class GroupPrivacyConfig {

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
     * 隐私保护是否开启: 0-关闭, 1-开启
     */
    private Integer privacyEnabled;

    /**
     * 管理员是否可见真实信息: 0-否, 1-是
     */
    private Integer adminViewReal;

    /**
     * 创建人ID（群主）
     */
    private Long createdBy;

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

