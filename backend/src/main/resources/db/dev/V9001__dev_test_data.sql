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
INSERT INTO exercise (owner_user_id, name, exercise_type, description, instructions) VALUES ((SELECT id FROM userdata WHERE username = 'max'), 'Slow Tempo Push-Up', 'STRENGTH', 'Custom push-up variation with a 4 second eccentric.', 'Lower for four seconds, pause briefly at the bottom, press up explosively.');
INSERT INTO exercise (owner_user_id, name, exercise_type, description, instructions) VALUES ((SELECT id FROM userdata WHERE username = 'lena_lifts'), 'Zone 2 Treadmill Run', 'CARDIO', 'Custom steady state run in heart rate zone 2.', 'Run 30 to 45 minutes at a pace that still allows talking.');
INSERT INTO exercise (owner_user_id, name, exercise_type, description, instructions) VALUES ((SELECT id FROM userdata WHERE username = 'marco'), 'Assault Bike Intervals', 'CARDIO', 'Custom HIIT on the air bike.', '10 rounds of 20 seconds all out, 40 seconds easy.');
-- soft deleted exercise
INSERT INTO exercise (owner_user_id, name, exercise_type, description, instructions, deleted_at) VALUES ((SELECT id FROM userdata WHERE username = 'sina'), 'Old Smith Machine Press', 'STRENGTH', 'Custom exercise that was removed again.', 'No longer in use.', TIMESTAMPTZ '2026-05-14 18:22:00+02');

-- custom exercises
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE name = 'Slow Tempo Push-Up'), (SELECT id FROM muscle_group WHERE name = 'Chest'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE name = 'Slow Tempo Push-Up'), (SELECT id FROM muscle_group WHERE name = 'Triceps'), 'SECONDARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE name = 'Zone 2 Treadmill Run'), (SELECT id FROM muscle_group WHERE name = 'Quadriceps'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE name = 'Zone 2 Treadmill Run'), (SELECT id FROM muscle_group WHERE name = 'Calves'), 'SECONDARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE name = 'Assault Bike Intervals'), (SELECT id FROM muscle_group WHERE name = 'Quadriceps'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE name = 'Assault Bike Intervals'), (SELECT id FROM muscle_group WHERE name = 'Glutes'), 'SECONDARY');
