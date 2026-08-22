import 'package:shared_preferences/shared_preferences.dart';

import 'session_local_data_source.dart';

class SharedPreferencesSessionLocalDataSource
    implements SessionLocalDataSource {
  const SharedPreferencesSessionLocalDataSource(this._preferences);

  static const _userPrefsName = 'tapori_prefs';
  static const _chatPrefsName = 'tapori_ai_prefs';
  static const _userIdKey = '$_userPrefsName.user_id';
  static const _userNameKey = '$_userPrefsName.user_name';
  static const _chatIdKey = '$_chatPrefsName.chat_id';

  final SharedPreferences _preferences;

  @override
  Future<void> saveUser({
    required String userIdToken,
    required String? userName,
  }) async {
    await _preferences.setString(_userIdKey, userIdToken);
    if (userName == null) {
      await _preferences.remove(_userNameKey);
    } else {
      await _preferences.setString(_userNameKey, userName);
    }
  }

  @override
  Future<String?> getUserIdToken() async => _preferences.getString(_userIdKey);

  @override
  Future<String?> getUserName() async => _preferences.getString(_userNameKey);

  @override
  Future<void> clearUser() async {
    await _preferences.remove(_userIdKey);
    await _preferences.remove(_userNameKey);
    await _preferences.remove(_chatIdKey);
  }

  @override
  Future<void> saveChatId(String chatId) async {
    await _preferences.setString(_chatIdKey, chatId);
  }

  @override
  Future<String?> getChatId() async => _preferences.getString(_chatIdKey);
}
