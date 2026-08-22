abstract interface class PaymentService {
  Future<void> startPayment({
    required int amount,
    required int credits,
    required void Function() onSuccess,
    required void Function(String message) onFailure,
  });

  void dispose();
}
