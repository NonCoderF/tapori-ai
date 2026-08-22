import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../chat/presentation/providers/chat_providers.dart';
import '../../../credits/domain/usecases/add_credits_use_case.dart';
import '../../../session/presentation/providers/session_providers.dart';
import '../../data/services/razorpay_payment_service.dart';
import '../../domain/services/payment_service.dart';

final paymentServiceProvider = Provider<PaymentService>((ref) {
  final service = RazorpayPaymentService();
  ref.onDispose(service.dispose);
  return service;
});

final addCreditsUseCaseProvider =
    FutureProvider<AddCreditsUseCase>((ref) async {
  final repository = await ref.watch(taporiRepositoryProvider.future);
  final sessionRepository = await ref.watch(sessionRepositoryProvider.future);
  return AddCreditsUseCase(
    repository: repository,
    sessionRepository: sessionRepository,
  );
});

final paymentNotifierProvider = NotifierProvider<PaymentNotifier, PaymentState>(
  PaymentNotifier.new,
);

class PaymentState {
  const PaymentState({
    this.showLoader = false,
    this.showSuccessDialog = false,
    this.newCredits = 0,
    this.creditsToAdd = 0,
  });

  final bool showLoader;
  final bool showSuccessDialog;
  final int newCredits;
  final int creditsToAdd;

  PaymentState copyWith({
    bool? showLoader,
    bool? showSuccessDialog,
    int? newCredits,
    int? creditsToAdd,
  }) {
    return PaymentState(
      showLoader: showLoader ?? this.showLoader,
      showSuccessDialog: showSuccessDialog ?? this.showSuccessDialog,
      newCredits: newCredits ?? this.newCredits,
      creditsToAdd: creditsToAdd ?? this.creditsToAdd,
    );
  }
}

class PaymentNotifier extends Notifier<PaymentState> {
  @override
  PaymentState build() => const PaymentState();

  void setCreditsToAdd(int credits) {
    state = state.copyWith(creditsToAdd: credits);
  }

  Future<void> handlePaymentSuccess() async {
    state = state.copyWith(showLoader: true);
    final useCase = await ref.read(addCreditsUseCaseProvider.future);
    final result = await useCase(state.creditsToAdd);
    result.when(
      success: (balance) {
        state = state.copyWith(
          newCredits: balance.credits,
          showSuccessDialog: true,
          showLoader: false,
        );
      },
      failure: (_) {
        state = state.copyWith(showLoader: false);
      },
    );
  }

  void dismissSuccessDialog() {
    state = state.copyWith(showSuccessDialog: false);
  }
}
