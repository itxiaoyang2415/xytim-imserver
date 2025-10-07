package com.bx.implatform.controller;

import com.bx.implatform.annotation.RepeatSubmit;
import com.bx.implatform.dto.SetPayPasswordDTO;
import com.bx.implatform.dto.UpdatePayPasswordDTO;
import com.bx.implatform.result.Result;
import com.bx.implatform.result.ResultUtils;
import com.bx.implatform.service.UserWalletService;
import com.bx.implatform.service.WalletOrderService;
import com.bx.implatform.service.WalletTransactionService;
import com.bx.implatform.vo.UserWalletVO;
import com.bx.implatform.vo.WalletOrderVO;
import com.bx.implatform.vo.WalletTransactionVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户钱包控制器
 *
 * @author blue
 * @since 2025-10-06
 */
@Tag(name = "钱包相关")
@RestController
@RequestMapping("/wallet")
@RequiredArgsConstructor
public class UserWalletController {

    private final UserWalletService userWalletService;
    private final WalletTransactionService walletTransactionService;
    private final WalletOrderService walletOrderService;

    @GetMapping("/info")
    @Operation(summary = "查询钱包信息", description = "查询当前用户的钱包信息")
    public Result<UserWalletVO> getWalletInfo() {
        UserWalletVO vo = userWalletService.getWalletInfo();
        return ResultUtils.success(vo);
    }

    @RepeatSubmit
    @PostMapping("/setPayPassword")
    @Operation(summary = "设置支付密码", description = "首次设置支付密码")
    public Result<Void> setPayPassword(@Valid @RequestBody SetPayPasswordDTO dto) {
        userWalletService.setPayPassword(dto);
        return ResultUtils.success();
    }

    @RepeatSubmit
    @PostMapping("/updatePayPassword")
    @Operation(summary = "修改支付密码", description = "修改支付密码")
    public Result<Void> updatePayPassword(@Valid @RequestBody UpdatePayPasswordDTO dto) {
        userWalletService.updatePayPassword(dto);
        return ResultUtils.success();
    }

    @GetMapping("/transactions")
    @Operation(summary = "查询交易记录", description = "分页查询当前用户的交易记录")
    public Result<List<WalletTransactionVO>> getTransactions(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        List<WalletTransactionVO> list = walletTransactionService.getTransactionList(page, size);
        return ResultUtils.success(list);
    }

    @GetMapping({"/recharges", "/recharge-records"})
    @Operation(summary = "查询充值记录", description = "分页查询当前用户的充值记录")
    public Result<List<WalletOrderVO>> getRechargeRecords(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        List<WalletOrderVO> list = walletOrderService.getRechargeRecords(page, size);
        return ResultUtils.success(list);
    }
}

