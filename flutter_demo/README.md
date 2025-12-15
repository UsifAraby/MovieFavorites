# MovieFavorites Flutter Demo

A minimal Flutter demo app for the MovieFavorites Android project. Shows an interactive movie card and can be launched from the Android app via Intent.

## Overview

- **Purpose:** Demonstrate Flutter basics (StatefulWidget, Material3 design, state management) and Intent-based inter-app communication.
- **Tech Stack:** Flutter 3.0+, Material Design 3.
- **Features:**
  - Interactive movie card with title, rating, year, description.
  - Counter widget that increments on tap (state management demo).
  - "Close" button that returns a result via `Navigator.pop()`.

## Project Structure

```
flutter_demo/
├── lib/
│   ├── main.dart           # App entry point and main screen
│   └── movie_card.dart     # Reusable MovieCardWidget
├── test/                   # Unit tests (optional)
├── android/                # Android host app configuration
├── ios/                    # iOS host app configuration
├── pubspec.yaml            # Flutter dependencies
└── README.md               # This file
```

## Setup & Run

### Prerequisites

- Flutter SDK 3.0+ ([install here](https://flutter.dev/docs/get-started/install))
- Android/iOS development environment (for running on device/emulator)

### Run Standalone

```bash
# From flutter_demo/ directory
flutter pub get
flutter run
```

### Run from Android App

The Flutter demo can be launched from the MovieFavorites Android app via an explicit Intent:

```kotlin
// In MainActivity.kt or DetailScreen.kt
val intent = packageManager.getLaunchIntentForPackage("com.example.flutter_demo")
if (intent != null) {
    startActivity(intent)
} else {
    Toast.makeText(this, "Flutter demo not installed", Toast.LENGTH_SHORT).show()
}
```

Or with result:

```kotlin
val intent = packageManager.getLaunchIntentForPackage("com.example.flutter_demo")
if (intent != null) {
    startActivityForResult(intent, REQUEST_CODE_FLUTTER_DEMO)
}
```

## Key Files

| File | Purpose |
|------|---------|
| `lib/main.dart` | App entry point, theme setup, MovieDemoScreen with Close button. |
| `lib/movie_card.dart` | MovieCardWidget: reusable card showing movie poster, title, rating, year, description, and interactive counter. |
| `pubspec.yaml` | Flutter dependencies and project metadata. |

## Features

### Interactive Movie Card

- **Poster Placeholder:** Shows a placeholder icon (no network calls required).
- **Title, Rating, Year:** Movie metadata displayed in a Material card.
- **Description:** Short text showing movie synopsis.
- **Counter:** A stateful counter that increments when the "Tap" button is pressed (demonstrates state management).

### Close Button

- Returns a string result (`"Demo closed"`) via `Navigator.pop()`.
- Used to pass data back to the Android app if launched with `startActivityForResult()`.

## Code Quality

- Clean, readable Dart code following Flutter conventions.
- Uses Material3 theme for consistency with Android app.
- Single StatefulWidget for demo purposes (counter state).
- KDoc comments on public classes and methods.
- No external API calls or authentication.

## Testing (Optional)

To add unit tests:

```bash
flutter test
```

Test file template in `test/widget_test.dart`.

## Build & Deploy

### Android

```bash
flutter build apk      # Release APK
flutter build appbundle # Google Play Bundle
```

APK will be generated at `build/app/outputs/flutter-apk/app-release.apk`.

### iOS

```bash
flutter build ios --release
```

### Update Package Name (if needed)

```bash
flutter pub global activate rename
rename setAppName --appName "MovieFavorites Demo"
rename setBundleId --bundleId com.example.flutter_demo
```

## Integration with MovieFavorites Android App

1. **Install Flutter Demo:**
   ```bash
   cd flutter_demo
   flutter pub get
   flutter install  # Installs to connected device
   ```

2. **Launch from Android App:**
   - Add a button in MainScreen or DetailScreen.
   - Use `packageManager.getLaunchIntentForPackage("com.example.flutter_demo")` to open it.
   - Optionally handle return results with `onActivityResult()`.

3. **Example Compose Button:**
   ```kotlin
   Button(
       onClick = {
           val intent = packageManager.getLaunchIntentForPackage("com.example.flutter_demo")
           if (intent != null) {
               startActivity(intent)
           }
       }
   ) {
       Text("Open Flutter Demo")
   }
   ```

## Notes

- The Flutter demo requires **no network connection** (all data is hardcoded).
- Placeholder images use Material Icons (no external image URLs required).
- The demo is lightweight and launches quickly.
- For production use, consider adding error handling and more complex state management (Provider, BLoC).

## License

Same as MovieFavorites parent project.
