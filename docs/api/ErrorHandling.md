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

The HTTP status says what kind of failure it is, and the frontend handles most errors by status alone
(401 session gone, 404 not found, 5xx try later). `code` refines it where one status has several
meanings the UI must tell apart, e.g. `INVALID_CREDENTIALS` and `UNAUTHENTICATED` are both 401. Every
error must be handled sensibly without knowing its code. The frontend mirrors this shape in
[ApiError](../../frontend/src/shared/api/errors/api-error.ts).

In the frontend, [toApiError](../../frontend/src/shared/api/errors/to-api-error.ts) turns every failed
response into an `ApiError`. A feature checks only the codes it reacts to specifically (e.g. `INVALID_PASSWORD`
in the account dialogs) and passes everything else to
[fallbackErrorMessage](../../frontend/src/shared/api/errors/fallback-error-message.ts), which picks the message by status.
User facing texts belong to the frontend, the backend `message` is not shown.

`ApiError.code` is typed as [ErrorCode](../../frontend/src/shared/api/errors/error-code.ts), so a
comparison against a misspelled code fails to compile. A new code in the backend is added there too.

### Codes in use

| Code                     | Status | Raised by                                                                                                                                                   |
| ------------------------ | ------ | ----------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `INVALID_CREDENTIALS`    | 401    | `BadCredentialsException` from `LoginService`                                                                                                               |
| `EMAIL_ALREADY_EXISTS`   | 409    | sign-up; `PUT /backend/me` when another user has the email                                                                                                  |
| `USERNAME_ALREADY_TAKEN` | 409    | sign-up; `PUT /backend/me` when another user has the username                                                                                               |
| `DEFAULT_ROLE_NOT_FOUND` | 500    | sign-up, missing seed data; the client only gets a generic message                                                                                          |
| `SIGNUP_DISABLED`        | 403    | `SignUpDisabledException` from `SignUpService` when `app.signup.enabled` is false; checked before the duplicate checks                                      |
| `UNAUTHENTICATED`        | 401    | security filter chain, no valid access token; `UserNotFoundException` on `/backend/me` when the token's user was deleted; `InvalidRefreshTokenException` on `/backend/auth/refresh` |
| `ACCESS_DENIED`          | 403    | ownership checks, and the filter chain                                                                                                                      |
| `EXERCISE_NOT_FOUND`     | 404    | `ExerciseNotFoundException` from `ExerciseService`: missing, soft deleted or owned by someone else; deliberately not 403, which would confirm the id exists |
| `INVALID_PASSWORD`       | 403    | `InvalidPasswordException` on `DELETE /backend/me`, `PUT /backend/me` and `PUT /backend/me/password`, wrong password; not 401, the session is still valid   |
| `INVALID_JSON`           | 400    | unparseable request body                                                                                                                                    |
| `VALIDATION_FAILED`      | 400    | bean validation on a `@Valid` request body, and a value outside an enum; `fieldErrors` maps each rejected field to its first message                        |
| `RESOURCE_NOT_FOUND`     | 404    | no handler mapped to the path                                                                                                                               |
| `INVALID_PARAMETER`      | 400    | path variable or query parameter of the wrong type, e.g. `/backend/exercises/abc`                                                                           |
| `DATA_CONFLICT`          | 409    | the database rejected a write (unique or foreign key constraint) the service did not check first; a safety net, only the constraint name is logged          |
| `INTERNAL_ERROR`         | 500    | any exception no other handler covers; no details to the client, stack trace in the log                                                                     |
| HTTP status name         | 4xx    | Spring's own web exceptions keep their status and get its name as code, e.g. 405 `METHOD_NOT_ALLOWED`, 415 `UNSUPPORTED_MEDIA_TYPE`                         |

---

## Two paths produce errors

**Inside the controller**: `@RestControllerAdvice`
([GlobalExceptionHandler](../../backend/src/main/java/de/phil/fitness/backend/common/GlobalExceptionHandler.java))
catches the exception and builds the body. This covers everything a service throws.

Own exceptions extend
[ApiException](../../backend/src/main/java/de/phil/fitness/backend/common/ApiException.java) and carry
their status, code and client message, so a single handler answers all of them. A new exception needs
no handler of its own. The exception message is written to the log only, never sent to the client.

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

`trackingType` is bound the same way and fails the same way. The `CHECK` constraints on `exercise.exercise_type` and `exercise.tracking_type` in [V1\_\_schema.sql](../../backend/src/main/resources/db/migration/V1__schema.sql) are ideally never reached.

---

## Name collisions worth knowing (and needing to be addressed)

**`AccessDeniedException` exists twice.** Services throw the own one from `auth.exception`. Spring Security's one (also raised by method security such as `@PreAuthorize`) and any other Spring Security `AuthenticationException` thrown inside a controller or service get their own handlers, so both answer like the filter chain (403 `ACCESS_DENIED`, 401 `UNAUTHENTICATED`) instead of falling into the catch-all. Picking the wrong import is therefore not harmful, only the log line differs.

**`ErrorResponse` exists twice.** The own record in `common` is the response body. Spring's interface `org.springframework.web.ErrorResponse` is implemented by its web exceptions and is what the catch-all reads the status from, so it is written fully qualified there.

**Jackson exists twice.** Spring Boot 4 ships Jackson 3. The old `com.fasterxml.jackson` version is pulled in by `jjwt-jackson` as a runtime dependency.

---

## To-Do

- After an account deletion, only `/backend/me` rejects the old access token. Every other endpoint
  accepts it until it expires (up to 15 min): `GET /backend/exercises` answers 200, a write that
  references the user fails on the foreign key with a 409 `DATA_CONFLICT`. Closes with the token lifecycle
  (revocation), or with a user lookup in `CurrentUser`.
- After a password change, tokens issued before it stay valid until they expire -> adress with the token lifecycle improvement
