# JWT Auth Baseline — Current Implementation

A snapshot of the JWT authentication as it stands today, written so it can be **reused as a
starting point in another application**. It is deliberately small: issues token on login,
verify the access token on protected endpoints, answer failures with a consistent error body.
That is enough to make the basic structure usable, without the full lifecycle.

For everything this baseline does *not* do (refresh, rotation, revocation, logout),
see [TokenLifecycle.md](./TokenLifecycle.md). For the error response format, see
[ErrorHandling.md](../api/ErrorHandling.md).

Stack it was built on: Spring Boot 4.0.3, Java 21, Spring Security + OAuth2 Resource Server, jjwt 0.12.6.

---

## The Idea in Three Sentences

1. On login, credentials are checked against the DB and two **signed JWTs** are returned (access + refresh).
2. The client sends the access token as `Authorization: Bearer <token>` on every subsequent request.
3. The server verifies **signature, expiry and token type** on each request; if valid, the request counts as authenticated. No session, no lookup.

The security of this rests entirely on the signing secret.

---

## Components

| Component | Responsibility |
|-----------|----------------|
| `JwtProperties` | Binds `app.jwt.*` config (secret, lifetimes) as a typed record |
| `JwtService` | Creates and signs tokens (`generateAccessToken`, `generateRefreshToken`); owns the `type` claim constants |
| `AccessTokenTypeValidator` | Rejects every token whose `type` claim is not `access` |
| `SecurityConfig` | Filter chain (public routes, error handlers), `JwtDecoder` bean incl. validators, `PasswordEncoder`, CORS |
| `RestAuthenticationEntryPoint` | 401 with error body when no valid access token is present |
| `RestAccessDeniedHandler` | 403 with error body when the filter chain refuses an authenticated request |
| `SecurityErrorWriter` | Writes the shared `ErrorResponse` body for failures inside the filter chain |
| `CurrentUser` | Reads the user id (`sub`) of the authenticated request from the security context |
| `LoginService` | Verifies credentials against the DB, triggers token issuance |
| `LoginController` | `POST /backend/auth/login` |

In this repo: [JwtProperties.java](../../backend/src/main/java/de/phil/fitness/backend/auth/JwtProperties.java),
[JwtService.java](../../backend/src/main/java/de/phil/fitness/backend/auth/JwtService.java),
[AccessTokenTypeValidator.java](../../backend/src/main/java/de/phil/fitness/backend/auth/AccessTokenTypeValidator.java),
[SecurityConfig.java](../../backend/src/main/java/de/phil/fitness/backend/config/SecurityConfig.java),
[RestAuthenticationEntryPoint.java](../../backend/src/main/java/de/phil/fitness/backend/auth/RestAuthenticationEntryPoint.java),
[RestAccessDeniedHandler.java](../../backend/src/main/java/de/phil/fitness/backend/auth/RestAccessDeniedHandler.java),
[SecurityErrorWriter.java](../../backend/src/main/java/de/phil/fitness/backend/auth/SecurityErrorWriter.java),
[CurrentUser.java](../../backend/src/main/java/de/phil/fitness/backend/auth/CurrentUser.java),
[LoginService.java](../../backend/src/main/java/de/phil/fitness/backend/login/service/LoginService.java),
[LoginController.java](../../backend/src/main/java/de/phil/fitness/backend/login/api/LoginController.java).

---

## Flow

```
POST /backend/auth/login  { username, password }
        │
        ├─ invalid body (malformed JSON / @Valid fails) ──> 400 INVALID_JSON / VALIDATION_FAILED
        ├─ UserRepository.findByUsername  ──> not found ──> BadCredentialsException
        ├─ PasswordEncoder.matches(raw, hash) ──> false ──> BadCredentialsException
        │        (same exception for both: no hint whether the user exists)
        │        ──> 401 INVALID_CREDENTIALS (controller advice)
        ▼
   JwtService.generateAccessToken(userId)   ──> 200 { accessToken, refreshToken }
   JwtService.generateRefreshToken(userId)

GET /backend/exercises    Authorization: Bearer <accessToken>
        │
        ▼
   Resource server: JwtDecoder (HS512)
        ├─ signature + expiry (JwtValidators.createDefault())
        ├─ type == "access"   (AccessTokenTypeValidator)
        │
        ├─ valid   ──> authenticated, CurrentUser.currentUserId() = sub
        ├─ missing / invalid / expired / refresh token
        │          ──> 401 UNAUTHENTICATED (RestAuthenticationEntryPoint)
        └─ authenticated but refused by the filter chain
                   ──> 403 ACCESS_DENIED (RestAccessDeniedHandler)
```

All error responses share the `ErrorResponse` shape (`code`, `message`, `path`, `fieldErrors`, `timestamp`),
whether they come from the filter chain (`SecurityErrorWriter`) or from the controller advice.

---

## Minimal Setup

**Dependencies:** `spring-boot-starter-security`,
`spring-boot-starter-security-oauth2-resource-server`, plus `jjwt-api` / `jjwt-impl` / `jjwt-jackson`.

The resource server starter does the verification side; jjwt does the signing side.
(They can be done with one library — using both is simply how this grew.)

**Config** (`application.yaml`):

```yaml
app:
  jwt:
    secret: ${JWT_SECRET}              # from env, never committed; >= 64 bytes (see pitfall)
    access-token-expiration-minutes: 15
    refresh-token-expiration-days: 7
```

`JwtProperties` is picked up via `@ConfigurationPropertiesScan` on the application class.

**Token creation** — the essential part. Both tokens are built the same way and differ only in
lifetime and the `type` claim:

