@echo off
REM Setup script for Flutter Demo app (Windows)
REM Initializes Flutter dependencies and builds for Android

setlocal enabledelayedexpansion

echo Setting up Flutter Demo...
echo 1. Getting Flutter dependencies...
call flutter pub get
if errorlevel 1 goto error

echo 2. Running analyzer...
call flutter analyze
if errorlevel 1 goto error

echo 3. Running tests...
call flutter test
if errorlevel 1 goto error

echo 4. Building Android APK...
call flutter build apk --release
if errorlevel 1 goto error

echo.
echo ✅ Flutter Demo setup complete!
echo APK location: build/app/outputs/flutter-apk/app-release.apk
goto end

:error
echo Error during setup!
exit /b 1

:end
endlocal
