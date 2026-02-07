package com.device.gateway.core.config;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.Map;

/**
 * 协议配置类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProtocolConfig {
    private String protocolType;
    private String host;
    private Integer port;
    private Integer maxConnections;
    private Integer connectionTimeout;
    private Integer readTimeout;
    private Integer writeTimeout;
    private Map<String, Object> properties;
    private Boolean sslEnabled;
    private String sslKeystorePath;
    private String sslKeystorePassword;
    private String sslTruststorePath;
    private String sslTruststorePassword;
}