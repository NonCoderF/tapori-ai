import {InsufficientCreditsError} from '../errors/AppError';
import {post} from './apiClient';

test('maps the Tapori HTTP 402 contract to a domain error', async () => {
  globalThis.fetch = jest.fn().mockResolvedValue({ok: false, status: 402, text: async () => JSON.stringify({reply: 'Credit khatam'})}) as jest.Mock;
  await expect(post('chat', {})).rejects.toBeInstanceOf(InsufficientCreditsError);
  await expect(post('chat', {})).rejects.toMatchObject({reply: 'Credit khatam'});
});
