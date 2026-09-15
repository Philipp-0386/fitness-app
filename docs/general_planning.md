# General Planning

This documentation will be dynamically adjusted. The only purpose it serves is brainstorming, planning certain steps, or explaining why certain decisions have been made, which allows me to properly document that in the future.

---

## Setup and base auth (WIP)

Note: These phases are not necessarily to be in order, nor are they fully closed within themselves or completely exclusive to each other. They act more as a conceptual blocks of tasks, while also creating some sort of timeline. For a more accurate display of continuity and exclusivity i would refer the github issues and milestones.

- Phase 0: Initial Setup (Done)
  - Setup Oracle database, Spring and Nextjs separately
  - Connect database to Spring (environment variables)
  - Create DB entries of user (and roles) to be displayed by nextjs (-> smoketest)
  - [Smoketest](../backend/src/main/java/de/phil/fitness/backend/smoketest/) contains successful smoketest (as of pre-jwt implementation and rework, not tested since) #

- Phase 1: Signup and Logins
  - add simple frontend sign up and login pages, and parse data correctly into database
  - allow user to log in with username and password (sonner used as feedback?)
    - Note: Conform with token based authentication after jwt rework (Phase 2, backend/feature/jwt-expansion)

- Phase 2: Backend Config
  - Adjust spring security (CORS, CRSF, default stand-alone spring authentication)
    - Protected endpoints?
  - Implement JWTs/Token based authentication
    - Note after rework: Token creation done. validation, refresh, and revoke is NOT DONE-
    - full token lifecycle is the endgoal, but maybe not fully covered within phase 2

- Phase 2.1: Token Lifecycle
  - Creation (done)
  - Validation (done)
  - Token storing in DB (next, groundwork for rest of 2.1)
  - Refresh
  - Revoke
  - Rotation
  - Reuse-Detection
  - (maybe) cleanup (@scheduled)

## Domain relevant implementations (partially started)

**TODO**, after class diagram

## Git Workflow (not started)

Implement git workflow similiar (Note: SWT2 project)

## Postgres migration (done)

Migrate from oracle to postgres. (Notes below)

## Single container root build (done)

Allow the entire application in its current state to be ran from a single compose in the root folder. Maybe with prod and dev line later on if relevant/needed.

## Key Decisions

### JWT > Sessions

Initially I wanted to use sessions because spring security comes with deployable sessions out of the box, but once I realised that sessions with nextjs frontend probably does not really work as well as it would have with an SPA vite react frontend, I decided against them. I also prefer token-based authorization conceptually.

### Token Authentication Idea

- JWT lifecycle implementation handled by spring
- stored as httpOnly Cookies

### Oracle vs Postgres (migrated 09.09.2026)

I started running this project with oracle, because i knew it from university, but there are downsides to using it compared to other databases like postgres. The switch to postgres happened on 09.09.2026: at that point the schema still lived in a single reset script, only two entities were mapped, and there were no native queries, so the migration was mostly a mechanical type rewrite instead of a real migration project.

### Database table structure

See [DB Schema](./database/DatabaseModelling.md##session_log-session_exercise-and-exercise_set).
