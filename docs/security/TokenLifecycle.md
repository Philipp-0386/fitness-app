# Token Lifecycle & Security Planning

**Disclaimer**: This document is originally AI generated while i was planning early on how auth could be implemented in the future.

This document plans the evolution of the JWT authentication from **one-time token issuance
at login** towards a **full token lifecycle** (refresh, rotation, revocation).

Related docs:

- [DatabaseModelling.md](../database/DatabaseModelling.md): DB schema (the `refresh_token` table is added here)
- [BackendModelling.md](../backend%20architecture/BackendModelling.md): backend structure, incl. the _Core infrastructure_ diagram for JWT/Spring Security
- Schema source: [V1\_\_schema.sql](../../backend/src/main/resources/db/migration/V1__schema.sql)

---

## Current State

`login` issues an **access token** (JWT, HS512, 15 min) and a **refresh token**
(JWT, 7 days) and returns both in the response body
([LoginService.java](../../backend/src/main/java/de/phil/fitness/backend/login/service/LoginService.java)).
Validation is stateless via `oauth2ResourceServer.jwt(...)`
([SecurityConfig.java](../../backend/src/main/java/de/phil/fitness/backend/config/SecurityConfig.java)).

Config: `app.jwt` in [application.yaml](../../backend/src/main/resources/application.yaml)
(`access-token-expiration-minutes: 15`, `refresh-token-expiration-days: 7`).

**Core problem:** the refresh token can be redeemed for a new access token, but nothing is
persisted, so tokens cannot be revoked and are valid until they expire.

### Known gaps

| #   | Gap                                    | Impact                                                                   | Status                   |
| --- | -------------------------------------- | ------------------------------------------------------------------------ | ------------------------ |
| 1   | No `/backend/auth/refresh` endpoint    | Refresh token is useless; re-login required after 15 min                 | **closed**               |
| 2   | Token type (`type` claim) not enforced | Refresh token is accepted as a valid access token on protected endpoints | **closed**               |
| 3   | No persistence of refresh tokens       | No revocation possible; JWTs are valid until expiry                      | open                     |
| 4   | No token rotation                      | No theft/reuse detection                                                 | open                     |
| 5   | No `jti` claim                         | Individual access tokens cannot be revoked via a denylist                | open, likely unnecessary |
| 6   | No logout endpoint                     | Session cannot be terminated server-side                                 | open                     |

### Gap 2 — how it was closed

[AccessTokenTypeValidator](../../backend/src/main/java/de/phil/fitness/backend/auth/AccessTokenTypeValidator.java)
rejects every token whose `type` claim is not `access`. It is chained behind
`JwtValidators.createDefault()` on the `NimbusJwtDecoder`, so the standard signature and expiry
checks stay in place. The claim values are constants on `JwtService`, so issuing and validating
cannot drift apart.

Before the validator existed, the 7-day refresh token returned `200` on any protected endpoint.
The probe used to run against `/backend/smoketest/users`, which no longer exists. It now runs against `/backend/exercises`. Both cases are kept as probes in
[http/auth.http](../../http/auth.http) and [http/exercises.http](../../http/exercises.http).

### Gap 1: how it was closed (stateless)

`POST /backend/auth/refresh` takes `{ refreshToken }` and returns `{ accessToken }`
([TokenRefreshService](../../backend/src/main/java/de/phil/fitness/backend/tokenRefresh/service/TokenRefreshService.java)).
`JwtService.verifyRefreshToken` verifies signature, expiry and `type=refresh` with jjwt. The service
then checks that the user still exists. Every failure answers 401 `UNAUTHENTICATED`.

The refresh token is deliberately **not** renewed. Without persistence, a new refresh token on
every call would make a stolen token usable forever. The session therefore ends 7 days after login.
Renewing it (rotation) waits for step 3 and 4.

#### Frontend: refresh in the proxy

The Next.js [proxy](../../frontend/src/proxy.ts) (formerly middleware) runs before every page and
`/api` route. When a refresh token cookie is present and the access token cookie is missing or
expires within 30 seconds, it calls the endpoint through
[refresh.ts](../../frontend/src/shared/auth/refresh.ts). The result has three outcomes:

| Outcome       | Cause                                | Proxy action                                      |
| ------------- | ------------------------------------ | ------------------------------------------------- |
| `ok`          | 200 with an access token             | set the new access token cookie, continue         |
| `invalid`     | 4xx from the backend                 | delete both cookies, `requireSession()` redirects |
| `unavailable` | network error, 5xx or malformed body | leave the cookies untouched, continue             |

`unavailable` is kept apart from `invalid` so that a backend restart does not log anyone out.

The refresh happens in the proxy because Server Components cannot set cookies. The new token is
written twice: on the response (`Set-Cookie`, for later requests) and on the forwarded request, so
the page rendering this very request already reads the new token.

Cookie names and options live in [cookies.ts](../../frontend/src/shared/auth/cookies.ts), shared by
the login route and the proxy.

**Consequence for step 4:** the refresh endpoint cannot validate its token through the resource
server, because that path now rejects `type=refresh` by design. It has to verify the token itself
(jjwt, in the service). Step 2 below listed this as an alternative; it is now the required route.

---

## Target Design

