package com.device.gateway.protocol.impl;

import com.device.gateway.common.entity.Device;
import com.device.gateway.common.message.Message;
import com.device.gateway.core.config.ProtocolConfig;
import com.device.gateway.protocol.AbstractProtocolHandler;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import java.util.concurrent.CompletableFuture;

/**
 * HTTP协议处理器实现
 */
public class HttpProtocolHandler extends AbstractProtocolHandler {
    
    private RestTemplate restTemplate;
    private ProtocolConfig config;
    
    @Override
    protected void doInitialize(ProtocolConfig config) {
        this.config = config;
        this.restTemplate = new RestTemplate();
    }
    
    @Override
    public CompletableFuture<Message> send(Message message) {
        CompletableFuture<Message> future = new CompletableFuture<>();
        
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Type", "application/json");
            headers.add("Device-Id", message.getDeviceId());
            
            HttpEntity<String> entity = new HttpEntity<>(message.getPayloadAsString(), headers);
            
            String url = String.format("http://%s:%d%s", 
                    config.getHost(), 
                    config.getPort(), 
                    message.getTopic() != null ? message.getTopic() : "/api/data");
            
            ResponseEntity<String> response = restTemplate.exchange(
                    url, HttpMethod.POST, entity, String.class);
            
            // 构建响应消息
            Message responseMessage = Message.builder()
                    .messageId(message.getMessageId())
                    .deviceId(message.getDeviceId())
                    .payload(response.getBody().getBytes())
                    .build();
            
            future.complete(responseMessage);
            
        } catch (Exception e) {
            future.completeExceptionally(e);
        }
        
        return future;
    }
    
    @Override
    public CompletableFuture<Boolean> connect(Device device) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        
        // 对于HTTP协议，主要是设置设备的连接信息
        device.setHost(config.getHost());
        device.setPort(config.getPort());
        
        future.complete(true);
        return future;
    }
    
    @Override
    protected void doDisconnect(Device device) {
        // HTTP是无状态协议，无需特别的断连操作
    }
    
    @Override
    public String getProtocolType() {
        return "http";
    }
}