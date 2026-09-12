# Database Modelling

This file documents the database modelling.

## Current State

Auth tables (`roles`, `userdata`) plus the core domain tables below (`exercise`, `muscle_group`, `exercise_musclegroup`, `workout_plan`, `workout_exercise`, `session_log`, `session_exercise`, `exercise_set`) are created via [db/src/main.sql](../../db/src/main.sql). No JPA entities/repositories exist for the domain tables yet.

## Planning

### exercise and muscle_group

`exercise` table contains predefined and custom exercises. `muscle_group` is just a lookup-table containing muscle groups, and being referenced by `exercise` entries through a join-table.

### workout_plan and workout_exercise

`workout_plan` and `workout_exercise` are templates defined by the user. `workout_exercise` contains target values (sets, rep ranges, RPE) per exercise. These entries will initially be only manual, but are planned to be automatically generated.

TODO: auto-generate targets from user's own training history (rep range preferences, last weights at target RPE).

### session_log, session_exercise and exercise_set

Once a user starts to track their workout, a `session_log` entry is created. Once a set is completed, this user's data will be stored in `exercise_set` entries. These entries reference `session_exercise` entries, and therefore grouping individual sets.

Important: `session` and `workout` tables decoupled, and only related to each other through a referenced exercise. This allows sessions to be created without an existing `workout_plan`. This has a couple advantages:
- Freestyle sessions without an existing `workout_plan`
- Spontaneously adding exercises during a session without modifying the plan
- Plan edits (renaming, reordering, replacing exercises) without corrupting historical sessions

Note: Session creation lazy vs eager?

### Tables (work in progress)

#### `exercise`

- **PK**: `id`
- **FK**: `owner_user_id` → `users.id` (nullable: NULL = standard, set = custom)
- **Required**: `name`, `exercise_type` (`STRENGTH` / `CARDIO` / `MOBILITY`)
- **Optional**: `description`, `instructions`
- **Audit**: `created_at`, `updated_at`, `deleted_at` (soft delete)

---

#### `muscle_group`

- **PK**: `id`
- **Required**: `name` (unique)
- **Optional**: `body_region` (e.g. `UPPER` / `LOWER` / `CORE`)

---

#### `exercise_musclegroup` (join table)

- **PK**: composite `(exercise_id, muscle_group_id)`
- **FK**: `exercise_id` → `exercise.id`, `muscle_group_id` → `muscle_group.id`
- **Required**: `role` (`PRIMARY` / `SECONDARY`)

---

#### `workout_plan`

- **PK**: `id`
- **FK**: `user_id` → `user.id`
- **Required**: `name`, `day_type` (`PUSH` / `PULL` / `LEGS` / `CUSTOM`)
- **Optional**: `description`
- **Audit**: `created_at`, `updated_at`, `deleted_at`

---

#### `workout_exercise`

- **PK**: `id`
- **FK**: `plan_id` → `workout_plan.id`, `exercise_id` → `exercise.id`
- **Required**: `order_index`
- **Optional (targets)**: `target_sets`, `target_reps_min`, `target_reps_max`, `target_rpe`, `target_rest_seconds`
- **Audit**: `created_at`, `updated_at`
- **Unique**: `(plan_id, order_index)`

---

#### `session_log`

- **PK**: `id`
- **FK**: `user_id` → `user.id`, `plan_id` → `workout_plan.id` (nullable: NULL = freestyle)
- **Required**: `started_at`, `status` (`IN_PROGRESS` / `COMPLETED` / `ABANDONED`)
- **Optional**: `ended_at`, `notes`, `session_type` (`FULL` / `QUICK`)
- **Audit**: `created_at`, `updated_at`

---

#### `session_exercise`

- **PK**: `id`
- **FK**: `session_log_id` → `session_log.id`, `exercise_id` → `exercise.id`
- **Required**: `order_index`, `status` (`PLANNED` / `COMPLETED` / `SKIPPED`)
- **Optional**: `notes`
- **Optional (target snapshots)**: `target_sets_snapshot`, `target_reps_min_snapshot`, `target_reps_max_snapshot`, `target_rpe_snapshot`
- **Audit**: `created_at`, `updated_at`
- **Unique**: `(session_log_id, order_index)`

---

#### `exercise_set`

- **PK**: `id`
- **FK**: `session_exercise_id` → `session_exercise.id`
- **Required**: `set_number`
- **Optional (strength)**: `reps`, `weight_kg`, `rpe`
- **Optional (cardio)**: `duration_seconds`, `distance_meters`, `avg_heart_rate`
- **Optional**: `rest_seconds_after`, `notes`
- **Audit**: `created_at`, `updated_at`
- **Unique**: `(session_exercise_id, set_number)`

## Open Points

Reviewed on 03.08.2026 against the feature goals in [domain_notes.md](../domain_notes.md). None of the following is implemented yet; this section exists so the gaps are recorded rather than rediscovered later.

