# CRS

CRS 是一个用于学习和构建 AI 聊天服务的前后端分离项目。主要目的是个人使用，为了方便切换不同的llm。

当前阶段只包含可运行的项目骨架，不包含 DeepSeek 调用、聊天逻辑、数据库或用户系统。

## 技术栈

- 前端：React、Vite、TypeScript、Material UI
- 后端：Java 21、Spring Boot、Maven
- 通信：后续使用 HTTP 和 SSE

## 目录结构

```text
CRS/
├── frontend/   React 单页面应用
├── backend/    Spring Boot API 服务
└── README.md   项目总说明
```

## 启动后端

```bash
cd backend
./mvnw spring-boot:run
```

后端健康检查：`http://localhost:8080/api/health`

## 启动前端

```bash
cd frontend
npm install
npm run dev
```

前端地址：`http://localhost:5173`

## 后续模块

1. 聊天消息模型和前端交互
2. DeepSeek 服务端适配器
3. SSE 流式响应
4. 对话存储
5. 登录、限流和部署
