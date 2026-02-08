package com.device.gateway.southbound.service;

import com.device.gateway.common.entity.Device;
import com.device.gateway.common.message.Message;
import com.device.gateway.core.handler.ProtocolHandler;
import com.device.gateway.core.manager.DeviceManager;
import com.device.gateway.infrastructure.event.ReactiveEventBus;
import com.device.gateway.infrastructure.rule.RuleEngine;
import com.device.gateway.protocol.impl.MqttProtocolHandler;
import com.device.gateway.protocol.impl.TcpProtocolHandler;
import com.device.gateway.protocol.impl.HttpProtocolHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CompletableFuture;

/**
 * 响应式南向网关服务
 * 基于JetLinks和IoT-DC3的设计理念，提供响应式设备连接和消息处理能力
 */
@Service
public class ReactiveSouthboundGatewayService {
    
    @Autowired
    private DeviceManager deviceManager;
    
    @Autowired(required = false)
    private ReactiveEventBus eventBus;
    
    @Autowired(required = false)
    private RuleEngine ruleEngine;
    
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
        
        System.out.println("Reactive Southbound Gateway Service initialized with protocols: " + protocolHandlers.keySet());
    }
    
    /**
     * 响应式连接设备
     * @param device 设备信息
     * @return 连接结果
     */
    public Mono<Boolean> connectDeviceReactive(Device device) {
        return Mono.fromCallable(() -> {
            ProtocolHandler handler = protocolHandlers.get(device.getProtocol().getValue());
            if (handler == null) {
                throw new IllegalArgumentException("Unsupported protocol: " + device.getProtocol());
            }
            
            // 注册设备到处理器
            handler.registerDevice(device);
            
            // 尝试连接设备
            CompletableFuture<Boolean> future = handler.connect(device);
            return future.join(); // 等待异步操作完成
        }).flatMap(result -> {
            // 如果连接成功，发布事件
            if (Boolean.TRUE.equals(result) && eventBus != null) {
                Message eventMessage = Message.builder()
                    .messageId("connect_event_" + device.getDeviceId())
                    .deviceId(device.getDeviceId())
                    .topic("device/connect")
                    .payload(("Connected to device: " + device.getDeviceId()).getBytes())
                    .build();
                
                eventBus.publish(eventMessage);
            }
            return Mono.just(result);
        });
    }
    
    /**
     * 响应式断开设备连接
     * @param deviceId 设备ID
     */
    public Mono<Void> disconnectDeviceReactive(String deviceId) {
        return Mono.fromRunnable(() -> {
            Device device = deviceManager.getDeviceById(deviceId);
            if (device != null) {
                ProtocolHandler handler = protocolHandlers.get(device.getProtocol().getValue());
                if (handler != null) {
                    handler.disconnect(deviceId);
                }
                deviceManager.unregisterDevice(deviceId);
            }
        });
    }
    
    /**
     * 响应式发送消息到设备
     * @param deviceId 设备ID
     * @param message 消息
     * @return 发送结果
     */
    public Mono<Message> sendMessageToDeviceReactive(String deviceId, Message message) {
        return Mono.fromCallable(() -> {
            Device device = deviceManager.getDeviceById(deviceId);
            if (device == null) {
                throw new IllegalArgumentException("Device not found: " + deviceId);
            }
            
            ProtocolHandler handler = protocolHandlers.get(device.getProtocol().getValue());
            if (handler == null) {
                throw new IllegalArgumentException("No handler for protocol: " + device.getProtocol());
            }
            
            message.setDeviceId(deviceId);
            CompletableFuture<Message> future = handler.send(message);
            return future.join(); // 等待异步操作完成
        });
    }
    
    /**
     * 响应式接收来自设备的消息
     * @param message 消息
     */
    public Mono<Void> receiveMessageFromDeviceReactive(Message message) {
        return Mono.fromRunnable(() -> {
            System.out.println("Reactive Southbound gateway received message from device: " + 
                              message.getDeviceId() + ", topic: " + message.getTopic());
            
            // 如果有规则引擎，则处理消息
            if (ruleEngine != null) {
                ruleEngine.process(message);
            }
            
            // 如果有事件总线，则发布消息
            if (eventBus != null) {
                eventBus.publish(message);
            }
            
            // 这里可以触发消息路由到平台
            routeMessageToNorthbound(message);
        });
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
     * 响应式获取设备列表
     * @return 设备流
     */
    public Flux<Device> getAllDevicesReactive() {
        return Flux.fromIterable(deviceManager.getAllDevices());
    }
    
    /**
     * 响应式根据ID获取设备
     * @param deviceId 设备ID
     * @return 设备对象
     */
    public Mono<Device> getDeviceByIdReactive(String deviceId) {
        return Mono.fromCallable(() -> deviceManager.getDeviceById(deviceId))
                   .filter(device -> device != null);
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
        
        System.out.println("Reactive Southbound Gateway Service destroyed");
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