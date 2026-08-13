# CRS 项目上下文与交接记录

> 最后更新：2026-08-12
> 项目路径：`/Users/thomasnoel/Documents/CS/CRS`

## 如何使用这份文件

新对话开始后，请先阅读本文件，再运行：

```bash
git status
```

不要在不了解工作区现有修改的情况下覆盖、撤销或提交文件。本文件记录的是项目当前方向、已完成内容、最新架构决定和下一步计划；如果它与较早的聊天摘要冲突，以本文件中日期较新的内容为准。

## 协作方式

这是 Thomas 的学习项目。

- 后端功能原则上由 Thomas 自己编写。
- AI 先解释概念、调用链和这一小步的目标，再让 Thomas 动手。
- Thomas 写完后，AI 检查代码、分析错误并解释原因。
- 不要一次性替 Thomas 完成整块后端功能，除非他明确要求。
- 前端、README、测试等内容可以在 Thomas 明确授权后由 AI 直接修改。
- 每次只推进一个范围清楚、可以验证的小步骤。
- 后续逐步加强架构训练：AI 优先提出业务需求、约束和验收标准，由 Thomas 先设计组件、职责、依赖方向、调用链和失败场景，再进行评审与实现。
- 业务代码默认不直接给完整答案；Thomas 如果写不出来，可以再请求提示或完整实现。
- 测试代码可以由 AI 提供完整版本，Thomas 负责理解和运行。
- 业务实现和测试都应以事先确认的需求/设计契约为依据，不能让测试机械复制当前实现，也不能为了让测试通过而盲目修改业务代码。

## 项目定位

CRS 正在从一个持久化聊天应用演化成一个可长期使用的“虚拟伙伴 / 虚拟助手”。

核心目标不是做通用 Agent，也不是简单复制 ChatGPT，而是让用户拥有一个属于自己的、长期存在的虚拟伙伴：

1. 助手拥有稳定的人设，例如名字、头像、性格、说话方式和默认语言。
2. 助手能够记住用户明确允许保存的个人信息、偏好、目标和共同经历。
3. 助手的人设与长期记忆跨 conversation、跨应用重启保存。
4. 用户可以自由配置自己使用的模型服务、API 地址、密钥和模型 ID。
5. 更换模型服务时，助手仍然是同一个助手；模型只是生成回答的“推理引擎”。
6. 支持文字对话，并逐步加入图片和文件附件。
7. Agent 工具调用、外部操作、定时任务和自动化暂时延后。

一句话定位：

> CRS 是一个支持自定义模型服务、持久化人设与长期记忆，并能通过文字、图片和文件交流的虚拟伙伴。

## 核心产品原则

### 人设和记忆属于 CRS

DeepSeek、Ollama 或其他模型本身不会永久记住用户。CRS 将人设、用户资料和记忆保存在自己的数据库中，并在每次请求时选择合适的上下文交给模型。

```text
助手人设
+ 用户资料
+ 长期记忆
+ 当前 conversation 历史
+ 当前消息与附件
        ↓
ContextAssembler
        ↓
选中的模型服务
```

因此，更换模型不应改变助手身份，也不应清空用户记忆。

### 记忆必须由用户控制

第一版长期记忆不应由模型完全自动创建。用户必须能够：

- 明确要求“记住”某件事。
- 查看 CRS 保存了什么。
- 修改错误记忆。
- 删除单条记忆或清空记忆。
- 后期允许模型“建议记忆”，但保存前需要用户确认。

### 模型数量不等于 Client 数量

不要为每个模型创建一个 Java Client。

错误方向：

```text
QwenClient
LlamaClient
GemmaClient
DeepSeekFlashClient
```

正确方向是“少量协议适配器 + 任意数量的用户配置”：

```text
协议适配器
    └── ProviderConfig（地址、密钥）
            └── ModelConfig（任意 modelId）
```

## 最新架构决定：按协议适配，不按厂商适配

早期设计将以下内容放在同一层：

