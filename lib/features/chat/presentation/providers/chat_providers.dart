import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../../core/network/dio_client.dart';
import '../../../session/presentation/providers/session_providers.dart';
import '../../data/datasources/tapori_remote_data_source.dart';
import '../../data/datasources/tapori_remote_data_source_impl.dart';
import '../../data/repositories/tapori_repository_impl.dart';
import '../../domain/repositories/tapori_repository.dart';
import '../../domain/usecases/load_chat_use_case.dart';
import '../../domain/usecases/send_message_use_case.dart';
import 'chat_state.dart';

final taporiRemoteDataSourceProvider = Provider<TaporiRemoteDataSource>((ref) {
  return TaporiRemoteDataSourceImpl(ref.watch(dioProvider));
});

final taporiRepositoryProvider = FutureProvider<TaporiRepository>((ref) async {
  final sessionRepository = await ref.watch(sessionRepositoryProvider.future);
  return TaporiRepositoryImpl(
    remoteDataSource: ref.watch(taporiRemoteDataSourceProvider),
    sessionRepository: sessionRepository,
  );
});

final loadChatUseCaseProvider = FutureProvider<LoadChatUseCase>((ref) async {
  final repository = await ref.watch(taporiRepositoryProvider.future);
  final sessionRepository = await ref.watch(sessionRepositoryProvider.future);
  return LoadChatUseCase(
    repository: repository,
    sessionRepository: sessionRepository,
  );
});

final sendMessageUseCaseProvider =
    FutureProvider<SendMessageUseCase>((ref) async {
  final repository = await ref.watch(taporiRepositoryProvider.future);
  final sessionRepository = await ref.watch(sessionRepositoryProvider.future);
  return SendMessageUseCase(
    repository: repository,
    sessionRepository: sessionRepository,
  );
});

final chatNotifierProvider =
    NotifierProvider<ChatNotifier, ChatState>(ChatNotifier.new);
