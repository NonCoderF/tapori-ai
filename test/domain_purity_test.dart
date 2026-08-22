import 'dart:io';

import 'package:flutter_test/flutter_test.dart';

void main() {
  test('domain layer does not import Flutter or infrastructure packages', () {
    final domainDir = Directory('lib/features');
    final violations = <String>[];

    for (final file in domainDir
        .listSync(recursive: true)
        .whereType<File>()
        .where((file) => file.path.contains(
            '${Platform.pathSeparator}domain${Platform.pathSeparator}'))
        .where((file) => file.path.endsWith('.dart'))) {
      final contents = file.readAsStringSync();
      const blockedImports = [
        'package:flutter/',
        'package:dio/',
        'package:flutter_riverpod/',
        'package:riverpod/',
        'package:shared_preferences/',
        'package:google_sign_in/',
        'package:razorpay_flutter/',
      ];
      for (final blockedImport in blockedImports) {
        if (contents.contains(blockedImport)) {
          violations.add('${file.path} imports $blockedImport');
        }
      }
    }

    expect(violations, isEmpty);
  });
}
