package com.device.gateway.infrastructure.config;

import com.device.gateway.infrastructure.session.ReactiveDeviceSessionManager;
import com.device.gateway.infrastructure.event.ReactiveEventBus;
import com.device.gateway.infrastructure.rule.RuleEngine;
import com.device.gateway.infrastructure.rule.impl.DefaultRuleEngine;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 基础设施配置类
 * 配置响应式组件和核心基础设施服务
 */
@Configuration
public class InfrastructureConfig {

    @Bean
    public ReactiveDeviceSessionManager reactiveDeviceSessionManager() {
        return new ReactiveDeviceSessionManager();
    }

    @Bean
    public ReactiveEventBus reactiveEventBus() {
        return new ReactiveEventBus();
    }

    @Bean
    public RuleEngine ruleEngine() {
        return new DefaultRuleEngine();
    }
}