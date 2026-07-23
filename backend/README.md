# CRS Backend

Java 21、Spring Boot 和 Maven 后端模块。

## 开发

```bash
./mvnw spring-boot:run
```

默认地址：`http://localhost:8080`

健康检查：`GET http://localhost:8080/api/health`

固定响应聊天接口：

```bash
curl -X POST http://localhost:8080/api/chat \
  -H "Content-Type: application/json" \
  -d '{"message":"What is Spring Boot?"}'
```

## DeepSeek 配置

Spring Boot 从环境变量读取 DeepSeek 配置。本地开发时也会显式加载 `backend/.env`；该文件已被 Git 忽略。

第一次配置时，可以复制示例文件，然后只在本机填写真实 Key：

```bash
cp .env.example .env
```

```text
DEEPSEEK_API_KEY=
DEEPSEEK_BASE_URL=https://api.deepseek.com
DEEPSEEK_MODEL=deepseek-v4-flash
DEEPSEEK_TIMEOUT=30s
```

系统环境变量的优先级高于 `.env`，因此部署时不需要携带 `.env`。当前阶段只完成配置绑定，聊天接口尚未调用 DeepSeek。

## 验证

```bash
./mvnw test
./mvnw package
```
