import 'chat_message.dart';

class SendMessageResult {
  const SendMessageResult({
    required this.chatId,
    required this.reply,
  });

  final String chatId;
  final ChatMessage reply;
}
