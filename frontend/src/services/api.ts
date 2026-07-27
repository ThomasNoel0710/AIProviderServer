const DEFAULT_API_BASE_URL = 'http://localhost:8080'

export const API_BASE_URL =
  (import.meta.env.VITE_API_BASE_URL ?? DEFAULT_API_BASE_URL).replace(/\/+$/, '')

export interface ChatRequest {
  message: string
}

export interface ChatResponse {
  message: string
}

export class ApiError extends Error {
  readonly status?: number

  constructor(message: string, status?: number, cause?: unknown) {
    super(message, { cause })
    this.name = 'ApiError'
    this.status = status
  }
}

function isChatResponse(value: unknown): value is ChatResponse {
  if (typeof value !== 'object' || value === null) {
    return false
  }

  const message = (value as Record<string, unknown>).message
  return typeof message === 'string' && message.trim().length > 0
}

function getHttpErrorMessage(status: number) {
  if (status === 400) {
    return '消息内容无效，请检查后重试。'
  }

  if (status >= 500) {
    return 'AI 服务暂时不可用，请稍后重试。'
  }

  return `请求失败（HTTP ${status}）。`
}

export async function sendChatMessage(
  message: string,
  signal?: AbortSignal,
): Promise<ChatResponse> {
  const request: ChatRequest = { message }
  let response: Response

  try {
    response = await fetch(`${API_BASE_URL}/api/chat`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(request),
      signal,
    })
  } catch (error) {
    if (error instanceof DOMException && error.name === 'AbortError') {
      throw error
    }

    throw new ApiError('无法连接后端服务，请确认后端已经启动。', undefined, error)
  }

  if (!response.ok) {
    throw new ApiError(getHttpErrorMessage(response.status), response.status)
  }

  let responseBody: unknown

  try {
    responseBody = await response.json()
  } catch (error) {
    throw new ApiError(
      '后端返回了无法识别的数据。',
      response.status,
      error,
    )
  }

  if (!isChatResponse(responseBody)) {
    throw new ApiError(
      '后端响应中缺少有效的 AI 回答。',
      response.status,
    )
  }

  return responseBody
}
