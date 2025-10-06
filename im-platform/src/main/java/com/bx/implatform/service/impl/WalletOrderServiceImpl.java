package com.bx.implatform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bx.implatform.entity.WalletOrder;
import com.bx.implatform.mapper.WalletOrderMapper;
import com.bx.implatform.service.WalletOrderService;
import com.bx.implatform.session.SessionContext;
import com.bx.implatform.vo.WalletOrderVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 充值提现订单服务实现类
 *
 * @author blue
 * @since 2025-10-07
 */
@Slf4j
@Service
public class WalletOrderServiceImpl extends ServiceImpl<WalletOrderMapper, WalletOrder> implements WalletOrderService {

    @Override
    public List<WalletOrderVO> getRechargeRecords(Integer page, Integer size) {
        Long userId = SessionContext.getSession().getUserId();

        // 查询当前用户的充值记录（type=1）
        LambdaQueryWrapper<WalletOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WalletOrder::getUserId, userId);
        wrapper.eq(WalletOrder::getType, 1); // 1-充值
        wrapper.orderByDesc(WalletOrder::getCreatedAt);

        Page<WalletOrder> pageResult = this.page(new Page<>(page, size), wrapper);
        List<WalletOrder> orders = pageResult.getRecords();

        // 转换为VO
        return orders.stream().map(order -> {
            WalletOrderVO vo = new WalletOrderVO();
            vo.setId(order.getId());
            vo.setUserId(order.getUserId());
            vo.setOrderNo(order.getOrderNo());
            vo.setType(order.getType());
            vo.setAmount(order.getAmount());
            vo.setChannel(order.getChannel());
            vo.setStatus(order.getStatus());
            vo.setChannelOrderNo(order.getChannelOrderNo());
            vo.setCompletedTime(order.getCompletedTime());
            vo.setCreatedAt(order.getCreatedAt());
            vo.setUpdatedAt(order.getUpdatedAt());

            // 设置类型描述
            vo.setTypeDesc(order.getType() == 1 ? "充值" : "提现");

            // 设置支付渠道描述
            vo.setChannelDesc(getChannelDesc(order.getChannel()));

            // 设置状态描述
            vo.setStatusDesc(getStatusDesc(order.getStatus()));

            return vo;
        }).collect(Collectors.toList());
    }

    /**
     * 获取支付渠道描述
     */
    private String getChannelDesc(String channel) {
        if (channel == null) {
            return "未知";
        }
        switch (channel.toLowerCase()) {
            case "alipay":
                return "支付宝";
            case "wechat":
                return "微信支付";
            case "bank":
                return "银行卡";
            default:
                return channel;
        }
    }

    /**
     * 获取状态描述
     */
    private String getStatusDesc(Integer status) {
        if (status == null) {
            return "未知";
        }
        switch (status) {
            case 1:
                return "处理中";
            case 2:
                return "成功";
            case 3:
                return "失败";
            case 4:
                return "已取消";
            default:
                return "未知";
        }
    }
}

