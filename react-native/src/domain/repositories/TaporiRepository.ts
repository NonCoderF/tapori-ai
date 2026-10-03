import {ChatDownloadResponse, ChatRequest, ChatResponse, CreditResponse} from '../../types/api';

export interface TaporiRepository {
  sendMessage(request: ChatRequest): Promise<ChatResponse>;
  downloadChat(idToken: string): Promise<ChatDownloadResponse>;
  addCredits(idToken: string, creditsToAdd: number): Promise<CreditResponse>;
}
