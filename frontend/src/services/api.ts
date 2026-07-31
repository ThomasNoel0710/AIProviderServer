const DEFAULT_API_BASE_URL = 'http://localhost:18080'

export const API_BASE_URL =
  (import.meta.env.VITE_API_BASE_URL ?? DEFAULT_API_BASE_URL).replace(/\/+$/, '')

export interface ConversationSummary {
  id: string
  title: string
  createdAt: string
  updatedAt: string
}

export interface ConversationMessage {
  id: string
  sequenceNumber: number
  role: 'user' | 'assistant'
  content: string
  createdAt: string
}

export interface ConversationDetail extends ConversationSummary {
  messages: ConversationMessage[]
}

export class ApiError extends Error {
  readonly status?: number

  constructor(message: string, status?: number, cause?: unknown) {
    super(message, { cause })
    this.name = 'ApiError'
    this.status = status
  }
}

function isRecord(value: unknown): value is Record<string, unknown> {
  return typeof value === 'object' && value !== null
}

function isConversationSummary(value: unknown): value is ConversationSummary {
  if (!isRecord(value)) {
    return false
  }

  return (
    typeof value.id === 'string'
    && typeof value.title === 'string'
    && typeof value.createdAt === 'string'
    && typeof value.updatedAt === 'string'
  )
}

function isConversationMessage(value: unknown): value is ConversationMessage {
  if (!isRecord(value)) {
    return false
  }

  return (
    typeof value.id === 'string'
    && typeof value.sequenceNumber === 'number'
    && (value.role === 'user' || value.role === 'assistant')
    && typeof value.content === 'string'
    && typeof value.createdAt === 'string'
  )
}

function isConversationDetail(value: unknown): value is ConversationDetail {
  if (!isConversationSummary(value)) {
    return false
  }

  const messages = (value as unknown as Record<string, unknown>).messages
  return Array.isArray(messages) && messages.every(isConversationMessage)
}

function getHttpErrorMessage(status: number) {
  if (status === 400) {
    return 'The message is invalid. Please check it and try again.'
  }

  if (status === 404) {
    return 'The conversation no longer exists.'
  }

  if (status >= 500) {
    return 'The AI service is temporarily unavailable. Please try again later.'
  }

  return `The request failed (HTTP ${status}).`
}

async function requestJson<T>(
  path: string,
  options: RequestInit,
  isValid: (value: unknown) => value is T,
): Promise<T> {
  let response: Response

  try {
    response = await fetch(`${API_BASE_URL}${path}`, options)
  } catch (error) {
    if (error instanceof DOMException && error.name === 'AbortError') {
      throw error
    }

    throw new ApiError(
      'Unable to connect to the backend. Make sure it is running.',
      undefined,
      error,
    )
  }

  if (!response.ok) {
    throw new ApiError(getHttpErrorMessage(response.status), response.status)
  }

  let responseBody: unknown

  try {
    responseBody = await response.json()
  } catch (error) {
    throw new ApiError(
      'The backend returned an unrecognized response.',
      response.status,
      error,
    )
  }

  if (!isValid(responseBody)) {
    throw new ApiError(
      'The backend response has an unexpected structure.',
      response.status,
    )
  }

  return responseBody
}

export async function deleteConversation(
  conversationId: string,
  signal?: AbortSignal,
): Promise<void> {
  let response: Response

  try {
    response = await fetch(
      `${API_BASE_URL}/api/conversations/${conversationId}`,
      {
        method: 'DELETE',
        signal,
      },
    )
  } catch (error) {
    if (error instanceof DOMException && error.name === 'AbortError') {
      throw error
    }

    throw new ApiError(
      'Unable to connect to the backend. Make sure it is running.',
      undefined,
      error,
    )
  }

  if (!response.ok) {
    throw new ApiError(getHttpErrorMessage(response.status), response.status)
  }
}

export function listConversations(
  signal?: AbortSignal,
): Promise<ConversationSummary[]> {
  return requestJson(
    '/api/conversations',
    { signal },
    (value): value is ConversationSummary[] => (
      Array.isArray(value) && value.every(isConversationSummary)
    ),
  )
}

export function createConversation(
  signal?: AbortSignal,
): Promise<ConversationSummary> {
  return requestJson(
    '/api/conversations',
    {
      method: 'POST',
      signal,
    },
    isConversationSummary,
  )
}

export function getConversation(
  conversationId: string,
  signal?: AbortSignal,
): Promise<ConversationDetail> {
  return requestJson(
    `/api/conversations/${conversationId}`,
    { signal },
    isConversationDetail,
  )
}

export function renameConversation(
  conversationId: string,
  title: string,
  signal?: AbortSignal,
): Promise<ConversationSummary> {
  return requestJson(
    `/api/conversations/${conversationId}`,
    {
      method: 'PATCH',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({ title }),
      signal,
    },
    isConversationSummary,
  )
}

export function sendConversationMessage(
  conversationId: string,
  message: string,
  signal?: AbortSignal,
): Promise<ConversationMessage> {
  return requestJson(
    `/api/conversations/${conversationId}/messages`,
    {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({ message }),
      signal,
    },
    isConversationMessage,
  )
}
