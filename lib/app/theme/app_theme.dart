import 'package:flutter/material.dart';

ThemeData buildTaporiTheme() {
  return ThemeData(
    useMaterial3: true,
    fontFamily: 'Poppins',
    colorScheme: ColorScheme.fromSeed(
      seedColor: const Color(0xFF1D4760),
      primary: const Color(0xFF1D4760),
      secondary: const Color(0xFF128C7E),
    ),
    scaffoldBackgroundColor: Colors.white,
  );
}
