package com.bx.implatform.service;

import com.bx.implatform.dto.ReceiveTransferDTO;
import com.bx.implatform.dto.TransferDTO;
import com.bx.implatform.vo.TransferVO;

/**
 * 转账服务接口
 *
 * @author blue
 * @since 2025-10-06
 */
public interface TransferService {

    /**
     * 转账（发送）
     *
     * @param dto 转账DTO
     * @return 转账结果VO
     */
    TransferVO transfer(TransferDTO dto);

    /**
     * 领取转账
     *
     * @param dto 领取转账DTO
     * @return 转账详情
     */
    TransferVO receiveTransfer(ReceiveTransferDTO dto);

    /**
     * 查询转账详情（通过转账编号）
     *
     * @param transferNo 转账编号
     * @return 转账详情
     */
    TransferVO getTransferDetail(String transferNo);

    /**
     * 退款过期转账（定时任务调用）
     */
    void refundExpiredTransfers();
}

