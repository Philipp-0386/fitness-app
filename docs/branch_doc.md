# Branch Documentation (DEPRECATED)

Note: This file is currently unused. I may revive it in the future, if there are enough active branches being worked on, but currently there is no need.

This file documents the purpose of each branch.

## backend/feature/jwt-expansion

Initially I wanted to expand the existing JWT infrastructure, but decided to rework it instead. The branch's purpose became reworking the JWT-based access-token creation and validation on the backend and adding the frontend's ability to handle token-based authentication.

## feature/jwt-token-lifecycle (not crreated yet)

On this branch I will implement a JWT token lifecycle including:

- refresh (backend and frontend!)
- revocation
- rotation
- storage of tokens

Once that is complete, i might work on the following topics regarding authentication: (potentially different branch, or different time of development all together)

- long-lasting tokens

## chore/database/coreInfrastructure

This branch sets up the core domain database infrastructure planned in [DatabaseModelling.md](./database/DatabaseModelling.md): the `exercise`, `muscle_group`, `exercise_musclegroup`, `workout_plan`, `workout_exercise`, `session_log`, `session_exercise` and `exercise_set` tables, added as SQL DDL to [db/src/main.sql](../db/src/main.sql) (no JPA entities/repositories yet).
