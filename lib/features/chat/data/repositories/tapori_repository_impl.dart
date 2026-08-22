import 'dart:io';

import 'package:dio/dio.dart';

import '../../../../core/error/failure.dart';
import '../../../../core/error/result.dart';
import '../../../../core/utils/id_generator.dart';
import '../../../credits/data/models/credit_models.dart';
import '../../../credits/domain/entities/credit_balance.dart';
import '../../../session/domain/repositories/session_repository.dart';
import '../../domain/entities/chat_history.dart';
import '../../domain/entities/send_message_result.dart';
import '../../domain/repositories/tapori_repository.dart';
import '../datasources/tapori_remote_data_source.dart';
import '../models/chat_models.dart';
import '../models/error_response_model.dart';

class TaporiRepositoryImpl implements TaporiRepository {
  const TaporiRepositoryImpl({
    required TaporiRemoteDataSource remoteDataSource,
    required SessionRepository sessionRepository,
  })  : _remoteDataSource = remoteDataSource,
        _sessionRepository = sessionRepository;

  final TaporiRemoteDataSource _remoteDataSource;
  final SessionRepository _sessionRepository;

  @override
  Future<Result<SendMessageResult>> sendMessage({
    required String idToken,
    required String chatId,
    required String prompt,
    String systemMessage =
        'You are a Mumbai Tapori assistant. Reply in Mumbai slang hinglish language fully.',
    int maxContextMessages = 50,
  }) {
    return _guard(() async {
      final response = await _remoteDataSource.sendMessage(
        ChatRequestModel(
          idToken: idToken,
          chatId: chatId,
          prompt: prompt,
          systemMessage: systemMessage,
          maxContextMessages: maxContextMessages,
        ),
      );
      if (response.chatId.isNotEmpty) {
        await _sessionRepository.saveChatId(response.chatId);
      }
      return response.toDomain();
    });
  }

  @override
  Future<Result<CreditBalance>> addCredit({
    required String idToken,
    required int creditsToAdd,
  }) {
    return _guard(() async {
      final response = await _remoteDataSource.addCredit(
        CreditRequestModel(idToken: idToken, creditsToAdd: creditsToAdd),
      );
      return response.toDomain();
    });
  }

  @override
  Future<Result<ChatHistory>> downloadChat({required String idToken}) {
    return _guard(() async {
      final fallbackChatId = generateRandomString();
      final response = await _remoteDataSource.downloadChat(
        ChatDownloadRequestModel(idToken: idToken),
      );
      final history = response.toDomain(fallbackChatId: fallbackChatId);
      await _sessionRepository.saveChatId(history.chatId);
      return history;
    });
  }

  Future<Result<T>> _guard<T>(Future<T> Function() action) async {
    try {
      return Success(await action());
    } on DioException catch (error) {
      return FailureResult(_mapDioException(error));
    } on SocketException {
      return const FailureResult(NetworkFailure());
    } on FormatException {
      return const FailureResult(MalformedResponseFailure());
    } catch (_) {
      return const FailureResult(UnknownFailure());
    }
  }

  Failure _mapDioException(DioException error) {
    if (error.type == DioExceptionType.connectionTimeout ||
        error.type == DioExceptionType.receiveTimeout ||
        error.type == DioExceptionType.sendTimeout) {
      return const TimeoutFailure();
    }

    if (error.type == DioExceptionType.connectionError) {
      return const NetworkFailure();
    }

    final statusCode = error.response?.statusCode;
    final remoteMessage = _remoteMessage(error.response?.data);
    if (statusCode == 401) {
      return UnauthorizedFailure(
          remoteMessage ?? const UnauthorizedFailure().message);
    }
    if (statusCode == 402) {
      return InsufficientCreditsFailure(
        _remoteReply(error.response?.data) ??
            remoteMessage ??
            const InsufficientCreditsFailure().message,
      );
    }
    if (statusCode != null && statusCode >= 500) {
      return ServerFailure(remoteMessage ?? const ServerFailure().message);
    }
    return UnknownFailure(remoteMessage ?? const UnknownFailure().message);
  }

  String? _remoteMessage(Object? data) {
    final model = _errorModel(data);
    return model?.error.isNotEmpty == true ? model!.error : null;
  }

  String? _remoteReply(Object? data) => _errorModel(data)?.reply;

  ErrorResponseModel? _errorModel(Object? data) {
    if (data is Map<String, Object?>) {
      return ErrorResponseModel.fromJson(data);
    }
    if (data is Map) {
      return ErrorResponseModel.fromJson(Map<String, Object?>.from(data));
    }
    return null;
  }
}
