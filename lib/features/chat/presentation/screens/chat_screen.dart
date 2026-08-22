import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import '../../../../app/router/app_routes.dart';
import '../../../../core/error/failure.dart';
import '../providers/chat_providers.dart';
import '../providers/chat_state.dart';
import '../widgets/chat_bubble.dart';
import '../widgets/chat_input_bar.dart';

class ChatScreen extends ConsumerWidget {
  const ChatScreen({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final state = ref.watch(chatNotifierProvider);
    final notifier = ref.read(chatNotifierProvider.notifier);

    ref.listen<ChatState>(chatNotifierProvider, (previous, next) {
      if (next.navigateToSignIn) {
        context.go(AppRoutes.signIn);
      }
    });

    if (state.isLoading) {
      return const Scaffold(
        body: Center(
          child: Padding(
            padding: EdgeInsets.all(16),
            child: Text(
              'Oye, chat ka jugaad ho raha hai re, thoda time de!',
              textAlign: TextAlign.center,
              style: TextStyle(fontSize: 24, fontWeight: FontWeight.w700),
            ),
          ),
        ),
      );
    }

    final loadingFailure = state.loadingFailure;
    if (loadingFailure != null) {
      return Scaffold(
        body: Center(
          child: Padding(
            padding: const EdgeInsets.all(16),
            child: Column(
              mainAxisSize: MainAxisSize.min,
              children: [
                Text(
                  loadingFailure.message,
                  textAlign: TextAlign.center,
                  style: const TextStyle(
                      fontSize: 24, fontWeight: FontWeight.w700),
                ),
                const SizedBox(height: 16),
                OutlinedButton(
                  onPressed: loadingFailure is UnauthorizedFailure
                      ? notifier.logoutUser
                      : notifier.loadChats,
                  child: Text(
                    loadingFailure is UnauthorizedFailure
                        ? 'Login Again'
                        : 'Chal Dobara!',
                  ),
                ),
              ],
            ),
          ),
        ),
      );
    }

    return PopScope(
      canPop: false,
      onPopInvokedWithResult: (didPop, result) async {
        if (didPop) {
          return;
        }
        final close = await _confirm(
          context,
          title: 'Oye item?',
          message: 'Sach mein nikalna hai kya?',
        );
        if (close && context.mounted) {
          Navigator.of(context).maybePop();
        }
      },
      child: Scaffold(
        appBar: AppBar(
          backgroundColor: Colors.white,
          title: Text('Kya re item,\n${state.userName}!'),
          actions: [
            IconButton(
              icon: const Icon(Icons.logout),
              onPressed: () async {
                final logout = await _confirm(
                  context,
                  title: 'Oye item?',
                  message: 'Sach mein logout kar raha hain kya?',
                );
                if (logout) {
                  await notifier.logoutUser();
                }
              },
            ),
          ],
        ),
        body: ColoredBox(
          color: Colors.white,
          child: ListView.builder(
            reverse: true,
            padding: const EdgeInsets.symmetric(vertical: 8),
            itemCount: state.messages.length + _paymentPromptCount(state),
            itemBuilder: (context, index) {
              if (state.chatFailure is InsufficientCreditsFailure &&
                  index == 0) {
                return Align(
                  alignment: Alignment.centerLeft,
                  child: Padding(
                    padding:
                        const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
                    child: OutlinedButton(
                      onPressed: () {
                        notifier.clearChatFailure();
                        context.push(AppRoutes.payments);
                      },
                      child: const Text('Paisa dalo'),
                    ),
                  ),
                );
              }

              final messageIndex = state.messages.length -
                  1 -
                  (index - _paymentPromptCount(state));
              final message = state.messages[messageIndex];
              return ChatBubble(message: message);
            },
          ),
        ),
        bottomNavigationBar: SafeArea(
          child: ChatInputBar(
            message: state.inputText,
            isSending: state.isSending,
            onMessageChange: notifier.onInputTextChanged,
            onMicClick: () => _showUnsupportedVoiceToast(context),
            onSendClick: notifier.sendMessage,
          ),
        ),
      ),
    );
  }

  int _paymentPromptCount(ChatState state) {
    return state.chatFailure is InsufficientCreditsFailure ? 1 : 0;
  }

  Future<bool> _confirm(
    BuildContext context, {
    required String title,
    required String message,
  }) async {
    return await showDialog<bool>(
          context: context,
          builder: (context) => AlertDialog(
            title: Text(title),
            content: Text(message),
            actions: [
              TextButton(
                onPressed: () => Navigator.of(context).pop(false),
                child: const Text('Nahi re'),
              ),
              FilledButton(
                onPressed: () => Navigator.of(context).pop(true),
                child: const Text('Haan re'),
              ),
            ],
          ),
        ) ??
        false;
  }

  void _showUnsupportedVoiceToast(BuildContext context) {
    ScaffoldMessenger.of(context).showSnackBar(
      const SnackBar(content: Text('Voice input available nahi hai')),
    );
  }
}
