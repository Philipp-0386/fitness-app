# General Planning

This documentation will be dynamically adjusted. The only purpose it serves is brainstorming, planning certain steps, or explaining why certain decisions have been made, which allows me to properly document that in the future.

---

## What now?

The current goal is to finish these tasks:

- Add refresh endpoint
- Implement `exercise` slice completely (backend)
- Start on frontend implementation for `exercise` slice (frontend)
- Incooperate userful tests for already existing code (auth, bean val, etc.)

## Relevant Points

### Setup and base auth (WIP, Phase 1 and 2 done)

Note: These phases are not necessarily to be in order, nor are they fully closed within themselves or completely exclusive to each other. They act more as a conceptual blocks of tasks, while also creating some sort of timeline. For a more accurate display of continuity and exclusivity i would refer the github issues and milestones.

- Phase 0: Initial Setup (Done)
  - Setup Oracle database, Spring and Nextjs separately
  - Connect database to Spring (environment variables)
  - Create DB entries of user (and roles) to be displayed by nextjs (-> smoketest)
  - The smoketest itself (backend `smoketest` and `testrouting` packages, frontend `app/smoketest`)
    was removed once a real slice covering the the same ground.

- Phase 1: Signup and Logins (done, but phase 2.1 mentions token relevant points)
  - add simple frontend sign up and login pages, and parse data correctly into database
  - allow user to log in with username and password (sonner used as feedback?)
    - Note: Conform with token based authentication after jwt rework (Phase 2, backend/feature/jwt-expansion)

- Phase 2: Backend Config (mostly done)
  - Adjust spring security (CORS, CRSF, default stand-alone spring authentication)
    - Protected endpoints?
  - Implement JWTs/Token based authentication
    - Note after rework: Token creation done. validation, refresh, and revoke is NOT DONE
    - full token lifecycle is the endgoal, but maybe not fully covered within phase 2

- Phase 2.1: Token Lifecycle (WIP)
  - Creation (done)
  - Validation (done)
  - Token storing in DB (next, groundwork for rest of 2.1)
  - Refresh
  - Revoke
  - Rotation
  - Reuse-Detection
  - (maybe) cleanup (@scheduled)

### Domain relevant implementations (partially started)

**TODO**, after class diagram (or not?)

I have already started implementing the exercise slice. The idea is, to have _thin_ slice ready to run, and use as a reference point to start working on other relevant things with context, such as:

- improving auth
- implementing tests
- start on frontend relevant tasks

### Git Workflow (WIP)

Currenlty base CI exists running backend and frontend test builds.
Drift between mapped (backend) entites and database entities prohibited. Since the Flyway switch (23.09.2026) CI no longer applies a schema script: the test context boots, Flyway migrates the empty database, and `validate` then checks the mapped entities against the migrated schema.

### Tests (not started)

Implement tests verfiying request checks and authorization behaviour. Ideally i had already done this for auth and some bean validations.

### Api error handling (idea)

As of now, i use custom codes within the response body of error handling. The frontend reacts to the codes rather than the pure http-code. I may change the way the frontend reacts to error responses. Currently not sure what goal structure i have in mind.

### Jackson 2 and 3 on the same classpath (addressed 22.09.2026)

Both Jackson lines sit on the classpath permanently: Spring Boot 4 binds bodies with Jackson 3,
while jjwt and springdoc pull in Jackson 2. Boot plans for this, and the two never interact at
runtime, so the dependency graph was never the problem. The build now keeps the 2.x packages off the compile classpath so the wrong import fails instead of silently doing nothing.

### Exercise empty list return case

The global catalog should never be empty, meaning, if a user calls for all available exercises to have selection from, the returned list can never be empty. I should handle this case within the backend.

## Key Decisions

### JWT > Sessions

Initially I wanted to use sessions because spring security comes with deployable sessions out of the box, but once I realised that sessions with nextjs frontend probably does not really work as well as it would have with an SPA vite react frontend, I decided against them. I also prefer token-based authorization conceptually.

### Token Authentication Idea

- JWT lifecycle implementation handled by spring
- stored as httpOnly Cookies

### Oracle vs Postgres (migrated 09.09.2026)

I started running this project with oracle, because i knew it from university, but there are downsides to using it compared to other databases like postgres. The switch to postgres happened on 09.09.2026: at that point the schema still lived in a single reset script, only two entities were mapped, and there were no native queries, so the migration was mostly a mechanical type rewrite instead of a real migration project.

Furthermore, this was partially done out of pure convenience after i have worked with postgres in the module SWT2.

### Database table structure

See [DB Schema](./database/DatabaseModelling.md##session_log-session_exercise-and-exercise_set).

### Schema freeze and Flyway (23.09.2026)

`db/src/main.sql` was a drop-and-recreate reset script. The script was frozen into `V1__schema.sql` and Flyway took over. The freeze was the moment to apply everything that is free now and expensive later (Goal of PR #77):

- `TIMESTAMP` → `TIMESTAMPTZ` on every timestamp column (entities moved from `LocalDateTime` to `Instant`)
- `ON DELETE CASCADE` on `session_exercise -> session_log` and `exercise_set -> session_exercise`
- Per-owner exercise name uniqueness, and a partial unique index for one active session per user.
- `userdata.date_of_birth` is nullable: nothing reads it, and less personal data stored is less to protect. Removed from `V1` again, see below.

The seed was split at the same time. Reference data (roles, muscle groups, the global catalog) runs everywhere; the test users live in a separate Flyway location that only the `dev` profile activates. They all share the password `password` in a public repository, so a production database must never see them.

Rollbacks are the cost side of this: with one instance migrating on startup, an old image can only be redeployed if the migration in between was additive. That is a rule to keep from the first migration on, not after the first incident.

### No personal fields on the account (24.09.2026)

`first_name`, `last_name` and `date_of_birth` are gone from `userdata`, the sign-up request and response, and the sign-up form.

The columns were removed from `V1__schema.sql` directly instead of a drop migration. There is no user data worth keeping yet, so every database (local and server) is reset.

The date of birth comes back with the energy requirement calculator (kcal calc), once there is a feature that reads it.

The `USERNAME_ALREADY_TAKEN` message is generic now ("Username already taken!"), like `EMAIL_ALREADY_EXISTS`, so the requested username no longer ends up in the log or the error body.

### Basic deployment (24.09.2026)

The app is deployed at https://fit.ringelkamp.dev. The setup is minimal: one server, the existing compose stack plus a `compose.prod.yaml` overlay, and Caddy in front for TLS. There is no CI/CD pipeline yet -> deploying means pulling on the server and rebuilding the images.

The production database gets two real accounts instead of the dev test users. They are set by a repeatable Flyway migration in the `prod` location, so production keeps a strictly increasing version history without `out-of-order`.

Open points:

- Automated deployment (build and roll out from CI)
- Database backups for the `pgdata` volume
- Log retention and monitoring beyond `docker compose logs`

For a detailed look at the deployment status and [everything surrounding deployment](/docs/deployment.md)
