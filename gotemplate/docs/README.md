# Go Template API

This is the API documentation for the Go Template project.

## Endpoints

### Authentication

- `POST /register`
  - Register a new user.
  - Body: `{"username": "string", "password": "string"}`
- `POST /login`
  - Login a user.
  - Body: `{"username": "string", "password": "string"}`

### User

- `GET /api/profile`
  - Get the profile of the authenticated user.
  - Headers: `Authorization: Bearer {token}`