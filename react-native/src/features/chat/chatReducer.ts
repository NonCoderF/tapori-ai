import {NetworkError, TimeoutError, UnauthorizedError} from '../../core/errors/AppError';
import {ApiError} from '../../core/network/apiClient';
import {ChatMessage} from '../../types/api';

export type ChatState = {messages: ChatMessage[]; input: string; loading: boolean; sending: boolean; error: string | null; paymentRequired: string | null; chatId: string | null};
export const initialChatState: ChatState = {messages: [], input: '', loading: false, sending: false, error: null, paymentRequired: null, chatId: null};
export type Action = {type: 'loadStart'} | {type: 'loadSuccess'; messages: ChatMessage[]; chatId: string} | {type: 'loadFailure'; error: string} | {type: 'input'; value: string} | {type: 'sendStart'; message: ChatMessage} | {type: 'sendSuccess'; message: ChatMessage} | {type: 'sendFailure'; error: string} | {type: 'paymentRequired'; message: ChatMessage; detail: string} | {type: 'clearPayment'};

export function chatReducer(state: ChatState, action: Action): ChatState {
  switch (action.type) {
    case 'loadStart': return {...state, loading: true, error: null};
    case 'loadSuccess': return {...state, loading: false, messages: action.messages, chatId: action.chatId, error: null};
    case 'loadFailure': return {...state, loading: false, error: action.error};
    case 'input': return {...state, input: action.value};
    case 'sendStart': return {...state, input: '', sending: true, messages: [...state.messages, action.message], error: null};
    case 'sendSuccess': return {...state, sending: false, messages: [...state.messages, action.message]};
    case 'sendFailure': return {...state, sending: false, error: action.error};
    case 'paymentRequired': return {...state, sending: false, messages: [...state.messages, action.message], paymentRequired: action.detail};
    case 'clearPayment': return {...state, paymentRequired: null};
  }
}

export function errorMessage(error: unknown): string {
  const status = error instanceof ApiError ? error.status : error instanceof Error && 'status' in error && typeof error.status === 'number' ? error.status : undefined;
  if (error instanceof UnauthorizedError || status === 401) return 'Arre bhai, token expire ho gaya!';
  if (error instanceof TimeoutError || status === 408) return 'Network slow hai bhidu, dobara try kar re.';
  if (error instanceof NetworkError) return 'Internet ka scene kharab hai bhidu, dobara try kar re.';
  return 'Arre bhai, chat pakad nahi paaye... dobara try kar re!';
}
