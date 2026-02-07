package com.device.gateway.core.handler;

import com.device.gateway.common.entity.Device;
import com.device.gateway.common.message.Message;
import com.device.gateway.core.config.ProtocolConfig;
import java.util.concurrent.CompletableFuture;

/**
 * 协议处理器接口
 */
public interface ProtocolHandler {
    
    /**
     * 初始化协议处理器
     * @param config 协议配置
     */
    void initialize(ProtocolConfig config);
    
    /**
     * 发送消息
     * @param message 消息内容
     * @return 发送结果
     */
    CompletableFuture<Message> send(Message message);
    
    /**
     * 注册设备
     * @param device 设备信息
     */
    void registerDevice(Device device);
    
    /**
     * 注销设备
     * @param deviceId 设备ID
     */
    void unregisterDevice(String deviceId);
    
    /**
     * 获取协议类型
     * @return 协议类型
     */
    String getProtocolType();
    
    /**
     * 检查连接状态
     * @param deviceId 设备ID
     * @return 是否已连接
     */
    boolean isConnected(String deviceId);
    
    /**
     * 断开连接
     * @param deviceId 设备ID
     */
    void disconnect(String deviceId);
    
    /**
     * 连接设备
     * @param device 设备信息
     * @return 连接结果
     */
    CompletableFuture<Boolean> connect(Device device);
}