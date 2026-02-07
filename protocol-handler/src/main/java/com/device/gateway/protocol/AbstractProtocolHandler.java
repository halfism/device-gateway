package com.device.gateway.protocol;

import com.device.gateway.common.entity.Device;
import com.device.gateway.common.message.Message;
import com.device.gateway.core.config.ProtocolConfig;
import com.device.gateway.core.handler.ProtocolHandler;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 抽象协议处理器
 */
public abstract class AbstractProtocolHandler implements ProtocolHandler {
    
    protected ProtocolConfig config;
    protected volatile boolean initialized = false;
    protected ConcurrentHashMap<String, Device> connectedDevices = new ConcurrentHashMap<>();
    
    @Override
    public void initialize(ProtocolConfig config) {
        this.config = config;
        doInitialize(config);
        this.initialized = true;
    }
    
    /**
     * 子类实现具体的初始化逻辑
     * @param config 协议配置
     */
    protected abstract void doInitialize(ProtocolConfig config);
    
    @Override
    public void registerDevice(Device device) {
        connectedDevices.put(device.getDeviceId(), device);
        onDeviceRegistered(device);
    }
    
    @Override
    public void unregisterDevice(String deviceId) {
        connectedDevices.remove(deviceId);
        onDeviceUnregistered(deviceId);
    }
    
    @Override
    public boolean isConnected(String deviceId) {
        Device device = connectedDevices.get(deviceId);
        return device != null && device.getStatus().getCode().equals("connected");
    }
    
    @Override
    public void disconnect(String deviceId) {
        Device device = connectedDevices.get(deviceId);
        if (device != null) {
            doDisconnect(device);
            device.setStatus(com.device.gateway.common.enums.ConnectionStatus.DISCONNECTED);
        }
    }
    
    /**
     * 子类实现具体的断连逻辑
     * @param device 设备
     */
    protected abstract void doDisconnect(Device device);
    
    /**
     * 设备注册回调
     * @param device 设备
     */
    protected void onDeviceRegistered(Device device) {
        // 默认空实现，子类可重写
    }
    
    /**
     * 设备注销回调
     * @param deviceId 设备ID
     */
    protected void onDeviceUnregistered(String deviceId) {
        // 默认空实现，子类可重写
    }
}