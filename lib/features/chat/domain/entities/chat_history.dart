import 'chat_message.dart';

class ChatHistory {
  const ChatHistory({
    required this.messages,
    required this.chatId,
  });

  final List<ChatMessage> messages;
  final String chatId;
}
