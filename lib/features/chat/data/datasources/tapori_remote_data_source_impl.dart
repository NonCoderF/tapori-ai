import 'package:dio/dio.dart';

import '../../../credits/data/models/credit_models.dart';
import '../models/chat_models.dart';
import 'tapori_remote_data_source.dart';

class TaporiRemoteDataSourceImpl implements TaporiRemoteDataSource {
  const TaporiRemoteDataSourceImpl(this._dio);

  final Dio _dio;

  @override
  Future<ChatResponseModel> sendMessage(ChatRequestModel request) async {
    final response = await _dio.post<Object?>('chat', data: request.toJson());
    return ChatResponseModel.fromJson(_asMap(response.data));
  }

  @override
  Future<CreditResponseModel> addCredit(CreditRequestModel request) async {
    final response =
        await _dio.post<Object?>('add_credits', data: request.toJson());
    return CreditResponseModel.fromJson(_asMap(response.data));
  }

  @override
  Future<ChatDownloadResponseModel> downloadChat(
      ChatDownloadRequestModel request) async {
    final response =
        await _dio.post<Object?>('download', data: request.toJson());
    return ChatDownloadResponseModel.fromJson(_asMap(response.data));
  }

  Map<String, Object?> _asMap(Object? data) {
    if (data is Map<String, Object?>) {
      return data;
    }
    if (data is Map) {
      return Map<String, Object?>.from(data);
    }
    throw const FormatException('Expected JSON object response.');
  }
}
