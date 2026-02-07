package com.device.gateway.common.enums;

/**
 * 连接状态枚举
 */
public enum ConnectionStatus {
    CONNECTED("connected", "已连接"),
    DISCONNECTED("disconnected", "未连接"),
    CONNECTING("connecting", "连接中"),
    DISCONNECTING("disconnecting", "断连中"),
    ERROR("error", "错误");

    private final String code;
    private final String description;

    ConnectionStatus(String code, String description) {
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