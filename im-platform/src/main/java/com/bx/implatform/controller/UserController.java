package com.bx.implatform.controller;

import com.bx.implatform.annotation.RepeatSubmit;
import com.bx.implatform.dto.BindEmailDTO;
import com.bx.implatform.dto.BindPhoneDTO;
import com.bx.implatform.result.Result;
import com.bx.implatform.result.ResultUtils;
import com.bx.implatform.service.UserService;
import com.bx.implatform.vo.UserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "用户相关")
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/self")
    @Operation(summary = "获取当前用户信息", description = "获取当前用户信息")
    public Result<UserVO> findSelfInfo() {
        return ResultUtils.success(userService.findSelfInfo());
    }


    @GetMapping("/find/{id}")
    @Operation(summary = "查找用户", description = "根据id查找用户")
    public Result<UserVO> findById(@NotNull @PathVariable("id") Long id) {
        return ResultUtils.success(userService.findUserById(id));
    }

    @RepeatSubmit
    @PutMapping("/update")
    @Operation(summary = "修改用户信息", description = "修改用户信息，仅允许修改登录用户信息")
    public Result update(@Valid @RequestBody UserVO vo) {
        userService.update(vo);
        return ResultUtils.success();
    }

    @GetMapping("/search")
    @Operation(summary = "查找用户", description = "根据用户名/昵称/手机/邮件查找用户")
    public Result<List<UserVO>> search(@RequestParam String name) {
        return ResultUtils.success(userService.search(name));
    }


    @PostMapping("/reportCid")
    @Operation(summary = "上报用户cid", description = "上报用户cid")
    public Result reportCid(@RequestParam String cid){
        userService.reportCid(cid);
        return ResultUtils.success();
    }


    @DeleteMapping("/removeCid")
    @Operation(summary = "清理用户cid", description = "清理用户cid")
    public Result removeCid(){
        userService.removeCid();
        return ResultUtils.success();
    }

    @PutMapping("/manualApprove")
    @Operation(summary = "开启/关闭好友验证", description = "开启/关闭好友验证")
    public Result setManualApprove(@RequestParam Boolean enabled){
        userService.setManualApprove(enabled);
        return ResultUtils.success();
    }

    @PutMapping("/audioTip")
    @Operation(summary = "开启/关闭新消息语音提醒", description = "开启/关闭新消息语音提醒")
    public Result setAudioTip(@RequestParam Boolean enabled){
        userService.setAudioTip(enabled);
        return ResultUtils.success();
    }


    @RepeatSubmit
    @PutMapping("/bindPhone")
    @Operation(summary = "绑定手机", description = "绑定手机")
    public Result bindPhone(@Valid @RequestBody BindPhoneDTO dto){
        userService.bindPhone(dto);
        return ResultUtils.success();
    }

    @RepeatSubmit
    @PutMapping("/bindEmail")
    @Operation(summary = "绑定邮箱", description = "绑定邮箱")
    public Result bindEmail(@Valid @RequestBody BindEmailDTO dto){
        userService.bindEmail(dto);
        return ResultUtils.success();
    }


}

