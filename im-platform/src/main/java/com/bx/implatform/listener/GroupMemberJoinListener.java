package com.bx.implatform.listener;

import com.bx.implatform.event.GroupMemberJoinEvent;
import com.bx.implatform.service.privacy.GroupPrivacyService;
import com.bx.implatform.service.privacy.VirtualMemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 群成员加入监听器
 * 
 * 当用户加入群组时，如果群组开启了隐私保护，自动为用户生成虚拟信息
 *
 * @author blue
 * @since 2025-10-06
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GroupMemberJoinListener {

    private final GroupPrivacyService groupPrivacyService;
    private final VirtualMemberService virtualMemberService;

    @Async
    @EventListener
    public void onGroupMemberJoin(GroupMemberJoinEvent event) {
        try {
            Long groupId = event.getGroupId();
            Long userId = event.getUserId();

            log.debug("接收到群成员加入事件, groupId:{}, userId:{}", groupId, userId);

            // 检查全局隐私开关
            if (!groupPrivacyService.isGlobalPrivacyEnabled()) {
                return;
            }

            // 检查群组隐私配置
            if (!groupPrivacyService.isPrivacyEnabled(groupId)) {
                return;
            }

            // 生成虚拟成员信息
            virtualMemberService.generateVirtualMember(groupId, userId);
            log.info("自动生成虚拟成员信息, groupId:{}, userId:{}", groupId, userId);

        } catch (Exception e) {
            log.error("处理群成员加入事件失败", e);
        }
    }
}

