# Safe Space Architecture

Safe Space is a local-first native Android demo with a production-oriented service boundary.

## Runtime shape

Mobile application
→ local private state / offline tools
→ Backend API
→ Authentication + validation + rate limiting
→ Application services
  - Check-ins
  - Journal
  - Wellbeing baseline and insights
  - AI service interface
  - Support requests
  - Appointments
→ Database
→ Authorized admin/support dashboard

AI is never called directly from the mobile client. Production AI integrations belong behind the backend service layer.

## Current demo implementation

The Android client is dependency-light native Java on SDK 35. It uses SharedPreferences for demo/session state and keeps the primary flows usable without network keys. The wellbeing engine is deterministic and compares recent check-ins with the same user's earlier pattern.

The Node/Express backend is a demo service with a JSON datastore. It now requires demo bearer sessions for protected endpoints, applies request limits, validates fields, checks journal ownership, and protects staff endpoints by role.

The admin dashboard is separate from the mobile app. Its API deliberately omits full journal content.

## Production migration

Replace demo auth with an audited identity provider, SharedPreferences sensitive content with encrypted structured storage, the JSON datastore with PostgreSQL, demo tokens with short-lived signed sessions, and synthetic directory entries with verified providers. Add formal observability, key management, data-retention jobs, security review, threat modeling, and deployment isolation.
