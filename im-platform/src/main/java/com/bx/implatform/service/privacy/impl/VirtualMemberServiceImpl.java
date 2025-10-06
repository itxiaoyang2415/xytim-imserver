package com.bx.implatform.service.privacy.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bx.implatform.entity.GroupMember;
import com.bx.implatform.entity.privacy.GroupVirtualMember;
import com.bx.implatform.entity.privacy.SystemPrivacyConfig;
import com.bx.implatform.enums.VirtualMemberStatusEnum;
import com.bx.implatform.mapper.privacy.GroupVirtualMemberMapper;
import com.bx.implatform.mapper.privacy.SystemPrivacyConfigMapper;
import com.bx.implatform.service.GroupMemberService;
import com.bx.implatform.service.privacy.VirtualMemberService;
import com.bx.implatform.util.BeanUtils;
import com.bx.implatform.vo.privacy.VirtualMemberVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 虚拟成员服务实现
 *
 * @author blue
 * @since 2025-10-06
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class VirtualMemberServiceImpl extends ServiceImpl<GroupVirtualMemberMapper, GroupVirtualMember> 
        implements VirtualMemberService {

    private final GroupMemberService groupMemberService;
    private final SystemPrivacyConfigMapper systemPrivacyConfigMapper;

    private static final String NICK_NAME_PREFIX_KEY = "virtual_nick_name_prefix";
    private static final String DEFAULT_NICK_NAME_PREFIX = "群友";

    @Override
    @Transactional(rollbackFor = Exception.class)
    public GroupVirtualMember generateVirtualMember(Long groupId, Long realUserId) {
        // 检查是否已存在
        LambdaQueryWrapper<GroupVirtualMember> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GroupVirtualMember::getGroupId, groupId)
               .eq(GroupVirtualMember::getRealUserId, realUserId)
               .eq(GroupVirtualMember::getStatus, VirtualMemberStatusEnum.NORMAL.getCode());
        
        GroupVirtualMember existing = this.getOne(wrapper);
        if (existing != null) {
            return existing;
        }

        // 创建新的虚拟成员信息
        GroupVirtualMember virtual = new GroupVirtualMember();
        virtual.setGroupId(groupId);
        virtual.setRealUserId(realUserId);

        // 生成虚拟用户ID（雪花算法）
        virtual.setVirtualUserId(IdUtil.getSnowflakeNextId());

        // 获取下一个显示序号
        Integer displayOrder = baseMapper.getNextDisplayOrder(groupId);
        virtual.setDisplayOrder(displayOrder);

        // 生成虚拟昵称
        String nickNamePrefix = getNickNamePrefix();
        String virtualNickName = String.format("%s%03d", nickNamePrefix, displayOrder);
        virtual.setVirtualNickName(virtualNickName);

        virtual.setStatus(VirtualMemberStatusEnum.NORMAL.getCode());

        this.save(virtual);
        log.info("生成虚拟成员信息, groupId:{}, realUserId:{}, virtualUserId:{}, virtualNickName:{}",
                groupId, realUserId, virtual.getVirtualUserId(), virtualNickName);

        return virtual;
    }

    @Override
    @Cacheable(value = "virtualMember", key = "#groupId + ':' + #realUserId")
    public GroupVirtualMember getVirtualMember(Long groupId, Long realUserId) {
        LambdaQueryWrapper<GroupVirtualMember> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GroupVirtualMember::getGroupId, groupId)
               .eq(GroupVirtualMember::getRealUserId, realUserId)
               .eq(GroupVirtualMember::getStatus, VirtualMemberStatusEnum.NORMAL.getCode());

        GroupVirtualMember virtual = this.getOne(wrapper);

        // 如果不存在，自动生成
        if (virtual == null) {
            virtual = generateVirtualMember(groupId, realUserId);
        }

        return virtual;
    }

    @Override
    public Map<Long, GroupVirtualMember> batchGetVirtualMembers(Long groupId, List<Long> realUserIds) {
        if (realUserIds == null || realUserIds.isEmpty()) {
            return new HashMap<>();
        }

        // 批量查询
        LambdaQueryWrapper<GroupVirtualMember> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GroupVirtualMember::getGroupId, groupId)
               .in(GroupVirtualMember::getRealUserId, realUserIds)
               .eq(GroupVirtualMember::getStatus, VirtualMemberStatusEnum.NORMAL.getCode());

        List<GroupVirtualMember> virtuals = this.list(wrapper);

        // 转换为Map
        Map<Long, GroupVirtualMember> resultMap = virtuals.stream()
                .collect(Collectors.toMap(GroupVirtualMember::getRealUserId, v -> v));

        // 检查是否有缺失的，自动生成
        for (Long realUserId : realUserIds) {
            if (!resultMap.containsKey(realUserId)) {
                GroupVirtualMember virtual = generateVirtualMember(groupId, realUserId);
                resultMap.put(realUserId, virtual);
            }
        }

        return resultMap;
    }

    @Override
    public List<VirtualMemberVO> listVirtualMembers(Long groupId) {
        LambdaQueryWrapper<GroupVirtualMember> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GroupVirtualMember::getGroupId, groupId)
               .eq(GroupVirtualMember::getStatus, VirtualMemberStatusEnum.NORMAL.getCode())
               .orderByAsc(GroupVirtualMember::getDisplayOrder);

        List<GroupVirtualMember> virtuals = this.list(wrapper);

        return virtuals.stream()
                .map(v -> BeanUtils.copyProperties(v, VirtualMemberVO.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchGenerateForGroup(Long groupId) {
        // 查询群组所有成员
        List<GroupMember> members = groupMemberService.findByGroupId(groupId, 0L);
        
        if (members == null || members.isEmpty()) {
            return 0;
        }

        int count = 0;
        for (GroupMember member : members) {
            if (!member.getQuit()) {
                try {
                    generateVirtualMember(groupId, member.getUserId());
                    count++;
                } catch (Exception e) {
                    log.error("生成虚拟成员信息失败, groupId:{}, userId:{}", groupId, member.getUserId(), e);
                }
            }
        }

        log.info("批量生成虚拟成员信息完成, groupId:{}, count:{}", groupId, count);
        return count;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "virtualMember", key = "#groupId + ':' + #realUserId")
    public boolean invalidateVirtualMember(Long groupId, Long realUserId) {
        LambdaQueryWrapper<GroupVirtualMember> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GroupVirtualMember::getGroupId, groupId)
               .eq(GroupVirtualMember::getRealUserId, realUserId)
               .eq(GroupVirtualMember::getStatus, VirtualMemberStatusEnum.NORMAL.getCode());

        GroupVirtualMember virtual = this.getOne(wrapper);
        if (virtual == null) {
            return true;
        }

        virtual.setStatus(VirtualMemberStatusEnum.INVALID.getCode());
        boolean result = this.updateById(virtual);

        if (result) {
            log.info("失效虚拟成员信息, groupId:{}, realUserId:{}", groupId, realUserId);
        }

        return result;
    }

    /**
     * 获取虚拟昵称前缀
     */
    private String getNickNamePrefix() {
        try {
            LambdaQueryWrapper<SystemPrivacyConfig> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SystemPrivacyConfig::getConfigKey, NICK_NAME_PREFIX_KEY);
            SystemPrivacyConfig config = systemPrivacyConfigMapper.selectOne(wrapper);

            if (config != null && config.getConfigValue() != null) {
                return config.getConfigValue();
            }
        } catch (Exception e) {
            log.warn("获取虚拟昵称前缀失败，使用默认值", e);
        }

        return DEFAULT_NICK_NAME_PREFIX;
    }
}

