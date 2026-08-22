class AuthUser {
  const AuthUser({
    required this.idToken,
    required this.displayName,
  });

  final String idToken;
  final String? displayName;
}

abstract interface class AuthService {
  Future<AuthUser?> signIn();

  Future<AuthUser?> refreshToken();

  Future<void> logout();
}
