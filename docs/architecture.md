# Architecture

```text
Android Native UI (Java)
  ├─ SharedPreferences/local prototype state
  ├─ Permission/SOS/audio/location adapters
  ├─ SupportSignalEngine (explainable local trend)
  └─ Optional service integration boundary
          │
          ├─ Node/Express demo API (`backend/`)
          ├─ Explainable AI reference logic (`ai/`)
          └─ Counsellor web dashboard (`counsellor-dashboard/`)
```

The shipped APK works as a local SIH prototype. Real deployment services should be connected through authenticated APIs rather than embedding secrets in the APK.
