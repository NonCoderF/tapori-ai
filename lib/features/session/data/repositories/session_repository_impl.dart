import '../../domain/repositories/session_repository.dart';
import '../datasources/session_local_data_source.dart';

class SessionRepositoryImpl implements SessionRepository {
  const SessionRepositoryImpl(this._localDataSource);

  final SessionLocalDataSource _localDataSource;

  @override
  Future<void> saveUser({
    required String userIdToken,
    required String? userName,
  }) {
    return _localDataSource.saveUser(
      userIdToken: userIdToken,
      userName: userName,
    );
  }

  @override
  Future<String?> getUserIdToken() => _localDataSource.getUserIdToken();

  @override
  Future<String?> getUserName() => _localDataSource.getUserName();

  @override
  Future<void> clearUser() => _localDataSource.clearUser();

  @override
  Future<void> saveChatId(String chatId) => _localDataSource.saveChatId(chatId);

  @override
  Future<String?> getChatId() => _localDataSource.getChatId();
}
