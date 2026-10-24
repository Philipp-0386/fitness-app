-- Initial accounts and the data owned by them. Production only (prod profile)
-- Repeatable and idempotent

-- userdata
UPDATE userdata SET username = 'luca1' WHERE username = 'luca';
INSERT INTO userdata (username, email, password_hashed, role_id)
VALUES ('admin', 'admin@fitness.local', '${seed_admin_password_hash}', 0)
ON CONFLICT (username) DO NOTHING;
INSERT INTO userdata (username, email, password_hashed, role_id)
VALUES ('luca1', 'luca@fitness.local', '${seed_user_password_hash}', 1)
ON CONFLICT (username) DO NOTHING;

-- user_settings (defaults)
INSERT INTO user_settings (user_id)
SELECT id FROM userdata WHERE username IN ('admin', 'luca1')
ON CONFLICT (user_id) DO NOTHING;

-- custom exercises
INSERT INTO exercise (owner_user_id, name, exercise_type, tracking_type, description, instructions)
VALUES ((SELECT id FROM userdata WHERE username = 'luca1'), 'Slow Tempo Push-Up', 'STRENGTH', 'BODYWEIGHT_REPS', 'Custom push-up variation with a 4 second eccentric.', 'Lower for four seconds, pause briefly at the bottom, press up explosively.')
ON CONFLICT (owner_user_id, name) WHERE deleted_at IS NULL DO NOTHING;

INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role)
VALUES ((SELECT e.id FROM exercise e JOIN userdata u ON u.id = e.owner_user_id WHERE u.username = 'luca1' AND e.name = 'Slow Tempo Push-Up' AND e.deleted_at IS NULL), (SELECT id FROM muscle_group WHERE name = 'Chest'), 'PRIMARY')
ON CONFLICT DO NOTHING;
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role)
VALUES ((SELECT e.id FROM exercise e JOIN userdata u ON u.id = e.owner_user_id WHERE u.username = 'luca1' AND e.name = 'Slow Tempo Push-Up' AND e.deleted_at IS NULL), (SELECT id FROM muscle_group WHERE name = 'Triceps'), 'SECONDARY')
ON CONFLICT DO NOTHING;
