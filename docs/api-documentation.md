# API Documentation

Base: `http://localhost:3000/api`

- `POST /auth/register`
- `POST /auth/login`
- `GET /user/profile`
- `PUT /user/profile`
- `POST /mood`
- `GET /mood/history`
- `POST /journal`
- `GET /journal`
- `PUT /journal/:id`
- `DELETE /journal/:id`
- `GET /counsellors`
- `GET /appointments`
- `POST /appointments`
- `GET /journey`
- `POST /journey`
- `GET /trends`

For protected demo endpoints the middleware uses `x-demo-user` and defaults to `demo`. Replace this with real authentication before deployment.
