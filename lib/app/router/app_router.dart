import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import '../../features/auth/presentation/screens/sign_in_screen.dart';
import '../../features/chat/presentation/screens/chat_screen.dart';
import '../../features/payments/presentation/screens/payment_screen.dart';
import '../../features/session/presentation/providers/session_providers.dart';
import 'app_routes.dart';

final appRouterProvider = Provider<GoRouter>((ref) {
  return GoRouter(
    initialLocation: AppRoutes.chat,
    redirect: (context, state) async {
      final hasSession = await ref.read(hasSessionProvider.future);
      final signingIn = state.matchedLocation == AppRoutes.signIn;

      if (!hasSession && !signingIn) {
        return AppRoutes.signIn;
      }
      if (hasSession && signingIn) {
        return AppRoutes.chat;
      }
      return null;
    },
    routes: [
      GoRoute(
        path: AppRoutes.signIn,
        builder: (context, state) => const SignInScreen(),
      ),
      GoRoute(
        path: AppRoutes.chat,
        builder: (context, state) => const ChatScreen(),
      ),
      GoRoute(
        path: AppRoutes.payments,
        builder: (context, state) => const PaymentScreen(),
      ),
    ],
  );
});
