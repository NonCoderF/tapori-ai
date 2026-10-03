export class AppError extends Error {
  constructor(message: string, public readonly code: string) {
    super(message);
    this.name = 'AppError';
  }
}

export class UnauthorizedError extends AppError {
  constructor(message = 'Your session has expired.') {
    super(message, 'UNAUTHORIZED');
    this.name = 'UnauthorizedError';
  }
}

export class InsufficientCreditsError extends AppError {
  constructor(public readonly reply: string) {
    super(reply, 'INSUFFICIENT_CREDITS');
    this.name = 'InsufficientCreditsError';
  }
}

export class NetworkError extends AppError {
  constructor(message = 'Network unavailable.') {
    super(message, 'NETWORK');
    this.name = 'NetworkError';
  }
}

export class TimeoutError extends NetworkError {
  constructor() {
    super('Request timed out.');
    this.name = 'TimeoutError';
  }
}

export class ServerError extends AppError {
  constructor(message = 'The Tapori server returned an error.', public readonly status = 500) {
    super(message, 'SERVER');
    this.name = 'ServerError';
  }
}
