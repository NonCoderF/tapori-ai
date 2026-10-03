import {CreditResponse} from '../../types/api';
import {TaporiRepository} from '../repositories/TaporiRepository';

export const addCreditsUseCase = (repository: TaporiRepository, token: string, credits: number): Promise<CreditResponse> => repository.addCredits(token, credits);
