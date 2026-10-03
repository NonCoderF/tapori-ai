import {TaporiRepository} from '../repositories/TaporiRepository';

export const loadChatUseCase = (repository: TaporiRepository, token: string) => repository.downloadChat(token);
