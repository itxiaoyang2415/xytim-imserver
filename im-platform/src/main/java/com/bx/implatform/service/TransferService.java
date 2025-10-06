package com.bx.implatform.service;

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
     * 转账
     *
     * @param dto 转账DTO
     * @return 转账结果VO
     */
    TransferVO transfer(TransferDTO dto);

    /**
     * 查询转账详情
     *
     * @param transactionNo 交易流水号
     * @return 转账详情
     */
    TransferVO getTransferDetail(String transactionNo);
}

