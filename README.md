# CRS

CRS is a private, modular AI chat workspace. The project is being built one
learning module at a time, with DeepSeek planned as the first model provider.

## Technology

- Next.js App Router: frontend pages and server API routes in one project
- TypeScript: explicit contracts between modules
- Material UI: accessible Material-style components and theme tokens
- DeepSeek API: planned server-side model provider

## Project structure

```text
src/
├── app/            Routes, layouts, and server endpoints
├── components/     Reusable interface components
├── server/         Server-only business logic and providers
└── theme/          Material design tokens and global theme setup
```

The browser must never call DeepSeek directly. Future chat requests will travel
from a browser component to a Next.js route handler, then to the server-only
DeepSeek provider.

## Learning modules

1. Foundation and Material-style application shell
2. Frontend chat state and message components
3. Server-side DeepSeek provider
4. Streaming responses
5. Conversation persistence
6. Authentication and usage controls

## Getting started

Install the dependencies and start the development server:

```bash
npm install
npm run dev
```

Open [http://localhost:3000](http://localhost:3000) in your browser. The health
endpoint is available at
[http://localhost:3000/api/health](http://localhost:3000/api/health).

## Useful checks

```bash
npm run lint
npm run build
```

## Environment safety

Never commit `.env.local` or place `DEEPSEEK_API_KEY` in browser-visible code.
Only variables prefixed with `NEXT_PUBLIC_` are intentionally exposed to the
browser, so the DeepSeek key must not use that prefix.
