# Setup

## Requirements

Android Studio or Android SDK 35, JDK 17 and Gradle 8.9+ for the native app. Node.js 20+ for the demo API.

## Android

From the android directory, configure local.properties with your Android SDK path, then run:

gradle :app:assembleDebug

The APK is produced at android/app/build/outputs/apk/debug/app-debug.apk.

The repository CI performs this build on every push and uploads the APK as an artifact.

## Backend

From backend:

npm install
npm test
npm start

The default API address is http://localhost:3000.

Demo user: demo@safespace.app / demo123
Demo administrator: admin@safespace.app / demo123

These are synthetic hackathon credentials, not production credentials.

## Admin dashboard

Start the backend, then serve or open admin-dashboard/index.html. For browser environments that block local file requests, serve the repository with any simple local static server. The dashboard expects the API at http://localhost:3000 by default.

## Environment

Copy backend/.env.example to backend/.env only when local overrides are needed. Do not commit production secrets.

Useful variables:
PORT
DATA_FILE
DEMO_PASSWORD
RATE_LIMIT_PER_MINUTE
CORS_ORIGIN
