import 'package:google_sign_in/google_sign_in.dart';

import '../../../../core/config/app_config.dart';
import '../../domain/services/auth_service.dart';

class GoogleAuthService implements AuthService {
  GoogleAuthService()
      : _googleSignIn = GoogleSignIn(
          scopes: const ['email'],
          clientId: AppConfig.googleClientId,
        );

  final GoogleSignIn _googleSignIn;

  @override
  Future<AuthUser?> signIn() async {
    final account = await _googleSignIn.signIn();
    return _toAuthUser(account);
  }

  @override
  Future<AuthUser?> refreshToken() async {
    final account = await _googleSignIn.signInSilently();
    return _toAuthUser(account);
  }

  @override
  Future<void> logout() async {
    await _googleSignIn.signOut();
    await _googleSignIn.disconnect();
  }

  Future<AuthUser?> _toAuthUser(GoogleSignInAccount? account) async {
    if (account == null) {
      return null;
    }
    final auth = await account.authentication;
    final idToken = auth.idToken;
    if (idToken == null || idToken.isEmpty) {
      return null;
    }
    return AuthUser(idToken: idToken, displayName: account.displayName);
  }
}
