package com.device.gateway.proxy.service;

import com.device.gateway.common.entity.Device;
import com.device.gateway.common.message.Message;
import com.device.gateway.proxy.config.GatewayProxyConfig;
import com.device.gateway.proxy.route.ProtocolRouter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 网关代理主服务
 * 统一代理设备接入，按协议分发到支持该协议的网关
 */
@Slf4j
@Service
public class GatewayProxyService {
    
    @Autowired
    private GatewayProxyConfig config;
    
    @Autowired
    private ProtocolRouter protocolRouter;
    
    @Autowired
    private ExternalPushService externalPushService;
    
    private final AtomicLong connectionCounter = new AtomicLong(0);
    private final AtomicLong messageCounter = new AtomicLong(0);
    private volatile boolean isRunning = false;
    
    @PostConstruct
    public void init() {
        log.info("=== 网关代理服务初始化 ===");
        log.info("监听地址: {}:{}", 
            config.getSouthbound().getHost(), 
            config.getSouthbound().getPort());
        log.info("最大连接数: {}", config.getSouthbound().getMaxConnections());
        log.info("连接超时: {}ms", config.getSouthbound().getConnectionTimeout());
        log.info("协议路由配置: {}", config.getProtocolRoutes().keySet());
        
        // 初始化外部推送端点
        if (config.getNorthbound().getEndpoints() != null) {
            config.getNorthbound().getEndpoints().forEach(endpoint -> {
                log.info("外部推送端点: {} ({}) -> {}", 
                    endpoint.getName(), 
                    endpoint.getType(), 
                    endpoint.getUrl());
            });
        }
        
        isRunning = true;
        log.info("网关代理服务启动成功!");
    }
    
    /**
     * 处理设备连接请求
     * @param device 设备信息
     * @return 连接是否成功
     */
    public boolean handleDeviceConnection(Device device) {
        if (!isRunning) {
            log.warn("网关代理服务未运行，拒绝连接请求: {}", device.getDeviceId());
            return false;
        }
        
        try {
            // 协议路由
            String targetGateway = protocolRouter.routeDeviceConnection(device);
            
            // 记录连接信息
            long connectionId = connectionCounter.incrementAndGet();
            log.info("[{}] 设备连接请求: {} -> {} (协议: {})", 
                connectionId, 
                device.getDeviceId(), 
                targetGateway,
                device.getProtocol());
            
            // 在实际实现中，这里会建立到目标网关的连接
            // 并设置消息转发规则
            
            // 模拟连接成功
            device.setStatus(com.device.gateway.common.enums.ConnectionStatus.CONNECTED);
            
            log.info("[{}] 设备连接成功: {}", connectionId, device.getDeviceId());
            return true;
            
        } catch (Exception e) {
            log.error("设备连接处理失败: {}", device.getDeviceId(), e);
            return false;
        }
    }
    
    /**
     * 处理来自设备的消息
     * @param message 消息
     */
    public void handleDeviceMessage(Message message) {
        if (!isRunning) {
            log.warn("网关代理服务未运行，丢弃消息: {}", message.getMessageId());
            return;
        }
        
        try {
            long messageId = messageCounter.incrementAndGet();
            
            // 消息处理流程:
            // 1. 协议验证
            // 2. 消息解析
            // 3. 数据转换
            // 4. 路由分发
            // 5. 外部推送
            
            log.debug("[{}] 接收设备消息: {} (协议: {}, 设备: {})", 
                messageId,
                message.getMessageId(),
                message.getProtocol(),
                message.getDeviceId());
            
            // 1. 协议路由 - 确定消息处理路径
            String targetGateway = protocolRouter.routeMessage(message);
            log.debug("[{}] 协议路由: {} -> {}", 
                messageId, message.getProtocol(), targetGateway);
            
            // 2. 模拟数据处理和转换（在实际实现中这里会处理具体的协议逻辑）
            processMessageData(message);
            
            // 3. 推送到北向网关
            pushToNorthbound(message);
            
            // 4. 推送到外部系统
            externalPushService.pushMessage(message);
            
            log.debug("[{}] 消息处理完成: {}", messageId, message.getMessageId());
            
        } catch (Exception e) {
            log.error("消息处理失败: {}", message.getMessageId(), e);
        }
    }
    
    /**
     * 消息数据处理
     * @param message 消息
     */
    private void processMessageData(Message message) {
        // 在实际实现中，这里会包含具体的协议处理逻辑
        // 比如:
        // - 数据解析和验证
        // - 协议格式转换
        // - 数据质量检查
        // - 消息增强
        // - 事件识别等
        
        // 现在只是一个示例处理
        message.getAttributes().put("processed_timestamp", System.currentTimeMillis());
        message.getAttributes().put("processed_by", "gateway-proxy");
        
        log.trace("消息数据处理完成: {}", message.getMessageId());
    }
    
    /**
     * 推送到北向网关
     * @param message 消息
     */
    private void pushToNorthbound(Message message) {
        try {
            // 模拟向北向网关推送数据
            // 在实际实现中会包含序列化和传输逻辑
            log.debug("向北向网关推送消息: {}", message.getMessageId());
            
            // 这里可以添加具体的推送逻辑，比如:
            // - HTTP POST请求
            // - 消息队列发送
            // - WebSocket推送等
            
        } catch (Exception e) {
            log.error("向北向网关推送失败: {}", message.getMessageId(), e);
            throw e;
        }
    }
    
    /**
     * 定时刷新路由缓存
     */
    @Scheduled(fixedRate = 300000) // 5分钟
    public void refreshRoutes() {
        if (isRunning) {
            protocolRouter.refreshRoutes();
            log.info("路由缓存已刷新");
        }
    }
    
    /**
     * 定时批量推送消息
     */
    @Scheduled(fixedRate = 5000) // 5秒
    public void scheduledBatchPush() {
        if (isRunning && externalPushService.getBufferedMessageCount() > 0) {
            externalPushService.flushMessages();
        }
    }
    
    /**
     * 获取服务状态
     * @return 状态信息
     */
    public String getStatus() {
        return String.format("运行状态: %s, 连接数: %d, 消息数: %d, 缓冲区: %d", 
            isRunning ? "运行中" : "已停止",
            connectionCounter.get(),
            messageCounter.get(),
            externalPushService.getBufferedMessageCount());
    }
    
    /**
     * 获取统计信息
     * @return 统计信息
     */
    public String getStatistics() {
        return String.format("总连接数: %d, 总消息数: %d, 当前缓冲区: %d, 路由缓存: %d", 
            connectionCounter.get(),
            messageCounter.get(),
            externalPushService.getBufferedMessageCount(),
            protocolRouter.getCachedRoutes().size());
    }
    
    @PreDestroy
    public void destroy() {
        isRunning = false;
        log.info("网关代理服务正在关闭...");
        
        // 刷新剩余消息
        externalPushService.flushMessages();
        
        log.info("网关代理服务已关闭");
    }
}