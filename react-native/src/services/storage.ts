import AsyncStorage from '@react-native-async-storage/async-storage';
import * as SecureStore from 'expo-secure-store';
const keys = {token: 'tapori.token', name: 'tapori.name', chatId: '@tapori/chat-id'} as const;
export const sessionStorage = {
  async read() { const [token, name, chatEntries] = await Promise.all([SecureStore.getItemAsync(keys.token), SecureStore.getItemAsync(keys.name), AsyncStorage.multiGet([keys.chatId])]); return {token, name, chatId: chatEntries[0]?.[1] ?? null}; },
  async save(token: string, name: string | null) { await Promise.all([SecureStore.setItemAsync(keys.token, token), SecureStore.setItemAsync(keys.name, name ?? '')]); },
  async saveChatId(chatId: string) { await AsyncStorage.setItem(keys.chatId, chatId); },
  async clear() { await Promise.all([SecureStore.deleteItemAsync(keys.token), SecureStore.deleteItemAsync(keys.name), AsyncStorage.multiRemove([keys.chatId])]); },
};
