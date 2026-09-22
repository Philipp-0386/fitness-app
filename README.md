# Fitness App

A backend-focused fitness tracking application, with the idea to go beyond a simple CRUD app by adding domain logic such as one-rep-max prediction. This is a personal side project i maintain besides uni.

Status:
A lot of ground work has been done, to begin with domain driven implementations. Token based authentication is implemented, but still WIP. Core domain model exists within the database, but most of it is not accessible through the backend yet. I am currently working on implementing the exercise slice.

Current focus:

- Token refresh endpoint
- Implementing `exercise` endpoints
- Laying out core frontend look (design planning)

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

(Reference Point: 11.09.2026)

Everything is defined in the root `compose.yaml`. Copy `.env.example` to `.env` and fill
in the secrets first; all three services read from that single file.

## Option A: full stack in Docker

```
docker compose --profile full up -d --build
```

Starts database, backend and frontend. The frontend is then reachable on
http://localhost:3000, the backend on http://localhost:8080. No IntelliJ run
configuration and no `npm run dev` needed.

Startup order is enforced: the backend waits for the database healthcheck (`ddl-auto:
validate` fails if the schema is missing), the frontend waits for
`/actuator/health` on the backend.

## Option B: database only (IntelliJ workflow)

```
docker compose up -d
```

Without `--profile full` only the `db` service starts; backend and frontend are run
from IntelliJ / `npm run dev` as before. Note that those read their own
`backend/.env` and `frontend/.env.local`, which must stay in sync with the root `.env`.

## Database notes

- `db/src/main.sql` is mounted into `/docker-entrypoint-initdb.d` and runs **once**, when
  the `pgdata` volume is created. It is a reset script, not a migration.
- After schema changes, re-seed with `docker compose down -v` followed by `up`.
- To reset without recreating the container:
  `docker exec -i fitness-app-db-1 psql -U <POSTGRES_USER> -d <POSTGRES_DB> -v ON_ERROR_STOP=1 < db/src/main.sql`

## Networking

Inside the compose network, services address each other by service name, not localhost:
`jdbc:postgresql://db:5432/...` and `http://backend:8080`. Both are injected by
`compose.yaml`, so the images stay environment-independent.

## Logs

Backend logs go to stdout (`docker compose logs -f backend`) and additionally to the
`backend-logs` volume, because `application.yaml` configures a file appender.

--

Note: Dependency changes might require **manual Maven reload** in IntelliJ.
