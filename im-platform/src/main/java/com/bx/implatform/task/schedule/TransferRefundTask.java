package com.bx.implatform.task.schedule;

import com.bx.implatform.service.TransferService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 转账过期退款定时任务
 *
 * @author blue
 * @since 2025-10-07
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TransferRefundTask {

    private final TransferService transferService;

    /**
     * 每小时执行一次，退款过期未领取的转账
     */
    @Scheduled(cron = "0 0 * * * ?")
    public void refundExpiredTransfers() {
        log.info("开始执行转账过期退款任务");
        try {
            transferService.refundExpiredTransfers();
            log.info("转账过期退款任务执行完成");
        } catch (Exception e) {
            log.error("转账过期退款任务执行失败", e);
        }
    }
}

