# Fitness App

A backend-focused fitness tracking application, with the idea to go beyond a simple CRUD app by adding domain logic such as one-rep-max prediction. This is a personal side project i maintain besides uni.

Status:
A lot of ground work has been done, to begin with domain driven implementations. Token based authentication is implemented, but still WIP. Core domain model exists within the database, but most of it is not accessible through the backend yet. I am currently working on implementing the exercise slice.

Current focus:

- Token refresh endpoint
- Implementing `exercise` endpoints
- Laying out core frontend look (design planning)

A basic deployment is live at https://fit.ringelkamp.dev (since 24.09.2026).

For more [insights](././docs/general_planning.md)

---

# Requirements

- Docker & Docker compose
- Java 21+
- Node.js 24+
- Maven (optional, wrapper included)

# Tech Stack

- PostgreSQL 17
- Spring Boot 4.x
- Next.js 16.x

---

# Setup Guide

(Reference Point: 23.09.2026)

Everything is defined in the root `compose.yaml`. Copy `.env.example` to `.env` and fill
in the secrets first; all three services read from that single file.

## Option A: full stack in Docker

```
docker compose --profile full up -d --build
```

Starts database, backend and frontend. The frontend is then reachable on
http://localhost:3000, the backend on http://localhost:8080. No IntelliJ run
configuration and no `npm run dev` needed.

Startup order is enforced: the backend waits for the database healthcheck, then
migrates the schema with Flyway before `ddl-auto: validate` checks the entities
against it. The frontend waits for `/actuator/health` on the backend.

## Option B: database only (IntelliJ workflow)

```
docker compose up -d
```

Without `--profile full` only the `db` service starts; backend and frontend are run
from IntelliJ / `npm run dev` as before. Note that those read their own
`backend/.env` and `frontend/.env.local`, which must stay in sync with the root `.env`.

The IntelliJ run configuration needs `SPRING_PROFILES_ACTIVE=dev`. Without it the
backend migrates the schema and the reference data, but no test users — so there is
nothing to log in with.

## Database notes

The schema is owned by Flyway and migrates itself when the backend starts. The
migrations live in `backend/src/main/resources/db/` — see the README there for the
layout and the rules.

- The database container starts empty. Nothing is mounted into
  `/docker-entrypoint-initdb.d` anymore.
- Test users (`max`, `lena_lifts`,  etc., all with the password `password`) come from
  the `dev` Flyway location and require `SPRING_PROFILES_ACTIVE=dev`.
  `compose.yaml` sets that by default; a deployed environment must not.
- Reset: `docker compose down -v` followed by `up`. Dropping the `pgdata` volume is
  the reset — the next startup migrates from scratch.
- Schema changes are new migration files. Editing a migration that has already run
  makes Flyway refuse to start.

## Deployment

The production stack runs on a single server from the same `compose.yaml`, extended by
`compose.prod.yaml`:

- `SPRING_PROFILES_ACTIVE=prod`, so the dev test users are never loaded.
- No app ports are published. Caddy (`Caddyfile`) is the only entry point on 80/443,
  terminates TLS with an automatic certificate and proxies to the frontend.
- The server's `.env` sets `COMPOSE_FILE=compose.yaml:compose.prod.yaml`, so the usual
  `docker compose --profile full up -d --build` starts the prod stack.

The `prod` profile seeds two accounts (`admin` and one regular user) from the `prod`
Flyway location. Their bcrypt hashes come from `SEED_ADMIN_PASSWORD_HASH` and
`SEED_USER_PASSWORD_HASH` in `.env`; compose refuses to start without them. Details in
the README under `backend/src/main/resources/db/`.

To run the prod configuration locally (fresh volume required, Caddy left out):

```
docker compose --profile full down -v
docker compose -f compose.yaml -f compose.override.yaml -f compose.prod.yaml --profile full up -d --build db backend frontend
```

For more [details](/docs/deployment.md).

## Networking

Inside the compose network, services address each other by service name, not localhost:
`jdbc:postgresql://db:5432/...` and `http://backend:8080`. Both are injected by
`compose.yaml`, so the images stay environment-independent.

## Logs

Backend logs go to stdout (`docker compose logs -f backend`) and additionally to the
`backend-logs` volume, because `application.yaml` configures a file appender.

--

Note: Dependency changes might require **manual Maven reload** in IntelliJ.
