package com.device.gateway.converter.service;

import com.device.gateway.common.entity.Device;
import com.device.gateway.common.message.Message;
import com.device.gateway.common.message.MessageType;
import com.device.gateway.common.ProtocolType;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.HashMap;
import java.util.regex.Pattern;

/**
 * 消息转换服务
 * 负责不同协议间的消息格式转换、数据校验与清洗、消息路由与过滤
 */
@Service
public class MessageConverterService {
    
    /**
     * 转换来自设备的消息到标准格式
     * @param rawMessage 原始消息
     * @param protocol 协议类型
     * @return 转换后的标准消息
     */
    public Message convertToDeviceMessage(byte[] rawMessage, ProtocolType protocol) {
        Message message = new Message();
        
        // 根据协议类型进行不同的解析
        switch (protocol) {
            case MQTT:
                message = parseMqttMessage(rawMessage);
                break;
            case TCP:
                message = parseTcpMessage(rawMessage);
                break;
            case HTTP:
                message = parseHttpMessage(rawMessage);
                break;
            case MODBUS:
                message = parseModbusMessage(rawMessage);
                break;
            case COAP:
                message = parseCoapMessage(rawMessage);
                break;
            case LORAWAN:
                message = parseLorawanMessage(rawMessage);
                break;
            case NBIOT:
                message = parseNbIotMessage(rawMessage);
                break;
            case WEBSOCKET:
                message = parseWebsocketMessage(rawMessage);
                break;
            case UDP:
                message = parseUdpMessage(rawMessage);
                break;
            default:
                // 如果协议不支持，使用默认解析
                message.setPayload(rawMessage);
                message.setMessageType(MessageType.DATA);
                break;
        }
        
        // 数据校验
        validateMessage(message);
        
        return message;
    }
    
    /**
     * 转换消息到目标协议格式
     * @param message 标准消息
     * @param targetProtocol 目标协议类型
     * @return 转换后的目标协议消息
     */
    public byte[] convertToProtocolMessage(Message message, ProtocolType targetProtocol) {
        byte[] convertedMessage;
        
        switch (targetProtocol) {
            case MQTT:
                convertedMessage = buildMqttMessage(message);
                break;
            case TCP:
                convertedMessage = buildTcpMessage(message);
                break;
            case HTTP:
                convertedMessage = buildHttpMessage(message);
                break;
            case MODBUS:
                convertedMessage = buildModbusMessage(message);
                break;
            case COAP:
                convertedMessage = buildCoapMessage(message);
                break;
            case LORAWAN:
                convertedMessage = buildLorawanMessage(message);
                break;
            case NBIOT:
                convertedMessage = buildNbIotMessage(message);
                break;
            case WEBSOCKET:
                convertedMessage = buildWebsocketMessage(message);
                break;
            case UDP:
                convertedMessage = buildUdpMessage(message);
                break;
            default:
                // 如果协议不支持，返回原始负载
                convertedMessage = message.getPayload();
                break;
        }
        
        return convertedMessage;
    }
    
    /**
     * 解析MQTT消息
     */
    private Message parseMqttMessage(byte[] rawMessage) {
        Message message = new Message();
        message.setPayload(rawMessage);
        message.setMessageType(MessageType.DATA);
        // MQTT消息解析逻辑
        return message;
    }
    
    /**
     * 解析TCP消息
     */
    private Message parseTcpMessage(byte[] rawMessage) {
        Message message = new Message();
        message.setPayload(rawMessage);
        message.setMessageType(MessageType.DATA);
        // TCP消息解析逻辑
        return message;
    }
    
    /**
     * 解析HTTP消息
     */
    private Message parseHttpMessage(byte[] rawMessage) {
        Message message = new Message();
        message.setPayload(rawMessage);
        message.setMessageType(MessageType.DATA);
        // HTTP消息解析逻辑
        return message;
    }
    
    /**
     * 解析Modbus消息
     */
    private Message parseModbusMessage(byte[] rawMessage) {
        Message message = new Message();
        message.setPayload(rawMessage);
        message.setMessageType(MessageType.DATA);
        // Modbus消息解析逻辑
        return message;
    }
    
    /**
     * 解析CoAP消息
     */
    private Message parseCoapMessage(byte[] rawMessage) {
        Message message = new Message();
        message.setPayload(rawMessage);
        message.setMessageType(MessageType.DATA);
        // CoAP消息解析逻辑
        return message;
    }
    
    /**
     * 解析LoRaWAN消息
     */
    private Message parseLorawanMessage(byte[] rawMessage) {
        Message message = new Message();
        message.setPayload(rawMessage);
        message.setMessageType(MessageType.DATA);
        // LoRaWAN消息解析逻辑
        return message;
    }
    
    /**
     * 解析NB-IoT消息
     */
    private Message parseNbIotMessage(byte[] rawMessage) {
        Message message = new Message();
        message.setPayload(rawMessage);
        message.setMessageType(MessageType.DATA);
        // NB-IoT消息解析逻辑
        return message;
    }
    
    /**
     * 解析WebSocket消息
     */
    private Message parseWebsocketMessage(byte[] rawMessage) {
        Message message = new Message();
        message.setPayload(rawMessage);
        message.setMessageType(MessageType.DATA);
        // WebSocket消息解析逻辑
        return message;
    }
    
