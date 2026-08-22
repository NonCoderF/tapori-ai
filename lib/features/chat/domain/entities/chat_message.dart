enum ChatRole { user, assistant }

class ChatMessage {
  const ChatMessage({
    required this.id,
    required this.text,
    required this.role,
    this.chatId,
    this.createdAt,
  });

  final String id;
  final String text;
  final ChatRole role;
  final String? chatId;
  final DateTime? createdAt;

  bool get isUser => role == ChatRole.user;
}
