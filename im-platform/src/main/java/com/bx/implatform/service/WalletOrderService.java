package com.bx.implatform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bx.implatform.entity.WalletOrder;
import com.bx.implatform.vo.WalletOrderVO;

import java.util.List;

/**
 * 充值提现订单服务接口
 *
 * @author blue
 * @since 2025-10-07
 */
public interface WalletOrderService extends IService<WalletOrder> {

    /**
     * 查询用户充值记录
     *
     * @param page 页码
     * @param size 每页数量
     * @return 充值记录列表
     */
    List<WalletOrderVO> getRechargeRecords(Integer page, Integer size);
}

