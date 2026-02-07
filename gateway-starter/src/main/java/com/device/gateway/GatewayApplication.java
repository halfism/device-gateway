package com.device.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 设备网关启动类
 * 整合所有模块，提供统一的启动入口
 */
@SpringBootApplication(scanBasePackages = {
    "com.device.gateway.common",
    "com.device.gateway.core",
    "com.device.gateway.protocol",
    "com.device.gateway.southbound",
    "com.device.gateway.northbound", 
    "com.device.gateway.converter",
    "com.device.gateway.streaming",
    "com.device.gateway.storage",
    "com.device.gateway"
})
public class GatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
        System.out.println("=================================");
        System.out.println(" IoT Device Gateway Started! ");
        System.out.println("=================================");
        System.out.println("Supported Protocols: MQTT, TCP, HTTP, Modbus, LoRaWAN, NB-IoT, CoAP, WebSocket, UDP");
        System.out.println("Southbound Gateway: /southbound");
        System.out.println("Northbound Gateway: /northbound");
        System.out.println("=================================");
    }
}