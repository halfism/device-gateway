package com.device.gateway.common.entity;

import com.device.gateway.common.ProtocolType;
import com.device.gateway.common.enums.ConnectionStatus;
import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.HashMap;

/**
 * 设备实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Device {
    private String deviceId;
    private String deviceName;
    private ProtocolType protocol;
    private String host;
    private Integer port;
    private ConnectionStatus status;
    private Map<String, Object> metadata;
    private Long createTime;
    private Long updateTime;

    public Device(String deviceId, ProtocolType protocol) {
        this.deviceId = deviceId;
        this.protocol = protocol;
        this.status = ConnectionStatus.DISCONNECTED;
        this.metadata = new HashMap<>();
        this.createTime = System.currentTimeMillis();
        this.updateTime = System.currentTimeMillis();
    }
}