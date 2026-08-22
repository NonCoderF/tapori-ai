import 'package:razorpay_flutter/razorpay_flutter.dart';

import '../../../../core/config/app_config.dart';
import '../../domain/services/payment_service.dart';

class RazorpayPaymentService implements PaymentService {
  RazorpayPaymentService() : _razorpay = Razorpay();

  final Razorpay _razorpay;

  @override
  Future<void> startPayment({
    required int amount,
    required int credits,
    required void Function() onSuccess,
    required void Function(String message) onFailure,
  }) async {
    _razorpay
      ..clear()
      ..on(Razorpay.EVENT_PAYMENT_SUCCESS, (_) => onSuccess())
      ..on(
        Razorpay.EVENT_PAYMENT_ERROR,
        (PaymentFailureResponse response) {
          onFailure(response.message ?? 'Payment failed');
        },
      );

    _razorpay.open({
      'key': AppConfig.razorpayKeyId,
      'amount': amount * 100,
      'name': 'Spark Studios',
      'description': 'Payment for you Tapori AI',
      'currency': 'INR',
      'prefill': {
        'email': 'sallyinfo365@gmail.com',
        'contact': '+917002601418',
      },
      'notes': {'credits': credits},
    });
  }

  @override
  void dispose() {
    _razorpay.clear();
  }
}
