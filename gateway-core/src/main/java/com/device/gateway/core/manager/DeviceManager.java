package com.device.gateway.core.manager;

import com.device.gateway.common.entity.Device;
import com.device.gateway.common.message.Message;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 设备管理接口
 */
public interface DeviceManager {
    
    /**
     * 连接设备
     * @param device 设备信息
     * @return 连接后的设备对象
     */
    Device connectDevice(Device device);
    
    /**
     * 断开设备连接
     * @param deviceId 设备ID
     */
    void disconnectDevice(String deviceId);
    
    /**
     * 发送消息给设备
     * @param deviceId 设备ID
     * @param message 消息
     * @return 发送结果
     */
    CompletableFuture<Boolean> sendMessageToDevice(String deviceId, Message message);
    
    /**
     * 获取设备列表
     * @return 设备列表
     */
    List<Device> getAllDevices();
    
    /**
     * 根据ID获取设备
     * @param deviceId 设备ID
     * @return 设备对象
     */
    Device getDeviceById(String deviceId);
    
    /**
     * 注册设备
     * @param device 设备对象
     */
    void registerDevice(Device device);
    
    /**
     * 注销设备
     * @param deviceId 设备ID
     */
    void unregisterDevice(String deviceId);
    
    /**
     * 更新设备状态
     * @param deviceId 设备ID
     * @param status 新状态
     */
    void updateDeviceStatus(String deviceId, String status);
}