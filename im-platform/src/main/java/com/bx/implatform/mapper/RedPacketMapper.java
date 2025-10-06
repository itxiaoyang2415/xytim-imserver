package com.bx.implatform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bx.implatform.entity.RedPacket;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;

/**
 * 红包 Mapper 接口
 *
 * @author blue
 * @since 2025-10-06
 */
public interface RedPacketMapper extends BaseMapper<RedPacket> {

    /**
     * 扣减红包剩余金额和个数（乐观锁）
     *
     * @param packetId 红包ID
     * @param amount 金额
     * @return 更新行数
     */
    int deductRedPacket(@Param("packetId") Long packetId,
                       @Param("amount") BigDecimal amount);
}

