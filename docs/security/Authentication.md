# Authentication

JWT auth as implemented today, plus the open steps towards a full token lifecycle.
Error body format: [ErrorHandling.md](../api/ErrorHandling.md).

Stack: Spring Boot 4, Spring Security + OAuth2 Resource Server (verification), jjwt (signing).

---

## How It Works

1. `POST /backend/auth/login` checks credentials (BCrypt) and returns two signed JWTs (HS512):
   access token (15 min, `type=access`) and refresh token (7 days, `type=refresh`). `sub` is the user id.
2. Every other request carries `Authorization: Bearer <accessToken>`. The resource server checks
   signature, expiry and `type=access`. No session, no DB lookup at all (yet).
3. `POST /backend/auth/refresh` takes `{ refreshToken }`, verifies it with jjwt (signature, expiry,
   `type=refresh`, user still exists) and returns a new access token. The refresh token is not
   renewed, so a session ends 7 days after login.

```
login         -> 400 INVALID_JSON / VALIDATION_FAILED, 401 INVALID_CREDENTIALS, 200 { accessToken, refreshToken }
refresh       -> 401 UNAUTHENTICATED on any failure, 200 { accessToken }
protected API -> 401 UNAUTHENTICATED (missing/invalid/expired/refresh token), 403 ACCESS_DENIED
```

Unknown user and wrong password raise the same exception, so the response does not reveal whether
a username exists.

## Components

| Component                                                                          | Responsibility                                                            |
| ---------------------------------------------------------------------------------- | ------------------------------------------------------------------------- |
| `JwtProperties`                                                                    | `app.jwt.*` config (secret, lifetimes)                                    |
| `JwtService`                                                                       | Issues tokens, verifies refresh tokens, owns the `type` claim constants   |
| `AccessTokenTypeValidator`                                                         | Rejects every token on protected endpoints whose `type` is not `access`   |
| `SecurityConfig`                                                                   | Filter chain, `JwtDecoder` (HS512 + validators), `PasswordEncoder`, CORS  |
| `RestAuthenticationEntryPoint` / `RestAccessDeniedHandler` / `SecurityErrorWriter` | 401/403 with the shared `ErrorResponse` body from inside the filter chain |
| `CurrentUser`                                                                      | Reads `sub` of the authenticated request from the security context        |
| `LoginService` / `LoginController`                                                 | Login endpoint                                                            |
| `TokenRefreshService` / `TokenRefreshController`                                   | Refresh endpoint                                                          |

## Filter Chain Notes

- **Default deny:** only `POST /backend/auth/**`, health, `/error` and the OpenAPI docs are public.
  Every new endpoint under `/backend/auth/` is public automatically.
- **Error handlers registered twice:** on the resource server (bad token) and in
  `exceptionHandling` (no token). Otherwise one path answers with an empty 401.
- **CSRF disabled, sessions stateless:** the token travels in a header, not a cookie.
- **Type check is essential:** without `AccessTokenTypeValidator` the 7-day refresh token would open
  every protected endpoint. For the same reason the refresh endpoint verifies its token itself
  instead of going through the resource server.

## Frontend (BFF)

The browser never talks to Spring. The Next.js `/api/login` route stores both tokens as HttpOnly
cookies (`SameSite=Lax`, `Path=/`) and server-side calls forward the access token as a bearer header.
Cookie-carrying `/api` routes are protected by a same-origin check on non-safe methods.

The [proxy](../../frontend/src/proxy.ts) runs before every page and `/api` route and refreshes the
access token when it is missing or expires within 30 seconds:

| Outcome       | Cause                              | Proxy action                                                   |
| ------------- | ---------------------------------- | -------------------------------------------------------------- |
| `ok`          | 200                                | Set the new cookie on response and forwarded request, continue |
| `invalid`     | 4xx                                | Delete both cookies, `requireSession()` redirects              |
| `unavailable` | Network error, 5xx, malformed body | Keep cookies, continue (a backend restart logs nobody out)     |

## Problem To Keep In Mind: Secret Length

Keys.hmacShaKeyFor picks the algorithm by key length (32 bytes HS256, 48 HS384, 64 HS512),
but the decoder is hardcoded to HS512. With a 32 to 63 byte JWT_SECRET login succeeds, but every
token is rejected with 401. Nothing validates the length yet.

Fix options: validate the length at startup, or sign explicitly with `Jwts.SIG.HS512`.
Also use `getBytes(StandardCharsets.UTF_8)` instead of the platform charset.

---

## Open Work

Core problem: refresh tokens are not persisted, so nothing can be revoked before expiry.

Target: access tokens stay stateless, only refresh tokens are persisted (probably hashed and rotated).

Other known limits: no roles and no username in the token (only `sub` and `type`).
HMAC (HS512) stays until auth and resource server are separated.
