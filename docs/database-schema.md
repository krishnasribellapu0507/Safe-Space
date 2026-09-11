# Demo Data Schema

- `users`: id, name, email, language
- `moods`: id, userId, mood, note, createdAt
- `journals`: id, userId, text, mood, favorite, createdAt
- `counsellors`: id, name, languages, availability
- `appointments`: id, userId, counsellorId, status, createdAt
- `journey`: id, userId, stage, date, note

Android prototype data is stored in app-private SharedPreferences. The backend uses `backend/data/demoData.json` only for demo/dev.
