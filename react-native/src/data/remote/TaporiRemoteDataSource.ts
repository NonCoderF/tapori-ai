import {post} from '../../core/network/apiClient';
import {ChatDownloadResponse, ChatRequest, ChatResponse, CreditResponse} from '../../types/api';

export const taporiRemoteDataSource = {
  sendMessage: (request: ChatRequest) => post<ChatResponse>('chat', request),
  downloadChat: (idToken: string) => post<ChatDownloadResponse>('download', {idToken}),
  addCredits: (idToken: string, creditsToAdd: number) => post<CreditResponse>('add_credits', {idToken, creditsToAdd}),
};
