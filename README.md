# CRS

CRS is a full-stack, persistent virtual companion application. It is evolving
from a DeepSeek-backed chat MVP into a personal assistant whose identity,
personality, knowledge about the user, and conversation history belong to CRS
rather than to any individual model provider.

The long-term goal is for a user to configure a companion's name, personality,
speaking style, and avatar; let that companion remember approved personal
information across conversations; connect the model services the user already
has; and communicate through text, images, and files.

## Product Direction

CRS is intended to provide:

- A persistent companion identity, including name, personality, speaking style,
  default language, and avatar.
- A user profile and user-controlled long-term memory that survive application
  restarts and new conversations.
- Persistent multi-turn conversations with a consistent companion even when the
  selected model changes.
- User-configured model providers, endpoints, credentials, and model IDs.
- Provider-neutral model routing so DeepSeek, Ollama, OpenAI-compatible APIs,
  and future providers can sit behind one application interface.
- Text chat, image input, and file attachments with model-capability checks.
- Clear controls for viewing, editing, and deleting personal information,
  memories, conversations, attachments, and provider settings.

The companion's identity and memory are application data. A model is an
inference engine used to generate a response; switching providers must not
replace the companion or erase what CRS has saved about the user.

Agent-style execution is not part of the near-term scope. Tool calling,
scheduling, external integrations, and autonomous actions may be added later,
after identity, memory, provider configuration, and multimodal conversation are
stable.

## Current MVP

The current implementation is a local, persistent, multi-turn chat application
backed by DeepSeek.

Implemented:

- DeepSeek text chat integration.
- A provider-neutral `ChatModelClient` interface, with `DeepSeekClient` as its
  first implementation.
- A model catalog exposed through `GET /api/models`.
- Per-conversation provider and model selection.
- A frontend model picker populated from the backend catalog.
- H2-backed conversation and message persistence managed by Flyway.
- Create, list, open, rename, and delete conversations.
- Ordered multi-turn history sent to the selected model for each new message.
- Automatic titles generated from a conversation's first message.
- Deletion confirmation, frontend error handling, `404` handling for missing
  conversations, and database-level cascading deletion of messages.
- Backend tests and frontend lint/production-build verification.

Not implemented yet:

- Routing between multiple `ChatModelClient` implementations.
- A persistent assistant profile or user profile.
- Long-term memories shared across conversations.
- User-managed provider configurations and encrypted API credentials.
- Ollama or other additional provider clients.
- Attachments, image input, and document processing.
- Streaming responses and cancellation.
- Agent tools, scheduled actions, or external-service automation.
- Authentication, multi-user ownership, and production deployment.

## Architecture

The current chat path is:

```text
React UI
   |
   v
ConversationController
   |
   v
ConversationService
   |---------------------> ConversationStore -----> JPA / H2
   |
   v
ChatModelClient
   |
   v
DeepSeekClient -----> DeepSeek API
```

The target companion architecture is:

```text
ConversationService
        |
        v
AssistantOrchestrator
        |
        v
ContextAssembler
   |         |           |
   v         v           v
Persona   User Profile   Long-term Memory
        |
        v
ChatModelClientRouter
   |              |                 |
   v              v                 v
DeepSeekClient  OllamaClient  OpenAICompatibleClient
```

For each message, `ContextAssembler` will combine the companion persona, user
profile, relevant approved memories, conversation history, and supported
attachments. `ChatModelClientRouter` will then select the correct client for
the conversation's configured model.

Provider-specific request and response formats must remain inside their client
implementations. Conversation, profile, memory, and attachment APIs must not
expose provider DTOs.

## Planned Data Model

```text
AssistantProfile
├── name
├── personality
├── speaking style
├── language
└── avatar

UserProfile
├── display name
├── language and timezone
├── biography
└── current goals

MemoryItem
├── category
├── content
├── source conversation/message
└── active status

Conversation
├── assistant profile
├── selected model configuration
└── messages
    └── attachments

ProviderConfig
├── adapter type
├── base URL
├── encrypted credential
└── model configurations
```

The first memory implementation will be explicit and user-controlled. Users
will be able to add, inspect, edit, and delete memories. Automatic memory
creation will not be introduced until a review or confirmation flow exists.

