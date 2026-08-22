import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../../core/error/failure.dart';
import '../../../../core/error/result.dart';
import '../../../../core/utils/id_generator.dart';
import '../../../session/presentation/providers/session_providers.dart';
import '../../domain/entities/chat_message.dart';
import 'chat_providers.dart';

class ChatState {
  const ChatState({
    this.messages = const [],
    this.inputText = '',
    this.isSending = false,
    this.isLoading = false,
    this.loadingFailure,
    this.chatFailure,
    this.userName = '',
    this.navigateToSignIn = false,
  });

  final List<ChatMessage> messages;
  final String inputText;
  final bool isSending;
  final bool isLoading;
  final Failure? loadingFailure;
  final Failure? chatFailure;
  final String userName;
  final bool navigateToSignIn;

  ChatState copyWith({
    List<ChatMessage>? messages,
    String? inputText,
    bool? isSending,
    bool? isLoading,
    Failure? loadingFailure,
    bool clearLoadingFailure = false,
    Failure? chatFailure,
    bool clearChatFailure = false,
    String? userName,
    bool? navigateToSignIn,
  }) {
    return ChatState(
      messages: messages ?? this.messages,
      inputText: inputText ?? this.inputText,
      isSending: isSending ?? this.isSending,
      isLoading: isLoading ?? this.isLoading,
      loadingFailure:
          clearLoadingFailure ? null : loadingFailure ?? this.loadingFailure,
      chatFailure: clearChatFailure ? null : chatFailure ?? this.chatFailure,
      userName: userName ?? this.userName,
      navigateToSignIn: navigateToSignIn ?? this.navigateToSignIn,
    );
  }
}

class ChatNotifier extends Notifier<ChatState> {
  @override
  ChatState build() {
    Future.microtask(_initialize);
    return const ChatState(isLoading: true);
  }

  Future<void> _initialize() async {
    final sessionRepository = await ref.read(sessionRepositoryProvider.future);
    final userName = await sessionRepository.getUserName() ?? '';
    state = state.copyWith(userName: userName);
    await loadChats();
  }

  void onInputTextChanged(String value) {
    state = state.copyWith(inputText: value);
  }

  void clearChatFailure() {
    state = state.copyWith(clearChatFailure: true);
  }

  Future<void> loadChats() async {
    state = state.copyWith(
      isLoading: true,
      clearLoadingFailure: true,
    );

    final useCase = await ref.read(loadChatUseCaseProvider.future);
    final result = await useCase();

    switch (result) {
      case Success(value: final history):
        state = state.copyWith(
          messages: history.messages,
          isLoading: false,
          clearLoadingFailure: true,
        );
      case FailureResult(failure: final failure):
        state = state.copyWith(
          isLoading: false,
          loadingFailure: failure,
        );
    }
  }

  Future<void> sendMessage() => _sendPrompt(state.inputText);

  Future<void> sendVoiceMessage(String prompt) => _sendPrompt(prompt);

  Future<void> _sendPrompt(String prompt) async {
    if (prompt.trim().isEmpty || state.isSending) {
      return;
    }

    final userMessage = ChatMessage(
      id: generateRandomString(),
      text: prompt,
      role: ChatRole.user,
    );

    state = state.copyWith(
      inputText: '',
      isSending: true,
      messages: [...state.messages, userMessage],
      clearChatFailure: true,
    );

    final useCase = await ref.read(sendMessageUseCaseProvider.future);
    final result = await useCase(prompt);

    switch (result) {
      case Success(value: final value):
        state = state.copyWith(
          messages: [...state.messages, value.reply],
          isSending: false,
        );
      case FailureResult(failure: final failure):
        final shouldAppendSystemMessage = failure is InsufficientCreditsFailure;
        state = state.copyWith(
          messages: shouldAppendSystemMessage
              ? [
                  ...state.messages,
                  ChatMessage(
                    id: generateRandomString(),
                    text: failure.message,
                    role: ChatRole.assistant,
                  ),
                ]
              : state.messages,
          chatFailure: failure,
          isSending: false,
        );
    }
  }

  Future<void> logoutUser() async {
    final sessionRepository = await ref.read(sessionRepositoryProvider.future);
    await sessionRepository.clearUser();
    state = state.copyWith(navigateToSignIn: true);
  }
}
