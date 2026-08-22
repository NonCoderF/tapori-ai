# Tapori AI Android to Flutter Architecture Mapping

The project has been converted from a Kotlin/Compose Android app to a Flutter app while preserving the original dependency direction:

```text
Presentation
    ↓
Domain
    ↓
Data
```

Domain code remains pure Dart. It does not import Flutter, Dio, Riverpod, shared_preferences, Google Sign-In, Razorpay, or platform SDKs.

| Current Android Component | Responsibility | Flutter Equivalent |
| --- | --- | --- |
| `MainActivity` | App entry point and navigation host | `main.dart`, `TaporiApp`, `GoRouter` |
| `PaymentActivity` | Razorpay host and payment callbacks | `PaymentScreen`, `PaymentNotifier`, `PaymentService` |
| `ChatViewModel` | Chat state and orchestration | `ChatNotifier` with Riverpod state |
| `PaymentViewModel` | Payment state and add-credit orchestration | `PaymentNotifier` |
| `StateFlow` | Presentation state stream | Riverpod state |
| `TaporiRepository` | Repository contract | `features/chat/domain/repositories/tapori_repository.dart` |
| `TaporiRepositoryImpl` | Repository implementation | `features/chat/data/repositories/tapori_repository_impl.dart` |
| Retrofit `ApiService` | HTTP endpoints | `TaporiRemoteDataSource` + Dio implementation |
| Network DTOs | Request/response payloads | Data-layer models under `features/*/data/models` |
| `ChatMessageDto` | Remote chat message DTO | `ChatMessageModel`, mapped to domain `ChatMessage` |
| Compose screens | UI | Flutter screens/widgets under `presentation` |
| Dagger modules | Dependency injection | Riverpod providers |
| SharedPreferences `Prefs` | Session/chat-id persistence | `SessionRepository` + `SessionLocalDataSource` |
| Google Sign-In helpers | Auth sign-in/refresh/logout | `AuthService` + `GoogleAuthService` |
| Razorpay `Checkout` | Payment SDK | `PaymentService` + `RazorpayPaymentService` |
| Navigation Compose | Route graph | GoRouter |
| HTTP/error parsing in ViewModels | UI-facing failure handling | Data-layer exception mapping to `Failure` types |

## Flutter Structure

```text
lib/
├── app/
│   ├── router/
│   ├── theme/
│   └── app.dart
├── core/
│   ├── config/
│   ├── error/
│   ├── network/
│   ├── storage/
│   └── utils/
└── features/
    ├── auth/
    ├── chat/
    ├── credits/
    ├── payments/
    └── session/
```

## Chat Flow

```text
ChatScreen
    ↓
ChatNotifier
    ↓
LoadChatUseCase / SendMessageUseCase
    ↓
TaporiRepository
    ↓
TaporiRepositoryImpl
    ↓
TaporiRemoteDataSource
    ↓
Dio
    ↓
Supabase Edge Function
```

DTOs are mapped into domain entities before they reach presentation. Dio and HTTP status codes stay inside data-layer code.

## Failure Mapping

| Infrastructure Condition | Flutter Failure |
| --- | --- |
| Socket/network unavailable | `NetworkFailure` |
| Timeout | `TimeoutFailure` |
| HTTP 401 | `UnauthorizedFailure` |
| HTTP 402 | `InsufficientCreditsFailure` |
| HTTP 500+ | `ServerFailure` |
| Malformed response | `MalformedResponseFailure` |
| Unknown exception | `UnknownFailure` |

