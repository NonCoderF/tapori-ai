import '../models/chat_models.dart';
import '../../../credits/data/models/credit_models.dart';

abstract interface class TaporiRemoteDataSource {
  Future<ChatResponseModel> sendMessage(ChatRequestModel request);

  Future<CreditResponseModel> addCredit(CreditRequestModel request);

  Future<ChatDownloadResponseModel> downloadChat(
      ChatDownloadRequestModel request);
}
