# Where We Did What

## Android app
Path: `android/app/src/main/java/com/safespace/app/`

- `MainActivity.java` — app lifecycle and screen navigation.
- `HomeDashboardView.java` — main Safe Space dashboard.
- `AuthScreenView.java` — signup/login/SIH demo login.
- `OnboardingQuestionsView.java` — onboarding questions and preferences.
- `PermissionsScreenView.java` — Android runtime permission flow.
- `ThemeManager.java` — persistent light/dark appearance.
- `MoodCheckInView.java` — mood check-in.
- `JournalView.java` — journal UI.
- `RelaxView.java` / `CalmAudioService.java` — calm tools and foreground audio.
- `ActivitiesView.java` / `MindfulGameView.java` — activities and games.
- `SupportSignalEngine.java` — explainable prototype support trend.
- `SafeSpace360View.java` — Emotional Weather, Justice Journey, Recovery Map and unique SIH features.
- `SafetyView.java`, `EmergencyHelper.java`, `SosReceiver.java`, `SosNotification.java` — trusted-contact/SOS architecture.
- `NearbyCareView.java` — nearby support-care integration.
- `CounselorView.java` — counsellor support request flow.
- `SettingsView.java` — app settings and logout.

## Web preview
`android/app/src/main/assets/` contains modular HTML/CSS/JS pages for browser-based explanation and fallback demos.

## Backend
`backend/` is a Node/Express demo service with routes/controllers/services and JSON demo storage.

## AI
`ai/` contains deterministic reference logic for sentiment, baseline, support score and explanations.

## Counsellor dashboard
`counsellor-dashboard/` is the human follow-up web prototype.

## Demo data
`demo/` contains judge-friendly seeded scenarios and the presentation flow.
