export type ChatRequest = {idToken: string; chat_id: string; prompt: string; system_message: string; max_context_messages: number};
export type ChatResponse = {chat_id: string; reply: string};
export type ChatDownloadResponse = {success: boolean; chats: ChatMessageDto[]};
export type ChatMessageDto = {id: string; user_id: string; chat_id: string; role: string; content: string; created_at: string};
export type CreditResponse = {success?: boolean; message?: string; credits?: number};
export type ChatMessage = {id: string; text: string; isUser: boolean; createdAt?: string};
