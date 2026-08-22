import '../../../../core/error/result.dart';
import '../../../session/domain/repositories/session_repository.dart';
import '../entities/chat_history.dart';
import '../repositories/tapori_repository.dart';

class LoadChatUseCase {
  const LoadChatUseCase({
    required TaporiRepository repository,
    required SessionRepository sessionRepository,
  })  : _repository = repository,
        _sessionRepository = sessionRepository;

  final TaporiRepository _repository;
  final SessionRepository _sessionRepository;

  Future<Result<ChatHistory>> call() async {
    final token = await _sessionRepository.getUserIdToken() ?? '';
    return _repository.downloadChat(idToken: token);
  }
}
