package com.bx.implatform.controller;

import com.bx.implatform.annotation.RepeatSubmit;
import com.bx.implatform.dto.ReceiveRedPacketDTO;
import com.bx.implatform.dto.SendRedPacketDTO;
import com.bx.implatform.result.Result;
import com.bx.implatform.result.ResultUtils;
import com.bx.implatform.service.RedPacketService;
import com.bx.implatform.vo.RedPacketReceiveVO;
import com.bx.implatform.vo.RedPacketVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * 红包控制器
 *
 * @author blue
 * @since 2025-10-06
 */
@Tag(name = "红包相关")
@RestController
@RequestMapping("/redpacket")
@RequiredArgsConstructor
public class RedPacketController {

    private final RedPacketService redPacketService;

    @RepeatSubmit
    @PostMapping("/send")
    @Operation(summary = "发送红包", description = "发送红包到聊天会话中")
    public Result<RedPacketVO> sendRedPacket(@Valid @RequestBody SendRedPacketDTO dto) {
        RedPacketVO vo = redPacketService.sendRedPacket(dto);
        return ResultUtils.success(vo);
    }

    @RepeatSubmit
    @PostMapping("/receive")
    @Operation(summary = "领取红包", description = "领取红包")
    public Result<BigDecimal> receiveRedPacket(@Valid @RequestBody ReceiveRedPacketDTO dto) {
        BigDecimal amount = redPacketService.receiveRedPacket(dto);
        return ResultUtils.success(amount);
    }

    @GetMapping("/detail/{packetNo}")
    @Operation(summary = "查询红包详情", description = "根据红包编号查询红包详情")
    public Result<RedPacketVO> getRedPacketDetail(@PathVariable String packetNo) {
        RedPacketVO vo = redPacketService.getRedPacketDetail(packetNo);
        return ResultUtils.success(vo);
    }

    @GetMapping("/receives/{packetNo}")
    @Operation(summary = "查询红包领取记录", description = "查询红包的所有领取记录")
    public Result<List<RedPacketReceiveVO>> getReceiveRecords(@PathVariable String packetNo) {
        List<RedPacketReceiveVO> list = redPacketService.getReceiveRecords(packetNo);
        return ResultUtils.success(list);
    }
}

