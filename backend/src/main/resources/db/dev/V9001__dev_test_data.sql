-- Test users and the data owned by them. Development and CI only (dev profile).

-- userdata (all password hashes are bcrypt of 'password', cost 10)
INSERT INTO userdata (username, email, password_hashed, role_id)
VALUES ('admin', 'admin@fitness.local', '$2a$10$N9qo8uLOickgx2ZMRZoMye8fOsiTWZqYtkxvXkKm8BMzjT7t/vIdq', 0);
INSERT INTO userdata (username, email, password_hashed, role_id)
VALUES ('max', 'max@fitness.local', '$2a$10$N9qo8uLOickgx2ZMRZoMye8fOsiTWZqYtkxvXkKm8BMzjT7t/vIdq', 1);
INSERT INTO userdata (username, email, password_hashed, role_id)
VALUES ('lena_lifts', 'lena@fitness.local', '$2a$10$N9qo8uLOickgx2ZMRZoMye8fOsiTWZqYtkxvXkKm8BMzjT7t/vIdq', 1);
INSERT INTO userdata (username, email, password_hashed, role_id)
VALUES ('marco', 'marco@fitness.local', '$2a$10$N9qo8uLOickgx2ZMRZoMye8fOsiTWZqYtkxvXkKm8BMzjT7t/vIdq', 1);
INSERT INTO userdata (username, email, password_hashed, role_id)
VALUES ('sina', 'sina@fitness.local', '$2a$10$N9qo8uLOickgx2ZMRZoMye8fOsiTWZqYtkxvXkKm8BMzjT7t/vIdq', 1);

-- custom exercises
INSERT INTO exercise (owner_user_id, name, exercise_type, tracking_type, description, instructions) VALUES ((SELECT id FROM userdata WHERE username = 'max'), 'Slow Tempo Push-Up', 'STRENGTH', 'BODYWEIGHT_REPS', 'Custom push-up variation with a 4 second eccentric.', 'Lower for four seconds, pause briefly at the bottom, press up explosively.');
INSERT INTO exercise (owner_user_id, name, exercise_type, tracking_type, description, instructions) VALUES ((SELECT id FROM userdata WHERE username = 'lena_lifts'), 'Zone 2 Treadmill Run', 'CARDIO', 'DISTANCE_DURATION', 'Custom steady state run in heart rate zone 2.', 'Run 30 to 45 minutes at a pace that still allows talking.');
INSERT INTO exercise (owner_user_id, name, exercise_type, tracking_type, description, instructions) VALUES ((SELECT id FROM userdata WHERE username = 'marco'), 'Assault Bike Intervals', 'CARDIO', 'DURATION', 'Custom HIIT on the air bike.', '10 rounds of 20 seconds all out, 40 seconds easy.');
-- soft deleted exercise
INSERT INTO exercise (owner_user_id, name, exercise_type, tracking_type, description, instructions, deleted_at) VALUES ((SELECT id FROM userdata WHERE username = 'sina'), 'Old Smith Machine Press', 'STRENGTH', 'WEIGHT_REPS', 'Custom exercise that was removed again.', 'No longer in use.', TIMESTAMPTZ '2026-05-14 18:22:00+02');

-- custom exercises
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE name = 'Slow Tempo Push-Up'), (SELECT id FROM muscle_group WHERE name = 'Chest'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE name = 'Slow Tempo Push-Up'), (SELECT id FROM muscle_group WHERE name = 'Triceps'), 'SECONDARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE name = 'Zone 2 Treadmill Run'), (SELECT id FROM muscle_group WHERE name = 'Quadriceps'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE name = 'Zone 2 Treadmill Run'), (SELECT id FROM muscle_group WHERE name = 'Calves'), 'SECONDARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE name = 'Assault Bike Intervals'), (SELECT id FROM muscle_group WHERE name = 'Quadriceps'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE name = 'Assault Bike Intervals'), (SELECT id FROM muscle_group WHERE name = 'Glutes'), 'SECONDARY');

-- routines and workouts
-- One DO block per user, so generated ids can be kept in variables instead of nested lookups per row.

