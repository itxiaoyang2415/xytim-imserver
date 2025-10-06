package com.bx.implatform.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 红包领取记录实体类
 *
 * @author blue
 * @since 2025-10-06
 */
@Data
@TableName("t_red_packet_receive")
public class RedPacketReceive {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 红包ID
     */
    private Long packetId;

    /**
     * 领取者ID
     */
    private Long receiverId;

    /**
     * 领取金额
     */
    private BigDecimal amount;

    /**
     * 是否手气最佳:0-否,1-是
     */
    private Integer isBestLuck;

    /**
     * 领取时间
     */
    private Date receiveTime;
}

