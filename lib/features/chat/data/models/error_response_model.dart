class ErrorResponseModel {
  const ErrorResponseModel({required this.error, this.reply});

  factory ErrorResponseModel.fromJson(Map<String, Object?> json) {
    return ErrorResponseModel(
      error: json['error'] as String? ?? '',
      reply: json['reply'] as String?,
    );
  }

  final String error;
  final String? reply;
}
