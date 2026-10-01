# Database Modelling

The migrations are the source of truth, mainly [V1\_\_schema.sql](../../backend/src/main/resources/db/migration/V1__schema.sql). This file explains the intent behind them. Until the public launch `V1` is still edited directly, because there is no user data worth keeping yet. Only `exercise` is mapped as a JPA entity so far.

The [relational model diagram](./database_relational_model.png) is outdated (Oracle types, removed fields, table names from before 01.10.2026).

## Model

Three aggregates. Each is written through its root, they reference each other by id only.

| Aggregate | Tables                                       | Written                                       |
| --------- | -------------------------------------------- | --------------------------------------------- |
| Exercise  | `exercise`, `exercise_musclegroup`           | when the catalog or a custom exercise changes |
| Routine   | `routine`, `routine_exercise`                | when the user edits a training day, rarely    |
| Workout   | `workout`, `workout_exercise`, `workout_set` | during training, after every set              |

`routine_` is the plan, `workout_` is what was done. "Program" is reserved for the layer above routines, see [program.md](../program.md).

### Exercise

`exercise` holds the global catalog (`owner_user_id IS NULL`) and every user's custom exercises. Muscle groups are linked through `exercise_musclegroup` (`PRIMARY` / `SECONDARY`).

`tracking_type` decides which values a set holds. It drives the logging UI, the validation in the service and the analytics. `exercise_type` is only a catalog filter, no logic may depend on it.

| `tracking_type`     | Values of a set                              | Examples                   |
| ------------------- | -------------------------------------------- | -------------------------- |
| `WEIGHT_REPS`       | `weight_kg`, `reps`                          | Bench Press, curls         |
| `BODYWEIGHT_REPS`   | `reps`, optional `weight_kg` as added weight | Pull-Up, Hanging Leg Raise |
| `ASSISTED_REPS`     | `reps`, `weight_kg` as assistance            | Assisted Pull-Up           |
| `DURATION`          | `duration_seconds`, optional `weight_kg`     | Plank, Dead Hang           |
| `WEIGHT_DISTANCE`   | `weight_kg`, `distance_meters`               | Farmers Walk               |
| `DISTANCE_DURATION` | `distance_meters`, `duration_seconds`        | Treadmill run              |

Global exercises carry a unique `slug`. Their ids differ between environments once later migrations add catalog entries, so images, translations and migrations reference them by slug, never by id or name.

### Routine

A `routine` is one training day. `routine_exercise` holds its exercises with aggregate targets per exercise ("3 x 8-12 @ RPE 8"), only the targets matching the `tracking_type` are filled.

TODO: auto-generate targets from the user's own history (rep range preferences, last weights at target RPE).

### Workout

- **Eager start:** the `workout` row is created at the start, all exercises of the routine are copied into `workout_exercise` right then, every set is stored immediately. After the start a workout never reads its routine again.
- **Snapshots:** targets become `*_snapshot` columns, the routine's name becomes `workout.name`. Routine edits never change history, freestyle workouts need no routine.
- **States:** a workout is `IN_PROGRESS` or `COMPLETED`, discarding deletes it. An exercise in a workout has no status, it counts as done once it has sets. No rest tracking: the app tracks training, it does not coach it live.
- **Open in the slice:** handling a workout left `IN_PROGRESS` (resume, finish, discard), and a client generated id per set against duplicates from retries (deferred).

### Deleting

Rule: **what a workout copies is hard deleted, what it references is soft deleted.**

- **Routines: hard delete.** Their `routine_exercise` rows go with them, workouts only lose `routine_id` (`ON DELETE SET NULL (routine_id)`; the column list is required, a plain `SET NULL` would also null `user_id`).
- **Exercises: soft delete.** `workout_exercise.exercise_id` is their identity for progression, PRs and 1RM.
- **Workouts: hard delete,** sets and exercises cascade.
- **Accounts: hard delete,** everything owned cascades. References between owned rows that point at an own exercise are `DEFERRABLE`, and `UserService.deleteUser` defers them, because Postgres works through the cascades one after the other. A new table holding user data needs `ON DELETE CASCADE` on its path to `userdata` and `DEFERRABLE` on references to other owned rows.

