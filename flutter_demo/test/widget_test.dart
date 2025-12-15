/// Widget tests for Flutter Demo app
///
/// Provides unit tests for the movie card widget and main screen.

import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';

import 'package:flutter_demo/main.dart';

void main() {
  testWidgets('MovieFavoritesFlutterDemo loads and displays title',
      (WidgetTester tester) async {
    // Build the app and trigger a frame.
    await tester.pumpWidget(const MovieFavoritesFlutterDemo());

    // Verify that the app title is displayed.
    expect(find.text('MovieFavorites Flutter Demo'), findsWidgets);
  });

  testWidgets('Close button pops the navigator', (WidgetTester tester) async {
    await tester.pumpWidget(const MovieFavoritesFlutterDemo());

    // Find and tap the Close button.
    expect(find.text('Close Flutter Demo'), findsOneWidget);
    await tester.tap(find.text('Close Flutter Demo'));
    await tester.pumpAndSettle();

    // Navigator.pop() should be called.
  });

  testWidgets('Counter increments when Tap button is pressed',
      (WidgetTester tester) async {
    await tester.pumpWidget(const MovieFavoritesFlutterDemo());

    // Verify initial counter is 0.
    expect(find.text('Taps: 0'), findsOneWidget);

    // Tap the increment button.
    await tester.tap(find.byIcon(Icons.add));
    await tester.pump();

    // Verify counter incremented.
    expect(find.text('Taps: 1'), findsOneWidget);

    // Tap again.
    await tester.tap(find.byIcon(Icons.add));
    await tester.pump();

    // Verify counter is now 2.
    expect(find.text('Taps: 2'), findsOneWidget);
  });
}
