package com.device.gateway.proxy.route;

import com.device.gateway.common.entity.Device;
import com.device.gateway.common.message.Message;
import com.device.gateway.proxy.config.GatewayProxyConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 协议路由器
 * 负责根据设备协议将连接请求路由到对应的协议网关
 */
@Slf4j
@Service
public class ProtocolRouter {
    
    @Autowired
    private GatewayProxyConfig config;
    
    // 协议到目标网关的映射缓存
    private final Map<String, String> protocolRouteCache = new ConcurrentHashMap<>();
    
    /**
     * 路由设备连接到对应的协议网关
     * @param device 设备信息
     * @return 目标网关地址
     */
    public String routeDeviceConnection(Device device) {
        String protocol = device.getProtocol().getValue().toLowerCase();
        
        // 先从缓存查找
        String targetGateway = protocolRouteCache.get(protocol);
        if (targetGateway != null) {
            log.debug("从缓存找到协议路由: {} -> {}", protocol, targetGateway);
            return targetGateway;
        }
        
        // 从配置查找
        GatewayProxyConfig.ProtocolRouteConfig routeConfig = 
            config.getProtocolRoutes().get(protocol);
        
        if (routeConfig != null) {
            targetGateway = String.format("%s:%d", 
                routeConfig.getTargetHost(), 
                routeConfig.getTargetPort());
            
            // 缓存路由信息
            protocolRouteCache.put(protocol, targetGateway);
            log.info("配置协议路由: {} -> {}", protocol, targetGateway);
            return targetGateway;
        }
        
        // 默认路由到南向网关
        targetGateway = String.format("%s:%d", 
            config.getSouthbound().getHost(),
            config.getSouthbound().getPort());
        
        log.warn("未找到协议 {} 的路由配置，使用默认路由: {}", protocol, targetGateway);
        return targetGateway;
    }
    
    /**
     * 路由消息到对应的目标网关
     * @param message 消息
     * @return 目标网关地址
     */
    public String routeMessage(Message message) {
        String protocol = message.getProtocol().getValue().toLowerCase();
        return protocolRouteCache.getOrDefault(protocol, 
            String.format("%s:%d", 
                config.getSouthbound().getHost(),
                config.getSouthbound().getPort()));
    }
    
    /**
     * 获取协议配置
     * @param protocol 协议类型
     * @return 协议配置
     */
    public GatewayProxyConfig.ProtocolRouteConfig getProtocolConfig(String protocol) {
        return config.getProtocolRoutes().get(protocol.toLowerCase());
    }
    
    /**
     * 刷新路由缓存
     */
    public void refreshRoutes() {
        protocolRouteCache.clear();
        log.info("协议路由缓存已刷新");
    }
    
    /**
     * 获取所有缓存的路由信息
     * @return 路由映射
     */
    public Map<String, String> getCachedRoutes() {
        return new ConcurrentHashMap<>(protocolRouteCache);
    }
}