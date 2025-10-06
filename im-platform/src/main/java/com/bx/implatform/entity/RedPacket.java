package com.bx.implatform.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 红包实体类
 *
 * @author blue
 * @since 2025-10-06
 */
@Data
@TableName("t_red_packet")
public class RedPacket {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 红包编号
     */
    private String packetNo;

    /**
     * 发送者ID
     */
    private Long senderId;

    /**
     * 红包类型:1-普通红包,2-拼手气红包
     */
    private Integer packetType;

    /**
     * 总金额
     */
    private BigDecimal totalAmount;

    /**
     * 总个数
     */
    private Integer totalCount;

    /**
     * 剩余金额
     */
    private BigDecimal remainingAmount;

    /**
     * 剩余个数
     */
    private Integer remainingCount;

    /**
     * 状态:1-已创建,2-发放中,3-已领完,4-已过期,5-已退款
     */
    private Integer status;

    /**
     * 过期时间
     */
    private Date expireTime;

    /**
     * 祝福语
     */
    private String message;

    /**
     * 聊天类型:1-单聊,2-群聊
     */
    private Integer chatType;

    /**
     * 目标ID(用户ID或群ID)
     */
    private Long targetId;

    /**
     * 创建时间
     */
    private Date createdAt;

    /**
     * 更新时间
     */
    private Date updatedAt;
}

