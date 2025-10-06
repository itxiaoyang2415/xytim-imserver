package com.bx.implatform.vo;

import com.bx.imcommon.serializer.DateToLongSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 红包VO
 *
 * @author blue
 * @since 2025-10-06
 */
@Data
@Schema(description = "红包VO")
public class RedPacketVO {

    @Schema(description = "红包ID")
    private Long id;

    @Schema(description = "红包编号")
    private String packetNo;

    @Schema(description = "发送者ID")
    private Long senderId;

    @Schema(description = "发送者昵称")
    private String senderName;

    @Schema(description = "发送者头像")
    private String senderAvatar;

    @Schema(description = "红包类型:1-普通红包,2-拼手气红包")
    private Integer packetType;

    @Schema(description = "红包类型描述")
    private String packetTypeDesc;

    @Schema(description = "总金额")
    private BigDecimal totalAmount;

    @Schema(description = "总个数")
    private Integer totalCount;

    @Schema(description = "剩余金额")
    private BigDecimal remainingAmount;

    @Schema(description = "剩余个数")
    private Integer remainingCount;

    @Schema(description = "状态:1-已创建,2-发放中,3-已领完,4-已过期,5-已退款")
    private Integer status;

    @Schema(description = "状态描述")
    private String statusDesc;

    @Schema(description = "过期时间")
    @JsonSerialize(using = DateToLongSerializer.class)
    private Date expireTime;

    @Schema(description = "祝福语")
    private String message;

    @Schema(description = "聊天类型:1-单聊,2-群聊")
    private Integer chatType;

    @Schema(description = "目标ID")
    private Long targetId;

    @Schema(description = "创建时间")
    @JsonSerialize(using = DateToLongSerializer.class)
    private Date createdAt;

    @Schema(description = "当前用户是否已领取")
    private Boolean received;

    @Schema(description = "当前用户领取的金额")
    private BigDecimal receivedAmount;
}

