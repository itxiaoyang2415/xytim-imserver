package com.bx.implatform.service.privacy;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bx.implatform.entity.privacy.GroupVirtualMember;
import com.bx.implatform.vo.privacy.VirtualMemberVO;

import java.util.List;
import java.util.Map;

/**
 * 虚拟成员服务接口
 *
 * @author blue
 * @since 2025-10-06
 */
public interface VirtualMemberService extends IService<GroupVirtualMember> {

    /**
     * 为用户生成虚拟信息
     *
     * @param groupId 群组ID
     * @param realUserId 真实用户ID
     * @return 虚拟成员信息
     */
    GroupVirtualMember generateVirtualMember(Long groupId, Long realUserId);

    /**
     * 获取用户的虚拟信息
     *
     * @param groupId 群组ID
     * @param realUserId 真实用户ID
     * @return 虚拟成员信息
     */
    GroupVirtualMember getVirtualMember(Long groupId, Long realUserId);

    /**
     * 批量获取虚拟信息
     *
     * @param groupId 群组ID
     * @param realUserIds 真实用户ID列表
     * @return 真实用户ID -> 虚拟成员信息的映射
     */
    Map<Long, GroupVirtualMember> batchGetVirtualMembers(Long groupId, List<Long> realUserIds);

    /**
     * 查询群组所有虚拟成员
     *
     * @param groupId 群组ID
     * @return 虚拟成员列表
     */
    List<VirtualMemberVO> listVirtualMembers(Long groupId);

    /**
     * 批量生成群组成员的虚拟信息
     *
     * @param groupId 群组ID
     * @return 生成数量
     */
    int batchGenerateForGroup(Long groupId);

    /**
     * 失效用户的虚拟信息（用户退群时调用）
     *
     * @param groupId 群组ID
     * @param realUserId 真实用户ID
     * @return 是否成功
     */
    boolean invalidateVirtualMember(Long groupId, Long realUserId);
}

