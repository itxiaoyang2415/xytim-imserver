package com.bx.implatform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bx.implatform.dto.ReceiveRedPacketDTO;
import com.bx.implatform.dto.SendRedPacketDTO;
import com.bx.implatform.entity.RedPacket;
import com.bx.implatform.vo.RedPacketReceiveVO;
import com.bx.implatform.vo.RedPacketVO;

import java.math.BigDecimal;
import java.util.List;

/**
 * 红包服务接口
 *
 * @author blue
 * @since 2025-10-06
 */
public interface RedPacketService extends IService<RedPacket> {

    /**
     * 发送红包
     *
     * @param dto 发送红包DTO
     * @return 红包VO
     */
    RedPacketVO sendRedPacket(SendRedPacketDTO dto);

    /**
     * 领取红包
     *
     * @param dto 领取红包DTO
     * @return 领取金额
     */
    BigDecimal receiveRedPacket(ReceiveRedPacketDTO dto);

    /**
     * 查询红包详情
     *
     * @param packetNo 红包编号
     * @return 红包VO
     */
    RedPacketVO getRedPacketDetail(String packetNo);

    /**
     * 查询红包领取记录
     *
     * @param packetNo 红包编号
     * @return 领取记录列表
     */
    List<RedPacketReceiveVO> getReceiveRecords(String packetNo);

    /**
     * 过期红包退款定时任务
     */
    void refundExpiredRedPackets();
}

