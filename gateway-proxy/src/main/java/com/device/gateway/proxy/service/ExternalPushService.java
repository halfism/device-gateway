package com.device.gateway.proxy.service;

import com.device.gateway.common.message.Message;
import com.device.gateway.proxy.config.GatewayProxyConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 外部推送服务
 * 支持向HTTP、AMQP、Kafka等外部系统推送数据
 */
@Slf4j
@Service
public class ExternalPushService {
    
    @Autowired
    private GatewayProxyConfig config;
    
    @Autowired(required = false)
    private KafkaTemplate<String, Object> kafkaTemplate;
    
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final List<Message> messageBuffer = new CopyOnWriteArrayList<>();
    
    /**
     * 推送消息到外部系统
     * @param message 消息
     */
    public void pushMessage(Message message) {
        // 添加到缓冲区
        messageBuffer.add(message);
        
        // 检查是否需要批量推送
        if (messageBuffer.size() >= config.getNorthbound().getBatchSize()) {
            flushMessages();
        }
    }
    
    /**
     * 批量推送消息
     */
    public void flushMessages() {
        if (messageBuffer.isEmpty()) {
            return;
        }
        
        List<Message> messagesToPush = new CopyOnWriteArrayList<>(messageBuffer);
        messageBuffer.clear();
        
        // 并行推送到所有配置的外部端点
        config.getNorthbound().getEndpoints().parallelStream()
            .filter(GatewayProxyConfig.ExternalEndpoint::isEnabled)
            .forEach(endpoint -> pushToEndpoint(endpoint, messagesToPush));
    }
    
    /**
     * 推送到指定端点
     * @param endpoint 端点配置
     * @param messages 消息列表
     */
    private void pushToEndpoint(GatewayProxyConfig.ExternalEndpoint endpoint, List<Message> messages) {
        try {
            switch (endpoint.getType().toUpperCase()) {
                case "HTTP":
                    pushToHttp(endpoint, messages);
                    break;
                case "AMQP":
                    pushToAmqp(endpoint, messages);
                    break;
                case "KAFKA":
                    pushToKafka(endpoint, messages);
                    break;
                default:
                    log.warn("不支持的推送类型: {}", endpoint.getType());
            }
        } catch (Exception e) {
            log.error("推送消息到端点 {} 失败: {}", endpoint.getName(), e.getMessage(), e);
        }
    }
    
    /**
     * HTTP推送
     * @param endpoint 端点配置
     * @param messages 消息列表
     */
    private void pushToHttp(GatewayProxyConfig.ExternalEndpoint endpoint, List<Message> messages) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            // 添加自定义头部
            if (endpoint.getHeaders() != null) {
                endpoint.getHeaders().forEach((key, value) -> 
                    headers.add(key, value != null ? value.toString() : ""));
            }
            
            String jsonData = objectMapper.writeValueAsString(messages);
            HttpEntity<String> request = new HttpEntity<>(jsonData, headers);
            
            ResponseEntity<String> response = restTemplate.postForEntity(
                endpoint.getUrl(), request, String.class);
            
            if (response.getStatusCode().is2xxSuccessful()) {
                log.debug("HTTP推送成功: {} -> {}", endpoint.getName(), response.getStatusCode());
            } else {
                log.warn("HTTP推送部分成功: {} -> {}", endpoint.getName(), response.getStatusCode());
            }
        } catch (Exception e) {
            log.error("HTTP推送失败: {} -> {}", endpoint.getName(), e.getMessage());
            throw new RuntimeException("HTTP推送失败", e);
        }
    }
    
    /**
     * AMQP推送
     * @param endpoint 端点配置
     * @param messages 消息列表
     */
    private void pushToAmqp(GatewayProxyConfig.ExternalEndpoint endpoint, List<Message> messages) {
        try {
            // 这里需要具体的AMQP实现，暂时记录日志
            log.info("AMQP推送: {} -> {} (exchange: {}, routingKey: {})",
                endpoint.getName(), 
                endpoint.getUrl(),
                endpoint.getExchange(),
                endpoint.getRoutingKey());
            
            // 实际实现中需要使用RabbitTemplate
            // rabbitTemplate.convertAndSend(endpoint.getExchange(), endpoint.getRoutingKey(), messages);
            
        } catch (Exception e) {
            log.error("AMQP推送失败: {} -> {}", endpoint.getName(), e.getMessage());
            throw new RuntimeException("AMQP推送失败", e);
        }
    }
    
    /**
     * Kafka推送
     * @param endpoint 端点配置
     * @param messages 消息列表
     */
    private void pushToKafka(GatewayProxyConfig.ExternalEndpoint endpoint, List<Message> messages) {
        if (kafkaTemplate == null) {
            log.warn("Kafka未配置，跳过Kafka推送: {}", endpoint.getName());
            return;
        }
        
        try {
            String topic = endpoint.getTopic();
            if (topic == null || topic.isEmpty()) {
                log.warn("Kafka端点 {} 缺少topic配置", endpoint.getName());
                return;
            }
            
            for (Message message : messages) {
                CompletableFuture<SendResult<String, Object>> future = 
                    kafkaTemplate.send(topic, message.getMessageId(), message)
                    .thenApply(result -> (SendResult<String, Object>) result);
                
                future.whenComplete((result, throwable) -> {
                    if (throwable != null) {
                        log.error("Kafka消息发送失败: {}", throwable.getMessage());
                    } else {
                        log.debug("Kafka消息发送成功: partition={}, offset={}",
                            result.getRecordMetadata().partition(),
                            result.getRecordMetadata().offset());
                    }
                });
            }
            
            log.debug("Kafka推送完成: {} -> {} (topic: {})", 
                endpoint.getName(), endpoint.getUrl(), topic);
                
        } catch (Exception e) {
            log.error("Kafka推送失败: {} -> {}", endpoint.getName(), e.getMessage());
            throw new RuntimeException("Kafka推送失败", e);
        }
    }
    
    /**
     * 获取缓冲区大小
     * @return 缓冲区消息数量
     */
    public int getBufferedMessageCount() {
        return messageBuffer.size();
    }
    
    /**
     * 清空缓冲区
     */
    public void clearBuffer() {
        messageBuffer.clear();
        log.info("消息缓冲区已清空");
    }
}