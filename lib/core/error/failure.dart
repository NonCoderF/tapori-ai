sealed class Failure {
  const Failure(this.message);

  final String message;
}

final class NetworkFailure extends Failure {
  const NetworkFailure([super.message = 'Network connection failed.']);
}

final class TimeoutFailure extends Failure {
  const TimeoutFailure([super.message = 'Request timed out.']);
}

final class UnauthorizedFailure extends Failure {
  const UnauthorizedFailure(
      [super.message = 'Arre bhai, token expire ho gaya!']);
}

final class InsufficientCreditsFailure extends Failure {
  const InsufficientCreditsFailure(
      [super.message = 'Credit khatam ho gaya re!']);
}

final class ServerFailure extends Failure {
  const ServerFailure(
      [super.message = 'Arre bhai, server ka scene samajh nahi aaya re!']);
}

final class MalformedResponseFailure extends Failure {
  const MalformedResponseFailure([
    super.message = 'Arre bhai, server ka scene samajh nahi aaya re!',
  ]);
}

final class UnknownFailure extends Failure {
  const UnknownFailure([super.message = 'Something went wrong.']);
}
