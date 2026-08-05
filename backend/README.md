# CRS Backend

Backend module built with Java 21, Spring Boot, and Maven.

## Development

```bash
./mvnw spring-boot:run
```

Default address: `http://localhost:18080`

Health check: `GET http://localhost:18080/api/health`

Persistent multi-turn conversations use:

```text
POST /api/conversations
GET  /api/conversations
GET  /api/conversations/{conversationId}
PATCH /api/conversations/{conversationId}
DELETE /api/conversations/{conversationId}
POST /api/conversations/{conversationId}/messages
```

The first message becomes the conversation title. Each subsequent request sends
the ordered conversation history to DeepSeek before saving the assistant
response. Deleting a conversation also deletes all of its messages.

## Database

The local application uses a file-backed H2 database at `backend/data` by
default. The directory is ignored by Git. Flyway owns the database schema, and
Hibernate validates that the Java entity mappings match it during startup.

Database settings can be overridden with:

```text
DATABASE_URL=
DATABASE_USERNAME=
DATABASE_PASSWORD=
```

## DeepSeek Configuration

Spring Boot reads the DeepSeek configuration from environment variables. For
local development, it also explicitly loads `backend/.env`, which is ignored
by Git.

Copy the example file when configuring the project for the first time, then add
your real API key locally:

```bash
cp .env.example .env
```

```text
DEEPSEEK_API_KEY=
DEEPSEEK_BASE_URL=https://api.deepseek.com
DEEPSEEK_MODEL=deepseek-v4-flash
DEEPSEEK_TIMEOUT=30s
```

System environment variables take precedence over `.env`, so deployments do
not need to include the local file.

## Verification

```bash
./mvnw test
./mvnw package
```

The real DeepSeek connection test is skipped during a normal test run. Run it
explicitly with:

```bash
RUN_DEEPSEEK_IT=true ./mvnw -Dtest=DeepSeekConnectionTest test
```
