# IoT Device Gateway 物联网设备网关

[![Java](https://img.shields.io/badge/Java-21-blue.svg)](https://www.oracle.com/java/technologies/javase-jdk21-downloads.html)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Maven](https://img.shields.io/badge/Maven-3.8.2-orange.svg)](https://maven.apache.org/)

一个基于Java 21 + Spring Boot 3的多协议物联网设备网关，支持MQTT、TCP、HTTP、Modbus、LoRaWAN、NB-IoT、CoAP、WebSocket、UDP等多种物联网协议接入。

## 🌟 项目特性

### 🔧 多协议支持
- **MQTT协议**: 基于Eclipse Paho客户端实现
- **TCP协议**: 基于Netty高性能网络框架
- **HTTP协议**: Spring WebFlux异步处理
- **Modbus协议**: 工业设备通信协议支持
- **LoRaWAN协议**: 低功耗广域网协议
- **NB-IoT协议**: 窄带物联网协议
- **CoAP协议**: 受限应用协议
- **WebSocket协议**: 实时双向通信
- **UDP协议**: 用户数据报协议

### 🏗️ 架构设计
- **模块化设计**: 采用Maven多模块架构，便于维护和扩展
- **南北向网关**: 分离设备接入和平台对接逻辑
- **协议插件化**: 支持动态协议注册和扩展
- **消息路由**: 灵活的消息转发和路由机制
- **流媒体处理**: 实时数据流处理和转发
- **数据存储**: 支持多种存储后端

## 📁 项目结构

```
device-gateway/
├── common/                 # 公共模块
│   ├── entity/            # 实体类
│   └── enums/             # 枚举类
├── gateway-core/          # 核心框架模块
│   ├── config/            # 配置类
│   ├── handler/           # 协议处理器接口
│   ├── manager/           # 设备管理接口
│   └── route/             # 消息路由接口
├── protocol-handler/      # 协议处理器模块
│   └── impl/              # 各协议实现
├── southbound-gateway/    # 南向网关（设备接入）
├── northbound-gateway/    # 北向网关（平台对接）
├── message-converter/     # 消息转换模块
├── streaming-service/     # 流媒体转发服务
├── storage-service/       # 存储服务
└── gateway-starter/       # 启动器模块
```

## 🚀 快速开始

### 环境要求

- **Java**: JDK 21 或更高版本
- **Maven**: 3.8.2 或更高版本
- **内存**: 建议4GB以上
- **操作系统**: Windows/Linux/macOS

### 编译构建

```bash
# 克隆项目
git clone <repository-url>
cd device-gateway

# 编译项目
mvn clean compile

# 打包项目
mvn clean package -DskipTests
```

### 运行应用

```bash
# 使用Java 21运行
java -jar gateway-starter/target/gateway-starter-1.0.0-SNAPSHOT.jar

# 或指定JDK路径运行
/path/to/jdk-21/bin/java -jar gateway-starter/target/gateway-starter-1.0.0-SNAPSHOT.jar
```

### 配置文件

应用配置文件位于 `gateway-starter/src/main/resources/application.yml`：

```yaml
server:
  port: 8080

spring:
  application:
    name: device-gateway

# 设备网关配置
device-gateway:
  mqtt:
    host: localhost
    port: 1883
  tcp:
    host: 0.0.0.0
    port: 8081
  http:
    port: 8082
```

## 📊 API接口

### 南向网关接口

#### 连接设备
```http
POST /southbound/connect
Content-Type: application/json

{
  "deviceId": "device001",
  "deviceName": "温度传感器",
  "protocol": "MQTT",
  "host": "192.168.1.100",
  "port": 1883
}
```

#### 断开设备
```http
DELETE /southbound/disconnect/{deviceId}
```

#### 发送消息到设备
```http
POST /southbound/send/{deviceId}
Content-Type: application/json

{
  "topic": "control/device001",
  "payload": "command_data",
  "qos": 1
}
```

### 北向网关接口

#### 连接平台
```http
POST /northbound/connect-platform?platformUrl=http://platform.example.com
```

#### 获取设备列表
```http
GET /northbound/devices
```

#### 获取设备状态
```http
GET /northbound/device/{deviceId}/status
```

## 🔧 开发指南

### 添加新的协议支持

1. 在 `protocol-handler` 模块中创建新的协议处理器：

```java
@Component
public class NewProtocolHandler extends AbstractProtocolHandler {
    
    @Override
    protected void doInitialize(ProtocolConfig config) {
        // 协议初始化逻辑
    }
    
    @Override
    public CompletableFuture<Message> send(Message message) {
        // 发送消息实现
    }
    
    @Override
    public String getProtocolType() {
        return "NEW_PROTOCOL";
    }
}
```

2. 在 `ProtocolType` 枚举中添加新的协议类型

3. 更新配置文件添加协议配置

### 扩展消息转换规则

在 `message-converter` 模块中实现自定义的消息转换逻辑：

```java
@Service
public class CustomMessageConverter {
    
    public Message convertToDeviceMessage(byte[] rawData, ProtocolType protocol) {
        // 自定义转换逻辑
        return convertedMessage;
    }
}
```

## 📈 监控与运维

### 健康检查
```http
GET /actuator/health
```

### 指标监控
```http
GET /actuator/metrics
```

### 日志配置
日志文件位于 `logs/device-gateway.log`，可通过以下方式配置：

```yaml
logging:
  level:
    com.device.gateway: DEBUG
  file:
    name: logs/device-gateway.log
```

## 🛡️ 安全配置

### 认证授权
- 支持JWT令牌认证
- 设备接入认证机制
- API访问权限控制

### 数据加密
- TLS/SSL传输加密
- 敏感数据存储加密
- 消息签名验证

## 🤝 贡献指南

1. Fork 项目
2. 创建功能分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 开启 Pull Request

## 📄 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情

## 📞 联系方式

- 项目维护者: [Your Name]
- 邮箱: [your.email@example.com]
- 项目地址: [https://github.com/username/device-gateway]

## 🙏 致谢

- [Spring Boot](https://spring.io/projects/spring-boot) - 应用框架
- [Netty](https://netty.io/) - 网络通信框架
- [Eclipse Paho](https://www.eclipse.org/paho/) - MQTT客户端
- [Project Lombok](https://projectlombok.org/) - 代码简化工具

---
**IoT Device Gateway** - 构建万物互联的桥梁 🌉