```text
DEEPSEEK
OLLAMA
OPENAI_COMPATIBLE
ANTHROPIC
```

这混淆了“厂商”和“网络协议”。最新讨论认为 Router 应根据协议选择 Client，而不是根据厂商名称选择 Client。

当前已确认的兼容关系：

- DeepSeek 支持 OpenAI-compatible Chat Completions。
- Ollama 支持 OpenAI-compatible Chat Completions，并支持部分 Responses API；也提供 Anthropic Messages 兼容接口。
- Anthropic 提供 OpenAI 兼容层，但完整 Claude 功能仍更适合原生 Anthropic Messages API。

因此第一版可以尽量精简为：

```text
ChatProtocol
└── OPENAI_CHAT_COMPLETIONS

OpenAiCompatibleChatClient
├── DeepSeek
├── Ollama
├── OpenRouter
├── 兼容接口的第三方服务
└── 用户自建服务
```

以后只有在确实需要原生功能时，才增加：

```text
OPENAI_RESPONSES
ANTHROPIC_MESSAGES
OLLAMA_NATIVE（仅模型查询、下载等 Ollama 专有管理功能）
```

这个命名已经落到代码中：

```java
ChatProtocol protocol();
```

而不是：

```java
ModelProvider provider();
```

原因是 `ChatModelClient` 声明的应该是它实现的调用协议，而不是厂商品牌。

## 计划中的配置层次

### ChatProtocol

表示后端实现的网络请求格式，例如：

```text
OPENAI_CHAT_COMPLETIONS
OPENAI_RESPONSES（未来可选）
ANTHROPIC_MESSAGES（未来可选）
```

### ProviderConfig

表示用户实际配置的一个模型服务：

```text
id
displayName
protocol
baseUrl
encryptedApiKey
enabled
```

示例：

```text
displayName: My DeepSeek
protocol: OPENAI_CHAT_COMPLETIONS
baseUrl: https://api.deepseek.com
apiKey: 加密保存
```

```text
displayName: Local Ollama
protocol: OPENAI_CHAT_COMPLETIONS
baseUrl: http://localhost:11434/v1
apiKey: 无
```

### ModelConfig

表示某个 ProviderConfig 下由用户配置的模型：

```text
id
providerConfigId
modelId
displayName
capabilities
enabled
```

用户可以添加任意数量的模型，不需要修改 Java 枚举或重新编译项目。

## 目标数据模型

### AssistantProfile

描述助手是谁：

```text
id
name
description
personality
speakingStyle
defaultLanguage
systemPrompt
avatar
createdAt
updatedAt
```

### UserProfile

描述用户相对稳定的个人资料：

```text
id
displayName
preferredLanguage
timezone
biography
currentGoals
createdAt
updatedAt
```

### MemoryItem

描述跨 conversation 保存的小型长期记忆：

```text
id
category
content
sourceConversationId
sourceMessageId
status
createdAt
updatedAt
```

类别初步考虑：

```text
PREFERENCE
PERSONAL_FACT
LEARNING_GOAL
PROJECT_CONTEXT
RELATIONSHIP_CONTEXT
```

### FileAsset 与 MessageAttachment

文件本体与消息关系分开：

```text
Message
└── MessageAttachment
        └── FileAsset
```

`FileAsset` 保存文件名、MIME type、大小和安全存储位置。图片与文件先作为当前消息的附件；知识库和 RAG 不是近期核心功能。

## 技术栈

### Frontend

- React
- TypeScript
- Vite
- Material UI

### Backend

- Java 21
- Spring Boot
- Spring MVC
- Spring Data JPA
- Flyway
- Maven Wrapper

### Database

- 当前：H2
- 数据库迁移：Flyway
- 以后只有在文件检索、RAG 或生产部署确有需要时，再考虑 PostgreSQL/pgvector。

## 当前已经完成的功能

### Conversation

- 创建 conversation。
- 按更新时间列出 conversation。
- 查看 conversation 详情。
- 重命名 conversation。
- 删除 conversation。
- 删除确认框与前端错误处理。
- 第一次用户消息自动生成标题。
- 数据库级 `ON DELETE CASCADE` 删除关联消息。

