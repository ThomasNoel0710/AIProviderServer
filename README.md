# CRS

CRS is a full-stack project for learning how to build an AI chat service.

The current MVP provides a local, single-turn chat experience backed by the
DeepSeek API. Conversation persistence, multi-turn context, user accounts, and
streaming responses are not implemented yet.

## Technology Stack

- Frontend: React, Vite, TypeScript, and Material UI
- Backend: Java 21, Spring Boot, and Maven
- Communication: HTTP, with SSE planned for streaming responses

## Project Structure

```text
CRS/
├── frontend/   React single-page application
├── backend/    Spring Boot API service
├── build.sh    Full project build script
├── start.sh    Local startup script
└── README.md   Project overview
```

## Configuration

Create the local backend environment file before starting the application:

```bash
cd backend
cp .env.example .env
```

Add your DeepSeek API key to `backend/.env`. This file is ignored by Git.

## Build and Run

From the project root:

```bash
./build.sh
./start.sh
```

Then open `http://localhost:5173`.

The backend health endpoint is available at
`http://localhost:8080/api/health`.

## Planned Work

1. Consistent API error responses
2. Multi-turn conversation models and persistence
3. Real conversation history in the sidebar
4. SSE streaming responses
5. Model selection and application settings
6. Authentication, rate limiting, observability, and deployment
