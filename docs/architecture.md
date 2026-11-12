# Architecture

Three views of the application, derived from the code as of November 2026. The backend
slice rules are enforced by
[ArchitectureTests](../backend/src/test/java/de/phil/fitness/backend/architecture/ArchitectureTests.java)
(part of `mvnw verify`), the frontend import rules by
[dependency-cruiser](../frontend/.dependency-cruiser.cjs) (`npm run depcruise`, part of CI).

## System

```mermaid
flowchart TB
    subgraph browser["Browser"]
        cc["Client Components<br/>('use client')"]
        cl["features/*/client/*.client.ts"]
    end

    caddy["Caddy<br/>TLS, reverse proxy<br/>(production only)"]

    subgraph nextjs["Next.js (frontend:3000)"]
        proxy["proxy.ts<br/>origin check + session refresh"]
        sc["Server Components<br/>app/**/page.tsx, layout.tsx"]
        api["features/*/api/*.api.ts<br/>(server-only)"]
        rh["Route Handlers<br/>app/api/**/route.ts"]
        fwd["forwardToBackend"]
        bf["backendFetch<br/>Bearer token from cookie"]
    end

    spring["Spring Boot (backend:8080)<br/>/backend/**"]
    db[("PostgreSQL 17<br/>schema by Flyway")]

    browser -- "read: GET page" --> caddy
    cc --> cl
    cl -- "write: fetch /api/*" --> caddy
    caddy --> proxy
    proxy -- "POST /backend/auth/refresh" --> spring
    proxy -- "read" --> sc
    proxy -- "write" --> rh
    sc --> api --> bf
    rh --> fwd --> bf
    bf -- "HTTP + JWT" --> spring
    spring -- "JPA" --> db
```

The browser never talks to Spring directly: Caddy forwards everything to Next.js, the tokens sit in
httpOnly cookies that only the Next.js server reads, and only that server calls the backend inside
the compose network. Every request except static assets first passes `proxy.ts`, which rejects
cross-origin writes to `/api/*` and renews a missing or expiring access token against
`/backend/auth/refresh`. Reads happen while rendering,
through the server-only `*.api.ts` modules, while writes start in a client component, go through a
`*.client.ts` function to a route handler under `/api/*`, and reach Spring via `forwardToBackend`.

## Backend slices

```mermaid
flowchart TD
    subgraph domain["Domain slices"]
        workout --> program
        workout --> routine
        workout --> exercise
        program --> routine
        routine --> exercise
        exercise -.->|ExerciseDeletedEvent| routine
    end

    subgraph account["Account slices"]
        login --> user
        signup --> user
        tokenRefresh --> user
    end

    subgraph infrastructure["Infrastructure"]
        config --> auth
        auth --> common
    end

    domain --> auth
    domain --> common
    account --> common
    login --> auth
    tokenRefresh --> auth
    user --> auth
```

Each package under `de.phil.fitness.backend` is a slice, and a solid arrow means that classes of
one slice import classes of the other (an arrow from a group stands for every slice in it). The
domain slices only depend downwards, so `exercise` knows
none of the others: when an exercise is deleted it publishes an `ExerciseDeletedEvent`, and the
routine slice listens to it and removes the exercise from its routines (the dotted arrow is that
runtime flow, the import still points from routine to exercise). The tests check that there are no
cycles between slices, that the domain direction holds and that `common` depends on no other slice.

## Domain model

```mermaid
erDiagram
    Role ||--o{ User : role
    User ||..|| UserSettings : userId

    User |o..o{ Exercise : ownerUserId
    Exercise ||--o{ ExerciseMuscle : muscles
    ExerciseMuscle }o--|| MuscleGroup : muscleGroup
    MuscleGroup |o--o{ MuscleGroup : parent

    User |o..o{ Routine : userId
    Routine ||--o{ RoutineExercise : exercises
    Exercise ||..o{ RoutineExercise : exerciseId

    User |o..o{ Program : userId
    Program ||--o{ ProgramDay : days
    Routine |o..o{ ProgramDay : routineId

    User ||..o{ Workout : userId
    Routine |o..o{ Workout : routineId
    ProgramDay |o..o{ Workout : programDayId
    Workout ||--o{ WorkoutExercise : exercises
    Exercise ||..o{ WorkoutExercise : exerciseId
    WorkoutExercise ||--o{ WorkoutSet : sets

    Role {
        Integer id PK
        String name
    }
    User {
        Long id PK
        String username
        String email
        Role role FK
    }
    UserSettings {
        Long userId PK, FK
        WeightUnit weightUnit
        DistanceUnit distanceUnit
        String timeZone
    }
    Exercise {
        Long id PK
        Long ownerUserId FK "null for the global catalog"
        String name
        ExerciseType exerciseType
        TrackingType trackingType
        Instant deletedAt "soft delete"
    }
    ExerciseMuscle {
        MuscleGroup muscleGroup FK
        MuscleRole role
    }
    MuscleGroup {
        Long id PK
        MuscleGroup parent FK
        String name
        BodyRegion bodyRegion
    }
    Routine {
        Long id PK
        Long userId FK "null for presets"
        String slug
        String name
    }
    RoutineExercise {
        Long id PK
        Routine routine FK
        Long exerciseId FK
        Integer orderIndex
        Integer targetSets
        Integer targetRepsMin
        Integer targetRepsMax
        Integer targetDurationSeconds
        BigDecimal targetDistanceMeters
    }
    Program {
        Long id PK
        Long userId FK "null for presets"
        String slug
        String name
        boolean active
    }
    ProgramDay {
        Long id PK
        Program program FK
        Long routineId FK "null for a rest day"
        Integer orderIndex
    }
    Workout {
        Long id PK
        Long userId FK
        Long routineId FK
        Long programDayId FK
        String name
        WorkoutStatus status
        Instant startedAt
        Instant endedAt
    }
    WorkoutExercise {
        Long id PK
        Workout workout FK
        Long exerciseId FK
        Integer orderIndex
        Integer targetSetsSnapshot "and the other target snapshots"
    }
    WorkoutSet {
        Long id PK
        WorkoutExercise workoutExercise FK
        UUID clientId
        Integer setNumber
        SetType setType
        Integer reps
        BigDecimal weightKg
        BigDecimal rpe
        Integer durationSeconds
        BigDecimal distanceMeters
    }
```

The JPA entities live in the `model` package of their slice, and `ExerciseMuscle` is an
`@Embeddable` stored in `EXERCISE_MUSCLEGROUP`. Solid lines are JPA references, which only exist
inside a slice; dashed lines are plain id columns, so slices refer to each other's rows by id only
(the foreign keys exist in the schema, not in the entities). Timestamps are left out. Preset
routines and programs have `userId` null, and users work with their own copies of them.
