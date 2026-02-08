package com.device.gateway.core.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 网关核心配置
 * 集成基础设施组件和核心功能
 */
@Configuration
@ComponentScan(basePackages = {
    "com.device.gateway.core",
    "com.device.gateway.infrastructure"
})
public class GatewayCoreConfig {
    // 核心配置类，启用组件扫描
}