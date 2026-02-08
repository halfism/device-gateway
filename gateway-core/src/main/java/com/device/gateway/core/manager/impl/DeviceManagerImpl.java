package com.device.gateway.core.manager.impl;

import com.device.gateway.common.entity.Device;
import com.device.gateway.common.message.Message;
import com.device.gateway.core.manager.DeviceManager;
import com.device.gateway.infrastructure.session.ReactiveDeviceSessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CompletableFuture;
import java.util.ArrayList;

/**
 * 设备管理器实现类
 * 集成响应式基础设施
 */
@Service
public class DeviceManagerImpl implements DeviceManager {
    
    private Map<String, Device> deviceRegistry = new ConcurrentHashMap<>();
    
    @Autowired(required = false)
    private ReactiveDeviceSessionManager reactiveSessionManager;
    
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
    
    @Override
    public ReactiveDeviceSessionManager getReactiveSessionManager() {
        return reactiveSessionManager;
    }
    
    @Override
    public Mono<Device> connectDeviceReactive(Device device) {
        // 临时实现 - 返回Mono包装的同步结果
        Device connectedDevice = connectDevice(device);
        return Mono.just(connectedDevice);
    }
    
    @Override
    public Mono<Void> disconnectDeviceReactive(String deviceId) {
        // 临时实现 - 执行同步操作并返回Mono
        disconnectDevice(deviceId);
        return Mono.empty();
    }
    
    @Override
    public Mono<Boolean> sendMessageToDeviceReactive(String deviceId, Message message) {
        // 临时实现 - 返回Mono包装的异步结果
        return Mono.fromFuture(sendMessageToDevice(deviceId, message));
    }
    
    @Override
    public Mono<Device> getDeviceByIdReactive(String deviceId) {
        // 临时实现 - 返回Mono包装的同步结果
        Device device = getDeviceById(deviceId);
        return device != null ? Mono.just(device) : Mono.empty();
    }
    
    @Override
    public Mono<List<Device>> getAllDevicesReactive() {
        // 临时实现 - 返回Mono包装的同步结果
        List<Device> devices = getAllDevices();
        return Mono.just(devices);
    }
}