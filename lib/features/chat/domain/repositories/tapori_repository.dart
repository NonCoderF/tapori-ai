import '../../../../core/error/result.dart';
import '../../../credits/domain/entities/credit_balance.dart';
import '../entities/chat_history.dart';
import '../entities/send_message_result.dart';

abstract interface class TaporiRepository {
  Future<Result<SendMessageResult>> sendMessage({
    required String idToken,
    required String chatId,
    required String prompt,
    String systemMessage =
        'You are a Mumbai Tapori assistant. Reply in Mumbai slang hinglish language fully.',
    int maxContextMessages = 50,
  });

  Future<Result<CreditBalance>> addCredit({
    required String idToken,
    required int creditsToAdd,
  });

  Future<Result<ChatHistory>> downloadChat({
    required String idToken,
  });
}
