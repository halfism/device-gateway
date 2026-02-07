package com.device.gateway.protocol.impl;

import com.device.gateway.common.entity.Device;
import com.device.gateway.common.message.Message;
import com.device.gateway.common.message.MessageType;
import com.device.gateway.core.config.ProtocolConfig;
import com.device.gateway.protocol.AbstractProtocolHandler;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import java.util.concurrent.CompletableFuture;

/**
 * MQTT协议处理器实现
 */
public class MqttProtocolHandler extends AbstractProtocolHandler {
    
    private MqttClient mqttClient;
    private String brokerUrl;
    
    @Override
    protected void doInitialize(ProtocolConfig config) {
        this.brokerUrl = String.format("tcp://%s:%d", config.getHost(), config.getPort());
        try {
            String clientId = "gateway_" + System.currentTimeMillis();
            this.mqttClient = new MqttClient(brokerUrl, clientId, new MemoryPersistence());
            
            MqttConnectOptions options = new MqttConnectOptions();
            options.setCleanSession(true);
            options.setConnectionTimeout(config.getConnectionTimeout() != null ? config.getConnectionTimeout() : 30);
            options.setKeepAliveInterval(60);
            
            if (config.getProperties() != null) {
                String username = (String) config.getProperties().get("username");
                String password = (String) config.getProperties().get("password");
                
                if (username != null && password != null) {
                    options.setUserName(username);
                    options.setPassword(password.toCharArray());
                }
            }
            
            mqttClient.connect(options);
            
        } catch (MqttException e) {
            throw new RuntimeException("Failed to initialize MQTT client", e);
        }
    }
    
    @Override
    public CompletableFuture<Message> send(Message message) {
        CompletableFuture<Message> future = new CompletableFuture<>();
        
        try {
            MqttMessage mqttMessage = new MqttMessage(message.getPayload());
            mqttMessage.setQos(message.getQos() != null ? message.getQos() : 1);
            mqttMessage.setRetained(message.getRetained() != null && message.getRetained());
            
            mqttClient.publish(message.getTopic(), mqttMessage);
            
            // 设置消息类型为响应
            message.setMessageType(MessageType.RESPONSE);
            future.complete(message);
            
        } catch (MqttException e) {
            future.completeExceptionally(e);
        }
        
        return future;
    }
    
    @Override
    public CompletableFuture<Boolean> connect(Device device) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        
        try {
            // 订阅设备相关的主题
            String topic = "device/" + device.getDeviceId() + "/#";
            mqttClient.subscribe(topic, (topicReceived, mqttMessage) -> {
                // 处理接收到的消息
                Message message = Message.builder()
                    .deviceId(device.getDeviceId())
                    .topic(topicReceived)
                    .payload(mqttMessage.getPayload())
                    .messageType(MessageType.DATA)
                    .build();
                
                // 这里可以进一步处理消息，比如路由到南向网关
                handleMessage(message);
            });
            
            future.complete(true);
        } catch (MqttException e) {
            future.completeExceptionally(e);
        }
        
        return future;
    }
    
    @Override
    protected void doDisconnect(Device device) {
        try {
            // 取消订阅设备相关的主题
            String topic = "device/" + device.getDeviceId() + "/#";
            mqttClient.unsubscribe(topic);
        } catch (MqttException e) {
            // 记录日志，但不抛出异常
            e.printStackTrace();
        }
    }
    
    @Override
    public String getProtocolType() {
        return "mqtt";
    }
    
    /**
     * 处理接收到的消息
     * @param message 消息
     */
    private void handleMessage(Message message) {
        // 在实际实现中，这里会将消息路由到相应的处理器
        System.out.println("Received MQTT message from device: " + message.getDeviceId());
    }
}