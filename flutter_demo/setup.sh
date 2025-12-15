#!/bin/bash
# Setup script for Flutter Demo app
# Initializes Flutter dependencies and builds for Android

set -e

echo "Setting up Flutter Demo..."
echo "1. Getting Flutter dependencies..."
flutter pub get

echo "2. Running analyzer..."
flutter analyze

echo "3. Running tests..."
flutter test

echo "4. Building Android APK..."
flutter build apk --release

echo "✅ Flutter Demo setup complete!"
echo "APK location: build/app/outputs/flutter-apk/app-release.apk"
