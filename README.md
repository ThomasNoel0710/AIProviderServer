# CRS

CRS is a full-stack AI workspace that is growing from a persistent chat MVP
into one place to use multiple AI providers and models.

The end goal is simple: configure the providers you use, choose an appropriate
model for a conversation, and send text, images, and files without changing
applications. The platform will support DeepSeek, OpenAI models, and Anthropic
Claude first, while keeping the provider-specific details behind a shared
application interface.

## Product Direction

CRS is intended to provide:

- One conversation UI for multiple AI providers and models.
- Per-conversation model selection, so an existing conversation retains the
  model used to create it.
- Text chat, image input, and file attachments.
- Model-aware capabilities: the UI and backend should only offer image,
  document, streaming, or other features when the selected model supports
  them.
- Persistent conversation history, with a clear path to authenticated,
  multi-user use later.

CRS is not intended to imitate every feature of any provider's consumer chat
product. Its focus is a clean, transparent workspace for switching between
models and working with the same conversations and attachments.

## Current MVP

The current implementation is a local, persistent, multi-turn chat
application backed by DeepSeek.

Implemented:

- DeepSeek text chat integration.
- H2-backed conversation and message persistence managed by Flyway.
- Create, list, open, rename, and delete conversations.
- Ordered multi-turn history sent to DeepSeek for each new message.
- Automatic titles from a conversation's first message.
- Deletion confirmation, `404` handling for missing conversations, and
  database-level cascading deletion of their messages.
- Backend tests and frontend production build verification.

Not implemented yet:

- Multiple providers and model selection.
- Attachments, image input, and document processing.
- Streaming responses.
- User accounts, ownership, and production deployment.

## Target Architecture

The implementation will evolve from its current direct dependency on
`DeepSeekClient` to a provider-neutral layer:

```text
ConversationService
        |
        v
ChatModelClient (shared interface)
   |           |            |
   v           v            v
DeepSeek     OpenAI      Anthropic
```

Each conversation will persist its selected provider and model ID. A model
capability record will describe whether that model supports text, images,
documents, attachments, or streaming.

Attachments will be represented as first-class records associated with a
message, rather than being embedded in the text content:

```text
Conversation
  └── Message
        ├── text content
        └── attachments (zero or more)
              ├── file name
              ├── MIME type
              ├── size
              └── storage location
```

This keeps file validation, storage, provider request conversion, and future
access control separate from the chat text itself.

## Roadmap

### 1. Multi-model foundation

- Introduce a shared `ChatModelClient` abstraction.
- Keep DeepSeek as the first implementation behind that abstraction.
- Persist `provider` and `modelId` on conversations.
- Turn the current model display into a real model picker.
- Define model capabilities and validate them on the backend.

### 2. Additional providers

- Add OpenAI API support.
- Add Anthropic Claude API support.
- Keep credentials in environment variables and provider-specific code inside
  its own client implementation.
- Present provider errors consistently to the frontend.

### 3. Attachments and multimodal messages

- Add attachment and message-attachment data models.
- Add secure upload endpoints with file-size and MIME-type validation.
- Start with image upload, preview, and image-capability checks.
- Add PDF, text, and Office-document handling with explicit extraction or
  provider-upload strategies.
- Use local storage for development, with an object-storage abstraction for
  deployment.

### 4. Chat experience

- Add streaming responses with SSE.
- Support cancellation and partial assistant messages safely.
- Add conversation search as a convenience feature.
- Add application settings such as model defaults and system prompts.

### 5. Multi-user and production readiness

- Add authentication and conversation ownership.
- Add authorization for conversations and attachments.
- Move production deployments to PostgreSQL and managed file storage.
- Add rate limiting, logging, observability, backups, CI/CD, and deployment.

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

Add your DeepSeek API key to `backend/.env`. This file is ignored by Git.

```text
DEEPSEEK_API_KEY=
```

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

## Current Conversation API

```text
POST   /api/conversations
GET    /api/conversations
GET    /api/conversations/{conversationId}
PATCH  /api/conversations/{conversationId}
DELETE /api/conversations/{conversationId}
POST   /api/conversations/{conversationId}/messages
```

The original `POST /api/chat` endpoint remains available for stateless,
single-turn DeepSeek requests during the MVP phase.

## Verification

```bash
./build.sh
```

Or verify modules individually:

```bash
cd backend && ./mvnw test && ./mvnw package
cd frontend && npm run lint && npm run build
```
