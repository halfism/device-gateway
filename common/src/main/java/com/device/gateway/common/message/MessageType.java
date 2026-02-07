package com.device.gateway.common.message;

/**
 * 消息类型枚举
 */
public enum MessageType {
    REQUEST("request", "请求消息"),
    RESPONSE("response", "响应消息"),
    EVENT("event", "事件消息"),
    COMMAND("command", "命令消息"),
    DATA("data", "数据消息"),
    CONTROL("control", "控制消息"),
    HEARTBEAT("heartbeat", "心跳消息");

    private final String code;
    private final String description;

    MessageType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}