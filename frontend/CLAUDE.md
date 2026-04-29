# CLAUDE.md

## Environment variables
- `SPRING_API_BASE_URL` — Spring Boot base URL (e.g. `http://localhost:8080`)

## Architecture

Next.js 16 / React 19 / TypeScript frontend using the App Router.

**Directory layout** under `src/`:
- `app/` — Pages and API route handlers (App Router)
  - `app/api/` — Server-side route handlers that proxy to the Spring backend
- `features/` — Feature modules, each with `api/`, `mapper/`, `types/`, `ui/` sub-folders (feature-sliced design)
- `shared/api/` — `backendFetch` server-only wrapper and error classes (`ApiError`, `NetworkError`)

**Client ↔ Backend flow:**
1. Client component calls a client-side fetch to a Next.js route (e.g. `POST /api/signup`)
2. The route handler calls `backendFetch()` from `shared/api/backend.ts`, which forwards the request to Spring Boot at `SPRING_API_BASE_URL`
3. Response (including field-level validation errors) is returned to the client

**Key conventions:**
- `backendFetch` is server-only — never import it in client components
- Client components are marked `'use client'`; server-only modules use the `server-only` package
- Path alias `@/*` maps to `src/*`
- Zod is available for schema validation; prefer it for any new form or API input validation
- UI notifications use Sonner (`toast`); icons use Lucide React
- Prettier config: single quotes, semi, 2-space indent, trailing commas, 90-char line width