```
Login ──> Access token (short-lived, stateless)  ─┐
     └──> Refresh token (long-lived, persisted)    │
                                                    ▼
Access expired ──> POST /auth/refresh (refresh token)
                       │  checks: signature, type=refresh, DB row not revoked/expired
                       ▼
                   new access token + rotated refresh token
                       │  old refresh token -> revoked (replaced_by set)
                       ▼
Logout ──> POST /auth/logout ──> refresh token (or whole chain) revoked
```

**Principle:** access tokens stay **stateless** (short lifetime, no DB lookup per request).
Only **refresh tokens** are persisted and therefore revocable.

---

## Steps (in this order)

### Step 1 — DB: `refresh_token` table

_Extends [DatabaseModelling.md](../database/DatabaseModelling.md). Since 23.09.2026 the schema belongs to Flyway, so this table arrives as a **new** migration (`V3__refresh_token.sql`) — [V1\_\_schema.sql](../../backend/src/main/resources/db/migration/V1__schema.sql) is frozen and must not be edited._

The refresh token is stored **hashed** (never in plaintext) — on a DB leak the token is worthless.

Proposal (Postgres syntax, consistent with the existing tables):

```sql
CREATE TABLE refresh_token (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id BIGINT NOT NULL,
    token_hash VARCHAR(64) NOT NULL UNIQUE,     -- SHA-256 hex of the refresh token
    expires_at TIMESTAMP NOT NULL,
    revoked_at TIMESTAMP,                       -- NULL = active
    replaced_by BIGINT,                         -- rotation: successor token (self-FK)
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT fk_refresh_token_user FOREIGN KEY (user_id) REFERENCES userdata(id),
    CONSTRAINT fk_refresh_token_replaced FOREIGN KEY (replaced_by) REFERENCES refresh_token(id)
);
```

Modelling note for DatabaseModelling.md:

> - **PK**: `id`
> - **FK**: `user_id` → `userdata.id`, `replaced_by` → `refresh_token.id` (rotation chain, nullable)
> - **Required**: `token_hash` (unique, hashed), `expires_at`
> - **Optional**: `revoked_at` (NULL = active), `replaced_by`
> - **Audit**: `created_at`

### Step 2 — Enforce token type (gap 2)

A `JwtAuthenticationConverter` / validator that requires `type=access` on protected endpoints.
The refresh token (`type=refresh`) must **only** be accepted at the refresh endpoint.
Alternative: validate the refresh token separately (not through the resource server).

### Step 3 — Persist on login

`LoginService` stores the hash + `expires_at` of the refresh token in `refresh_token` when issuing it.
New JPA entity `RefreshToken` + `RefreshTokenRepository`.

### Step 4 — Refresh endpoint + rotation (gaps 1 & 4)

`POST /backend/auth/refresh`:

1. Verify the refresh token (signature, `type=refresh`, not expired).
2. Look up the hash in the DB → must exist, `revoked_at IS NULL`, not expired.
3. Issue a new access **and** refresh token.
4. Mark the old row `revoked_at = now`, `replaced_by = <new row>` (rotation).
5. **Reuse detection:** if an already revoked/replaced token is presented again → revoke the entire chain (`user_id`) (suspected theft).

### Step 5 — Logout (gap 6)

`POST /backend/auth/logout`: marks the provided refresh token (or all of the user's tokens) as revoked.

CSRF does not concern Spring here since the browser never reaches Spring (Caddy only proxies the
frontend), and the Next.js server sends the refresh token in the request body. The cookie-carrying endpoints are the Next.js `/api` routes, which the proxy
protects with a same-origin check on every non-safe method.

Logout offers two variants: revoke only the current refresh token (this device) or all of the user's tokens (all devices).

### Step 6 (optional) — `jti` + access-token denylist (gap 5)

Only needed if access tokens must be invalidated immediately (before expiry). With a 15-min lifetime this is
usually unnecessary. If required: add a `jti` claim in `JwtService` + a denylist table/cache check.

### Step 7 — Scheduled cleanup

`@Scheduled` job (e.g. daily): `DELETE FROM refresh_token WHERE expires_at < now` and
`revoked_at < now - 30 days`. Active and recently revoked tokens are retained for reuse detection.

---

## Decisions Made

| Topic                | Decision                                                                                        | Consequence for the implementation                                                                                                                                                                                              |
| -------------------- | ----------------------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Token transport**  | **Both tokens as HttpOnly cookies**, set by the Next.js BFF                                     | Browser JavaScript never sees a token. `SameSite=Lax` and `Path=/` on both, because the proxy needs the refresh token on every request. CSRF is handled by the proxy's same-origin check on `/api`, not by Spring (see Step 5). |
| **Cleanup**          | **Scheduled job**: delete expired rows, keep revoked ones for a retention window (e.g. 30 days) | Own Step 7 (`@Scheduled` cleanup). Reuse detection stays possible within the audit window.                                                                                                                                      |
| **Multi-device**     | **Multiple active refresh tokens per user** (per device/session)                                | `refresh_token` stays 1:n to `userdata` (table already supports this). Logout distinguishes "this device" vs. "all devices".                                                                                                    |
| **Signature scheme** | **Keep HMAC (HS512)**                                                                           | No change. Switch to RSA/EC only once the auth and resource servers are separated.                                                                                                                                              |
