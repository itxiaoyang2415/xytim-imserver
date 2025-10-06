package com.bx.implatform.vo;

import com.bx.imcommon.serializer.DateToLongSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 红包领取记录VO
 *
 * @author blue
 * @since 2025-10-06
 */
@Data
@Schema(description = "红包领取记录VO")
public class RedPacketReceiveVO {

    @Schema(description = "记录ID")
    private Long id;

    @Schema(description = "红包ID")
    private Long packetId;

    @Schema(description = "领取者ID")
    private Long receiverId;

    @Schema(description = "领取者昵称")
    private String receiverName;

    @Schema(description = "领取者头像")
    private String receiverAvatar;

    @Schema(description = "领取金额")
    private BigDecimal amount;

    @Schema(description = "是否手气最佳:0-否,1-是")
    private Integer isBestLuck;

    @Schema(description = "领取时间")
    @JsonSerialize(using = DateToLongSerializer.class)
    private Date receiveTime;
}

