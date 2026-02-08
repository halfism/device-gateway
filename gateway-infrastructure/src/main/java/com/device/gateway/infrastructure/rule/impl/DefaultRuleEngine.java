package com.device.gateway.infrastructure.rule.impl;

import com.device.gateway.common.message.Message;
import com.device.gateway.infrastructure.rule.Rule;
import com.device.gateway.infrastructure.rule.RuleEngine;
import reactor.core.publisher.Mono;

/**
 * 默认规则引擎实现
 */
public class DefaultRuleEngine implements RuleEngine {
    
    @Override
    public Mono<Message> process(Message message) {
        // 默认实现：直接返回消息
        return Mono.just(message);
    }
    
    @Override
    public void registerRule(Rule rule) {
        // 默认实现：空操作
    }
    
    @Override
    public void removeRule(String ruleId) {
        // 默认实现：空操作
    }
    
    @Override
    public void start() {
        // 默认实现：空操作
    }
    
    @Override
    public void stop() {
        // 默认实现：空操作
    }
}