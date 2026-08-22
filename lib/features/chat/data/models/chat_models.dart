import '../../domain/entities/chat_history.dart';
import '../../domain/entities/chat_message.dart';
import '../../domain/entities/send_message_result.dart';

class ChatRequestModel {
  const ChatRequestModel({
    required this.idToken,
    required this.chatId,
    required this.prompt,
    required this.systemMessage,
    required this.maxContextMessages,
  });

  final String idToken;
  final String chatId;
  final String prompt;
  final String systemMessage;
  final int maxContextMessages;

  Map<String, Object?> toJson() {
    return {
      'idToken': idToken,
      'chat_id': chatId,
      'prompt': prompt,
      'system_message': systemMessage,
      'max_context_messages': maxContextMessages,
    };
  }
}

class ChatResponseModel {
  const ChatResponseModel({
    required this.chatId,
    required this.reply,
  });

  factory ChatResponseModel.fromJson(Map<String, Object?> json) {
    return ChatResponseModel(
      chatId: json['chat_id'] as String? ?? '',
      reply: json['reply'] as String? ?? 'No response',
    );
  }

  final String chatId;
  final String reply;

  SendMessageResult toDomain() {
    return SendMessageResult(
      chatId: chatId,
      reply: ChatMessage(
        id: DateTime.now().microsecondsSinceEpoch.toString(),
        text: reply,
        role: ChatRole.assistant,
        chatId: chatId,
      ),
    );
  }
}

class ChatDownloadRequestModel {
  const ChatDownloadRequestModel({required this.idToken});

  final String idToken;

  Map<String, Object?> toJson() => {'idToken': idToken};
}

class ChatDownloadResponseModel {
  const ChatDownloadResponseModel({
    required this.success,
    required this.chats,
  });

  factory ChatDownloadResponseModel.fromJson(Map<String, Object?> json) {
    final rawChats = json['chats'];
    return ChatDownloadResponseModel(
      success: json['success'] as bool? ?? false,
      chats: rawChats is List
          ? rawChats
              .whereType<Map>()
              .map((item) =>
                  ChatMessageModel.fromJson(Map<String, Object?>.from(item)))
              .toList()
          : const [],
    );
  }

  final bool success;
  final List<ChatMessageModel> chats;

  ChatHistory toDomain({required String fallbackChatId}) {
    final messages = chats.map((chat) => chat.toDomain()).toList();
    return ChatHistory(
      messages: messages,
      chatId: chats.isNotEmpty ? chats.last.chatId : fallbackChatId,
    );
  }
}

class ChatMessageModel {
  const ChatMessageModel({
    required this.id,
    required this.userId,
    required this.chatId,
    required this.role,
    required this.content,
    required this.createdAt,
  });

  factory ChatMessageModel.fromJson(Map<String, Object?> json) {
    return ChatMessageModel(
      id: json['id'] as String? ?? '',
      userId: json['user_id'] as String? ?? '',
      chatId: json['chat_id'] as String? ?? '',
      role: json['role'] as String? ?? '',
      content: json['content'] as String? ?? '',
      createdAt: json['created_at'] as String? ?? '',
    );
  }

  final String id;
  final String userId;
  final String chatId;
  final String role;
  final String content;
  final String createdAt;

  ChatMessage toDomain() {
    return ChatMessage(
      id: id,
      text: content,
      role: role == 'user' ? ChatRole.user : ChatRole.assistant,
      chatId: chatId,
      createdAt: DateTime.tryParse(createdAt),
    );
  }
}
