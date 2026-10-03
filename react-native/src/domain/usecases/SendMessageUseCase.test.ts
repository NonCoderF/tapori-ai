import {sendMessageUseCase} from './SendMessageUseCase';

test('builds the exact native Tapori chat request contract', async () => {
  const repository = {sendMessage: jest.fn().mockResolvedValue({chat_id: 'c1', reply: 'Jhakaas'}), downloadChat: jest.fn(), addCredits: jest.fn()};
  await sendMessageUseCase(repository, 'token', 'chat-1', 'hello');
  expect(repository.sendMessage).toHaveBeenCalledWith(expect.objectContaining({idToken: 'token', chat_id: 'chat-1', prompt: 'hello', max_context_messages: 50}));
});