### Message

- 多轮消息持久化。
- 消息顺序号。
- 每次请求向模型发送完整的 conversation 历史。
- 保存用户消息与 assistant 回复。

### 模型调用

- `DeepSeekClient` 已实现 DeepSeek 文本对话。
- `ChatModelClient` 已建立为 provider-neutral 接口。
- `ChatProtocol` 已创建，当前包含 `OPENAI_CHAT_COMPLETIONS`。
- `ChatModelClient` 已从按厂商表达的 `provider()` 改为按协议表达的 `protocol()`。
- `DeepSeekClient.protocol()` 返回 `OPENAI_CHAT_COMPLETIONS`。
- `ChatModelClientRouter` 已创建并注入 `List<ChatModelClient>`；Spring 会收集所有注册为 Bean 的 Client 实现。
- Router 已实现按 `ChatProtocol` 筛选，并要求恰好匹配一个 Client：一个匹配则返回，无匹配或重复匹配则抛异常。
- `ChatModelMessage` 与 `ChatModelRole` 已隔离通用消息结构。
- DeepSeek 专用 DTO 保留在 `ai/deepseek/dto` 内。

### 模型选择

- Conversation 数据库字段已经保存 `provider` 和 `model_id`。
- Flyway `V2__add_model_to_conversations.sql` 已完成迁移。
- `ModelCatalog` 当前静态提供：
  - `deepseek-v4-flash`
  - `deepseek-v4-pro`
- `ChatModelDefinition` 已增加 `protocol` 字段。
- 两个 DeepSeek 模型都已登记为使用 `OPENAI_CHAT_COMPLETIONS`。
- `ModelCatalog.getModel(provider, modelId)` 已实现：返回完整模型定义，找不到时抛出 `UnsupportedModelException`。
- `GET /api/models` 已实现。
- 模型 API 测试已同步四参数 `ChatModelDefinition`，并验证 JSON 中的 `protocol`。
- 创建 conversation 时会验证 provider/modelId。
- 前端已经从 `/api/models` 加载并显示模型下拉框。
- Conversation DTO 已返回 provider/modelId。

### 已删除的旧功能

旧的无状态单轮接口已删除：

```text
POST /api/chat
ChatController
ChatService
ChatRequest
ChatResponse
ChatControllerTest
```

不要删除 DeepSeek 自己仍在使用的 DTO：

```text
DeepSeekChatRequest
DeepSeekChatResponse
DeepSeekMessage
```

## 当前尚未完成

- Router 的异常类型尚未统一：当前业务代码抛 `IllegalArgumentException`，Router 测试期待 `IllegalStateException`。需要先完成设计决定并同步实现与测试。
- Router 尚未接入 `ConversationService`。
- 尚未加入第二种协议 Client，因此还未验证真正的多协议路由。
- 通用的 OpenAI-compatible Client。
- Ollama 接入。
- 用户动态配置 Provider/API。
- API key 加密存储。
- AssistantProfile。
- UserProfile。
- 跨 conversation 的 MemoryItem。
- ContextAssembler。
- 文件和图片上传。
- 模型能力描述。
- SSE 流式回复与取消。
- Agent 工具与外部执行能力。

## 当前代码状态（2026-08-12）

最近提交：

```text
aac05e1 Upgrade
02daab3 Integrate different models
```

当前存在未提交修改，请勿覆盖：

```text
PROJECT_CONTEXT.md
README.md
backend/src/main/java/com/thomasnoel/crs/ai/ChatModelClient.java
backend/src/main/java/com/thomasnoel/crs/ai/ChatModelClientRouter.java
backend/src/main/java/com/thomasnoel/crs/ai/ChatModelDefinition.java
backend/src/main/java/com/thomasnoel/crs/ai/ChatProtocol.java
backend/src/main/java/com/thomasnoel/crs/ai/ModelCatalog.java
backend/src/main/java/com/thomasnoel/crs/ai/deepseek/DeepSeekClient.java
backend/src/test/java/com/thomasnoel/crs/api/model/ModelControllerTest.java
backend/src/test/java/com/thomasnoel/crs/ai/ChatModelClientRouterTest.java
```

