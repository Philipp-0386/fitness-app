# Error Handling

How the backend reports failures, and how the responses look.

Executable probes for everything described in [http/auth.http](../../http/auth.http),
[http/exercises.http](../../http/exercises.http) and [http/me.http](../../http/me.http).

---

## The response contract

Every error the API produces carries the same body, defined by
[ErrorResponse](../../backend/src/main/java/de/phil/fitness/backend/common/ErrorResponse.java):

```json
{
  "code": "UNAUTHENTICATED",
  "message": "Authentication is required to access this resource",
  "path": "/backend/exercises",
  "fieldErrors": null,
  "timestamp": "2026-09-15T23:48:06.262185877Z"
}
```

`code` is the application code the frontend switches on — deliberately not the HTTP status.
The frontend mirrors this shape in
[ApiError](../../frontend/src/shared/api/errors/api-error.ts).

### Codes in use

| Code                     | Status | Raised by                                                                                                                            |
| ------------------------ | ------ | ------------------------------------------------------------------------------------------------------------------------------------ |
| `INVALID_CREDENTIALS`    | 401    | `BadCredentialsException` from `LoginService`                                                                                        |
| `EMAIL_ALREADY_EXISTS`   | 409    | sign-up                                                                                                                              |
| `USERNAME_ALREADY_TAKEN` | 409    | sign-up                                                                                                                              |
| `DEFAULT_ROLE_NOT_FOUND` | 500    | sign-up, missing seed data                                                                                                           |
| `SIGNUP_DISABLED`        | 403    | `SignUpDisabledException` from `SignUpService` when `app.signup.enabled` is false; checked before the duplicate checks               |
| `UNAUTHENTICATED`        | 401    | security filter chain, no valid access token; `UserNotFoundException` on `/backend/me` when the token's user was deleted             |
| `ACCESS_DENIED`          | 403    | ownership checks, and the filter chain                                                                                               |
| `INVALID_PASSWORD`       | 403    | `InvalidPasswordException` on `DELETE /backend/me`, wrong password; not 401, the session is still valid                              |
| `INVALID_JSON`           | 400    | unparseable request body                                                                                                             |
| `VALIDATION_FAILED`      | 400    | bean validation on a `@Valid` request body, and a value outside an enum; `fieldErrors` maps each rejected field to its first message |
| `RESOURCE_NOT_FOUND`     | 404    | no handler mapped to the path                                                                                                        |

---

## Two paths produce errors

**Inside the controller**: `@RestControllerAdvice`
([GlobalExceptionHandler](../../backend/src/main/java/de/phil/fitness/backend/common/GlobalExceptionHandler.java))
catches the exception and builds the body. This covers everything a service throws.

**Inside the security filter chain**: the request never reaches a controller, so the advice is
never consulted. Spring's defaults answer with an **empty body**. Two components fix that, both
writing through
[SecurityErrorWriter](../../backend/src/main/java/de/phil/fitness/backend/auth/SecurityErrorWriter.java):

- `RestAuthenticationEntryPoint` -> 401 `UNAUTHENTICATED`
- `RestAccessDeniedHandler` -> 403 `ACCESS_DENIED`

Both are registered twice in
[SecurityConfig](../../backend/src/main/java/de/phil/fitness/backend/config/SecurityConfig.java):
on `oauth2ResourceServer`, which brings its own entry point that would otherwise take precedence,
and on `exceptionHandling` for everything else.

`UNAUTHENTICATED` is the one code both paths produce. A token whose user was deleted still passes
the filter chain (signature and expiry are valid), so `/backend/me` finds no user and the advice
answers with 401 and body as the entry point.

### Why `/error` is permitted

An unmapped path produces a 404 that Spring **forwards to `/error`**. That forward is a fresh
request.

Problem that existed: Non existent endpoint handling gave same error as unauthorized access attempts.

---

## Enum fields fail before validation

A request DTO binds a closed set of values as an enum rather than a `String`, so the frontend
offering three options in a dropdown is convenience while the backend still enforces it.

| Sent for `exerciseType`      | Fails in   | Code                |
| ---------------------------- | ---------- | ------------------- |
| `"STRENGTH"`                 | -          | -                   |
| `"YOGA"`, `"strength"`, `42` | Jackson    | `VALIDATION_FAILED` |
| missing, `null`              | `@NotNull` | `VALIDATION_FAILED` |

(Jackson is case sensitive here)

The first row of failures arrives as an `InvalidFormatException` wrapped in
`HttpMessageNotReadableException`. `handleUnreadableBody` picks those out and reports them as a field error rather than `INVALID_JSON`, so the client sees one shape no matter which layer rejected the field. A body that is not parseable JSON at all still yields `INVALID_JSON`, because there are no fields.

The `CHECK` constraint on `exercise.exercise_type` in [V1\_\_schema.sql](../../backend/src/main/resources/db/migration/V1__schema.sql) is ideally never reached.

---

## Two name collisions worth knowing (and needing to be addressed)

**`AccessDeniedException` exists twice.**: Both paths produce `ACCESS_DENIED`, picking the wrong import is not harmful but the log line differs.

**Jackson exists twice.** Spring Boot 4 ships Jackson 3. The old `com.fasterxml.jackson` version is pulled in by `jjwt-jackson` as a runtime dependency.

---

## To-Do

- `405 Method Not Allowed` still answers in Spring's default shape.
- After an account deletion, only `/backend/me` rejects the old access token. Every other endpoint
  accepts it until it expires (up to 2h): `GET /backend/exercises` answers 200, a write that
  references the user fails on the foreign key with a 500. Closes with the token lifecycle
  (revocation), or with a user lookup in `CurrentUser`.
- Frontend handling overhaul (maybe)
