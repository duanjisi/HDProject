package com.atgc.hd.entity;

/**
 * <p>描述：消息载体，用于EventBus消息传递
 * <p>作者：liangguokui 2018/1/31
 */
public class EventMessage {

    // 用于标识message类别
    public String eventTag;

    public Object object;

    public EventMessage(String eventTag) {
        this.eventTag = eventTag;
    }

    public EventMessage(String eventTag, Object object) {
        this.eventTag = eventTag;
        this.object = object;
    }
}
