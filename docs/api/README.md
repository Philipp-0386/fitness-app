# API Documentation

Endpoints and request/response schemas are generated from the code with
[springdoc-openapi](https://springdoc.org/). The error contract lives in
[ErrorHandling.md](./ErrorHandling.md), authentication in [JwtBaseline.md](../security/JwtBaseline.md).

## Viewing

Disabled by default. Enable locally with `API_DOCS_ENABLED=true` (IDE run config or env).

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

For protected endpoints: log in via `POST /backend/auth/login`, click _Authorize_ and paste the
`accessToken` (without `Bearer `).
