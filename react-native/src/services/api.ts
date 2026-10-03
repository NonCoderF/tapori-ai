import { API_BASE_URL } from './config';
import { ChatDownloadResponse, ChatRequest, ChatResponse, CreditResponse } from '../types/api';

export class ApiError extends Error {
  constructor(
    public readonly status: number,
    message: string,
    public readonly body?: unknown,
  ) {
    super(message);
    this.name = 'ApiError';
  }
}
type RequestOptions = { signal?: AbortSignal; timeoutMs?: number };
async function request<T>(path: string, body: unknown, options: RequestOptions = {}): Promise<T> {
  const controller = new AbortController();
  const timeout = setTimeout(() => controller.abort(), options.timeoutMs ?? 15000);
  const signal = options.signal
    ? mergeSignals(options.signal, controller.signal)
    : controller.signal;
  try {
    const response = await fetch(`${API_BASE_URL}${path}`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(body),
      signal,
    });
    const raw = await response.text();
    let parsed: unknown;
    if (raw) {
      try {
        parsed = JSON.parse(raw) as unknown;
      } catch {
        parsed = raw;
      }
    }
    if (!response.ok) {
      const message =
        typeof parsed === 'object' && parsed !== null && 'error' in parsed
          ? String(parsed.error)
          : `Request failed (${response.status})`;
      throw new ApiError(response.status, message, parsed);
    }
    return parsed as T;
  } catch (error) {
    if (error instanceof ApiError) {
      throw error;
    }
    if (error instanceof Error && error.name === 'AbortError') {
      throw new ApiError(408, 'Request timed out');
    }
    throw new ApiError(0, 'Network unavailable');
  } finally {
    clearTimeout(timeout);
  }
}
function mergeSignals(first: AbortSignal, second: AbortSignal): AbortSignal {
  const controller = new AbortController();
  const abort = () => controller.abort();
  first.addEventListener('abort', abort, { once: true });
  second.addEventListener('abort', abort, { once: true });
  return controller.signal;
}
export const taporiApi = {
  sendMessage: (body: ChatRequest, options?: RequestOptions) =>
    request<ChatResponse>('chat', body, options),
  downloadChat: (idToken: string, options?: RequestOptions) =>
    request<ChatDownloadResponse>('download', { idToken }, options),
  addCredits: (idToken: string, creditsToAdd: number, options?: RequestOptions) =>
    request<CreditResponse>('add_credits', { idToken, creditsToAdd }, options),
};
