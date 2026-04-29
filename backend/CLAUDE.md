# CLAUDE.md

## Environment variables

- `DB_URL` — Oracle JDBC URL
- `DB_USERNAME` / `DB_PASSWORD` — Oracle credentials
- `jwt.secret` — Base64-encoded 256-bit secret for JWT signing
- `jwt.expiration` — Token lifetime in ms (default: 900000 / 15 min)

## Architecture

Spring Boot 4 / Java 21 backend using MVC, Spring Data JPA (Oracle DB), Spring Security, and jjwt for JWT auth.

**Package structure** follows a feature-first layout under `de.phil.fitness.backend`:
- `config/` — Cross-cutting Spring beans: `SecurityConfig` (filter chain, CORS, BCrypt) and `JwtService` (token generation/validation)
- `common/` — `GlobalExceptionHandler` (`@RestControllerAdvice`) and `ErrorResponse` DTO used by all features
- `signup/` — Full vertical slice: `api/`, `service/`, `repository/`, `model/`, `dto/`, `mapper/`, `exception/`
- `login/` — `AuthController` at `/backend/login` (JWT issuance via `JwtService`, in progress)
- `smoketest/` — Throwaway controller (`/backend/smoketest`) for manual DB connectivity checks; not production code
- `testrouting/` — Another scratch controller, not production code

**Data model:** `User` entity → `USERDATA` table; `Role` entity → looked up by ID 1 as the default role on signup. Hibernate DDL mode is `validate` — schema must already exist in the DB.

**Security:** Currently all routes are `permitAll()` with CSRF disabled (development mode). CORS is configured for `http://localhost:3000`. JWT infrastructure (`JwtService`) is in place but not yet wired into a filter chain.

**Exception handling:** Domain exceptions (e.g. `EmailAlreadyExistsException`, `UsernameAlreadyTaken`) are thrown from services and caught by `GlobalExceptionHandler`, which returns structured `ErrorResponse` JSON with an error code, message, and request URI.

**API base path:** All controllers use `/backend/` prefix (e.g. `POST /backend/signup`, `POST /backend/login`).
