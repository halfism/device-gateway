package com.device.gateway.proxy.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 网关代理配置类
 */
@Data
@Component
@ConfigurationProperties(prefix = "gateway.proxy")
public class GatewayProxyConfig {
    
    /**
     * 南向网关配置
     */
    private SouthboundConfig southbound = new SouthboundConfig();
    
    /**
     * 北向网关配置
     */
    private NorthboundConfig northbound = new NorthboundConfig();
    
    /**
     * 协议路由配置
     */
    private Map<String, ProtocolRouteConfig> protocolRoutes;
    
    @Data
    public static class SouthboundConfig {
        private int port = 8081;
        private String host = "0.0.0.0";
        private int maxConnections = 1000;
        private long connectionTimeout = 30000;
    }
    
    @Data
    public static class NorthboundConfig {
        private List<ExternalEndpoint> endpoints;
        private int batchSize = 100;
        private long batchTimeout = 5000;
    }
    
    @Data
    public static class ProtocolRouteConfig {
        private String targetGateway;
        private String targetHost;
        private int targetPort;
        private Map<String, Object> protocolSpecificConfig;
    }
    
    @Data
    public static class ExternalEndpoint {
        private String name;
        private String type; // HTTP, AMQP, KAFKA
        private String url;
        private String topic; // for Kafka
        private String exchange; // for AMQP
        private String routingKey; // for AMQP
        private Map<String, Object> headers;
        private boolean enabled = true;
    }
}