其中：

- `README.md` 已由 AI 更新为新的虚拟伙伴方向，但尚未提交。
- `PROJECT_CONTEXT.md` 是本交接记录，目前已加入 Git 暂存区，但尚未提交。
- Thomas 已完成 `ChatProtocol`、`ChatModelClient.protocol()`、`DeepSeekClient.protocol()`、`ChatModelDefinition.protocol` 和 `ModelCatalog.getModel()`。
- Thomas 已完成 `ChatModelClientRouter` 的 Client 收集、协议筛选、零匹配、重复匹配与成功返回逻辑。
- `ChatModelClientRouterTest` 已创建，使用不依赖 Spring/Mockito 的 Fake Client 覆盖成功、无匹配和重复匹配三条路径。
- AI 已按 Thomas 的明确授权整理 `ModelCatalog.getModel()` 格式，并同步 `ModelControllerTest` 的新构造参数和协议断言。
- 不要撤销或覆盖上述未提交修改。

当前有一个明确的未解决差异：

```text
ChatModelClientRouter：无匹配/重复匹配时抛 IllegalArgumentException
ChatModelClientRouterTest：期待 IllegalStateException
```

此前设计倾向 `IllegalStateException`，理由是协议枚举值本身合法，错误来自后端 Client 注册状态；但 Thomas 正确指出测试不应反过来盲目决定业务实现。下一会话应先确认业务契约，再统一两者，而不是单纯为了测试通过修改任意一边。

验证状态：

- `./mvnw -DskipTests package` 成功，生产代码和测试代码均完整编译，JAR 打包成功。
- `git diff --check` 成功。
- Codex 运行环境执行 `./mvnw clean test` 时，Mockito/Byte Buddy 无法 self-attach 到当前 JVM，导致依赖 mock 的测试报 `Could not self-attach to current VM`。这是运行环境的动态代理限制，不是本轮构造参数或 Catalog 逻辑错误。
- Thomas 之前在自己的终端运行测试正常。新对话可以让 Thomas 再运行一次完整测试确认。

常用验证命令：

```bash
cd backend
./mvnw test
```

完整验证：

```bash
./build.sh
```

模块验证：

```bash
cd backend && ./mvnw test && ./mvnw package
cd frontend && npm run lint && npm run build
```

## 当前调用链

```text
React UI
    ↓
ConversationController
    ↓
ConversationService
    ├── ModelCatalog：创建 conversation 时验证模型
    ├── ConversationStore：保存/查询 conversation 和 message
    └── ChatModelClient：发送通用消息
            ↓
       DeepSeekClient
            ↓
       DeepSeek API
```

目前 `ConversationService` 仍直接注入一个 `ChatModelClient`。加入第二个 Client Bean 后会产生注入歧义，所以最终仍然需要 Router；只是 Router 应当按 `ChatProtocol`，而不是按厂商品牌选择实现。

## 推荐演化路线

### 阶段1：协议与路由基础

1. 已完成：将 `ChatModelClient.provider()` 改为 `protocol()`。
2. 已完成：引入 `ChatProtocol.OPENAI_CHAT_COMPLETIONS`。
3. 已完成：保留现有 Conversation 的 `ModelProvider` 和数据库 `provider` 字段；它们仍表示用户选择的服务，不与协议字段互相替代。
4. 已完成：让 `ChatModelDefinition` 保存协议，并通过 `ModelCatalog.getModel()` 查找完整定义。
5. 已完成：建立按协议筛选 Client 的 Router 业务逻辑和三场景纯单元测试。
6. 待完成：确认 Router 配置错误的异常契约，统一业务代码和测试。
7. 待完成：把 Router 接入 `ConversationService`。
8. 第一版 Router 只需要处理 `OPENAI_CHAT_COMPLETIONS`。
9. 后续逐步让 DeepSeek 和 Ollama 共用通用的 OpenAI-compatible Client。
10. 保持现有 DeepSeek 功能和测试持续通过。

