# Trucking Manager Game

This repository contains a minimal trucking business management game.

## Backend

The backend is a Spring Boot application (Java 21) located in `backend/`.
Run it with Maven:

```bash
cd backend
mvn spring-boot:run
```

REST endpoints provide the game state and actions.

## Frontend

The frontend is an Angular application in `frontend/trucking-manager-frontend/`.
Install dependencies and start the development server:

```bash
cd frontend/trucking-manager-frontend
npm install
ng serve
```

The app will be available on `http://localhost:4200`.
