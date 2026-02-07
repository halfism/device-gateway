package com.device.gateway.common;

/**
 * 协议类型枚举
 */
public enum ProtocolType {
    MQTT("mqtt"),
    TCP("tcp"),
    HTTP("http"),
    MODBUS("modbus"),
    LORAWAN("lorawan"),
    NBIOT("nbiot"),
    COAP("coap"),
    WEBSOCKET("websocket"),
    UDP("udp");

    private final String value;

    ProtocolType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static ProtocolType fromValue(String value) {
        for (ProtocolType type : ProtocolType.values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown protocol type: " + value);
    }
}