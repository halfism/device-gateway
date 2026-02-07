package com.device.gateway.core.manager.impl;

import com.device.gateway.common.entity.Device;
import com.device.gateway.common.message.Message;
import com.device.gateway.core.manager.DeviceManager;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CompletableFuture;
import java.util.ArrayList;

/**
 * 设备管理器实现类
 */
@Service
public class DeviceManagerImpl implements DeviceManager {
    
    private Map<String, Device> deviceRegistry = new ConcurrentHashMap<>();
    
    @Override
    public Device connectDevice(Device device) {
        deviceRegistry.put(device.getDeviceId(), device);
        System.out.println("Device connected: " + device.getDeviceId());
        return device;
    }
    
    @Override
    public void disconnectDevice(String deviceId) {
        Device device = deviceRegistry.remove(deviceId);
        if (device != null) {
            System.out.println("Device disconnected: " + deviceId);
        }
    }
    
    @Override
    public CompletableFuture<Boolean> sendMessageToDevice(String deviceId, Message message) {
        Device device = deviceRegistry.get(deviceId);
        if (device == null) {
            return CompletableFuture.completedFuture(false);
        }
        
        // 模拟发送消息
        System.out.println("Sending message to device: " + deviceId + 
                          ", message: " + new String(message.getPayload()));
        return CompletableFuture.completedFuture(true);
    }
    
    @Override
    public List<Device> getAllDevices() {
        return new ArrayList<>(deviceRegistry.values());
    }
    
    @Override
    public Device getDeviceById(String deviceId) {
        return deviceRegistry.get(deviceId);
    }
    
    @Override
    public void registerDevice(Device device) {
        deviceRegistry.put(device.getDeviceId(), device);
        System.out.println("Device registered: " + device.getDeviceId());
    }
    
    @Override
    public void unregisterDevice(String deviceId) {
        deviceRegistry.remove(deviceId);
        System.out.println("Device unregistered: " + deviceId);
    }
    
    @Override
    public void updateDeviceStatus(String deviceId, String status) {
        Device device = deviceRegistry.get(deviceId);
        if (device != null) {
            // 在实际实现中，这里会更新设备状态
            System.out.println("Device status updated: " + deviceId + " -> " + status);
        }
    }
}