package com.taotao.cloud.message.api.enums;

/**
 * 消息状态枚举
 */
public enum MessageStatusEnum {

    //未读消息
    UN_READY("未读消息"),
    //已读消息
    ALREADY_READY("已读消息"),
    //回收站
    ALREADY_REMOVE("回收站");

    private final String description;

    MessageStatusEnum(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }


}
