# Trucking Manager

Small trucking business game with a Java 25/Spring Boot 4.1 backend and Angular 22 frontend.

## Backend

```bash
cd backend
./mvnw verify        # compiles, runs tests, generates JaCoCo coverage report
./mvnw spring-boot:run
```

The backend listens on `http://localhost:8080` (configurable via `server.port`).
CORS allowed origins are configurable via `app.cors.allowed-origins`
(default `http://localhost:4200`, comma-separated for multiple).

## Frontend

```bash
cd frontend/trucking-manager-frontend
npm ci
npm audit --audit-level=low   # dependency vulnerability gate
npm test                       # vitest unit tests
npm run build                  # production build
npm start                      # dev server on :4200, /api proxied to :8080
```

Node.js 22 (LTS) or 24 and npm 11 are supported. In development the dev server
proxies `/api` to the backend on `http://localhost:8080` (see
`proxy.conf.json`). In production the frontend calls a same-origin `/api`, so
serve it behind a reverse proxy that forwards `/api` to the backend.

## Tests

- Backend: 30 tests (controllers, `GameService`, validation) with JaCoCo
  coverage (~87% instructions, controllers and service >96%).
- Frontend: 12 tests (`Api` service endpoint/payload contract, `Dashboard` and
  `Jobs` component behavior).
