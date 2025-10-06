package com.bx.implatform.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * 群成员加入事件
 *
 * @author blue
 * @since 2025-10-06
 */
@Getter
public class GroupMemberJoinEvent extends ApplicationEvent {

    private final Long groupId;
    private final Long userId;

    public GroupMemberJoinEvent(Object source, Long groupId, Long userId) {
        super(source);
        this.groupId = groupId;
        this.userId = userId;
    }
}