-- max: a routine, a completed workout started from it, and a freestyle workout
DO $$
DECLARE
    v_user BIGINT := (SELECT id FROM userdata WHERE username = 'max');
    v_routine BIGINT;
    v_workout BIGINT;
    v_entry BIGINT;
BEGIN
    INSERT INTO routine (user_id, name, description)
    VALUES (v_user, 'Push A', 'Chest, shoulders and triceps.')
    RETURNING id INTO v_routine;

    INSERT INTO routine_exercise (routine_id, exercise_id, order_index, target_sets, target_reps_min, target_reps_max) VALUES
        (v_routine, (SELECT id FROM exercise WHERE slug = 'barbell-bench-press'), 0, 3, 8, 10),
        (v_routine, (SELECT id FROM exercise WHERE slug = 'seated-dumbbell-shoulder-press'), 1, 3, 10, 12),
        (v_routine, (SELECT id FROM exercise WHERE slug = 'triceps-rope-pushdown'), 2, 3, 12, 15);
    INSERT INTO routine_exercise (routine_id, exercise_id, order_index, target_sets, target_duration_seconds)
    VALUES (v_routine, (SELECT id FROM exercise WHERE slug = 'plank'), 3, 3, 60);

    INSERT INTO workout (user_id, routine_id, name, started_at, status, ended_at)
    VALUES (v_user, v_routine, 'Push A', TIMESTAMPTZ '2026-09-28 18:00:00+02', 'COMPLETED', TIMESTAMPTZ '2026-09-28 19:05:00+02')
    RETURNING id INTO v_workout;

    INSERT INTO workout_exercise (workout_id, exercise_id, order_index, target_sets_snapshot, target_reps_min_snapshot, target_reps_max_snapshot)
    VALUES (v_workout, (SELECT id FROM exercise WHERE slug = 'barbell-bench-press'), 0, 3, 8, 10)
    RETURNING id INTO v_entry;
    INSERT INTO workout_set (workout_exercise_id, set_number, set_type, reps, weight_kg, rpe) VALUES
        (v_entry, 1, 'WARMUP', 10, 20, NULL),
        (v_entry, 2, 'WARMUP', 5, 50, NULL),
        (v_entry, 3, 'WORKING', 10, 80, 7.5),
        (v_entry, 4, 'WORKING', 9, 80, 8.0),
        (v_entry, 5, 'WORKING', 8, 80, 9.0);

    -- dumbbells: weight_kg is the weight of one dumbbell
    INSERT INTO workout_exercise (workout_id, exercise_id, order_index, target_sets_snapshot, target_reps_min_snapshot, target_reps_max_snapshot)
    VALUES (v_workout, (SELECT id FROM exercise WHERE slug = 'seated-dumbbell-shoulder-press'), 1, 3, 10, 12)
    RETURNING id INTO v_entry;
    INSERT INTO workout_set (workout_exercise_id, set_number, set_type, reps, weight_kg, rpe) VALUES
        (v_entry, 1, 'WORKING', 12, 24, 7.5),
        (v_entry, 2, 'WORKING', 11, 24, 8.0),
        (v_entry, 3, 'WORKING', 10, 24, 8.5);

    -- planned but not done: the row stays without sets
    INSERT INTO workout_exercise (workout_id, exercise_id, order_index, target_sets_snapshot, target_reps_min_snapshot, target_reps_max_snapshot)
    VALUES (v_workout, (SELECT id FROM exercise WHERE slug = 'triceps-rope-pushdown'), 2, 3, 12, 15);

    INSERT INTO workout_exercise (workout_id, exercise_id, order_index, target_sets_snapshot, target_duration_seconds_snapshot)
    VALUES (v_workout, (SELECT id FROM exercise WHERE slug = 'plank'), 3, 3, 60)
    RETURNING id INTO v_entry;
    INSERT INTO workout_set (workout_exercise_id, set_number, set_type, duration_seconds) VALUES
        (v_entry, 1, 'WORKING', 60),
        (v_entry, 2, 'WORKING', 60),
        (v_entry, 3, 'WORKING', 45);

    -- freestyle: no routine, the name is chosen freely
    INSERT INTO workout (user_id, routine_id, name, started_at, status, ended_at, notes)
    VALUES (v_user, NULL, 'Pull-ups at home', TIMESTAMPTZ '2026-09-30 07:15:00+02', 'COMPLETED', TIMESTAMPTZ '2026-09-30 07:40:00+02', 'Door frame bar, short on time.')
    RETURNING id INTO v_workout;

    -- bodyweight exercise: weight_kg is the added weight, NULL means bodyweight only
    INSERT INTO workout_exercise (workout_id, exercise_id, order_index)
    VALUES (v_workout, (SELECT id FROM exercise WHERE slug = 'pull-up'), 0)
    RETURNING id INTO v_entry;
    INSERT INTO workout_set (workout_exercise_id, set_number, set_type, reps, weight_kg, rpe) VALUES
        (v_entry, 1, 'WORKING', 10, NULL, 8.0),
        (v_entry, 2, 'WORKING', 6, 10, 9.0),
        (v_entry, 3, 'WORKING', 5, 10, 9.5);

    INSERT INTO workout_exercise (workout_id, exercise_id, order_index)
    VALUES (v_workout, (SELECT id FROM exercise WHERE slug = 'dead-hang'), 1)
    RETURNING id INTO v_entry;
    INSERT INTO workout_set (workout_exercise_id, set_number, set_type, duration_seconds)
    VALUES (v_entry, 1, 'WORKING', 45);
