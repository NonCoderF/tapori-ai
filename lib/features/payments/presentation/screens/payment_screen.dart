import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../providers/payment_providers.dart';

class PaymentScreen extends ConsumerWidget {
  const PaymentScreen({super.key});

  static const _packs = [
    ('Cutting Chai Pack', 10, 100, 'Sasta aur tikau, bas shuruat ke liye.'),
    ('Full Bottle Pack', 50, 600, 'Thoda zyada, mast discount ke saath.'),
    (
      'Don Pack',
      250,
      1500,
      'Hardcore bhidus ke liye, ekdum unlimited jaisa feel.'
    ),
  ];

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final state = ref.watch(paymentNotifierProvider);
    final notifier = ref.read(paymentNotifierProvider.notifier);

    ref.listen<PaymentState>(paymentNotifierProvider, (previous, next) {
      if (next.showSuccessDialog && previous?.showSuccessDialog != true) {
        showDialog<void>(
          context: context,
          builder: (context) => AlertDialog(
            title: const Text('Payment Successful!'),
            content: Text(
                'Bhai, ab tere account mein ${next.newCredits} credits hain!'),
            actions: [
              TextButton(
                onPressed: () {
                  ref
                      .read(paymentNotifierProvider.notifier)
                      .dismissSuccessDialog();
                  Navigator.of(context).pop();
                },
                child: const Text('Theek hai bhidu'),
              ),
            ],
          ),
        );
      }
    });

    return Scaffold(
      appBar: AppBar(title: const Text('Kuch choose kar!')),
      body: Stack(
        children: [
          ListView.separated(
            padding: const EdgeInsets.all(16),
            itemCount: _packs.length,
            separatorBuilder: (_, __) => const SizedBox(height: 16),
            itemBuilder: (context, index) {
              final (title, price, chats, description) = _packs[index];
              return Card(
                color: Colors.white,
                elevation: 6,
                shape: RoundedRectangleBorder(
                    borderRadius: BorderRadius.circular(16)),
                child: ListTile(
                  title: Text(title,
                      style: const TextStyle(fontWeight: FontWeight.w600)),
                  subtitle: Text('Rs $price -> $chats chats\n$description'),
                  onTap: () {
                    notifier.setCreditsToAdd(chats);
                    ref.read(paymentServiceProvider).startPayment(
                          amount: price,
                          credits: chats,
                          onSuccess: notifier.handlePaymentSuccess,
                          onFailure: (message) {
                            ScaffoldMessenger.of(context).showSnackBar(
                              SnackBar(
                                  content: Text('Payment failed: $message')),
                            );
                          },
                        );
                  },
                ),
              );
            },
          ),
          if (state.showLoader) const _LoaderDialog(),
        ],
      ),
    );
  }
}

class _LoaderDialog extends StatelessWidget {
  const _LoaderDialog();

  @override
  Widget build(BuildContext context) {
    return const ColoredBox(
      color: Color(0x66000000),
      child: Center(
        child: Card(
          child: Padding(
            padding: EdgeInsets.all(24),
            child: Column(
              mainAxisSize: MainAxisSize.min,
              children: [
                CircularProgressIndicator(),
                SizedBox(height: 16),
                Text('Ruk zara bhidu credit update ho raha hain...'),
              ],
            ),
          ),
        ),
      ),
    );
  }
}
