# Error Handling

How the backend reports failures, and how the responses look.

Executable probes for everything described in [http/auth.http](../../http/auth.http)
and [http/exercises.http](../../http/exercises.http).

---

## The response contract

Every error the API produces carries the same body, defined by
[ErrorResponse](../../backend/src/main/java/de/phil/fitness/backend/common/ErrorResponse.java):

```json
{
  "code": "UNAUTHENTICATED",
  "message": "Authentication is required to access this resource",
  "path": "/backend/test",
  "fieldErrors": null,
  "timestamp": "2026-09-15T23:48:06.262185877Z"
}
```

`code` is the application code the frontend switches on — deliberately not the HTTP status.
The frontend mirrors this shape in
[ApiError](../../frontend/src/shared/api/errors/api-error.ts).

### Codes in use

| Code                     | Status | Raised by                                                                                               |
| ------------------------ | ------ | ------------------------------------------------------------------------------------------------------- |
| `INVALID_CREDENTIALS`    | 401    | `BadCredentialsException` from `LoginService`                                                           |
| `EMAIL_ALREADY_EXISTS`   | 409    | sign-up                                                                                                 |
| `USERNAME_ALREADY_TAKEN` | 409    | sign-up                                                                                                 |
| `DEFAULT_ROLE_NOT_FOUND` | 500    | sign-up, missing seed data                                                                              |
| `UNAUTHENTICATED`        | 401    | security filter chain, no valid access token                                                            |
| `ACCESS_DENIED`          | 403    | ownership checks, and the filter chain                                                                  |
| `INVALID_JSON`           | 400    | unparseable request body                                                                                |
| `VALIDATION_FAILED`      | 400    | bean validation on a `@Valid` request body; `fieldErrors` maps each rejected field to its first message |
| `RESOURCE_NOT_FOUND`     | 404    | no handler mapped to the path                                                                           |

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

### Why `/error` is permitted

An unmapped path produces a 404 that Spring **forwards to `/error`**. That forward is a fresh
request.

Problem that existed: Non existent endpoint handling gave same error as unauthorized access attempts.

---

## Two name collisions worth knowing (and needing to be addressed)

**`AccessDeniedException` exists twice.**: Both paths produce `ACCESS_DENIED`, picking the wrong import is not harmful but the log line differs.

**Jackson exists twice.** Spring Boot 4 ships Jackson 3. The old `com.fasterxml.jackson` version is pulled in by `jjwt-jackson` as a runtime dependency.

---

## To-Do

- `405 Method Not Allowed` still answers in Spring's default shape.
- Frontend handling overhaul (maybe)
