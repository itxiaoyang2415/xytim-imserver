package com.bx.implatform.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 用户钱包实体类
 *
 * @author blue
 * @since 2025-10-06
 */
@Data
@TableName("t_user_wallet")
public class UserWallet {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID，关联IM系统用户
     */
    private Long userId;

    /**
     * 可用余额
     */
    private BigDecimal balance;

    /**
     * 冻结金额
     */
    private BigDecimal frozenBalance;

    /**
     * 币种
     */
    private String currency;

    /**
     * 钱包状态:1-正常,2-冻结,3-禁用
     */
    private Integer walletStatus;

    /**
     * 安全等级
     */
    private Integer securityLevel;

    /**
     * 支付密码(加密存储)
     */
    private String payPassword;

    /**
     * 版本号(乐观锁)
     */
    @Version
    private Integer version;

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