```java
Jwts.builder()
    .subject(userId)
    .issuedAt(Date.from(now))
    .expiration(Date.from(expiry))
    .claim(TOKEN_TYPE_CLAIM, TOKEN_TYPE_ACCESS)   // "type": "access" | "refresh"
    .signWith(key)
    .compact();
```

**Decoder** — verification plus token-type check:

```java
NimbusJwtDecoder decoder = NimbusJwtDecoder.withSecretKey(key)
        .macAlgorithm(MacAlgorithm.HS512)
        .build();

decoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(
        JwtValidators.createDefault(),          // keep the default signature/expiry checks
        new AccessTokenTypeValidator()));       // only type=access passes
```

Without the type check, the 7-day refresh token would open every protected endpoint, because
signature and expiry alone do not distinguish the two token kinds. The claim name and values are
constants on `JwtService`, so issuing and validating cannot drift apart.

**Filter chain** — the essential part:

```java
http
    .csrf(csrf -> csrf.disable())                                   // ok: token in header, no cookie
    .sessionManagement(sm -> sm.sessionCreationPolicy(STATELESS))   // no server-side session
    .oauth2ResourceServer(oauth2 -> oauth2
        .jwt(Customizer.withDefaults())
        .authenticationEntryPoint(authenticationEntryPoint)
        .accessDeniedHandler(accessDeniedHandler))
    .exceptionHandling(ex -> ex
        .authenticationEntryPoint(authenticationEntryPoint)
        .accessDeniedHandler(accessDeniedHandler))
    .authorizeHttpRequests(auth -> auth
        .requestMatchers(HttpMethod.POST, "/backend/auth/**").permitAll()
        .requestMatchers(HttpMethod.GET, "/actuator/health/**").permitAll()
        .requestMatchers("/error").permitAll()
        .anyRequest().authenticated())
    .formLogin(form -> form.disable())
    .httpBasic(httpBasic -> httpBasic.disable());
```

Points worth copying:

- **Default-deny ordering:** everything not explicitly public requires a valid access token.
- **Handlers registered twice:** on the resource server (invalid/expired token) *and* in
  `exceptionHandling` (no token at all). Otherwise one of the two paths answers with an empty 401.
- **`/error` must be public:** error dispatch is a forwarded request. If it is gated, every
  4xx from a controller (e.g. 400 on a broken body) turns into an empty 401.

Note: `POST /backend/auth/**` is public as a whole, so any future endpoint under that path
(e.g. refresh, logout) is reachable without an access token.

**Reading the user in a controller:**

```java
Long userId = currentUser.currentUserId();   // sub claim of the validated token
```

`CurrentUser` does not decode tokens itself; it only reads what the resource server placed into
the security context.

**Client side (this repo):** the browser never calls Spring directly. The Next.js BFF route
`/api/login` stores both tokens as HttpOnly cookies and forwards the access token as a
bearer header on server-side backend calls.

---

## Pitfall: Algorithm Is Tied to Secret Length (AI)

The single most likely thing to break when reusing this — and still unresolved in this repo.

`Keys.hmacShaKeyFor(secret.getBytes())` picks the algorithm **by key length**:
≥32 bytes → HS256, ≥48 → HS384, ≥64 → HS512. The decoder here is **hardcoded to HS512**:

```java
new SecretKeySpec(jwtProperties.secret().getBytes(), "HmacSHA512");
NimbusJwtDecoder.withSecretKey(key).macAlgorithm(MacAlgorithm.HS512).build();
```

Consequences by secret length:

| `JWT_SECRET` length | Behaviour |
|---------------------|-----------|
| < 32 bytes | Startup fails with `WeakKeyException` (at least a clear error) |
| 32–63 bytes | Login succeeds, but **every token is rejected with 401** — the error does not point at the secret |
| ≥ 64 bytes | Works |

Nothing in the code enforces or validates the required length. Only the test config
(`application-test.yaml`) documents it. Note that the placeholder in `.env.example` is currently
shorter than 32 bytes.

When reusing, do one of:
- validate the secret length at startup (fail fast with a clear message), or
- set the algorithm explicitly on both sides (`signWith(key, Jwts.SIG.HS512)`) instead of deriving it implicitly.

Also: `getBytes()` uses the platform default charset. Fine within one JVM, fragile across environments —
prefer `getBytes(StandardCharsets.UTF_8)`.

---

## Scope — What This Baseline Does and Does Not Cover

**Covered:** login against DB credentials, BCrypt password hashing, token issuance,
stateless verification, token-type enforcement (refresh token rejected on protected endpoints),
default-deny route protection, uniform error on bad credentials, consistent JSON error bodies
for 401/403 from the filter chain.

**Not covered** (see [TokenLifecycle.md](./TokenLifecycle.md)):

| Missing | Consequence to be aware of |
|---------|---------------------------|
| No refresh endpoint | The refresh token is issued but unusable — after 15 min the user must log in again |
| No persistence | No revocation possible; a token is valid until it expires |
| No logout | Sessions cannot be terminated server-side; the frontend only deletes its cookies |
| No roles in the token | Only `sub` and `type` are carried — `hasRole(...)` style authorization is not possible; `role_id` never reaches the token |
| No username in the token | `sub` is the user id; anything displaying a name needs a separate lookup |
| No secret validation | See pitfall above |

**As an entry point this is sound:** the structure (properties → service → decoder/validators →
filter chain → protected route) stays the same once the lifecycle is added. Refresh and revocation
slot in on top rather than requiring a rewrite. One constraint for the refresh endpoint: it cannot
validate its token through the resource server, since that path rejects `type=refresh` by design. It has to verify the token itself (jjwt, in the service).
