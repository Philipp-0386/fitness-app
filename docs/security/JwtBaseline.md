# JWT Auth Baseline — Current Implementation

A snapshot of the JWT authentication as it stands today, written so it can be **reused as a
starting point in another application**. It is deliberately small: issue a token on login,
verify it on protected endpoints. That is enough to make the basic structure usable, without
the full lifecycle.

For everything this baseline does *not* do (refresh, rotation, revocation, logout),
see [TokenLifecycle.md](./TokenLifecycle.md).

Stack it was built on: Spring Boot 4.0.3, Java 21, Spring Security + OAuth2 Resource Server, jjwt 0.12.6.

---

## The Idea in Three Sentences

1. On login, credentials are checked against the DB and a **signed JWT** is returned — the server stores nothing.
2. The client sends that JWT as `Authorization: Bearer <token>` on every subsequent request.
3. The server verifies the **signature and expiry** on each request; if valid, the request counts as authenticated. No session, no lookup.

The security of this rests entirely on the signing secret. Anyone who has it can mint valid tokens.

---

## Components

| Component | Responsibility |
|-----------|----------------|
| `JwtProperties` | Binds `app.jwt.*` config (secret, lifetimes) as a typed record |
| `JwtService` | Creates and signs tokens (`generateAccessToken`, `generateRefreshToken`) |
| `SecurityConfig` | Filter chain (which routes are public), `JwtDecoder` bean for verification, `PasswordEncoder` |
| `LoginService` | Verifies credentials against the DB, triggers token issuance |
| `LoginController` | `POST /backend/auth/login` |

In this repo: [JwtProperties.java](../../backend/src/main/java/de/phil/fitness/backend/auth/JwtProperties.java),
[JwtService.java](../../backend/src/main/java/de/phil/fitness/backend/auth/JwtService.java),
[SecurityConfig.java](../../backend/src/main/java/de/phil/fitness/backend/config/SecurityConfig.java),
[LoginService.java](../../backend/src/main/java/de/phil/fitness/backend/login/service/LoginService.java),
[LoginController.java](../../backend/src/main/java/de/phil/fitness/backend/login/api/LoginController.java).

---

## Flow

```
POST /backend/auth/login  { username, password }
        │
        ├─ UserRepository.findByUsername  ──> not found ──> BadCredentialsException
        ├─ PasswordEncoder.matches(raw, hash) ──> false ──> BadCredentialsException
        │        (same exception for both: no hint whether the user exists)
        ▼
   JwtService.generateAccessToken(userId)   ──> { accessToken, refreshToken }
   JwtService.generateRefreshToken(userId)

GET /backend/test        Authorization: Bearer <accessToken>
        │
        ▼
   Resource server: signature + expiry checked against the JwtDecoder bean
        ├─ valid   ──> authenticated, `sub` = userId available
        └─ invalid ──> 401
```

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
    secret: ${JWT_SECRET}              # from env, never committed
    access-token-expiration-minutes: 15
    refresh-token-expiration-days: 7
```

`JwtProperties` is picked up via `@ConfigurationPropertiesScan` on the application class.

**Token creation** — the essential part:

```java
Jwts.builder()
    .subject(userId)
    .issuedAt(Date.from(now))
    .expiration(Date.from(expiry))
    .claim("type", "access")
    .signWith(key)
    .compact();
```

**Filter chain** — the essential part:

```java
http
    .csrf(csrf -> csrf.disable())                                   // ok: token in header, no cookie
    .sessionManagement(sm -> sm.sessionCreationPolicy(STATELESS))   // no server-side session
    .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
    .authorizeHttpRequests(auth -> auth
        .requestMatchers(HttpMethod.POST, "/backend/auth/**").permitAll()
        .anyRequest().authenticated());
```

Everything not explicitly public requires a valid token. That default-deny ordering is the
part worth copying.

---

## Pitfall: Algorithm Is Tied to Secret Length

The single most likely thing to break when reusing this.

`Keys.hmacShaKeyFor(secret.getBytes())` picks the algorithm **by key length**:
≥32 bytes → HS256, ≥48 → HS384, ≥64 → HS512. The decoder here is **hardcoded to HS512**:

```java
new SecretKeySpec(jwtProperties.secret().getBytes(), "HmacSHA512");
NimbusJwtDecoder.withSecretKey(key).macAlgorithm(MacAlgorithm.HS512).build();
```

If `JWT_SECRET` is shorter than 64 bytes, signing uses HS256 while verification expects HS512 →
**every token is rejected**, with an error that does not point at the secret. Nothing in the code
enforces or documents the required length.

When reusing, do one of:
- validate the secret length at startup (fail fast with a clear message), or
- set the algorithm explicitly on both sides instead of deriving it implicitly.

Also: `getBytes()` uses the platform default charset. Fine within one JVM, fragile across environments —
prefer `getBytes(StandardCharsets.UTF_8)`.

---

## Scope — What This Baseline Does and Does Not Cover

**Covered:** login against DB credentials, BCrypt password hashing, token issuance,
stateless verification, default-deny route protection, uniform error on bad credentials.

**Not covered** (see [TokenLifecycle.md](./TokenLifecycle.md)):

| Missing | Consequence to be aware of |
|---------|---------------------------|
| No refresh endpoint | The refresh token is issued but unusable — after 15 min the user must log in again |
| `type` claim not enforced | The refresh token is accepted as an access token on protected endpoints |
| No persistence | No revocation possible; a token is valid until it expires |
| No roles in the token | Only `sub` and `type` are carried — `hasRole(...)` style authorization is not possible; `role_id` never reaches the token |
| No logout | Sessions cannot be terminated server-side |

**As an entry point this is sound:** the structure (properties → service → filter chain → protected route)
stays the same once the lifecycle is added. Refresh and revocation slot in on top rather than
requiring a rewrite. What must not be treated as production-ready is the missing revocation path —
if a token leaks, there is currently no way to invalidate it before expiry.
