import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../../core/storage/shared_preferences_provider.dart';
import '../../data/datasources/session_local_data_source.dart';
import '../../data/datasources/shared_preferences_session_local_data_source.dart';
import '../../data/repositories/session_repository_impl.dart';
import '../../domain/repositories/session_repository.dart';

final sessionLocalDataSourceProvider =
    FutureProvider<SessionLocalDataSource>((ref) async {
  final preferences = await ref.watch(sharedPreferencesProvider.future);
  return SharedPreferencesSessionLocalDataSource(preferences);
});

final sessionRepositoryProvider =
    FutureProvider<SessionRepository>((ref) async {
  final localDataSource =
      await ref.watch(sessionLocalDataSourceProvider.future);
  return SessionRepositoryImpl(localDataSource);
});

final hasSessionProvider = FutureProvider<bool>((ref) async {
  final repository = await ref.watch(sessionRepositoryProvider.future);
  final token = await repository.getUserIdToken();
  return token != null && token.isNotEmpty;
});
