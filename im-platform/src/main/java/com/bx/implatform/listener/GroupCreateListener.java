package com.bx.implatform.listener;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.bx.implatform.entity.privacy.GroupPrivacyConfig;
import com.bx.implatform.event.GroupCreateEvent;
import com.bx.implatform.mapper.privacy.GroupPrivacyConfigMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 群组创建监听器
 * 
 * 当群组创建时，自动创建群隐私配置记录（默认关闭）
 *
 * @author blue
 * @since 2025-10-06
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GroupCreateListener {

    private final GroupPrivacyConfigMapper groupPrivacyConfigMapper;

    @Async
    @EventListener
    public void onGroupCreate(GroupCreateEvent event) {
        try {
            Long groupId = event.getGroupId();
            Long ownerId = event.getOwnerId();

            log.debug("接收到群组创建事件, groupId:{}, ownerId:{}", groupId, ownerId);

            // 检查是否已存在配置
            LambdaQueryWrapper<GroupPrivacyConfig> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(GroupPrivacyConfig::getGroupId, groupId);
            GroupPrivacyConfig existing = groupPrivacyConfigMapper.selectOne(wrapper);

            if (existing != null) {
                log.debug("群组隐私配置已存在, groupId:{}", groupId);
                return;
            }

            // 创建默认配置（关闭状态）
            GroupPrivacyConfig config = new GroupPrivacyConfig();
            config.setGroupId(groupId);
            config.setPrivacyEnabled(0);  // 默认关闭
            config.setAdminViewReal(1);
            config.setCreatedBy(ownerId);

            groupPrivacyConfigMapper.insert(config);
            log.info("自动创建群组隐私配置, groupId:{}, privacyEnabled:0 (默认关闭)", groupId);

        } catch (Exception e) {
            log.error("处理群组创建事件失败", e);
        }
    }
}

