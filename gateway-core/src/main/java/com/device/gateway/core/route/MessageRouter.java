package com.device.gateway.core.route;

import com.device.gateway.common.message.Message;

/**
 * 消息路由器接口
 */
public interface MessageRouter {
    
    /**
     * 路由南向消息（从设备到平台）
     * @param message 消息
     */
    void routeSouthboundMessage(Message message);
    
    /**
     * 路由北向消息（从平台到设备）
     * @param message 消息
     */
    void routeNorthboundMessage(Message message);
    
    /**
     * 路由消息
     * @param message 消息
     * @param direction 方向：northbound/southbound
     */
    void routeMessage(Message message, String direction);
}