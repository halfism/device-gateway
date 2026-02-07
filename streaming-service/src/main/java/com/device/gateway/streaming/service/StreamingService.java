package com.device.gateway.streaming.service;

import com.device.gateway.common.message.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

/**
 * 流媒体转发服务
 * 负责实时数据流处理、数据压缩与加密、负载均衡与高可用、流量控制与限速
 */
@Service
public class StreamingService {
    
    @Autowired(required = false)
    private KafkaTemplate<String, Object> kafkaTemplate;
    
    // 使用反应式编程处理数据流
    private final Sinks.Many<Message> messageSink = Sinks.many().multicast().directBestEffort();
    private final Flux<Message> messageStream = messageSink.asFlux();
    
    // 用于跟踪流状态
    private final AtomicBoolean isActive = new AtomicBoolean(false);
    
    // 数据压缩器
    private final Deflater deflater = new Deflater();
    private final Inflater inflater = new Inflater();
    
    // 速率限制映射
    private final ConcurrentHashMap<String, Long> rateLimitMap = new ConcurrentHashMap<>();
    
    /**
     * 初始化流媒体服务
     */
    @PostConstruct
    public void init() {
        isActive.set(true);
        System.out.println("Streaming Service initialized");
        
        // 启动数据流处理
        startMessageProcessing();
    }
    
    /**
     * 启动消息处理流程
     */
    private void startMessageProcessing() {
        // 订阅消息流并处理
        messageStream
            .filter(this::checkRateLimit) // 应用速率限制
            .map(this::compressMessage) // 压缩消息
            .doOnNext(this::processMessage) // 处理消息
            .subscribe(
                message -> {
                    // 成功处理消息
                    System.out.println("Processed message: " + message.getMessageId());
                },
                error -> {
                    // 处理错误
                    System.err.println("Error processing message: " + error.getMessage());
                }
            );
    }
    
    /**
     * 发布消息到流
     * @param message 消息
     * @return 是否发布成功
     */
    public boolean publishMessage(Message message) {
        if (!isActive.get()) {
            return false;
        }
        
        // 发布消息到流
        return messageSink.tryEmitNext(message).isSuccess();
    }
    
    /**
     * 订阅消息流
     * @return 消息流
     */
    public Flux<Message> subscribeToStream() {
        return messageStream;
    }
    
    /**
     * 检查速率限制
     * @param message 消息
     * @return 是否允许通过
     */
    private boolean checkRateLimit(Message message) {
        String deviceId = message.getDeviceId();
        if (deviceId == null) {
            return true; // 如果没有设备ID，则不限制
        }
        
        long currentTime = System.currentTimeMillis();
        Long lastTime = rateLimitMap.get(deviceId);
        
        // 简单的速率限制：每个设备每秒最多10条消息
        if (lastTime != null && (currentTime - lastTime) < 100) {
            System.out.println("Rate limit exceeded for device: " + deviceId);
            return false;
        }
        
        rateLimitMap.put(deviceId, currentTime);
        return true;
    }
    
    /**
     * 压缩消息
     * @param message 消息
     * @return 压缩后的消息
     */
    private Message compressMessage(Message message) {
        if (message.getPayload() == null || message.getPayload().length < 1024) {
            // 小于1KB的消息不需要压缩
            return message;
        }
        
        try {
            byte[] data = message.getPayload();
            deflater.setInput(data);
            deflater.finish();
            
            byte[] compressedData = new byte[data.length];
            int compressedLength = deflater.deflate(compressedData);
            deflater.reset();
            
            // 创建压缩后的消息副本
            Message compressedMessage = new Message();
            compressedMessage.setMessageId(message.getMessageId());
            compressedMessage.setDeviceId(message.getDeviceId());
            compressedMessage.setTopic(message.getTopic());
            compressedMessage.setPayload(java.util.Arrays.copyOf(compressedData, compressedLength));
            compressedMessage.setProtocol(message.getProtocol());
            compressedMessage.setMessageType(message.getMessageType());
            compressedMessage.setAttributes(message.getAttributes());
            compressedMessage.setTimestamp(message.getTimestamp());
            compressedMessage.setQos(message.getQos());
            compressedMessage.setRetained(message.getRetained());
            
            System.out.println("Compressed message from " + data.length + " to " + compressedLength + " bytes");
            
            return compressedMessage;
        } catch (Exception e) {
            System.err.println("Failed to compress message: " + e.getMessage());
            return message; // 返回原始消息
        }
    }
    
