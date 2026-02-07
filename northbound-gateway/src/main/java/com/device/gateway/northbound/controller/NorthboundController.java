package com.device.gateway.northbound.controller;

import com.device.gateway.common.entity.Device;
import com.device.gateway.common.message.Message;
import com.device.gateway.northbound.service.NorthboundGatewayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 北向网关控制器
 * 提供北向网关的REST API接口
 */
@RestController
@RequestMapping("/northbound")
public class NorthboundController {
    
    @Autowired
    private NorthboundGatewayService northboundGatewayService;
    
    /**
     * 连接平台
     * @param platformUrl 平台地址
     * @return 连接结果
     */
    @PostMapping("/connect-platform")
    public ResponseEntity<?> connectPlatform(@RequestParam String platformUrl) {
        try {
            CompletableFuture<Boolean> result = northboundGatewayService.connectPlatform(platformUrl);
            Boolean connected = result.get(); // 在实际应用中应避免阻塞等待
            return ResponseEntity.ok().body(connected);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to connect platform: " + e.getMessage());
        }
    }
    
    /**
     * 断开平台连接
     * @param platformUrl 平台地址
     * @return 断连结果
     */
    @PostMapping("/disconnect-platform")
    public ResponseEntity<?> disconnectPlatform(@RequestParam String platformUrl) {
        try {
            northboundGatewayService.disconnectPlatform(platformUrl);
            return ResponseEntity.ok().body("Platform disconnected successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to disconnect platform: " + e.getMessage());
        }
    }
    
    /**
     * 发送消息到平台
     * @param message 消息
     * @return 发送结果
     */
    @PostMapping("/send-message")
    public ResponseEntity<?> sendMessageToPlatform(@RequestBody Message message) {
        try {
            CompletableFuture<Message> result = northboundGatewayService.sendMessageToPlatform(message);
            Message response = result.get(); // 在实际应用中应避免阻塞等待
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to send message: " + e.getMessage());
        }
    }
    
    /**
     * 接收来自平台的消息
     * @param message 消息
     * @return 接收结果
     */
    @PostMapping("/receive-message")
    public ResponseEntity<?> receiveMessageFromPlatform(@RequestBody Message message) {
        try {
            northboundGatewayService.receiveMessageFromPlatform(message);
            return ResponseEntity.ok().body("Message received successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to receive message: " + e.getMessage());
        }
    }
    
    /**
     * 发送设备数据到平台
     * @param deviceId 设备ID
     * @param data 设备数据
     * @return 发送结果
     */
    @PostMapping("/send-device-data/{deviceId}")
    public ResponseEntity<?> sendDeviceDataToPlatform(@PathVariable String deviceId, 
                                                   @RequestBody Object data) {
        try {
            CompletableFuture<Message> result = northboundGatewayService.sendDeviceDataToPlatform(deviceId, data);
            Message response = result.get(); // 在实际应用中应避免阻塞等待
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to send device data: " + e.getMessage());
        }
    }
    
    /**
     * 发送设备状态到平台
     * @param deviceId 设备ID
     * @param status 设备状态
     * @return 发送结果
     */
    @PostMapping("/send-device-status/{deviceId}")
    public ResponseEntity<?> sendDeviceStatusToPlatform(@PathVariable String deviceId, 
                                                     @RequestParam String status) {
        try {
            CompletableFuture<Message> result = northboundGatewayService.sendDeviceStatusToPlatform(deviceId, status);
            Message response = result.get(); // 在实际应用中应避免阻塞等待
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to send device status: " + e.getMessage());
        }
    }
    
    /**
     * 获取连接的平台列表
     * @return 平台列表
     */
    @GetMapping("/connected-platforms")
    public ResponseEntity<List<String>> getConnectedPlatforms() {
        List<String> platforms = northboundGatewayService.getConnectedPlatforms();
        return ResponseEntity.ok().body(platforms);
    }
    
    /**
     * 获取设备列表（从平台获取）
     * @return 设备列表
     */
    @GetMapping("/devices")
    public ResponseEntity<?> getDevices() {
        // 在实际实现中，这里会从连接的平台获取设备列表
        return ResponseEntity.ok().body("Device list not implemented yet");
    }
}