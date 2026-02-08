package com.device.gateway.infrastructure.session;

import com.device.gateway.common.entity.Device;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

/**
 * 响应式设备会话管理器
 * 基于JetLinks的设计理念，提供设备会话的统一管理
 */
public class ReactiveDeviceSessionManager {
    
    private final Map<String, DeviceSession> sessions = new ConcurrentHashMap<>();
    
    /**
     * 创建设备会话
     */
    public Mono<DeviceSession> createSession(Device device) {
        DeviceSession session = new DeviceSession(device);
        sessions.put(device.getDeviceId(), session);
        return Mono.just(session);
    }
    
    /**
     * 获取设备会话
     */
    public Mono<DeviceSession> getSession(String deviceId) {
        DeviceSession session = sessions.get(deviceId);
        return session != null ? Mono.just(session) : Mono.empty();
    }
    
    /**
     * 删除设备会话
     */
    public Mono<Void> removeSession(String deviceId) {
        sessions.remove(deviceId);
        return Mono.empty();
    }
    
    /**
     * 获取所有活动会话
     */
    public Flux<DeviceSession> getAllSessions() {
        return Flux.fromIterable(sessions.values());
    }
    
    /**
     * 关闭指定设备会话
     */
    public Mono<Void> closeSession(String deviceId) {
        DeviceSession session = sessions.get(deviceId);
        if (session != null) {
            session.close();
        }
        return Mono.empty();
    }
    
    /**
     * 设备会话内部类
     */
    public static class DeviceSession {
        private final Device device;
        private volatile boolean active = true;
        
        public DeviceSession(Device device) {
            this.device = device;
        }
        
        public Device getDevice() {
            return device;
        }
        
        public boolean isActive() {
            return active;
        }
        
        public void close() {
            this.active = false;
        }
    }
}