import {SYSTEM_MESSAGE} from '../../services/config';
import {ChatResponse} from '../../types/api';
import {TaporiRepository} from '../repositories/TaporiRepository';

export function sendMessageUseCase(repository: TaporiRepository, token: string, chatId: string, prompt: string): Promise<ChatResponse> {
  return repository.sendMessage({idToken: token, chat_id: chatId, prompt, system_message: SYSTEM_MESSAGE, max_context_messages: 50});
}
