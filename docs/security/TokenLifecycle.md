# Token Lifecycle & Security Planning

This document plans the evolution of the JWT authentication from **one-time token issuance
at login** towards a **full token lifecycle** (refresh, rotation, revocation).

Related docs:
- [DatabaseModelling.md](../database/DatabaseModelling.md) — DB schema (the `refresh_token` table is added here)
- [BackendModelling.md](../backend%20architecture/BackendModelling.md) — backend structure, incl. the *Core infrastructure* diagram for JWT/Spring Security
- Schema source: [db/src/main.sql](../../db/src/main.sql)

---

## Current State

`login` issues an **access token** (JWT, HS512, 15 min) and a **refresh token**
(JWT, 7 days) and returns both in the response body
([LoginService.java](../../backend/src/main/java/de/phil/fitness/backend/login/service/LoginService.java)).
Validation is stateless via `oauth2ResourceServer.jwt(...)`
([SecurityConfig.java](../../backend/src/main/java/de/phil/fitness/backend/config/SecurityConfig.java)).

Config: `app.jwt` in [application.yaml](../../backend/src/main/resources/application.yaml)
(`access-token-expiration-minutes: 15`, `refresh-token-expiration-days: 7`).

**Core problem:** tokens can only be issued *once, at login*. There is no refresh endpoint,
no persistence, and therefore no way to renew or revoke tokens.

### Known gaps

| # | Gap | Impact |
|---|-----|--------|
| 1 | No `/backend/auth/refresh` endpoint | Refresh token is useless; re-login required after 15 min |
| 2 | Token type (`type` claim) not enforced | Refresh token is accepted as a valid access token on protected endpoints |
| 3 | No persistence of refresh tokens | No revocation possible; JWTs are valid until expiry |
| 4 | No token rotation | No theft/reuse detection |
| 5 | No `jti` claim | Individual access tokens cannot be revoked via a denylist |
| 6 | No logout endpoint | Session cannot be terminated server-side |

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
*Extends [DatabaseModelling.md](../database/DatabaseModelling.md) and [db/src/main.sql](../../db/src/main.sql).*

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

Because the refresh token now travels as an ambient cookie (see decisions below), these cookie-authenticated
`POST` endpoints (refresh/logout) need **CSRF protection** — `csrf.disable()` is no longer sufficient here.
Options: enable CSRF selectively for these endpoints only, or `SameSite=Strict` + double-submit token.

Logout offers two variants: revoke only the current refresh token (this device) or all of the user's tokens (all devices).

### Step 6 (optional) — `jti` + access-token denylist (gap 5)
Only needed if access tokens must be invalidated immediately (before expiry). With a 15-min lifetime this is
usually unnecessary. If required: add a `jti` claim in `JwtService` + a denylist table/cache check.

### Step 7 — Scheduled cleanup
`@Scheduled` job (e.g. daily): `DELETE FROM refresh_token WHERE expires_at < now` and
`revoked_at < now - 30 days`. Active and recently revoked tokens are retained for reuse detection.

---

## Decisions Made

| Topic | Decision | Consequence for the implementation |
|-------|----------|-----------------------------------|
| **Token transport** | Refresh token as **HttpOnly+Secure cookie** (access token in client memory) | Set the cookie via the Next.js BFF route; **CSRF must be handled**, since `csrf.disable()` is currently set (see Step 5). Use `SameSite=Strict`, restrict `Path` to the refresh/logout endpoint. |
| **Cleanup** | **Scheduled job**: delete expired rows, keep revoked ones for a retention window (e.g. 30 days) | Own Step 7 (`@Scheduled` cleanup). Reuse detection stays possible within the audit window. |
| **Multi-device** | **Multiple active refresh tokens per user** (per device/session) | `refresh_token` stays 1:n to `userdata` (table already supports this). Logout distinguishes "this device" vs. "all devices". |
| **Signature scheme** | **Keep HMAC (HS512)** | No change. Switch to RSA/EC only once the auth and resource servers are separated. |