### 阶段2：上下文与助手人设

1. 创建 `ContextAssembler`。
2. 创建 `AssistantProfile` 表、Entity、Repository、Service 和 API。
3. 保存名字、性格、说话方式、语言和头像信息。
4. 每次调用模型时将人设加入 system context。
5. 增加前端人设设置页面。

### 阶段3：用户资料与长期记忆

1. 创建 `UserProfile`。
2. 创建 `MemoryItem`。
3. 完成记忆的查看、添加、修改和删除。
4. 第一版只保存用户明确要求记住的内容。
5. `ContextAssembler` 将用户资料与 active memories 加入请求。

### 阶段4：动态 Provider/API 配置

1. 创建 `ProviderConfig` 与 `ModelConfig`。
2. 支持用户配置显示名称、协议、Base URL、API key 和 modelId。
3. API key 只在后端处理并加密存储。
4. 前端永远不能读取保存后的完整 key。
5. 增加测试连接、修改、禁用和删除配置。
6. Conversation 最终应引用 `modelConfigId`，而不是只保存静态枚举和 modelId。

### 阶段5：文件与图片

1. 创建 `FileAsset` 和 `MessageAttachment`。
2. 实现安全的本地文件存储抽象。
3. 支持上传、预览、下载和删除。
4. 为模型增加 `TEXT`、`IMAGE`、`DOCUMENT`、`STREAMING` 等能力描述。
5. 当前模型不支持附件类型时，在调用前返回清晰错误。
6. 先支持图片、TXT、Markdown 和 PDF；长文档检索以后再做。

### 阶段6：体验完善

1. SSE 流式输出。
2. 停止生成与失败状态。
3. 重新生成回答。
4. 对话搜索。
5. 个人数据导出、备份与彻底删除。
6. 显示本次数据将发送到哪个云端 Provider。

### 延后：Agent 能力

近期不实现：

- Shell 执行。
- 邮件与日历。
- 自动操作电脑。
- 定时任务。
- 多 Agent。
- 无确认的外部操作。

以后如果需要，再引入 `AgentRunner`、`ToolRegistry`、`ToolExecution` 和用户审批机制。

## 新对话的下一步

先解决 Router 异常契约的未完成差异。讨论并确认：当传入合法的 `ChatProtocol`，但系统注册了零个或多个匹配 Client 时，这是调用参数错误还是应用组装状态错误。当前设计更倾向 `IllegalStateException`，但应先由 Thomas 作出设计判断并解释理由，然后同步业务代码和测试。

确认后，运行纯单元测试：

```bash
cd backend
./mvnw -Dtest=ChatModelClientRouterTest test
```

Router 测试通过后，下一项架构练习是：由 Thomas 设计如何将 Router 接入 `ConversationService`。不要先给完整业务代码。需求链条是：

```text
provider + modelId
        ↓ ModelCatalog.getModel(...)
ChatModelDefinition
        ↓ definition.protocol()
ChatProtocol
        ↓ Router 从 List<ChatModelClient> 中选择
ChatModelClient
```

设计约束：

1. `ConversationService` 不再直接依赖某个单独的 `ChatModelClient`。
2. 从 conversation 读取 `provider` 和 `modelId`。
3. 使用 `ModelCatalog.getModel(provider, modelId)` 得到 `ChatModelDefinition`。
4. 使用 definition 的 `protocol` 让 Router 选择 Client。
5. 使用选中的 Client 调用 `chat(modelId, messages)`。
6. 暂时不要重写 `DeepSeekClient`，不要同时引入动态 ProviderConfig。

一次只修改一个概念，并在每个小步骤后运行测试。不要在这一小步同时重写 `DeepSeekClient` 为通用 OpenAI-compatible Client。