    /**
     * 解析UDP消息
     */
    private Message parseUdpMessage(byte[] rawMessage) {
        Message message = new Message();
        message.setPayload(rawMessage);
        message.setMessageType(MessageType.DATA);
        // UDP消息解析逻辑
        return message;
    }
    
    /**
     * 构建MQTT消息
     */
    private byte[] buildMqttMessage(Message message) {
        // MQTT消息构建逻辑
        return message.getPayload();
    }
    
    /**
     * 构建TCP消息
     */
    private byte[] buildTcpMessage(Message message) {
        // TCP消息构建逻辑
        return message.getPayload();
    }
    
    /**
     * 构建HTTP消息
     */
    private byte[] buildHttpMessage(Message message) {
        // HTTP消息构建逻辑
        return message.getPayload();
    }
    
    /**
     * 构建Modbus消息
     */
    private byte[] buildModbusMessage(Message message) {
        // Modbus消息构建逻辑
        return message.getPayload();
    }
    
    /**
     * 构建CoAP消息
     */
    private byte[] buildCoapMessage(Message message) {
        // CoAP消息构建逻辑
        return message.getPayload();
    }
    
    /**
     * 构建LoRaWAN消息
     */
    private byte[] buildLorawanMessage(Message message) {
        // LoRaWAN消息构建逻辑
        return message.getPayload();
    }
    
    /**
     * 构建NB-IoT消息
     */
    private byte[] buildNbIotMessage(Message message) {
        // NB-IoT消息构建逻辑
        return message.getPayload();
    }
    
    /**
     * 构建WebSocket消息
     */
    private byte[] buildWebsocketMessage(Message message) {
        // WebSocket消息构建逻辑
        return message.getPayload();
    }
    
    /**
     * 构建UDP消息
     */
    private byte[] buildUdpMessage(Message message) {
        // UDP消息构建逻辑
        return message.getPayload();
    }
    
    /**
     * 数据校验
     * @param message 消息对象
     */
    private void validateMessage(Message message) {
        if (message == null) {
            throw new IllegalArgumentException("Message cannot be null");
        }
        
        // 基本校验
        if (message.getPayload() == null) {
            throw new IllegalArgumentException("Message payload cannot be null");
        }
        
        // 长度校验
        if (message.getPayload().length > 1024 * 1024) { // 1MB限制
            throw new IllegalArgumentException("Message payload exceeds maximum size of 1MB");
        }
        
        // 内容校验（简单示例）
        String payloadStr = new String(message.getPayload());
        if (payloadStr.contains("<?")) {
            // 检查是否有潜在的XML注入风险
            System.out.println("Warning: Potential XML content detected in message");
        }
    }
    
    /**
     * 消息过滤
     * @param message 消息对象
     * @return 是否通过过滤
     */
    public boolean filterMessage(Message message) {
        // 示例过滤规则
        if (message.getAttributes() != null) {
            // 检查是否有被标记为需要过滤的属性
            if (Boolean.TRUE.equals(message.getAttributes().get("filtered"))) {
                return false;
            }
        }
        
        // 检查消息大小
        if (message.getPayload() != null && message.getPayload().length > 1024 * 1024) {
            return false; // 超过1MB的消息被过滤
        }
        
        return true;
    }
    
    /**
     * 消息路由
     * @param message 消息对象
     * @param routingRules 路由规则
     * @return 目标地址
     */
    public String routeMessage(Message message, Map<String, Object> routingRules) {
        if (routingRules == null || routingRules.isEmpty()) {
            // 默认路由
            return "/default";
        }
        
        // 根据设备ID路由
        String deviceId = message.getDeviceId();
        if (deviceId != null) {
            String route = (String) routingRules.get("device." + deviceId);
            if (route != null) {
                return route;
            }
        }
        
        // 根据消息类型路由
        MessageType messageType = message.getMessageType();
        if (messageType != null) {
            String route = (String) routingRules.get("type." + messageType.getCode());
            if (route != null) {
                return route;
            }
        }
        
        // 根据协议类型路由
        if (message.getProtocol() != null) {
            String route = (String) routingRules.get("protocol." + message.getProtocol().getValue());
            if (route != null) {
                return route;
            }
        }
        
        // 默认路由
        return (String) routingRules.getOrDefault("default", "/default");
    }
    
    /**
     * 格式化消息为JSON
     * @param message 消息对象
     * @return JSON字符串
     */
    public String formatAsJson(Message message) {
        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"messageId\":\"").append(message.getMessageId()).append("\",");
        json.append("\"deviceId\":\"").append(message.getDeviceId()).append("\",");
        json.append("\"topic\":\"").append(message.getTopic()).append("\",");
        json.append("\"payload\":\"").append(new String(message.getPayload())).append("\",");
        json.append("\"protocol\":\"").append(message.getProtocol() != null ? message.getProtocol().getValue() : "").append("\",");
        json.append("\"messageType\":\"").append(message.getMessageType() != null ? message.getMessageType().getCode() : "").append("\"");
        json.append("}");
        
        return json.toString();
    }
}