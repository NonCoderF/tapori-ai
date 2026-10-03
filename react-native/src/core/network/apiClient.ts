import {API_BASE_URL} from '../../services/config';
import {InsufficientCreditsError, NetworkError, ServerError, TimeoutError, UnauthorizedError} from '../errors/AppError';

export class ApiError extends Error {
  constructor(public readonly status: number, message: string, public readonly body?: unknown) {
    super(message);
    this.name = 'ApiError';
  }
}

type RequestOptions = {signal?: AbortSignal; timeoutMs?: number};

export async function post<T>(path: string, body: unknown, options: RequestOptions = {}): Promise<T> {
  const timeoutController = new AbortController();
  const timeout = setTimeout(() => timeoutController.abort(), options.timeoutMs ?? 15_000);
  const signal = mergeSignals(options.signal, timeoutController.signal);
  try {
    const response = await fetch(`${API_BASE_URL}${path}`, {
      method: 'POST',
      headers: {'Content-Type': 'application/json'},
      body: JSON.stringify(body),
      signal,
    });
    const raw = await response.text();
    let parsed: unknown;
    if (raw) {
      try { parsed = JSON.parse(raw) as unknown; } catch { parsed = raw; }
    }
    if (!response.ok) {
      const message = readErrorMessage(parsed, response.status);
      if (response.status === 401) throw new UnauthorizedError(message);
      if (response.status === 402) {
        const reply = readString(parsed, 'reply') ?? 'Credit khatam ho gaya re!';
        throw new InsufficientCreditsError(reply);
      }
      throw new ServerError(message, response.status);
    }
    return parsed as T;
  } catch (error) {
    if (error instanceof UnauthorizedError || error instanceof InsufficientCreditsError || error instanceof ServerError) throw error;
    if (error instanceof Error && error.name === 'AbortError') throw new TimeoutError();
    throw new NetworkError();
  } finally {
    clearTimeout(timeout);
  }
}

function mergeSignals(first: AbortSignal | undefined, second: AbortSignal): AbortSignal {
  if (!first) return second;
  const controller = new AbortController();
  const abort = () => controller.abort();
  first.addEventListener('abort', abort, {once: true});
  second.addEventListener('abort', abort, {once: true});
  return controller.signal;
}

function readString(value: unknown, key: string): string | undefined {
  if (typeof value !== 'object' || value === null) return undefined;
  const candidate = (value as Record<string, unknown>)[key];
  return typeof candidate === 'string' ? candidate : undefined;
}

function readErrorMessage(value: unknown, status: number): string {
  return readString(value, 'error') ?? readString(value, 'message') ?? `Request failed (${status})`;
}
