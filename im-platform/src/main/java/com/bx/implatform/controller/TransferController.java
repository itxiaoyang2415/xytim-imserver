package com.bx.implatform.controller;

import com.bx.implatform.annotation.RepeatSubmit;
import com.bx.implatform.dto.ReceiveTransferDTO;
import com.bx.implatform.dto.TransferDTO;
import com.bx.implatform.result.Result;
import com.bx.implatform.result.ResultUtils;
import com.bx.implatform.service.TransferService;
import com.bx.implatform.vo.TransferVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


/**
 * 转账控制器
 *
 * @author blue
 * @since 2025-10-06
 */
@Tag(name = "转账相关")
@RestController
@RequestMapping("/transfer")
@RequiredArgsConstructor
public class TransferController {

    private final TransferService transferService;

    @RepeatSubmit
    @PostMapping("/send")
    @Operation(summary = "发送转账", description = "向其他用户转账，需对方领取")
    public Result<TransferVO> transfer(@Valid @RequestBody TransferDTO dto) {
        TransferVO vo = transferService.transfer(dto);
        return ResultUtils.success(vo);
    }

    @RepeatSubmit
    @PostMapping("/receive")
    @Operation(summary = "领取转账", description = "领取别人发送的转账")
    public Result<TransferVO> receiveTransfer(@Valid @RequestBody ReceiveTransferDTO dto) {
        TransferVO vo = transferService.receiveTransfer(dto);
        return ResultUtils.success(vo);
    }

    @GetMapping("/detail/{transferNo}")
    @Operation(summary = "查询转账详情", description = "根据转账编号查询转账详情")
    public Result<TransferVO> getTransferDetail(@PathVariable String transferNo) {
        TransferVO vo = transferService.getTransferDetail(transferNo);
        return ResultUtils.success(vo);
    }
}

