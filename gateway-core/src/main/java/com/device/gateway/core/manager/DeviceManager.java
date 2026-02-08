package com.device.gateway.core.manager;

import com.device.gateway.common.entity.Device;
import com.device.gateway.common.message.Message;
import com.device.gateway.infrastructure.session.ReactiveDeviceSessionManager;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 设备管理接口
 * 结合传统同步方法和响应式编程方法
 */
public interface DeviceManager {
    
    /**
     * 连接设备 (同步)
     * @param device 设备信息
     * @return 连接后的设备对象
     */
    Device connectDevice(Device device);
    
    /**
     * 断开设备连接 (同步)
     * @param deviceId 设备ID
     */
    void disconnectDevice(String deviceId);
    
    /**
     * 发送消息给设备 (异步)
     * @param deviceId 设备ID
     * @param message 消息
     * @return 发送结果
     */
    CompletableFuture<Boolean> sendMessageToDevice(String deviceId, Message message);
    
    /**
     * 获取设备列表 (同步)
     * @return 设备列表
     */
    List<Device> getAllDevices();
    
    /**
     * 根据ID获取设备 (同步)
     * @param deviceId 设备ID
     * @return 设备对象
     */
    Device getDeviceById(String deviceId);
    
    /**
     * 注册设备 (同步)
     * @param device 设备对象
     */
    void registerDevice(Device device);
    
    /**
     * 注销设备 (同步)
     * @param deviceId 设备ID
     */
    void unregisterDevice(String deviceId);
    
    /**
     * 更新设备状态 (同步)
     * @param deviceId 设备ID
     * @param status 新状态
     */
    void updateDeviceStatus(String deviceId, String status);
    
    /**
     * 获取响应式设备会话管理器
     */
    ReactiveDeviceSessionManager getReactiveSessionManager();
    
    /**
     * 响应式连接设备
     * @param device 设备信息
     * @return 连接结果
     */
    Mono<Device> connectDeviceReactive(Device device);
    
    /**
     * 响应式断开设备
     * @param deviceId 设备ID
     * @return 断开结果
     */
    Mono<Void> disconnectDeviceReactive(String deviceId);
    
    /**
     * 响应式发送消息给设备
     * @param deviceId 设备ID
     * @param message 消息
     * @return 发送结果
     */
    Mono<Boolean> sendMessageToDeviceReactive(String deviceId, Message message);
    
    /**
     * 响应式获取设备
     * @param deviceId 设备ID
     * @return 设备对象
     */
    Mono<Device> getDeviceByIdReactive(String deviceId);
    
    /**
     * 响应式获取所有设备
     * @return 设备列表流
     */
    Mono<List<Device>> getAllDevicesReactive();
}