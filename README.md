# Trucking Manager

Small trucking business game with a Java 25/Spring Boot 4.1 backend and Angular 22 frontend.

## Backend

```bash
cd backend
./mvnw verify
./mvnw spring-boot:run
```

## Frontend

```bash
cd frontend/trucking-manager-frontend
npm ci
npm test
npm run build
npm start
```

Node.js 22.12 or 24 and npm 11 are supported. The frontend runs on
`http://localhost:4200` and calls the backend on `http://localhost:8080`.