END $$;

-- lena_lifts: a running workout (the one IN_PROGRESS per user) and a cardio workout on an own exercise
DO $$
DECLARE
    v_user BIGINT := (SELECT id FROM userdata WHERE username = 'lena_lifts');
    v_routine BIGINT;
    v_workout BIGINT;
    v_entry BIGINT;
BEGIN
    INSERT INTO routine (user_id, name)
    VALUES (v_user, 'Legs')
    RETURNING id INTO v_routine;

    INSERT INTO routine_exercise (routine_id, exercise_id, order_index, target_sets, target_reps_min, target_reps_max) VALUES
        (v_routine, (SELECT id FROM exercise WHERE slug = 'barbell-back-squat'), 0, 4, 5, 8),
        (v_routine, (SELECT id FROM exercise WHERE slug = 'romanian-deadlift'), 1, 3, 8, 10),
        (v_routine, (SELECT id FROM exercise WHERE slug = 'standing-calf-raise'), 2, 3, 10, 15);

    -- started eagerly: every exercise of the routine is copied at the start, the later ones have no sets yet
    INSERT INTO workout (user_id, routine_id, name, started_at, status)
    VALUES (v_user, v_routine, 'Legs', TIMESTAMPTZ '2026-09-30 17:30:00+02', 'IN_PROGRESS')
    RETURNING id INTO v_workout;

    INSERT INTO workout_exercise (workout_id, exercise_id, order_index, target_sets_snapshot, target_reps_min_snapshot, target_reps_max_snapshot)
    VALUES (v_workout, (SELECT id FROM exercise WHERE slug = 'barbell-back-squat'), 0, 4, 5, 8)
    RETURNING id INTO v_entry;
    INSERT INTO workout_set (workout_exercise_id, set_number, set_type, reps, weight_kg, rpe) VALUES
        (v_entry, 1, 'WARMUP', 8, 40, NULL),
        (v_entry, 2, 'WORKING', 6, 70, 7.5),
        (v_entry, 3, 'WORKING', 6, 70, 8.0);

    INSERT INTO workout_exercise (workout_id, exercise_id, order_index, target_sets_snapshot, target_reps_min_snapshot, target_reps_max_snapshot) VALUES
        (v_workout, (SELECT id FROM exercise WHERE slug = 'romanian-deadlift'), 1, 3, 8, 10),
        (v_workout, (SELECT id FROM exercise WHERE slug = 'standing-calf-raise'), 2, 3, 10, 15);

    INSERT INTO workout (user_id, routine_id, name, started_at, status, ended_at)
    VALUES (v_user, NULL, 'Zone 2 run', TIMESTAMPTZ '2026-09-27 09:00:00+02', 'COMPLETED', TIMESTAMPTZ '2026-09-27 09:40:00+02')
    RETURNING id INTO v_workout;

    INSERT INTO workout_exercise (workout_id, exercise_id, order_index)
    VALUES (v_workout, (SELECT id FROM exercise WHERE owner_user_id = v_user AND name = 'Zone 2 Treadmill Run'), 0)
    RETURNING id INTO v_entry;
    INSERT INTO workout_set (workout_exercise_id, set_number, set_type, distance_meters, duration_seconds)
    VALUES (v_entry, 1, 'WORKING', 6000, 2280);
