# InfiniteChat (千言) - 微服务聊天应用

## 📖 项目简介

InfiniteChat（千言）是一个基于Spring Cloud微服务架构的现代化聊天应用，提供实时通信、消息管理、用户认证、联系人管理、朋友圈等功能。项目采用分布式架构设计，支持高并发、高可用的聊天服务。

## 🏗️ 系统架构

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   前端应用      │    │   移动端应用    │    │   Web端应用     │
└─────────┬───────┘    └─────────┬───────┘    └─────────┬───────┘
          │                      │                      │
          └──────────────────────┼──────────────────────┘
                                 │
                    ┌─────────────┴─────────────┐
                    │        Gateway            │
                    │     (API网关服务)         │
                    └─────────────┬─────────────┘
                                  │
        ┌─────────────────────────┼─────────────────────────┐
        │                         │                         │
┌───────▼────────┐    ┌───────────▼──────────┐    ┌────────▼────────┐
│Authentication  │    │RealTimeCommunication │    │  Messaging      │
│Service         │    │Service               │    │  Service        │
│(认证服务)      │    │(实时通信服务)         │    │  (消息服务)     │
└────────────────┘    └──────────────────────┘    └─────────────────┘
        │                         │                         │
        └─────────────────────────┼─────────────────────────┘
                                  │
        ┌─────────────────────────┼─────────────────────────┐
        │                         │                         │
┌───────▼────────┐    ┌───────────▼──────────┐    ┌────────▼────────┐
│ContactService  │    │OfflineDataStore      │    │  MomentService  │
│(联系人服务)    │    │Service               │    │  (朋友圈服务)   │
└────────────────┘    │(离线数据存储服务)     │    └─────────────────┘
                      └──────────────────────┘
```

## 🛠️ 技术栈

### 后端技术
- **框架**: Spring Boot 2.6.13
- **微服务**: Spring Cloud Gateway 3.1.3
- **服务发现**: Nacos Discovery 2021.0.5.0
- **负载均衡**: Spring Cloud LoadBalancer 3.1.4
- **数据库**: MySQL + MyBatis-Plus 3.5.2
- **缓存**: Redis + Redisson 3.16.0
- **实时通信**: Netty
- **认证**: JWT (JSON Web Token)
- **文件存储**: MinIO 8.2.1
- **短信服务**: 阿里云短信服务
- **邮件服务**: JavaMail
- **工具库**: Hutool 5.7.17, OkHttp 4.8.1

### 开发环境
- **JDK**: 1.8
- **构建工具**: Maven
- **编码**: UTF-8

## 📦 服务模块

### 1. GateWay (API网关服务)
- **功能**: 统一入口、路由转发、负载均衡
- **端口**: 默认8080
- **技术**: Spring Cloud Gateway + Nacos Discovery

### 2. AuthenticationService (认证服务)
- **功能**: 用户注册、登录、JWT认证、权限管理
- **主要特性**:
  - 用户注册与登录
  - JWT Token生成与验证
  - 短信验证码服务
  - 邮件验证服务
  - 文件上传管理
  - 用户信息管理

### 3. RealTimeCommunicationService (实时通信服务)
- **功能**: WebSocket连接管理、实时消息推送
- **主要特性**:
  - 基于Netty的WebSocket服务
  - 实时消息推送
  - 在线状态管理
  - 连接池管理

### 4. MessagingService (消息服务)
- **功能**: 消息存储、历史记录、消息状态管理
- **主要特性**:
  - 消息发送与接收
  - 消息历史记录
  - 消息状态跟踪
  - 消息搜索功能

### 5. ContactService (联系人服务)
- **功能**: 好友管理、群组管理、联系人信息
- **主要特性**:
  - 好友添加与删除
  - 群组创建与管理
  - 联系人信息维护
  - 好友关系管理

### 6. OfflineDataStoreService (离线数据存储服务)
- **功能**: 离线消息存储、数据同步
- **主要特性**:
  - 离线消息缓存
  - 数据同步机制
  - 消息队列处理

### 7. MomentService (朋友圈服务)
- **功能**: 动态发布、点赞评论、社交功能
- **主要特性**:
  - 动态发布与展示
  - 点赞与评论功能
  - 朋友圈权限管理
  - 社交互动功能

## 🚀 快速开始

### 环境要求
- JDK 1.8+
- Maven 3.6+
- MySQL 5.7+
- Redis 5.0+
- Nacos 2.0+

### 安装步骤

1. **克隆项目**
```bash
git clone <repository-url>
cd infiniteChat
```

2. **配置数据库**
```bash
# 创建数据库
CREATE DATABASE infinite_chat CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

3. **配置Nacos**
```bash
# 启动Nacos服务
# 在Nacos控制台创建命名空间和配置
```

4. **修改配置文件**
```bash
# 在各个服务的application.yml中配置数据库连接、Redis连接等
```

5. **编译项目**
```bash
mvn clean compile
```

6. **启动服务**
```bash
# 按顺序启动服务
mvn spring-boot:run -pl GateWay
mvn spring-boot:run -pl AuthenticationService
mvn spring-boot:run -pl RealTimeCommunicationService
mvn spring-boot:run -pl MessagingService
mvn spring-boot:run -pl ContactService
mvn spring-boot:run -pl OfflineDataStoreService
mvn spring-boot:run -pl MomentService
```

## 📁 项目结构

```
infiniteChat/
├── pom.xml                          # 父项目POM文件
├── BubbleSort.java                  # 冒泡排序算法示例
├── GateWay/                         # API网关服务
├── AuthenticationService/           # 认证服务
├── RealTimeCommunicationService/    # 实时通信服务
├── MessagingService/                # 消息服务
├── ContactService/                  # 联系人服务
├── OfflineDataStoreService/         # 离线数据存储服务
└── MomentService/                   # 朋友圈服务
```

## 🔧 配置说明

### 数据库配置
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/infinite_chat?useUnicode=true&characterEncoding=utf8&useSSL=false
    username: your_username
    password: your_password
    driver-class-name: com.mysql.cj.jdbc.Driver
```

### Redis配置
```yaml
spring:
  redis:
    host: localhost
    port: 6379
    password: your_password
    database: 0
```

### Nacos配置
```yaml
spring:
  cloud:
    nacos:
      discovery:
        server-addr: localhost:8848
        namespace: your_namespace
```

## 📝 API文档

各服务的API文档可通过以下方式访问：
- Swagger UI: `http://localhost:{port}/swagger-ui.html`
- API文档: `http://localhost:{port}/v2/api-docs`

## 🧪 测试

```bash
# 运行所有测试
mvn test

# 运行特定服务的测试
mvn test -pl AuthenticationService
```

## 📊 性能优化

- 使用Redis缓存热点数据
- 数据库连接池优化
- 消息队列异步处理
- 负载均衡分发请求
- 微服务独立部署

## 🔒 安全特性

- JWT Token认证
- 接口权限控制
- 数据加密传输
- SQL注入防护
- XSS攻击防护

## 🤝 贡献指南

1. Fork 项目
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 打开 Pull Request

## 📄 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情

## 📞 联系方式

- 项目维护者: Orion
- 邮箱: workwy@outlook.com
- 项目地址: [https://github.com/your-username/infiniteChat]

## 🙏 致谢

感谢所有为这个项目做出贡献的开发者和开源社区。

---

**注意**: 这是一个开发中的项目，部分功能可能仍在开发中。请查看最新的提交记录了解项目状态。 