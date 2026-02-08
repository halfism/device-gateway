package com.device.gateway.infrastructure.event;

import com.device.gateway.common.message.Message;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

/**
 * 响应式事件总线
 * 基于Reactor的Sinks实现，支持发布-订阅模式
 */
public class ReactiveEventBus {
    
    private final Sinks.Many<Message> messageSink = Sinks.many().multicast().onBackpressureBuffer();
    private final Flux<Message> messageStream = messageSink.asFlux();
    
    private final Map<String, Sinks.Many<Message>> topicSinks = new ConcurrentHashMap<>();
    
    /**
     * 发布全局消息
     */
    public void publish(Message message) {
        messageSink.tryEmitNext(message);
    }
    
    /**
     * 发布到特定主题
     */
    public void publishToTopic(String topic, Message message) {
        Sinks.Many<Message> topicSink = topicSinks.computeIfAbsent(topic, 
            t -> Sinks.many().multicast().onBackpressureBuffer());
        topicSink.tryEmitNext(message);
    }
    
    /**
     * 订阅所有消息
     */
    public Flux<Message> subscribeAll() {
        return messageStream;
    }
    
    /**
     * 订阅特定主题
     */
    public Flux<Message> subscribeToTopic(String topic) {
        return topicSinks.computeIfAbsent(topic, 
            t -> Sinks.many().multicast().onBackpressureBuffer()).asFlux();
    }
    
    /**
     * 订阅多个主题
     */
    public Flux<Message> subscribeToTopics(String... topics) {
        Flux<Message> mergedFlux = Flux.empty();
        for (String topic : topics) {
            mergedFlux = mergedFlux.mergeWith(subscribeToTopic(topic));
        }
        return mergedFlux;
    }
    
    /**
     * 关闭事件总线
     */
    public void close() {
        messageSink.tryEmitComplete();
        topicSinks.values().forEach(sink -> sink.tryEmitComplete());
    }
}