END $$;

-- marco: weight and distance
DO $$
DECLARE
    v_user BIGINT := (SELECT id FROM userdata WHERE username = 'marco');
    v_workout BIGINT;
    v_entry BIGINT;
BEGIN
    INSERT INTO workout (user_id, routine_id, name, started_at, status, ended_at)
    VALUES (v_user, NULL, 'Conditioning', TIMESTAMPTZ '2026-09-29 12:00:00+02', 'COMPLETED', TIMESTAMPTZ '2026-09-29 12:35:00+02')
    RETURNING id INTO v_workout;

    INSERT INTO workout_exercise (workout_id, exercise_id, order_index)
    VALUES (v_workout, (SELECT id FROM exercise WHERE slug = 'farmers-walk'), 0)
    RETURNING id INTO v_entry;
    INSERT INTO workout_set (workout_exercise_id, set_number, set_type, weight_kg, distance_meters) VALUES
        (v_entry, 1, 'WORKING', 32, 40),
        (v_entry, 2, 'WORKING', 32, 40);

    INSERT INTO workout_exercise (workout_id, exercise_id, order_index)
    VALUES (v_workout, (SELECT id FROM exercise WHERE owner_user_id = v_user AND name = 'Assault Bike Intervals'), 1)
    RETURNING id INTO v_entry;
    INSERT INTO workout_set (workout_exercise_id, set_number, set_type, duration_seconds)
    VALUES (v_entry, 1, 'WORKING', 600);
END $$;

-- sina: history on an exercise that was soft deleted afterwards, which history reads must still show
DO $$
DECLARE
    v_user BIGINT := (SELECT id FROM userdata WHERE username = 'sina');
    v_workout BIGINT;
    v_entry BIGINT;
BEGIN
    INSERT INTO workout (user_id, routine_id, name, started_at, status, ended_at)
    VALUES (v_user, NULL, 'Shoulders', TIMESTAMPTZ '2026-05-10 18:00:00+02', 'COMPLETED', TIMESTAMPTZ '2026-05-10 18:50:00+02')
    RETURNING id INTO v_workout;

    INSERT INTO workout_exercise (workout_id, exercise_id, order_index)
    VALUES (v_workout, (SELECT id FROM exercise WHERE owner_user_id = v_user AND name = 'Old Smith Machine Press'), 0)
    RETURNING id INTO v_entry;
    INSERT INTO workout_set (workout_exercise_id, set_number, set_type, reps, weight_kg, rpe) VALUES
        (v_entry, 1, 'WORKING', 10, 40, 8.0),
        (v_entry, 2, 'WORKING', 9, 40, 8.5);
END $$;

-- programs

-- max: an active program with a copied preset and a rest day, plus an inactive one for planning
DO $$
DECLARE
    v_user BIGINT := (SELECT id FROM userdata WHERE username = 'max');
    v_push BIGINT := (SELECT id FROM routine WHERE user_id = (SELECT id FROM userdata WHERE username = 'max') AND name = 'Push A');
    v_pull BIGINT;
    v_program BIGINT;
    v_first_day BIGINT;
