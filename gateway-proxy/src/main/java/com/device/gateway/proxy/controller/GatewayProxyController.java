package com.device.gateway.proxy.controller;

import com.device.gateway.common.entity.Device;
import com.device.gateway.common.message.Message;
import com.device.gateway.proxy.service.GatewayProxyService;
import com.device.gateway.proxy.service.ExternalPushService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 网关代理控制器
 * 提供代理服务的REST API接口
 */
@Slf4j
@RestController
@RequestMapping("/proxy")
public class GatewayProxyController {
    
    @Autowired
    private GatewayProxyService gatewayProxyService;
    
    @Autowired
    private ExternalPushService externalPushService;
    
    /**
     * 设备连接接口
     * @param device 设备信息
     * @return 连接结果
     */
    @PostMapping("/connect")
    public ResponseEntity<ConnectResponse> connectDevice(@RequestBody Device device) {
        try {
            boolean success = gatewayProxyService.handleDeviceConnection(device);
            
            ConnectResponse response = new ConnectResponse();
            response.setSuccess(success);
            response.setDeviceId(device.getDeviceId());
            response.setMessage(success ? "设备连接成功" : "设备连接失败");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("设备连接接口异常: {}", e.getMessage(), e);
            ConnectResponse response = new ConnectResponse();
            response.setSuccess(false);
            response.setMessage("连接处理异常: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 接收设备消息接口
     * @param message 消息
     * @return 处理结果
     */
    @PostMapping("/message")
    public ResponseEntity<MessageResponse> receiveMessage(@RequestBody Message message) {
        try {
            gatewayProxyService.handleDeviceMessage(message);
            
            MessageResponse response = new MessageResponse();
            response.setSuccess(true);
            response.setMessageId(message.getMessageId());
            response.setMessage("消息处理成功");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("消息处理接口异常: {}", e.getMessage(), e);
            MessageResponse response = new MessageResponse();
            response.setSuccess(false);
            response.setMessage("消息处理异常: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 获取代理服务状态
     * @return 服务状态
     */
    @GetMapping("/status")
    public ResponseEntity<StatusResponse> getStatus() {
        try {
            StatusResponse response = new StatusResponse();
            response.setStatus(gatewayProxyService.getStatus());
            response.setStatistics(gatewayProxyService.getStatistics());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("获取状态接口异常: {}", e.getMessage(), e);
            StatusResponse response = new StatusResponse();
            response.setError("获取状态失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 刷新路由缓存
     * @return 刷新结果
     */
    @PostMapping("/refresh-routes")
    public ResponseEntity<SimpleResponse> refreshRoutes() {
        try {
            // 在实际实现中调用路由刷新方法
            SimpleResponse response = new SimpleResponse();
            response.setSuccess(true);
            response.setMessage("路由缓存刷新成功");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("刷新路由接口异常: {}", e.getMessage(), e);
            SimpleResponse response = new SimpleResponse();
            response.setSuccess(false);
            response.setMessage("路由刷新失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 手动触发批量推送
     * @return 推送结果
     */
    @PostMapping("/flush-messages")
    public ResponseEntity<FlushResponse> flushMessages() {
        try {
            int bufferedCount = externalPushService.getBufferedMessageCount();
            externalPushService.flushMessages();
            
            FlushResponse response = new FlushResponse();
            response.setSuccess(true);
            response.setFlushedCount(bufferedCount);
            response.setMessage("批量推送完成");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("批量推送接口异常: {}", e.getMessage(), e);
            FlushResponse response = new FlushResponse();
            response.setSuccess(false);
            response.setMessage("批量推送失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 清空消息缓冲区
     * @return 清空结果
     */
    @DeleteMapping("/clear-buffer")
    public ResponseEntity<SimpleResponse> clearBuffer() {
        try {
            externalPushService.clearBuffer();
            
            SimpleResponse response = new SimpleResponse();
            response.setSuccess(true);
            response.setMessage("缓冲区清空成功");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("清空缓冲区接口异常: {}", e.getMessage(), e);
            SimpleResponse response = new SimpleResponse();
            response.setSuccess(false);
            response.setMessage("缓冲区清空失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    // 响应数据类
    @Data
    public static class ConnectResponse {
        private boolean success;
        private String deviceId;
        private String message;
    }
    
    @Data
    public static class MessageResponse {
        private boolean success;
        private String messageId;
        private String message;
    }
    
    @Data
    public static class StatusResponse {
        private String status;
        private String statistics;
        private String error;
    }
    
    @Data
    public static class FlushResponse {
        private boolean success;
        private int flushedCount;
        private String message;
    }
    
    @Data
    public static class SimpleResponse {
        private boolean success;
        private String message;
    }
}