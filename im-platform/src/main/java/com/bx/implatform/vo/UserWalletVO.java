package com.bx.implatform.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 用户钱包VO
 *
 * @author blue
 * @since 2025-10-06
 */
@Data
@Schema(description = "用户钱包VO")
public class UserWalletVO {

    @Schema(description = "钱包ID")
    private Long id;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "可用余额")
    private BigDecimal balance;

    @Schema(description = "冻结金额")
    private BigDecimal frozenBalance;

    @Schema(description = "币种")
    private String currency;

    @Schema(description = "钱包状态:1-正常,2-冻结,3-禁用")
    private Integer walletStatus;

    @Schema(description = "安全等级")
    private Integer securityLevel;

    @Schema(description = "是否已设置支付密码")
    private Boolean hasPayPassword;

    @Schema(description = "创建时间")
    private Date createdAt;

    @Schema(description = "更新时间")
    private Date updatedAt;
}

