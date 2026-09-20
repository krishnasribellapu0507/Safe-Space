# Safe Space

**Your wellbeing. Your space.**

Safe Space is a privacy-first wellbeing-change detection and support ecosystem inspired by **SIH Problem Statement 26094 — Dynamic Mental Health Monitoring and Distress Prediction for victims and complainants**.

It is not positioned as a therapist or diagnostic system. The product combines personal check-ins, explainable trend analysis, a private journal, calming tools, a supportive AI-service interface, safety/support workflows and an authorized staff dashboard.

## What is working in this repository

### Native Android demo
- Animated splash and four-part onboarding.
- Demo/local authentication and session restore.
- Contextual Home dashboard.
- Mood check-in plus optional detailed sleep/stress/energy/safety/connection check-in.
- Personal-baseline wellbeing engine with explainable states.
- Journal timeline with real local text entry and private save.
- Insights and Safe Space 360 trend views.
- Calm/breathing tools and background sound service.
- Companion, safety, SOS architecture, nearby-care entry point and counsellor flow.
- Settings with consent center, accessibility/reduced motion, appearance and repeatable presentation-demo reset.
- Offline-first local operation for the core demo.

### Demo backend
- Express API with authenticated demo sessions.
- Ownership-scoped journal endpoints.
- Input limits and rate limiting.
- Personal-baseline trend service.
- Support-request API.
- Role-protected staff endpoints.
- Audit-log events.
- Synthetic fixtures only.

### Staff dashboard
- Separate admin-dashboard workspace.
- Authorized staff sign-in.
- Overview metrics, explainable alerts, support requests and access trail.
- No full journal text in staff responses.

## Core innovation

Instead of treating one score as a universal mental-health threshold, Safe Space compares a user's recent self-reported pattern with that same user's earlier pattern. The demo engine can explain a change using concrete inputs such as lower mood, less sleep and higher stress.

The resulting labels are:
- Stable
- Slight change
- Needs attention
- Consider additional support

They are wellbeing-support states, not diagnoses.

## Tech stack

Android: native Java, Android SDK 35, JDK 17.
Local state: Android SharedPreferences for the hackathon build.
Backend: Node.js + Express.
Demo datastore: JSON fixture.
AI/trend reference: deterministic explainable service; no production model key is required.
Admin: HTML/CSS/JavaScript against the protected demo API.
CI: GitHub Actions builds APK/source artifacts and runs backend/AI checks.

## Repository

safe-space/
- android/ — native mobile application
- backend/ — demo API and tests
- admin-dashboard/ — role-protected staff UI
- counsellor-dashboard/ — earlier counsellor prototype retained for reference
- ai/ — AI/reference prototype
- database/ — normalized PostgreSQL target schema
- demo/ — demonstration assets/flow
- docs/ — architecture, API, design, motion, security, privacy and setup
- tests/ — existing project checks

## Demo credentials

Mobile user: demo@safespace.app / demo123
Staff administrator: admin@safespace.app / demo123

These credentials are intentionally synthetic. The demo bearer session mechanism is not production authentication.

## Build

See docs/setup.md. CI also builds the debug APK and source ZIP for every push.

## Privacy and security

Full journal text is user-scoped and is not part of the admin dashboard response contract. Sensitive analysis/sharing controls are explicit in the mobile Consent Center. The repository contains no production AI key.

See docs/security.md and docs/privacy.md.

## Documentation

- docs/architecture.md
- docs/api.md
- docs/security.md
- docs/privacy.md
- docs/motion-system.md
- docs/design-system.md
- docs/demo-flow.md
- docs/setup.md
- database/schema.sql

## Known limitations

This is a presentation-ready engineering demo, not a deployed clinical or emergency service. Demo auth tokens and JSON storage must be replaced before production. Live provider availability, government-system integrations, SMS/IVRS, emergency dispatch, real biometric/PIN lock, encrypted journal database, cloud sync, production AI and verified support directories require authorized production infrastructure.

The app never claims prediction accuracy or medical diagnosis.

## Production next steps

Move authentication to a managed identity service, migrate sensitive local data to encrypted structured storage, deploy PostgreSQL with row-level authorization, add secure backend AI gateways, verify support-directory data, implement export/deletion jobs, complete localization strings for English/Hindi/Telugu, run accessibility testing on physical devices, add instrumentation/UI tests, perform a security review, and sign release builds with a protected production key.