## Roadmap

### 1. Complete the model-routing foundation

- Let each `ChatModelClient` identify the provider protocol it supports.
- Add a `ChatModelClientRouter` that selects a client by provider.
- Route messages using both the provider and model stored on the conversation.
- Add routing tests while preserving the existing DeepSeek behavior.
- Add explicit model capabilities such as text, image, document, streaming,
  tool calling, and embeddings.

### 2. Add persistent companion identity

- Add an `AssistantProfile` entity and Flyway migration.
- Store the companion's name, personality, speaking style, language, prompt,
  and avatar metadata.
- Add profile read/update APIs and a frontend settings view.
- Introduce a `ContextAssembler` and include the active persona in every model
  request.

### 3. Add the user profile and long-term memory

- Add persistent `UserProfile` and `MemoryItem` entities.
- Provide APIs and UI for viewing, adding, editing, and deleting memories.
- Begin with memories explicitly saved by the user.
- Include relevant active memories when assembling model context.
- Later allow the companion to propose a memory that the user can approve or
  reject.

### 4. Let users configure model services

- Replace the static-only catalog with persisted provider and model
  configurations while retaining built-in defaults where useful.
- Support custom display names, base URLs, model IDs, and capability settings.
- Encrypt API credentials at rest and never return raw credentials to the
  frontend or logs.
- Add connection testing and clear provider error reporting.
- Add Ollama for local models and an OpenAI-compatible adapter for compatible
  third-party or self-hosted endpoints.

### 5. Add attachments and multimodal messages

- Add file and message-attachment records.
- Add a storage abstraction with local storage for development.
- Validate file size, MIME type, file names, and model capabilities.
- Support image upload and preview before adding vision-model request
  conversion.
- Start document support with text, Markdown, and PDF extraction.
- Consider reusable knowledge bases and retrieval only after basic attachments
  are stable.

### 6. Improve the conversation experience

- Add streaming responses with SSE.
- Support cancellation, failed generations, and partial assistant messages.
- Add conversation search and regeneration.
- Show which provider receives a request and whether attachments or personal
  context will leave the local machine.
- Add data export, complete deletion, backup, and recovery controls.

### 7. Optional future agent capabilities

- Introduce structured model results and tool calls.
- Add a permission-aware tool registry and execution audit trail.
- Begin with internal tools such as creating notes or tasks.
- Require confirmation for external side effects.
- Consider reminders, scheduling, messaging channels, and other integrations
  only after the companion foundation is secure and reliable.

## Technology Stack

- Frontend: React, Vite, TypeScript, and Material UI
- Backend: Java 21, Spring Boot, Spring Data JPA, Flyway, and Maven
- Local database: H2
- Communication: HTTP today; SSE is planned for streaming

## Project Structure

```text
CRS/
├── frontend/   React single-page application
├── backend/    Spring Boot API service
├── build.sh    Full project build script
├── start.sh    Local startup script
└── README.md   Project overview and roadmap
```

## Configuration

Create a local backend environment file before starting the application:

```bash
cd backend
cp .env.example .env
```

Add a DeepSeek API key to `backend/.env`. This file is ignored by Git.

```text
DEEPSEEK_API_KEY=
```

The current MVP reads provider credentials from backend environment
configuration. User-managed, encrypted provider credentials are planned but
not implemented yet.

The frontend defaults to `http://localhost:18080`. To use another backend
address, set `VITE_API_BASE_URL` in the frontend environment.

## Build and Run

From the project root:

```bash
./build.sh
./start.sh
```

Open `http://localhost:5173`.

The backend health endpoint is available at
`http://localhost:18080/api/health`.

## Current API

```text
GET    /api/models
POST   /api/conversations
GET    /api/conversations
GET    /api/conversations/{conversationId}
PATCH  /api/conversations/{conversationId}
DELETE /api/conversations/{conversationId}
POST   /api/conversations/{conversationId}/messages
```

## Verification

```bash
./build.sh
```

Or verify modules individually:

```bash
cd backend && ./mvnw test && ./mvnw package
cd frontend && npm run lint && npm run build
```
