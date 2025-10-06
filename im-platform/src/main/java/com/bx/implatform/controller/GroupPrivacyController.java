package com.bx.implatform.controller;

import com.bx.implatform.dto.privacy.GroupPrivacyConfigDTO;
import com.bx.implatform.result.Result;
import com.bx.implatform.result.ResultUtils;
import com.bx.implatform.service.privacy.GroupPrivacyService;
import com.bx.implatform.service.privacy.VirtualMemberService;
import com.bx.implatform.vo.privacy.GroupPrivacyConfigVO;
import com.bx.implatform.vo.privacy.VirtualMemberVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 群组隐私保护控制器
 *
 * @author blue
 * @since 2025-10-06
 */
@Tag(name = "群组隐私保护")
@RestController
@RequestMapping("/group/privacy")
@RequiredArgsConstructor
public class GroupPrivacyController {

    private final GroupPrivacyService groupPrivacyService;
    private final VirtualMemberService virtualMemberService;

    @Operation(summary = "配置群组隐私保护", description = "开启/关闭群组隐私保护功能（仅群主可操作）")
    @PostMapping("/configure")
    public Result<GroupPrivacyConfigVO> configurePrivacy(@Valid @RequestBody GroupPrivacyConfigDTO dto) {
        GroupPrivacyConfigVO vo = groupPrivacyService.configurePrivacy(dto);
        return ResultUtils.success(vo);
    }

    @Operation(summary = "查询群组隐私配置", description = "查询指定群组的隐私保护配置")
    @GetMapping("/config/{groupId}")
    public Result<GroupPrivacyConfigVO> getConfig(@PathVariable Long groupId) {
        GroupPrivacyConfigVO vo = groupPrivacyService.getGroupPrivacyConfig(groupId);
        return ResultUtils.success(vo);
    }

    @Operation(summary = "检查群组隐私状态", description = "检查群组是否开启隐私保护")
    @GetMapping("/status/{groupId}")
    public Result<Boolean> checkPrivacyStatus(@PathVariable Long groupId) {
        boolean enabled = groupPrivacyService.isPrivacyEnabled(groupId);
        return ResultUtils.success(enabled);
    }

    @Operation(summary = "查询虚拟成员列表", description = "查询群组所有虚拟成员信息（用于管理和调试）")
    @GetMapping("/virtual-members/{groupId}")
    public Result<List<VirtualMemberVO>> listVirtualMembers(@PathVariable Long groupId) {
        List<VirtualMemberVO> list = virtualMemberService.listVirtualMembers(groupId);
        return ResultUtils.success(list);
    }

    @Operation(summary = "批量生成虚拟成员", description = "为群组所有成员生成虚拟信息（仅群主可操作）")
    @PostMapping("/generate/{groupId}")
    public Result<Integer> batchGenerate(@PathVariable Long groupId) {
        int count = virtualMemberService.batchGenerateForGroup(groupId);
        return ResultUtils.success(count);
    }
}

