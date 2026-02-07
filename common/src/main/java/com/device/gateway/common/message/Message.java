package com.device.gateway.common.message;

import com.device.gateway.common.ProtocolType;
import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.HashMap;
import java.time.LocalDateTime;

/**
 * 消息实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    private String messageId;
    private String deviceId;
    private String topic;
    private byte[] payload;
    private ProtocolType protocol;
    private MessageType messageType;
    private Map<String, Object> attributes;
    private LocalDateTime timestamp;
    private Integer qos;
    private Boolean retained;

    public Message(String deviceId, String topic, byte[] payload) {
        this.deviceId = deviceId;
        this.topic = topic;
        this.payload = payload;
        this.attributes = new HashMap<>();
        this.timestamp = LocalDateTime.now();
        this.qos = 1;
        this.retained = false;
    }

    public String getPayloadAsString() {
        if (payload != null) {
            return new String(payload);
        }
        return null;
    }

    public void setPayloadFromString(String payloadStr) {
        if (payloadStr != null) {
            this.payload = payloadStr.getBytes();
        }
    }
}