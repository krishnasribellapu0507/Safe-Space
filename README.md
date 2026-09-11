# Safe Space

**Safe Space** is a justice-aware, privacy-focused wellbeing support prototype for Smart India Hackathon. It supports victims across complaint, investigation, hearing, trial, compensation and rehabilitation with consented check-ins, explainable support trends, human counsellor escalation, recovery tools and offline-friendly architecture.

> Safe Space does **not** diagnose mental illness. Its AI/trend components are prototype decision-support signals designed to support human review.

## Problem Statement
Victims may experience prolonged psychological distress throughout investigation, court proceedings, compensation and rehabilitation. Existing workflows often treat legal/financial support separately from continuous wellbeing monitoring.

## Solution
Safe Space connects mobile check-ins, justice-stage context, explainable wellbeing trends, human counsellor follow-up, recovery/progress tools, privacy controls and low-connectivity support architecture into one support loop.

## Key Features
- Emotional Weather + explainable support trend
- Justice Journey: Complaint → Investigation → Hearing → Trial → Compensation → Rehabilitation
- Recovery / Progress Map and Healing Garden
- Adaptive check-ins and voice-check-in architecture
- Safe Space Assistant (supportive, non-diagnostic)
- Journal, breathing, grounding and working games
- Counsellor request + separate counsellor dashboard
- Trusted contact / SOS architecture
- Nearby care/support map integration
- Discreet mode, privacy controls and consent settings
- Offline/IVRS/SMS architecture
- Light blue and dark purple themes
- Demo mode for rapid SIH judging

## Architecture
See `docs/architecture.md` and `docs/code-map.md`.

## Tech Stack
- Android: Native Java, Android SDK 35, Gradle, JDK 17
- Local app state: Android SharedPreferences
- Web demo: HTML/CSS/JavaScript/localStorage
- Backend: Node.js + Express + JSON demo datastore
- AI reference: Python standard-library explainable prototype
- Counsellor portal: HTML/CSS/JavaScript
- Source control/CI: GitHub + GitHub Actions

## Folder Structure
```text
Safe-Space/
├── android/
├── backend/
├── ai/
├── counsellor-dashboard/
├── docs/
├── demo/
└── tests/
```

## Installation
### Android APK
```powershell
cd android
Set-Content local.properties 'sdk.dir=C:/Users/<YOU>/AppData/Local/Android/Sdk'
.\gradlew.bat assembleDebug
```
APK: `android/app/build/outputs/apk/debug/app-debug.apk`.

### Backend
```bash
cd backend
npm install
npm start
```

### AI demo
```bash
cd ai
python demo_model.py
```

## Demo Credentials
- Email: `demo@safespace.app`
- Password: `demo123`

## SIH Demo Flow
See `demo/sih-demo-flow.md`.

## Security and Privacy
See `docs/security-and-privacy.md`. No production secrets are included in the repository.

## Limitations
- SOS-to-police, SHE Teams, IVRS, SMS gateways, live hospital doctor availability and government case-system integration require authorized production APIs/services; the prototype does not fake these integrations.
- The wellbeing score is deterministic demo logic, not a clinical tool.

## Future Scope
Validated models, secure cloud sync, verified provider directory, authorized justice-system connectors, multilingual voice workflows, counsellor RBAC/audit logs and deployment monitoring.

## Team
Smart India Hackathon team — update names/roles in this section before submission.
