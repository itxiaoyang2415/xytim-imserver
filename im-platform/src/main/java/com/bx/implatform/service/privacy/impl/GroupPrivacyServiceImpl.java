package com.bx.implatform.service.privacy.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bx.implatform.dto.privacy.GroupPrivacyConfigDTO;
import com.bx.implatform.entity.GroupMember;
import com.bx.implatform.entity.privacy.GroupPrivacyConfig;
import com.bx.implatform.entity.privacy.SystemPrivacyConfig;
import com.bx.implatform.exception.GlobalException;
import com.bx.implatform.mapper.privacy.GroupPrivacyConfigMapper;
import com.bx.implatform.mapper.privacy.SystemPrivacyConfigMapper;
import com.bx.implatform.service.GroupMemberService;
import com.bx.implatform.service.GroupService;
import com.bx.implatform.service.privacy.GroupPrivacyService;
import com.bx.implatform.session.SessionContext;
import com.bx.implatform.util.BeanUtils;
import com.bx.implatform.vo.GroupVO;
import com.bx.implatform.vo.privacy.GroupPrivacyConfigVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 群组隐私服务实现
 *
 * @author blue
 * @since 2025-10-06
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GroupPrivacyServiceImpl extends ServiceImpl<GroupPrivacyConfigMapper, GroupPrivacyConfig> 
        implements GroupPrivacyService {

    private final GroupMemberService groupMemberService;
    private final GroupService groupService;
    private final SystemPrivacyConfigMapper systemPrivacyConfigMapper;

    private static final String GLOBAL_PRIVACY_KEY = "group_privacy_global_enabled";

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "groupPrivacyConfig", key = "#dto.groupId")
    public GroupPrivacyConfigVO configurePrivacy(GroupPrivacyConfigDTO dto) {
        Long userId = SessionContext.getSession().getUserId();

        // 验证用户是否为群主
        GroupVO group = groupService.findById(dto.getGroupId());
        if (group == null || !userId.equals(group.getOwnerId())) {
            throw new GlobalException("只有群主可以配置隐私保护");
        }

        // 查询是否已有配置
        LambdaQueryWrapper<GroupPrivacyConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GroupPrivacyConfig::getGroupId, dto.getGroupId());
        GroupPrivacyConfig config = this.getOne(wrapper);

        if (config == null) {
            // 创建新配置
            config = new GroupPrivacyConfig();
            config.setGroupId(dto.getGroupId());
            config.setPrivacyEnabled(dto.getPrivacyEnabled());
            config.setAdminViewReal(1);
            config.setCreatedBy(userId);
            this.save(config);
            log.info("创建群组隐私配置, groupId:{}, privacyEnabled:{}", dto.getGroupId(), dto.getPrivacyEnabled());
        } else {
            // 更新配置
            config.setPrivacyEnabled(dto.getPrivacyEnabled());
            this.updateById(config);
            log.info("更新群组隐私配置, groupId:{}, privacyEnabled:{}", dto.getGroupId(), dto.getPrivacyEnabled());
        }

        return BeanUtils.copyProperties(config, GroupPrivacyConfigVO.class);
    }

    @Override
    @Cacheable(value = "groupPrivacyConfig", key = "#groupId", unless = "#result == null")
    public GroupPrivacyConfigVO getGroupPrivacyConfig(Long groupId) {
        LambdaQueryWrapper<GroupPrivacyConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GroupPrivacyConfig::getGroupId, groupId);
        GroupPrivacyConfig config = this.getOne(wrapper);

        if (config == null) {
            return null;
        }

        return BeanUtils.copyProperties(config, GroupPrivacyConfigVO.class);
    }

    @Override
    public boolean isPrivacyEnabled(Long groupId) {
        // 先检查全局开关
        if (!isGlobalPrivacyEnabled()) {
            return false;
        }

        // 查询群组配置
        LambdaQueryWrapper<GroupPrivacyConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GroupPrivacyConfig::getGroupId, groupId);
        GroupPrivacyConfig config = this.getOne(wrapper);

        if (config == null) {
            return false;
        }

        return config.getPrivacyEnabled() != null && config.getPrivacyEnabled() == 1;
    }

    @Override
    public boolean canViewRealInfo(Long groupId, Long userId) {
        // 检查全局开关
        if (!isGlobalPrivacyEnabled()) {
            return true;
        }

        // 检查群隐私配置
        if (!isPrivacyEnabled(groupId)) {
            return true;
        }

        // 查询群组信息，判断是否为群主
        GroupVO group = groupService.findById(groupId);
        if (group != null && userId.equals(group.getOwnerId())) {
            return true;
        }

        // 检查用户角色（管理员）
        GroupMember member = groupMemberService.findByGroupAndUserId(groupId, userId);
        if (member == null || member.getQuit()) {
            return false;
        }

        // 管理员可以查看真实信息
        return member.getIsManager() != null && member.getIsManager();
    }

    @Override
    @Cacheable(value = "systemPrivacyConfig", key = "'global_enabled'")
    public boolean isGlobalPrivacyEnabled() {
        LambdaQueryWrapper<SystemPrivacyConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SystemPrivacyConfig::getConfigKey, GLOBAL_PRIVACY_KEY);
        SystemPrivacyConfig config = systemPrivacyConfigMapper.selectOne(wrapper);

        if (config == null) {
            return true; // 默认开启
        }

        return "1".equals(config.getConfigValue());
    }
}

