import '../../../../core/error/result.dart';
import '../../../session/domain/repositories/session_repository.dart';
import '../entities/send_message_result.dart';
import '../repositories/tapori_repository.dart';

class SendMessageUseCase {
  const SendMessageUseCase({
    required TaporiRepository repository,
    required SessionRepository sessionRepository,
  })  : _repository = repository,
        _sessionRepository = sessionRepository;

  final TaporiRepository _repository;
  final SessionRepository _sessionRepository;

  Future<Result<SendMessageResult>> call(String prompt) async {
    final token = await _sessionRepository.getUserIdToken() ?? '';
    final chatId = await _sessionRepository.getChatId() ?? '';
    return _repository.sendMessage(
      idToken: token,
      chatId: chatId,
      prompt: prompt,
    );
  }
}
