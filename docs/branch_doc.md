# Branch Documentation

This file documents the purpose of each branch.

## backend/feature/jwt-expansion

Initially I wanted to expand the existing JWT infrastructure, but decided to rework it instead. The branch's purpose became reworking the JWT-based access-token creation and validation on the backend and adding the frontend's ability to handle token-based authentication.

## feature/jwt-token-lifecycle

On this branch I will implement a JWT token lifecycle including:

- refresh (backend and frontend!)
- revocation
- rotation
- storage of tokens

Once that is complete, i might work on the following topics regarding authentication: (potentially different branch, or different time of development all together)

- long-lasting tokens
