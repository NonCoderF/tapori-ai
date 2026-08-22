abstract interface class SessionRepository {
  Future<void> saveUser({
    required String userIdToken,
    required String? userName,
  });

  Future<String?> getUserIdToken();

  Future<String?> getUserName();

  Future<void> clearUser();

  Future<void> saveChatId(String chatId);

  Future<String?> getChatId();
}
