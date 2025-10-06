package com.bx.implatform.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * 群组创建事件
 *
 * @author blue
 * @since 2025-10-06
 */
@Getter
public class GroupCreateEvent extends ApplicationEvent {

    private final Long groupId;
    private final Long ownerId;

    public GroupCreateEvent(Object source, Long groupId, Long ownerId) {
        super(source);
        this.groupId = groupId;
        this.ownerId = ownerId;
    }
}

