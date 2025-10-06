package com.bx.implatform.task.schedule;

import com.bx.implatform.service.RedPacketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 红包过期退款定时任务
 *
 * @author blue
 * @since 2025-10-06
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RedPacketRefundTask {

    private final RedPacketService redPacketService;

    /**
     * 每小时执行一次，检查并退款过期红包
     */
    @Scheduled(cron = "0 0 * * * ?")
    public void refundExpiredRedPackets() {
        log.info("开始执行红包过期退款任务");
        try {
            redPacketService.refundExpiredRedPackets();
            log.info("红包过期退款任务执行完成");
        } catch (Exception e) {
            log.error("红包过期退款任务执行失败", e);
        }
    }
}

