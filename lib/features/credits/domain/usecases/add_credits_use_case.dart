import '../../../../core/error/result.dart';
import '../../../chat/domain/repositories/tapori_repository.dart';
import '../../../session/domain/repositories/session_repository.dart';
import '../entities/credit_balance.dart';

class AddCreditsUseCase {
  const AddCreditsUseCase({
    required TaporiRepository repository,
    required SessionRepository sessionRepository,
  })  : _repository = repository,
        _sessionRepository = sessionRepository;

  final TaporiRepository _repository;
  final SessionRepository _sessionRepository;

  Future<Result<CreditBalance>> call(int creditsToAdd) async {
    final token = await _sessionRepository.getUserIdToken() ?? '';
    return _repository.addCredit(idToken: token, creditsToAdd: creditsToAdd);
  }
}
