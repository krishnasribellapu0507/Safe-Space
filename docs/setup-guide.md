# Setup Guide

## Android
1. Install JDK 17 and Android SDK Platform 35.
2. `cd android`.
3. Create `local.properties` with `sdk.dir=C:/Users/<YOU>/AppData/Local/Android/Sdk`.
4. Windows: `.\gradlew.bat assembleDebug`; macOS/Linux: `./gradlew assembleDebug`.
5. APK: `android/app/build/outputs/apk/debug/app-debug.apk`.

## Backend
`cd backend && npm install && npm start`

## AI reference demo
`cd ai && python demo_model.py`

## Counsellor dashboard
Open `counsellor-dashboard/index.html`; if backend is running it fetches live demo data, otherwise it uses fallback demo data.
