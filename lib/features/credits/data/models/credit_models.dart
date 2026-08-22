import '../../domain/entities/credit_balance.dart';

class CreditRequestModel {
  const CreditRequestModel({
    required this.idToken,
    required this.creditsToAdd,
  });

  final String idToken;
  final int creditsToAdd;

  Map<String, Object?> toJson() {
    return {
      'idToken': idToken,
      'creditsToAdd': creditsToAdd,
    };
  }
}

class CreditResponseModel {
  const CreditResponseModel({
    required this.success,
    required this.message,
    required this.credits,
  });

  factory CreditResponseModel.fromJson(Map<String, Object?> json) {
    return CreditResponseModel(
      success: json['success'] as bool?,
      message: json['message'] as String?,
      credits: json['credits'] as int? ?? 0,
    );
  }

  final bool? success;
  final String? message;
  final int credits;

  CreditBalance toDomain() => CreditBalance(credits: credits);
}
