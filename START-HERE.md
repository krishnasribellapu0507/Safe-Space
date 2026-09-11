# START HERE — Safe Space

## 1. What this repository contains
- `android/` — the Android APK source code.
- `android/app/src/main/java/com/safespace/app/` — the actual native Java screens and Android feature logic.
- `android/app/src/main/assets/` — separated HTML/CSS/JavaScript browser demo/preview.
- `backend/` — Node.js/Express prototype API.
- `ai/` — explainable wellbeing-trend reference logic.
- `counsellor-dashboard/` — separate human counsellor dashboard.
- `docs/` — architecture, API, database, code map, privacy, setup and SIH demo explanation.
- `demo/` — seeded demo data and judging sequence.
- `tests/` — smoke-test instructions.

## 2. Build the Android APK on Windows
Open PowerShell inside `Safe-Space/android` and run:

```powershell
Set-Content local.properties 'sdk.dir=C:/Users/Krish/AppData/Local/Android/Sdk'
.\gradlew.bat assembleDebug
```

APK output:

`android/app/build/outputs/apk/debug/app-debug.apk`

## 3. SIH demo login
- Email: `demo@safespace.app`
- Password: `demo123`

## 4. Run the optional backend

```powershell
cd backend
npm install
npm start
```

## 5. Run the AI reference demo

```powershell
cd ai
python demo_model.py
```

## 6. What to tell judges
Open `docs/code-map.md` for where every major app feature is coded and `docs/tools-and-workflow.md` for the tools/technology explanation.

## 7. GitHub owner
Target repository owner requested by the team: `krishnasribellapu0507`.
Do not commit `local.properties`, `.env`, APK files or production API credentials.
