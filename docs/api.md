# Safe Space API

Base path: /api

The repository API is a demo service. Protected endpoints require an Authorization header carrying the session returned by demo login.

## Public

POST /auth/register — demo registration; name, email and password are required.
POST /auth/login — returns a demo session for known synthetic accounts.
GET /health — service health and demo mode.

## User-scoped

GET /user/profile
PUT /user/profile
GET/POST /mood
GET/POST /journal
GET/PUT/DELETE /journal/:id
GET /trends
GET/POST /support
GET/POST /appointments
GET /counsellors
GET/POST /journey

Journal handlers always scope reads and writes to req.user.id. A request for another user's journal returns 404.

## Staff-scoped

GET /admin/overview
GET /admin/alerts
GET /admin/support-requests
GET /admin/appointments
GET /admin/audit

Allowed roles: administrator, counsellor, support-worker and supervisor.

The staff responses expose only workflow and explainable trend fields needed for support. They do not return journal text.

## Error behavior

400 malformed or missing input
401 missing/invalid session
403 authenticated but insufficient role
404 resource not found or not owned by the current user
429 request limit exceeded

Production integrations should version this API, use signed short-lived tokens, schema validation, persistent audit logging and stricter origin controls.