BEGIN
    -- taking a preset always creates an own copy
    INSERT INTO routine (user_id, name, description)
    SELECT v_user, name, description FROM routine WHERE slug = 'pull'
    RETURNING id INTO v_pull;
    INSERT INTO routine_exercise (routine_id, exercise_id, order_index, target_sets, target_reps_min, target_reps_max, target_duration_seconds, target_distance_meters)
    SELECT v_pull, exercise_id, order_index, target_sets, target_reps_min, target_reps_max, target_duration_seconds, target_distance_meters
    FROM routine_exercise WHERE routine_id = (SELECT id FROM routine WHERE slug = 'pull');

    INSERT INTO program (user_id, name, is_active)
    VALUES (v_user, 'Push Pull', TRUE)
    RETURNING id INTO v_program;

    -- routine_id NULL is a rest day
    INSERT INTO program_day (user_id, program_id, routine_id, order_index)
    VALUES (v_user, v_program, v_push, 0)
    RETURNING id INTO v_first_day;
    INSERT INTO program_day (user_id, program_id, routine_id, order_index) VALUES
        (v_user, v_program, v_pull, 1),
        (v_user, v_program, NULL, 2);

    UPDATE workout SET program_day_id = v_first_day
    WHERE user_id = v_user AND started_at = TIMESTAMPTZ '2026-09-28 18:00:00+02';

    INSERT INTO program (user_id, name, description)
    VALUES (v_user, 'Push only', 'Trying out a lower frequency.')
    RETURNING id INTO v_program;
    INSERT INTO program_day (user_id, program_id, routine_id, order_index) VALUES
        (v_user, v_program, v_push, 0),
        (v_user, v_program, NULL, 1),
        (v_user, v_program, NULL, 2);
END $$;

-- lena_lifts: her running workout belongs to the first slot of her active program
DO $$
DECLARE
    v_user BIGINT := (SELECT id FROM userdata WHERE username = 'lena_lifts');
    v_program BIGINT;
    v_first_day BIGINT;
BEGIN
    INSERT INTO program (user_id, name, is_active)
    VALUES (v_user, 'Legs and rest', TRUE)
    RETURNING id INTO v_program;

    INSERT INTO program_day (user_id, program_id, routine_id, order_index)
    VALUES (v_user, v_program, (SELECT id FROM routine WHERE user_id = v_user AND name = 'Legs'), 0)
    RETURNING id INTO v_first_day;
    INSERT INTO program_day (user_id, program_id, routine_id, order_index)
    VALUES (v_user, v_program, NULL, 1);

    UPDATE workout SET program_day_id = v_first_day
    WHERE user_id = v_user AND status = 'IN_PROGRESS';
END $$;

-- max: the preset program taken over, as the copy endpoint will do it (program, slots and an own copy of every routine)
DO $$
DECLARE
    v_user BIGINT := (SELECT id FROM userdata WHERE username = 'max');
    v_preset BIGINT := (SELECT id FROM program WHERE slug = 'push-pull-legs');
    v_program BIGINT;
    v_slot RECORD;
    v_copy BIGINT;
BEGIN
    INSERT INTO program (user_id, name, description)
    SELECT v_user, name, description FROM program WHERE id = v_preset
    RETURNING id INTO v_program;

    -- the preset uses every routine once, so one copy per slot is one copy per routine
    FOR v_slot IN SELECT routine_id, order_index FROM program_day WHERE program_id = v_preset ORDER BY order_index LOOP
        v_copy := NULL;
        IF v_slot.routine_id IS NOT NULL THEN
            INSERT INTO routine (user_id, name, description)
            SELECT v_user, name, description FROM routine WHERE id = v_slot.routine_id
            RETURNING id INTO v_copy;
            INSERT INTO routine_exercise (routine_id, exercise_id, order_index, target_sets, target_reps_min, target_reps_max, target_duration_seconds, target_distance_meters)
            SELECT v_copy, exercise_id, order_index, target_sets, target_reps_min, target_reps_max, target_duration_seconds, target_distance_meters
            FROM routine_exercise WHERE routine_id = v_slot.routine_id;
        END IF;
        INSERT INTO program_day (user_id, program_id, routine_id, order_index)
        VALUES (v_user, v_program, v_copy, v_slot.order_index);
    END LOOP;
END $$;
