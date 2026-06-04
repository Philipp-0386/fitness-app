# General Planning

This documentation will be dynamically adjusted. The only purpose it serves is brainstorming, planning certain steps, or explaining why certain decisions have been made, which allows me to properly document that in the future.

---

## Planning

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
    - Note after rework: Token creation done. validation, refresh, and revoke is NOT DONE

- Phase 3: Domain relevant implementations

  **TODO**, after class diagram

## Key Decisions

### JWT > Sessions

Initially I wanted to use sessions because spring security comes with deployable sessions out of the box, but once I realised that sessions with nextjs frontend probably does not really work as well as it would have with an SPA vite react frontend, I decided against them. I also prefer token-based authorization conceptually.

### Token Authentication Idea

- JWT lifecycle implementation handled by spring
- stored as httpOnly Cookies
