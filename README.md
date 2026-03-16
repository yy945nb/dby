# DBY 微服务 + AI 平台

基于 **Spring Boot 3.x** + **Spring Cloud** + **Spring AI** 构建的微服务平台，集成 OpenAI 实现智能对话与文本分析能力。

## 📐 系统架构

```
                    ┌──────────────────────────────────────┐
                    │         客户端 (Browser / App)         │
                    └─────────────────┬────────────────────┘
                                      │ :8080
                    ┌─────────────────▼────────────────────┐
                    │          API 网关 (gateway)            │
                    │      Spring Cloud Gateway             │
                    │  • 统一路由  • CORS  • 请求日志        │
                    └────────┬───────────────┬─────────────┘
                             │               │
             /api/users/**   │               │  /api/ai/**
                    ┌────────▼───┐    ┌──────▼────────┐
                    │ user-service│    │  ai-service   │
                    │  :8081      │    │   :8082       │
                    │             │    │               │
                    │ • 用户注册  │    │ • 智能对话    │
                    │ • 用户查询  │    │ • 文本摘要    │
                    │ • 密码加密  │    │ • 情感分析    │
                    └────────┬───┘    └──────┬────────┘
                             │               │
                    ┌────────▼───┐    ┌──────▼────────┐
                    │  H2 数据库 │    │  OpenAI API   │
                    └────────────┘    └───────────────┘
```

## 📦 模块说明

| 模块 | 端口 | 描述 |
|------|------|------|
| `common` | - | 共享 DTO、异常处理、工具类 |
| `user-service` | 8081 | 用户注册、查询、管理（H2 内存数据库） |
| `ai-service` | 8082 | 智能对话、文本摘要、情感分析（OpenAI） |
| `gateway` | 8080 | API 网关，统一路由与 CORS 处理 |

## 🚀 快速开始

### 前置要求

- JDK 17+
- Maven 3.8+
- OpenAI API Key（AI 服务需要）

### 启动步骤

```bash
# 1. 编译打包
mvn clean package -DskipTests

# 2. 启动用户服务（新终端）
cd user-service
mvn spring-boot:run

# 3. 启动 AI 服务（新终端，需设置 OpenAI Key）
export OPENAI_API_KEY=your-api-key-here
cd ai-service
mvn spring-boot:run

# 4. 启动 API 网关（新终端）
cd gateway
mvn spring-boot:run
```

### 运行测试

```bash
# 运行全部测试
mvn test

# 只运行用户服务测试
cd user-service && mvn test

# 只运行 AI 服务测试（使用 Mock，无需真实 OpenAI Key）
cd ai-service && mvn test
```

## 🔌 API 文档

所有请求通过网关 `http://localhost:8080` 访问。

### 用户服务 API

#### 注册用户
```http
POST /api/users/register
Content-Type: application/json

{
  "username": "zhangsan",
  "email": "zhangsan@example.com",
  "password": "password123",
  "nickname": "张三"
}
```

#### 查询用户
```http
GET /api/users/{id}
GET /api/users/username/{username}
GET /api/users
```

#### 更新昵称
```http
PATCH /api/users/{id}/nickname?nickname=新昵称
```

### AI 服务 API

#### 智能对话
```http
POST /api/ai/chat
Content-Type: application/json

{
  "message": "请介绍一下微服务架构的优缺点",
  "systemPrompt": "你是一个软件架构专家"
}
```

#### 文本摘要
```http
POST /api/ai/summarize?text=需要摘要的长文本...
```

#### 情感分析
```http
POST /api/ai/sentiment?text=今天天气真好，心情很愉快！
```

### 统一响应格式

```json
{
  "code": 200,
  "message": "success",
  "data": { ... }
}
```

## ⚙️ 配置说明

### AI 服务配置

AI 服务需要配置 OpenAI API Key，支持以下方式：

1. **环境变量**（推荐）：
   ```bash
   export OPENAI_API_KEY=sk-your-key-here
   ```

2. **application.yml**（开发环境）：
   ```yaml
   spring:
     ai:
       openai:
         api-key: sk-your-key-here
   ```

### 服务发现

当前使用静态路由配置。生产环境可接入 Nacos/Eureka 实现动态服务发现。

## 🛡️ 安全特性

- **密码加密**：BCrypt 算法加密存储用户密码
- **输入校验**：所有 API 入参均经过 Bean Validation 校验
- **统一异常**：全局异常处理，不泄露内部信息
- **API Key 保护**：OpenAI Key 通过环境变量注入，不提交代码

## 🏗️ 技术栈

| 技术 | 版本 | 用途 |
|------|------|------|
| Spring Boot | 3.2.3 | 微服务基础框架 |
| Spring Cloud Gateway | 2023.0.0 | API 网关 |
| OpenAI REST API | - | AI 接口（直接调用） |
| Spring WebFlux (WebClient) | - | 响应式 HTTP 客户端 |
| Spring Data JPA | - | 数据持久化 |
| Spring Security | - | 安全认证 |
| H2 Database | - | 内存数据库（开发） |
| OpenAI GPT-4o-mini | - | AI 语言模型 |
