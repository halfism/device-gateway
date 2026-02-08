package com.device.gateway.infrastructure.rule;

import com.device.gateway.common.message.Message;
import reactor.core.publisher.Mono;

/**
 * 规则接口
 */
public interface Rule {
    String getId();
    String getName();
    boolean evaluate(Message message);
    Mono<Message> execute(Message message);
    boolean isEnabled();
}