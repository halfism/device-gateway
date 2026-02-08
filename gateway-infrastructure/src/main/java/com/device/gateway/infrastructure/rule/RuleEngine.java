package com.device.gateway.infrastructure.rule;

import com.device.gateway.common.message.Message;
import reactor.core.publisher.Mono;

/**
 * 规则引擎接口
 * 基于JetLinks的规则引擎设计理念，支持数据处理、告警、转发等功能
 */
public interface RuleEngine {
    
    /**
     * 处理传入的消息
     */
    Mono<Message> process(Message message);
    
    /**
     * 注册规则
     */
    void registerRule(Rule rule);
    
    /**
     * 移除规则
     */
    void removeRule(String ruleId);
    
    /**
     * 启动规则引擎
     */
    void start();
    
    /**
     * 停止规则引擎
     */
    void stop();
}