### Logging conventions

These live in no column, but fix what every logged set means. A later migration cannot reconstruct a meaning that was never fixed.

- **Bodyweight exercises:** `weight_kg` is the added weight. For `ASSISTED_REPS` it is the assistance, as a positive value.
- **Dumbbells:** weight of one dumbbell.
- **Unilateral exercises:** repetitions of one side.
- **Set types:** warm-ups are `WARMUP`. Volume, set counts, PRs and 1RM only count `WORKING` sets.
- **`tracking_type` is immutable** once sets exist for the exercise.
- **Units:** kg. Other units are a display conversion.
- **Derived values** (PRs, volume, 1RM) are calculated, never stored.

### Constraints worth knowing

- `uq_exercise_name`: unique per owner, `NULLS NOT DISTINCT` so global names are unique too, `WHERE deleted_at IS NULL` frees a name after a soft delete.
- `uq_workout_active`: at most one `IN_PROGRESS` workout per user. The service checks first for a usable error, the index closes the race.
- `fk_workout_routine` over `(user_id, routine_id)`: a workout can only reference a routine of the same user.
- The three order constraints are `DEFERRABLE INITIALLY DEFERRED`. Postgres checks non-deferrable unique constraints row by row and Hibernate runs deletes last, so reordering would fail otherwise.
- Plausibility checks on all value and target columns.

## Rules for the routine and workout slices

The schema cannot express these, so the code has to.

- **Referenced exercises must be global or own and not deleted.** One guard in `ExerciseService` for every write path that sets an `exercise_id`, covered by access tests. A missed check leaks the foreign exercise and blocks the other user's account deletion.
- **Children are loaded through the root,** always filtered by the root's `user_id`, like `ExerciseRepository.findAvailableById`.
- **History reads include soft deleted exercises.** No global soft delete filter (`@SQLRestriction`) on `Exercise`.
- **Sets are validated against `tracking_type`** in the service. The wide `workout_set` table (all values nullable) cannot check that itself.
- **Sharing a routine** would be a copy, never a reference.

## Open Points

- **Bodyweight tracking:** needs a `bodyweight_log` (user, value, measured_at). It also turns `BODYWEIGHT_REPS` sets into real loads, so it must answer "bodyweight on the date of this workout". Possibly health data under Art. 9 GDPR, check consent and the privacy policy before it arrives.
- **Programs:** designed in [program.md](../program.md), deliberately deferred.
- **Time zone:** weekly analytics must not group in implicit UTC (Monday 00:30 Berlin time lands in the previous week). Queries take the zone as a parameter from the start, a column can follow.
- **Additive when needed:** `equipment` on `exercise` (plate diagram), supersets (group column), per set targets, merging exercises, case insensitive names (`lower(name)`; [R\_\_prod_seed_users.sql](../../backend/src/main/resources/db/prod/R__prod_seed_users.sql) uses `uq_exercise_name` as its `ON CONFLICT` target).

History of the changes: the data model rework on 01.10.2026 (Issue #122) is summarized in [general_planning.md](../general_planning.md).

## Index candidates

Deliberately not created yet. An index picked before its query is a guess, each belongs in the migration of the slice that queries it (`CREATE INDEX CONCURRENTLY`, migration marked `-- executeInTransaction=false`). PKs and unique constraints already cover the parent to child paths.

| Index                                                           | Reason                                                              |
| --------------------------------------------------------------- | ------------------------------------------------------------------- |
| `exercise_musclegroup(muscle_group_id)`                         | "which exercises train this muscle group", the PK only serves the other direction |
| `workout(user_id, started_at DESC)`                             | "my recent workouts"                                                |
| `workout_exercise(exercise_id)`                                 | analytics: progression, 1RM, volume per muscle group                |
| `workout(user_id, routine_id)`, `routine_exercise(exercise_id)` | foreign key lookups on delete                                       |

`exercise(owner_user_id)` is served by `uq_exercise_name` for "my custom exercises", not for the foreign key lookup on account deletion (partial index). Irrelevant at the current size.

Not planned: indexes on `deleted_at` or low cardinality columns (`status`, `set_type`, `body_region`).
