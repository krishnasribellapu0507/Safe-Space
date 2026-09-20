# Security Notes

Safe Space treats journals, check-ins, voice entries and support requests as sensitive data.

## Implemented in this demo

- Protected API routes require a demo bearer session.
- Staff endpoints require an authorized role.
- Journal list/read/update/delete operations verify ownership.
- Staff support responses do not include journal text.
- Request bodies are size-limited and key text inputs are length-validated.
- A simple request limiter reduces accidental or abusive flooding in the demo server.
- Android discreet mode can apply FLAG_SECURE to reduce sensitive screen capture exposure.
- Consent choices are stored separately from wellbeing data and journal analysis defaults off.
- No production API keys are stored in the client.

## Demo-only limitations

The bearer tokens are intentionally simple demo tokens and are not production authentication. The JSON datastore is not encrypted at rest. The Android demo uses local preferences rather than an encrypted relational store for several flows. Debug signing is used for the hackathon APK.

## Production requirements

Use TLS everywhere, signed short-lived sessions with rotation, platform secure storage, encrypted database/storage, row-level authorization, server-side ownership checks on every resource, managed secrets, CSRF/CORS controls appropriate to deployment, rate limits backed by shared infrastructure, deletion/export workflows, retention policy, security logging, dependency scanning and independent penetration testing.

Never add a route that retrieves a journal entry by arbitrary user ID without checking ownership.