    /**
     * 解压消息
     * @param message 消息
     * @return 解压后的消息
     */
    private Message decompressMessage(Message message) {
        if (message.getAttributes() == null || !Boolean.TRUE.equals(message.getAttributes().get("compressed"))) {
            // 如果没有压缩标识，则不解压
            return message;
        }
        
        try {
            byte[] compressedData = message.getPayload();
            inflater.setInput(compressedData);
            
            byte[] decompressedData = new byte[compressedData.length * 10]; // 预估解压后大小
            int decompressedLength = inflater.inflate(decompressedData);
            inflater.reset();
            
            // 创建解压后的消息副本
            Message decompressedMessage = new Message();
            decompressedMessage.setMessageId(message.getMessageId());
            decompressedMessage.setDeviceId(message.getDeviceId());
            decompressedMessage.setTopic(message.getTopic());
            decompressedMessage.setPayload(java.util.Arrays.copyOf(decompressedData, decompressedLength));
            decompressedMessage.setProtocol(message.getProtocol());
            decompressedMessage.setMessageType(message.getMessageType());
            decompressedMessage.setAttributes(message.getAttributes());
            decompressedMessage.setTimestamp(message.getTimestamp());
            decompressedMessage.setQos(message.getQos());
            decompressedMessage.setRetained(message.getRetained());
            
            // 移除压缩标识
            if (decompressedMessage.getAttributes() != null) {
                decompressedMessage.getAttributes().remove("compressed");
            }
            
            System.out.println("Decompressed message from " + compressedData.length + " to " + decompressedLength + " bytes");
            
            return decompressedMessage;
        } catch (Exception e) {
            System.err.println("Failed to decompress message: " + e.getMessage());
            return message; // 返回原始消息
        }
    }
    
    /**
     * 处理消息
     * @param message 消息
     */
    private void processMessage(Message message) {
        System.out.println("Processing message: " + message.getMessageId() + 
                          " from device: " + message.getDeviceId());
        
        // 在实际实现中，这里可能会：
        // 1. 将消息发送到Kafka
        if (kafkaTemplate != null) {
            try {
                // 发送到Kafka主题，按设备ID分区
                kafkaTemplate.send("iot-messages", message.getDeviceId(), message);
                System.out.println("Sent message to Kafka topic: iot-messages");
            } catch (Exception e) {
                System.err.println("Failed to send message to Kafka: " + e.getMessage());
            }
        }
        
        // 2. 进行数据预处理
        preprocessData(message);
        
        // 3. 触发其他处理流程
        triggerDownstreamProcessing(message);
    }
    
    /**
     * 预处理数据
     * @param message 消息
     */
    private void preprocessData(Message message) {
        // 在这里可以进行数据清洗、格式转换等操作
        System.out.println("Preprocessing data for message: " + message.getMessageId());
    }
    
    /**
     * 触发下游处理
     * @param message 消息
     */
    private void triggerDownstreamProcessing(Message message) {
        // 在这里可以触发存储、分析或其他处理流程
        System.out.println("Triggering downstream processing for message: " + message.getMessageId());
    }
    
    /**
     * 获取流状态
     * @return 是否活跃
     */
    public boolean isActive() {
        return isActive.get();
    }
    
    /**
     * 获取当前流统计
     * @return 统计信息
     */
    public String getStatistics() {
        return String.format("Streaming Service Stats: Active=%b, Subscribers=%d", 
                           isActive.get(), messageStream != null ? 1 : 0);
    }
    
    /**
     * 销毁流媒体服务
     */
    @PreDestroy
    public void destroy() {
        isActive.set(false);
        messageSink.tryEmitComplete(); // 完成消息流
        deflater.end();
        inflater.end();
        System.out.println("Streaming Service destroyed");
    }
    
    /**
     * 模拟数据流聚合
     * @param deviceId 设备ID
     * @param windowDuration 时间窗口
     * @return 聚合后的数据流
     */
    public Flux<Message> aggregateByDevice(String deviceId, Duration windowDuration) {
        return messageStream
            .filter(msg -> deviceId.equals(msg.getDeviceId()))
            .window(windowDuration)
            .flatMap(window -> window.reduce(
                new Message(), 
                (acc, msg) -> aggregateMessages(acc, msg)
            ));
    }
    
    /**
     * 聚合消息
     * @param acc 累积消息
     * @param msg 当前消息
     * @return 聚合后的消息
     */
    private Message aggregateMessages(Message acc, Message msg) {
        if (acc.getMessageId() == null) {
            // 第一条消息
            return msg;
        }
        
        // 简单聚合：合并负载
        byte[] combinedPayload = new byte[acc.getPayload().length + msg.getPayload().length];
        System.arraycopy(acc.getPayload(), 0, combinedPayload, 0, acc.getPayload().length);
        System.arraycopy(msg.getPayload(), 0, combinedPayload, acc.getPayload().length, msg.getPayload().length);
        
        acc.setPayload(combinedPayload);
        return acc;
    }
}