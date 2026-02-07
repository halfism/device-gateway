package com.device.gateway.southbound.service;

import com.device.gateway.common.entity.Device;
import com.device.gateway.common.message.Message;
import com.device.gateway.core.handler.ProtocolHandler;
import com.device.gateway.core.manager.DeviceManager;
import com.device.gateway.protocol.impl.MqttProtocolHandler;
import com.device.gateway.protocol.impl.TcpProtocolHandler;
import com.device.gateway.protocol.impl.HttpProtocolHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CompletableFuture;

/**
 * 南向网关服务
 * 负责连接物理设备，处理来自设备的消息
 */
@Service
public class SouthboundGatewayService {
    
    @Autowired
    private DeviceManager deviceManager;
    
    // 协议处理器映射
    private Map<String, ProtocolHandler> protocolHandlers = new ConcurrentHashMap<>();
    
    /**
     * 初始化南向网关
     */
    @PostConstruct
    public void init() {
        // 初始化各种协议处理器
        protocolHandlers.put("mqtt", new MqttProtocolHandler());
        protocolHandlers.put("tcp", new TcpProtocolHandler());
        protocolHandlers.put("http", new HttpProtocolHandler());
        
        System.out.println("Southbound Gateway Service initialized with protocols: " + protocolHandlers.keySet());
    }
    
    /**
     * 连接设备
     * @param device 设备信息
     * @return 连接结果
     */
    public CompletableFuture<Boolean> connectDevice(Device device) {
        ProtocolHandler handler = protocolHandlers.get(device.getProtocol().getValue());
        if (handler == null) {
            throw new IllegalArgumentException("Unsupported protocol: " + device.getProtocol());
        }
        
        // 注册设备到处理器
        handler.registerDevice(device);
        
        // 尝试连接设备
        return handler.connect(device);
    }
    
    /**
     * 断开设备连接
     * @param deviceId 设备ID
     */
    public void disconnectDevice(String deviceId) {
        Device device = deviceManager.getDeviceById(deviceId);
        if (device != null) {
            ProtocolHandler handler = protocolHandlers.get(device.getProtocol().getValue());
            if (handler != null) {
                handler.disconnect(deviceId);
            }
            deviceManager.unregisterDevice(deviceId);
        }
    }
    
    /**
     * 发送消息到设备
     * @param deviceId 设备ID
     * @param message 消息
     * @return 发送结果
     */
    public CompletableFuture<Message> sendMessageToDevice(String deviceId, Message message) {
        Device device = deviceManager.getDeviceById(deviceId);
        if (device == null) {
            throw new IllegalArgumentException("Device not found: " + deviceId);
        }
        
        ProtocolHandler handler = protocolHandlers.get(device.getProtocol().getValue());
        if (handler == null) {
            throw new IllegalArgumentException("No handler for protocol: " + device.getProtocol());
        }
        
        message.setDeviceId(deviceId);
        return handler.send(message);
    }
    
    /**
     * 接收来自设备的消息
     * @param message 消息
     */
    public void receiveMessageFromDevice(Message message) {
        // 在实际实现中，这里会将消息转发到北向网关或消息转换模块
        System.out.println("Southbound gateway received message from device: " + 
                          message.getDeviceId() + ", topic: " + message.getTopic());
        
        // 这里可以触发消息路由到平台
        routeMessageToNorthbound(message);
    }
    
    /**
     * 将消息路由到北向网关
     * @param message 消息
     */
    private void routeMessageToNorthbound(Message message) {
        // 在实际实现中，这里会将消息传递给消息转换模块或直接到北向网关
        System.out.println("Routing message to northbound gateway: " + message.getMessageId());
    }
    
    /**
     * 销毁南向网关
     */
    @PreDestroy
    public void destroy() {
        // 关闭所有协议处理器
        protocolHandlers.values().forEach(handler -> {
            // 在实际实现中，这里会有更优雅的关闭逻辑
        });
        
        System.out.println("Southbound Gateway Service destroyed");
    }
    
    /**
     * 获取协议处理器
     * @param protocol 协议类型
     * @return 协议处理器
     */
    public ProtocolHandler getProtocolHandler(String protocol) {
        return protocolHandlers.get(protocol);
    }
}