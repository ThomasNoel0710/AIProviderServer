# CRS Frontend

Frontend module built with React, Vite, TypeScript, and Material UI.

## Development

```bash
npm install
npm run dev
```

Default address: `http://localhost:5173`

The application sends chat requests to `http://localhost:18080` by default. Set
`VITE_API_BASE_URL` to use a different backend address.

Conversation history is loaded from the backend. New conversations are created
when their first message is sent, and selecting an item in the sidebar reloads
its saved messages. Conversations can be renamed or deleted; deleting the
active conversation returns the UI to a new chat.

## Verification

```bash
npm run lint
npm run build
```
