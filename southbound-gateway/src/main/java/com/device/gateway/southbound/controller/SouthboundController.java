package com.device.gateway.southbound.controller;

import com.device.gateway.common.entity.Device;
import com.device.gateway.common.message.Message;
import com.device.gateway.southbound.service.SouthboundGatewayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

/**
 * 南向网关控制器
 * 提供南向网关的REST API接口
 */
@RestController
@RequestMapping("/southbound")
public class SouthboundController {
    
    @Autowired
    private SouthboundGatewayService southboundGatewayService;
    
    /**
     * 连接设备
     * @param device 设备信息
     * @return 连接结果
     */
    @PostMapping("/connect")
    public ResponseEntity<?> connectDevice(@RequestBody Device device) {
        try {
            CompletableFuture<Boolean> result = southboundGatewayService.connectDevice(device);
            Boolean connected = result.get(); // 在实际应用中应避免阻塞等待
            return ResponseEntity.ok().body(connected);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to connect device: " + e.getMessage());
        }
    }
    
    /**
     * 断开设备连接
     * @param deviceId 设备ID
     * @return 断连结果
     */
    @PostMapping("/disconnect/{deviceId}")
    public ResponseEntity<?> disconnectDevice(@PathVariable String deviceId) {
        try {
            southboundGatewayService.disconnectDevice(deviceId);
            return ResponseEntity.ok().body("Device disconnected successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to disconnect device: " + e.getMessage());
        }
    }
    
    /**
     * 发送消息到设备
     * @param deviceId 设备ID
     * @param message 消息
     * @return 发送结果
     */
    @PostMapping("/send/{deviceId}")
    public ResponseEntity<?> sendMessageToDevice(@PathVariable String deviceId, 
                                              @RequestBody Message message) {
        try {
            CompletableFuture<Message> result = southboundGatewayService.sendMessageToDevice(deviceId, message);
            Message response = result.get(); // 在实际应用中应避免阻塞等待
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to send message: " + e.getMessage());
        }
    }
    
    /**
     * 接收来自设备的消息（内部方法）
     * @param deviceId 设备ID
     * @param message 消息
     * @return 接收结果
     */
    @PostMapping("/receive/{deviceId}")
    public ResponseEntity<?> receiveMessageFromDevice(@PathVariable String deviceId, 
                                                   @RequestBody Message message) {
        try {
            message.setDeviceId(deviceId);
            southboundGatewayService.receiveMessageFromDevice(message);
            return ResponseEntity.ok().body("Message received successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to receive message: " + e.getMessage());
        }
    }
    
    /**
     * 获取设备连接状态
     * @param deviceId 设备ID
     * @return 设备状态
     */
    @GetMapping("/status/{deviceId}")
    public ResponseEntity<?> getDeviceStatus(@PathVariable String deviceId) {
        // 在实际实现中，这里会查询设备的实际连接状态
        return ResponseEntity.ok().body("Device status check not implemented yet");
    }
}