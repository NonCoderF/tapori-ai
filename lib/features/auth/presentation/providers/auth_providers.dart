import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../session/presentation/providers/session_providers.dart';
import '../../data/services/google_auth_service.dart';
import '../../domain/services/auth_service.dart';

final authServiceProvider = Provider<AuthService>((ref) => GoogleAuthService());

final authNotifierProvider =
    NotifierProvider<AuthNotifier, bool>(AuthNotifier.new);

class AuthNotifier extends Notifier<bool> {
  @override
  bool build() => false;

  Future<bool> signIn() async {
    state = true;
    final user = await ref.read(authServiceProvider).signIn();
    if (user == null) {
      state = false;
      return false;
    }

    final sessionRepository = await ref.read(sessionRepositoryProvider.future);
    await sessionRepository.saveUser(
      userIdToken: user.idToken,
      userName: user.displayName,
    );
    ref.invalidate(hasSessionProvider);
    state = false;
    return true;
  }
}
