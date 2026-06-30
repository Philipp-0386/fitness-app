# Database Modelling

This file documents the database modelling.

## Current State

Currently the database only consists of a `user` table relevant for auth.

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