The core tracking flow itself holds up: `session_log` → `session_exercise` → `exercise_set` covers logging, and the deliberate decoupling from `workout_plan` (see above) delivers what it promises — freestyle sessions, spontaneous exercises, and plan edits that do not corrupt history.

### Missing: bodyweight tracking

`domain_notes.md` lists a bodyweight tracker under Core and historical bodyweight progression under Analytics, but there is no table for it. Needs a `bodyweight_log` (user, value, measured_at) before either feature is possible.

### Missing: load type on `exercise`

For pull-ups or dips it is currently undecidable whether `exercise_set.weight_kg` means added weight or total weight. This blocks the 1RM and volume-per-muscle-group analytics: a bodyweight set stored with `weight_kg = NULL` counts as zero volume.

Suggested: a `load_type` column on `exercise` (`EXTERNAL` / `BODYWEIGHT` / `BODYWEIGHT_PLUS` / `ASSISTED`). Turning that into real numbers additionally requires the bodyweight at the time of the session, so this point and the one above should be implemented together.

### Missing: routine layer above `workout_plan`

`workout_plan` models a single training day (`day_type`). The weekly programme / multi-week plans from `domain_notes.md` have no representation: a PPL split is currently just a loose set of unrelated plans, without ordering, weekday assignment or week cycle. This requires a new table (e.g. `routine` plus an assignment table), not an extra column.

Deliberately deferred for now.

### Missing: delete story for sessions

`exercise` and `workout_plan` use soft deletes (`deleted_at`); the three session tables do not, and no foreign key declares `ON DELETE CASCADE`. Deleting a mislogged session therefore fails unless `exercise_set` and `session_exercise` rows are removed manually first.

Cascade is semantically correct here — a set has no meaning without its session — and this is the only place in the schema where it is appropriate.

### Constraint: `exercise` name uniqueness

`exercise.name` has no uniqueness constraint, so duplicate standard exercises can be created. The intent is not a global constraint but a per-owner one, so that each user can define their own variant of an existing name. A plain `UNIQUE (owner_user_id, name)` does not fully express that in Postgres: NULLs are treated as distinct there, so it would still allow duplicate standard exercises (`owner_user_id IS NULL`). Two constraints are needed instead — `UNIQUE (owner_user_id, name)` for custom exercises, plus a partial index `CREATE UNIQUE INDEX ... ON exercise (name) WHERE owner_user_id IS NULL` for the standard ones. More explicit than the Oracle equivalent, and the partial index states the rule directly instead of relying on NULL semantics.

### Tradeoff: wide `exercise_set` table

Strength columns (`reps`, `weight_kg`, `rpe`) and cardio columns (`duration_seconds`, `distance_meters`, `avg_heart_rate`) live in one table, all nullable, with no check enforcing a coherent combination. This is the pragmatic choice — table-per-type and EAV are both worse here — but nothing currently prevents a set with both `reps` and `distance_meters`. A guard would have to consider `exercise.exercise_type`, which is not reachable from a row-level check on `exercise_set`.

### Tradeoff: `day_type` as a CHECK constraint

`PUSH` / `PULL` / `LEGS` / `CUSTOM` is committed to one style of split; Upper/Lower, Full Body or Arms days all collapse into `CUSTOM`. Since every extension means a schema change, a free-text label or a lookup table (like `muscle_group`) would be more flexible.

### Index candidates

Primary keys and unique constraints already cover the hottest parent-to-child paths: `(plan_id, order_index)`, `(session_log_id, order_index)` and `(session_exercise_id, set_number)` each serve lookups by their leading column, and `muscle_group` is fully covered by its PK and unique name (21 rows — an index scan would not beat a full scan anyway).

Remaining gaps:

| Index | Reason |
| --- | --- |
| `exercise_musclegroup(muscle_group_id)` | The composite PK only serves the `exercise_id` direction; "which exercises train this muscle group" scans the whole join table |
| `session_log(user_id, started_at DESC)` | "my recent sessions" — filter and sort in one index |
| `session_exercise(exercise_id)` | The analytics path: per-exercise progression, 1RM, volume per muscle group |
| `exercise(owner_user_id)` | "my custom exercises", plus the foreign key reason below |
| `workout_plan(user_id)` | "my plans" |
| `session_log(plan_id)`, `workout_exercise(exercise_id)` | Foreign key reason only |

The Oracle-specific reason for indexing every foreign key column no longer applies: Oracle takes a lock on the *entire* child table when a parent row is deleted and the foreign key column is unindexed, Postgres only takes row-level locks. What remains is the ordinary performance reason — Postgres still has to scan the whole child table to verify that no referencing rows exist on a parent delete or PK update, and the query paths in the table above are the actual justification.

Note that with the current mock data volume none of these will produce a measurable difference; the value right now is documenting the intended access paths.

### Not planned

Indexes on `deleted_at` or on low-cardinality status columns (`status`, `session_type`, `body_region`) — write cost without meaningful benefit.
