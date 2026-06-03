# General Planning

This documentation will be dynamically adjusted. The only purpose it serves is brainstorming, planning certain steps, or explaining why certain decisions have been made, which allows me to properly document that in the future.

---

## Planning

- Phase 0: Initial Setup (Done)
  - Setup Oracle database, Spring and Nextjs seperately
  - Connect database to Spring (enviorment variables)
  - Create DB entries of user (and roles) to be displayed by nextjs (-> smoketest)
  - [Smoketest](../backend/src/main/java/de/phil/fitness/backend/smoketest/) contains successful smoketest (as of pre-jwt implementation and rework, not tested since) #

- Phase 1: Signup and Logins
  -

- Phase 2: Backend Config
  - Adjust spring security (CORS, CRSF, default stand-alone spring authentication)
    - Protected endpoints?
  - Implement JWTs/Token based authentication
    - Note after rework: Token creation done, rest isnt

## Key Decisions

### JWT > Sessions

Initially i wanted to use sessions because spring security comes with deployable sessions out of the box, but once i realised that sessions with nextjs frontend probably doesnt really work as well as it would have with an SPA vite react frontend. On top of that, do i prefer the approach of token based authoriaztion more.
