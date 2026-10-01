-- Reference data: roles, muscle groups and the global exercise catalog.
-- Runs in every environment, production included.

-- roles
INSERT INTO roles (name) VALUES ('USER');
INSERT INTO roles (id, name) VALUES (0, 'ADMIN');

-- muscle_group
INSERT INTO muscle_group (name, body_region) VALUES ('Chest', 'UPPER');
INSERT INTO muscle_group (name, body_region) VALUES ('Upper Back', 'UPPER');
INSERT INTO muscle_group (name, body_region) VALUES ('Lats', 'UPPER');
INSERT INTO muscle_group (name, body_region) VALUES ('Trapezius', 'UPPER');
INSERT INTO muscle_group (name, body_region) VALUES ('Front Delts', 'UPPER');
INSERT INTO muscle_group (name, body_region) VALUES ('Side Delts', 'UPPER');
INSERT INTO muscle_group (name, body_region) VALUES ('Rear Delts', 'UPPER');
INSERT INTO muscle_group (name, body_region) VALUES ('Biceps', 'UPPER');
INSERT INTO muscle_group (name, body_region) VALUES ('Triceps', 'UPPER');
INSERT INTO muscle_group (name, body_region) VALUES ('Forearms', 'UPPER');
INSERT INTO muscle_group (name, body_region) VALUES ('Neck', 'UPPER');
INSERT INTO muscle_group (name, body_region) VALUES ('Abs', 'CORE');
INSERT INTO muscle_group (name, body_region) VALUES ('Obliques', 'CORE');
INSERT INTO muscle_group (name, body_region) VALUES ('Lower Back', 'CORE');
INSERT INTO muscle_group (name, body_region) VALUES ('Quadriceps', 'LOWER');
INSERT INTO muscle_group (name, body_region) VALUES ('Hamstrings', 'LOWER');
INSERT INTO muscle_group (name, body_region) VALUES ('Glutes', 'LOWER');
INSERT INTO muscle_group (name, body_region) VALUES ('Calves', 'LOWER');
INSERT INTO muscle_group (name, body_region) VALUES ('Hip Adductors', 'LOWER');
INSERT INTO muscle_group (name, body_region) VALUES ('Hip Abductors', 'LOWER');
INSERT INTO muscle_group (name, body_region) VALUES ('Tibialis Anterior', 'LOWER');

-- exercise (owner_user_id NULL = standard exercise, set = custom exercise of that user)
-- Global exercises are referenced by slug. A name can repeat across owners, so a lookup by name breaks once users exist.
-- Chest
INSERT INTO exercise (owner_user_id, slug, name, exercise_type, tracking_type, description, instructions) VALUES (NULL, 'barbell-bench-press', 'Barbell Bench Press', 'STRENGTH', 'WEIGHT_REPS', 'Flat barbell press, main horizontal pushing movement.', 'Lie flat, grip slightly wider than shoulders, lower to mid chest, press back up.');
INSERT INTO exercise (owner_user_id, slug, name, exercise_type, tracking_type, description, instructions) VALUES (NULL, 'incline-dumbbell-press', 'Incline Dumbbell Press', 'STRENGTH', 'WEIGHT_REPS', 'Incline press emphasising the upper chest.', 'Set bench to 30 degrees, press dumbbells up without locking harshly.');
INSERT INTO exercise (owner_user_id, slug, name, exercise_type, tracking_type, description, instructions) VALUES (NULL, 'cable-chest-fly', 'Cable Chest Fly', 'STRENGTH', 'WEIGHT_REPS', 'Isolation for the chest with constant cable tension.', 'Slight elbow bend, bring handles together in front of the sternum.');
-- Upper Back
INSERT INTO exercise (owner_user_id, slug, name, exercise_type, tracking_type, description, instructions) VALUES (NULL, 'barbell-row', 'Barbell Row', 'STRENGTH', 'WEIGHT_REPS', 'Bent-over row for mid back thickness.', 'Hinge to about 45 degrees, row the bar to the lower ribs, keep spine neutral.');
INSERT INTO exercise (owner_user_id, slug, name, exercise_type, tracking_type, description, instructions) VALUES (NULL, 'seated-cable-row', 'Seated Cable Row', 'STRENGTH', 'WEIGHT_REPS', 'Horizontal pull on the cable stack.', 'Sit upright, pull the handle to the navel, control the return.');
INSERT INTO exercise (owner_user_id, slug, name, exercise_type, tracking_type, description, instructions) VALUES (NULL, 'chest-supported-dumbbell-row', 'Chest-Supported Dumbbell Row', 'STRENGTH', 'WEIGHT_REPS', 'Row variation without lower back involvement.', 'Chest on an incline bench, row dumbbells towards the hips.');
-- Lats
INSERT INTO exercise (owner_user_id, slug, name, exercise_type, tracking_type, description, instructions) VALUES (NULL, 'pull-up', 'Pull-Up', 'STRENGTH', 'BODYWEIGHT_REPS', 'Bodyweight vertical pull.', 'Hang with pronated grip, pull the chest towards the bar, lower fully.');
INSERT INTO exercise (owner_user_id, slug, name, exercise_type, tracking_type, description, instructions) VALUES (NULL, 'lat-pulldown', 'Lat Pulldown', 'STRENGTH', 'WEIGHT_REPS', 'Machine vertical pull, scalable load.', 'Pull the bar to the upper chest, keep the torso nearly upright.');
INSERT INTO exercise (owner_user_id, slug, name, exercise_type, tracking_type, description, instructions) VALUES (NULL, 'straight-arm-pulldown', 'Straight-Arm Pulldown', 'STRENGTH', 'WEIGHT_REPS', 'Lat isolation with extended arms.', 'Keep elbows locked, pull the bar down to the thighs.');
-- Trapezius
INSERT INTO exercise (owner_user_id, slug, name, exercise_type, tracking_type, description, instructions) VALUES (NULL, 'barbell-shrug', 'Barbell Shrug', 'STRENGTH', 'WEIGHT_REPS', 'Shrug for the upper traps.', 'Shrug straight up, pause briefly, lower under control.');
INSERT INTO exercise (owner_user_id, slug, name, exercise_type, tracking_type, description, instructions) VALUES (NULL, 'dumbbell-shrug', 'Dumbbell Shrug', 'STRENGTH', 'WEIGHT_REPS', 'Shrug with a longer range of motion.', 'Dumbbells at the sides, elevate the shoulders without rolling them.');
INSERT INTO exercise (owner_user_id, slug, name, exercise_type, tracking_type, description, instructions) VALUES (NULL, 'farmers-walk', 'Farmers Walk', 'STRENGTH', 'WEIGHT_DISTANCE', 'Loaded carry for traps, forearms and core.', 'Carry heavy dumbbells for distance or time, ribs down, shoulders packed.');
-- Front Delts
INSERT INTO exercise (owner_user_id, slug, name, exercise_type, tracking_type, description, instructions) VALUES (NULL, 'overhead-barbell-press', 'Overhead Barbell Press', 'STRENGTH', 'WEIGHT_REPS', 'Standing vertical press.', 'Press the bar overhead, move the head back slightly, lock out over mid foot.');
INSERT INTO exercise (owner_user_id, slug, name, exercise_type, tracking_type, description, instructions) VALUES (NULL, 'seated-dumbbell-shoulder-press', 'Seated Dumbbell Shoulder Press', 'STRENGTH', 'WEIGHT_REPS', 'Supported vertical press.', 'Press dumbbells from shoulder height to lockout, elbows slightly in front.');
INSERT INTO exercise (owner_user_id, slug, name, exercise_type, tracking_type, description, instructions) VALUES (NULL, 'dumbbell-front-raise', 'Dumbbell Front Raise', 'STRENGTH', 'WEIGHT_REPS', 'Isolation for the anterior deltoid.', 'Raise the dumbbells to eye level with a slight elbow bend.');
-- Side Delts
INSERT INTO exercise (owner_user_id, slug, name, exercise_type, tracking_type, description, instructions) VALUES (NULL, 'dumbbell-lateral-raise', 'Dumbbell Lateral Raise', 'STRENGTH', 'WEIGHT_REPS', 'Classic isolation for the lateral deltoid.', 'Raise to shoulder height, lead with the elbows, no swinging.');
INSERT INTO exercise (owner_user_id, slug, name, exercise_type, tracking_type, description, instructions) VALUES (NULL, 'cable-lateral-raise', 'Cable Lateral Raise', 'STRENGTH', 'WEIGHT_REPS', 'Lateral raise with constant tension.', 'Cable from the low pulley behind the body, raise sideways to shoulder height.');
INSERT INTO exercise (owner_user_id, slug, name, exercise_type, tracking_type, description, instructions) VALUES (NULL, 'upright-row', 'Upright Row', 'STRENGTH', 'WEIGHT_REPS', 'Vertical pull for side delts and traps.', 'Shoulder-width grip, pull to the lower chest, keep elbows above the wrists.');
-- Rear Delts
INSERT INTO exercise (owner_user_id, slug, name, exercise_type, tracking_type, description, instructions) VALUES (NULL, 'face-pull', 'Face Pull', 'STRENGTH', 'WEIGHT_REPS', 'Rear delt and external rotator work.', 'Rope at face height, pull towards the forehead, elbows high.');
INSERT INTO exercise (owner_user_id, slug, name, exercise_type, tracking_type, description, instructions) VALUES (NULL, 'reverse-pec-deck', 'Reverse Pec Deck', 'STRENGTH', 'WEIGHT_REPS', 'Machine reverse fly.', 'Chest against the pad, open the arms in a wide arc.');
INSERT INTO exercise (owner_user_id, slug, name, exercise_type, tracking_type, description, instructions) VALUES (NULL, 'bent-over-dumbbell-reverse-fly', 'Bent-Over Dumbbell Reverse Fly', 'STRENGTH', 'WEIGHT_REPS', 'Free weight reverse fly.', 'Hinge forward, raise dumbbells sideways with slightly bent elbows.');
-- Biceps
INSERT INTO exercise (owner_user_id, slug, name, exercise_type, tracking_type, description, instructions) VALUES (NULL, 'barbell-curl', 'Barbell Curl', 'STRENGTH', 'WEIGHT_REPS', 'Basic biceps curl with the barbell.', 'Elbows at the sides, curl up, lower for about three seconds.');
INSERT INTO exercise (owner_user_id, slug, name, exercise_type, tracking_type, description, instructions) VALUES (NULL, 'incline-dumbbell-curl', 'Incline Dumbbell Curl', 'STRENGTH', 'WEIGHT_REPS', 'Curl in a stretched shoulder position.', 'Bench at 45 degrees, let the arms hang, curl without moving the elbows.');
INSERT INTO exercise (owner_user_id, slug, name, exercise_type, tracking_type, description, instructions) VALUES (NULL, 'hammer-curl', 'Hammer Curl', 'STRENGTH', 'WEIGHT_REPS', 'Neutral grip curl for biceps and brachialis.', 'Neutral grip, curl up, keep the wrists straight.');
-- Triceps
INSERT INTO exercise (owner_user_id, slug, name, exercise_type, tracking_type, description, instructions) VALUES (NULL, 'close-grip-bench-press', 'Close-Grip Bench Press', 'STRENGTH', 'WEIGHT_REPS', 'Compound triceps press.', 'Shoulder-width grip, elbows close to the body, press to lockout.');
INSERT INTO exercise (owner_user_id, slug, name, exercise_type, tracking_type, description, instructions) VALUES (NULL, 'triceps-rope-pushdown', 'Triceps Rope Pushdown', 'STRENGTH', 'WEIGHT_REPS', 'Cable isolation for the triceps.', 'Elbows fixed at the sides, push down and spread the rope at the bottom.');
INSERT INTO exercise (owner_user_id, slug, name, exercise_type, tracking_type, description, instructions) VALUES (NULL, 'overhead-cable-triceps-extension', 'Overhead Cable Triceps Extension', 'STRENGTH', 'WEIGHT_REPS', 'Triceps work in the stretched position.', 'Rope overhead, extend the elbows fully, keep the upper arms still.');
-- Forearms
INSERT INTO exercise (owner_user_id, slug, name, exercise_type, tracking_type, description, instructions) VALUES (NULL, 'wrist-curl', 'Wrist Curl', 'STRENGTH', 'WEIGHT_REPS', 'Flexor training for the forearms.', 'Forearms on the thighs, curl the wrists up, full range of motion.');
INSERT INTO exercise (owner_user_id, slug, name, exercise_type, tracking_type, description, instructions) VALUES (NULL, 'reverse-wrist-curl', 'Reverse Wrist Curl', 'STRENGTH', 'WEIGHT_REPS', 'Extensor training for the forearms.', 'Pronated grip, extend the wrists upwards, use light weight.');
INSERT INTO exercise (owner_user_id, slug, name, exercise_type, tracking_type, description, instructions) VALUES (NULL, 'dead-hang', 'Dead Hang', 'MOBILITY', 'DURATION', 'Passive hang for grip and shoulder health.', 'Hang from the bar with relaxed shoulders, hold for time.');
-- Neck
INSERT INTO exercise (owner_user_id, slug, name, exercise_type, tracking_type, description, instructions) VALUES (NULL, 'neck-curl', 'Neck Curl', 'STRENGTH', 'WEIGHT_REPS', 'Flexion for the front of the neck.', 'Lie supine, plate on the forehead, flex the chin towards the chest.');
INSERT INTO exercise (owner_user_id, slug, name, exercise_type, tracking_type, description, instructions) VALUES (NULL, 'neck-extension', 'Neck Extension', 'STRENGTH', 'WEIGHT_REPS', 'Extension for the back of the neck.', 'Lie prone, plate on the back of the head, extend the neck slowly.');
INSERT INTO exercise (owner_user_id, slug, name, exercise_type, tracking_type, description, instructions) VALUES (NULL, 'lateral-neck-flexion', 'Lateral Neck Flexion', 'STRENGTH', 'WEIGHT_REPS', 'Side flexion for the neck.', 'Lie on your side, move the ear towards the shoulder, use light load.');
-- Abs
INSERT INTO exercise (owner_user_id, slug, name, exercise_type, tracking_type, description, instructions) VALUES (NULL, 'hanging-leg-raise', 'Hanging Leg Raise', 'STRENGTH', 'BODYWEIGHT_REPS', 'Abdominal work while hanging.', 'Hang from the bar, raise the legs to hip height or above, no swinging.');
INSERT INTO exercise (owner_user_id, slug, name, exercise_type, tracking_type, description, instructions) VALUES (NULL, 'cable-crunch', 'Cable Crunch', 'STRENGTH', 'WEIGHT_REPS', 'Loadable crunch on the cable.', 'Kneel, rope behind the head, flex the spine down against the cable.');
INSERT INTO exercise (owner_user_id, slug, name, exercise_type, tracking_type, description, instructions) VALUES (NULL, 'plank', 'Plank', 'STRENGTH', 'DURATION', 'Isometric hold for the whole trunk.', 'Forearms and toes on the floor, keep hips, back and head in line.');
-- Obliques
INSERT INTO exercise (owner_user_id, slug, name, exercise_type, tracking_type, description, instructions) VALUES (NULL, 'russian-twist', 'Russian Twist', 'STRENGTH', 'WEIGHT_REPS', 'Rotational work for the obliques.', 'Sit with the torso leaned back, rotate a weight from side to side.');
INSERT INTO exercise (owner_user_id, slug, name, exercise_type, tracking_type, description, instructions) VALUES (NULL, 'cable-woodchop', 'Cable Woodchop', 'STRENGTH', 'WEIGHT_REPS', 'Diagonal rotation on the cable.', 'Pull the handle diagonally across the body, rotate through the hips.');
INSERT INTO exercise (owner_user_id, slug, name, exercise_type, tracking_type, description, instructions) VALUES (NULL, 'side-plank', 'Side Plank', 'STRENGTH', 'DURATION', 'Lateral isometric hold.', 'Support on one forearm, lift the hips, hold in a straight line.');
-- Lower Back
INSERT INTO exercise (owner_user_id, slug, name, exercise_type, tracking_type, description, instructions) VALUES (NULL, 'back-extension', 'Back Extension', 'STRENGTH', 'BODYWEIGHT_REPS', 'Extension for the erector spinae.', 'Hinge at the hips on the bench, extend back to a neutral spine.');
INSERT INTO exercise (owner_user_id, slug, name, exercise_type, tracking_type, description, instructions) VALUES (NULL, 'good-morning', 'Good Morning', 'STRENGTH', 'WEIGHT_REPS', 'Hip hinge with the bar on the back.', 'Push the hips back with a neutral spine, come up by extending the hips.');
INSERT INTO exercise (owner_user_id, slug, name, exercise_type, tracking_type, description, instructions) VALUES (NULL, 'conventional-deadlift', 'Conventional Deadlift', 'STRENGTH', 'WEIGHT_REPS', 'Full body pull from the floor.', 'Bar over mid foot, brace, push the floor away, lock out the hips.');
-- Quadriceps
INSERT INTO exercise (owner_user_id, slug, name, exercise_type, tracking_type, description, instructions) VALUES (NULL, 'barbell-back-squat', 'Barbell Back Squat', 'STRENGTH', 'WEIGHT_REPS', 'Main squat pattern.', 'Bar on the upper back, squat to at least parallel, keep the knees tracking the toes.');
INSERT INTO exercise (owner_user_id, slug, name, exercise_type, tracking_type, description, instructions) VALUES (NULL, 'leg-press', 'Leg Press', 'STRENGTH', 'WEIGHT_REPS', 'Machine leg press.', 'Feet shoulder-width on the platform, lower to a deep knee angle, press back.');
INSERT INTO exercise (owner_user_id, slug, name, exercise_type, tracking_type, description, instructions) VALUES (NULL, 'leg-extension', 'Leg Extension', 'STRENGTH', 'WEIGHT_REPS', 'Isolation for the quadriceps.', 'Extend the knees fully, pause at the top, lower under control.');
-- Hamstrings
INSERT INTO exercise (owner_user_id, slug, name, exercise_type, tracking_type, description, instructions) VALUES (NULL, 'romanian-deadlift', 'Romanian Deadlift', 'STRENGTH', 'WEIGHT_REPS', 'Hip hinge with a stretch on the hamstrings.', 'Push the hips back, bar close to the legs, stop at the end of the stretch.');
INSERT INTO exercise (owner_user_id, slug, name, exercise_type, tracking_type, description, instructions) VALUES (NULL, 'lying-leg-curl', 'Lying Leg Curl', 'STRENGTH', 'WEIGHT_REPS', 'Knee flexion lying face down.', 'Curl the heels towards the glutes, keep the hips on the pad.');
INSERT INTO exercise (owner_user_id, slug, name, exercise_type, tracking_type, description, instructions) VALUES (NULL, 'seated-leg-curl', 'Seated Leg Curl', 'STRENGTH', 'WEIGHT_REPS', 'Knee flexion in hip flexion.', 'Sit upright, curl against the pad, control the return.');
-- Glutes
INSERT INTO exercise (owner_user_id, slug, name, exercise_type, tracking_type, description, instructions) VALUES (NULL, 'barbell-hip-thrust', 'Barbell Hip Thrust', 'STRENGTH', 'WEIGHT_REPS', 'Main glute exercise.', 'Upper back on the bench, drive the hips up, ribs down at the top.');
INSERT INTO exercise (owner_user_id, slug, name, exercise_type, tracking_type, description, instructions) VALUES (NULL, 'bulgarian-split-squat', 'Bulgarian Split Squat', 'STRENGTH', 'WEIGHT_REPS', 'Unilateral squat with rear foot elevated.', 'Rear foot on the bench, lower straight down, torso slightly forward.');
INSERT INTO exercise (owner_user_id, slug, name, exercise_type, tracking_type, description, instructions) VALUES (NULL, 'cable-glute-kickback', 'Cable Glute Kickback', 'STRENGTH', 'WEIGHT_REPS', 'Hip extension isolation on the cable.', 'Ankle strap on the cable, extend the hip back, avoid arching the lower back.');
-- Calves
INSERT INTO exercise (owner_user_id, slug, name, exercise_type, tracking_type, description, instructions) VALUES (NULL, 'standing-calf-raise', 'Standing Calf Raise', 'STRENGTH', 'WEIGHT_REPS', 'Calf raise with extended knees.', 'Full stretch at the bottom, push up onto the toes, pause at the top.');
INSERT INTO exercise (owner_user_id, slug, name, exercise_type, tracking_type, description, instructions) VALUES (NULL, 'seated-calf-raise', 'Seated Calf Raise', 'STRENGTH', 'WEIGHT_REPS', 'Calf raise with bent knees for the soleus.', 'Pad on the thighs, push up onto the toes, lower slowly.');
INSERT INTO exercise (owner_user_id, slug, name, exercise_type, tracking_type, description, instructions) VALUES (NULL, 'donkey-calf-raise', 'Donkey Calf Raise', 'STRENGTH', 'WEIGHT_REPS', 'Calf raise in a hip hinge position.', 'Torso bent forward, raise the heels, full range of motion.');
-- Hip Adductors
INSERT INTO exercise (owner_user_id, slug, name, exercise_type, tracking_type, description, instructions) VALUES (NULL, 'hip-adduction-machine', 'Hip Adduction Machine', 'STRENGTH', 'WEIGHT_REPS', 'Machine work for the adductors.', 'Press the legs together against the pads, control the return.');
INSERT INTO exercise (owner_user_id, slug, name, exercise_type, tracking_type, description, instructions) VALUES (NULL, 'cable-hip-adduction', 'Cable Hip Adduction', 'STRENGTH', 'WEIGHT_REPS', 'Unilateral adduction on the cable.', 'Ankle strap on the outside leg, pull the leg across the midline.');
INSERT INTO exercise (owner_user_id, slug, name, exercise_type, tracking_type, description, instructions) VALUES (NULL, 'copenhagen-plank', 'Copenhagen Plank', 'STRENGTH', 'DURATION', 'Isometric adductor hold.', 'Upper leg on the bench, lift the hips, hold in a straight line.');
-- Hip Abductors
INSERT INTO exercise (owner_user_id, slug, name, exercise_type, tracking_type, description, instructions) VALUES (NULL, 'hip-abduction-machine', 'Hip Abduction Machine', 'STRENGTH', 'WEIGHT_REPS', 'Machine work for the abductors.', 'Press the legs apart, hold briefly, return under control.');
INSERT INTO exercise (owner_user_id, slug, name, exercise_type, tracking_type, description, instructions) VALUES (NULL, 'cable-hip-abduction', 'Cable Hip Abduction', 'STRENGTH', 'WEIGHT_REPS', 'Unilateral abduction on the cable.', 'Ankle strap on the inside leg, move the leg outwards, keep the torso still.');
INSERT INTO exercise (owner_user_id, slug, name, exercise_type, tracking_type, description, instructions) VALUES (NULL, 'banded-lateral-walk', 'Banded Lateral Walk', 'MOBILITY', 'BODYWEIGHT_REPS', 'Activation for the gluteus medius.', 'Band above the knees, step sideways in a half squat.');
-- Tibialis Anterior
INSERT INTO exercise (owner_user_id, slug, name, exercise_type, tracking_type, description, instructions) VALUES (NULL, 'tibialis-raise', 'Tibialis Raise', 'STRENGTH', 'BODYWEIGHT_REPS', 'Dorsiflexion against the wall or a machine.', 'Heels on the floor, pull the toes up, lower slowly.');
INSERT INTO exercise (owner_user_id, slug, name, exercise_type, tracking_type, description, instructions) VALUES (NULL, 'weighted-toe-raise', 'Weighted Toe Raise', 'STRENGTH', 'WEIGHT_REPS', 'Loaded dorsiflexion.', 'Weight over the forefoot, raise the toes, pause at the top.');
INSERT INTO exercise (owner_user_id, slug, name, exercise_type, tracking_type, description, instructions) VALUES (NULL, 'banded-dorsiflexion', 'Banded Dorsiflexion', 'MOBILITY', 'BODYWEIGHT_REPS', 'Band work for the tibialis anterior.', 'Band around the forefoot, pull the foot towards the shin.');

-- exercise_musclegroup
-- Chest
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'barbell-bench-press'), (SELECT id FROM muscle_group WHERE name = 'Chest'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'barbell-bench-press'), (SELECT id FROM muscle_group WHERE name = 'Triceps'), 'SECONDARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'barbell-bench-press'), (SELECT id FROM muscle_group WHERE name = 'Front Delts'), 'SECONDARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'incline-dumbbell-press'), (SELECT id FROM muscle_group WHERE name = 'Chest'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'incline-dumbbell-press'), (SELECT id FROM muscle_group WHERE name = 'Front Delts'), 'SECONDARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'incline-dumbbell-press'), (SELECT id FROM muscle_group WHERE name = 'Triceps'), 'SECONDARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'cable-chest-fly'), (SELECT id FROM muscle_group WHERE name = 'Chest'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'cable-chest-fly'), (SELECT id FROM muscle_group WHERE name = 'Front Delts'), 'SECONDARY');
-- Upper Back
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'barbell-row'), (SELECT id FROM muscle_group WHERE name = 'Upper Back'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'barbell-row'), (SELECT id FROM muscle_group WHERE name = 'Lats'), 'SECONDARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'barbell-row'), (SELECT id FROM muscle_group WHERE name = 'Biceps'), 'SECONDARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'seated-cable-row'), (SELECT id FROM muscle_group WHERE name = 'Upper Back'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'seated-cable-row'), (SELECT id FROM muscle_group WHERE name = 'Lats'), 'SECONDARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'seated-cable-row'), (SELECT id FROM muscle_group WHERE name = 'Biceps'), 'SECONDARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'chest-supported-dumbbell-row'), (SELECT id FROM muscle_group WHERE name = 'Upper Back'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'chest-supported-dumbbell-row'), (SELECT id FROM muscle_group WHERE name = 'Rear Delts'), 'SECONDARY');
-- Lats
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'pull-up'), (SELECT id FROM muscle_group WHERE name = 'Lats'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'pull-up'), (SELECT id FROM muscle_group WHERE name = 'Biceps'), 'SECONDARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'pull-up'), (SELECT id FROM muscle_group WHERE name = 'Upper Back'), 'SECONDARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'lat-pulldown'), (SELECT id FROM muscle_group WHERE name = 'Lats'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'lat-pulldown'), (SELECT id FROM muscle_group WHERE name = 'Biceps'), 'SECONDARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'straight-arm-pulldown'), (SELECT id FROM muscle_group WHERE name = 'Lats'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'straight-arm-pulldown'), (SELECT id FROM muscle_group WHERE name = 'Triceps'), 'SECONDARY');
-- Trapezius
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'barbell-shrug'), (SELECT id FROM muscle_group WHERE name = 'Trapezius'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'barbell-shrug'), (SELECT id FROM muscle_group WHERE name = 'Forearms'), 'SECONDARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'dumbbell-shrug'), (SELECT id FROM muscle_group WHERE name = 'Trapezius'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'dumbbell-shrug'), (SELECT id FROM muscle_group WHERE name = 'Forearms'), 'SECONDARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'farmers-walk'), (SELECT id FROM muscle_group WHERE name = 'Trapezius'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'farmers-walk'), (SELECT id FROM muscle_group WHERE name = 'Forearms'), 'SECONDARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'farmers-walk'), (SELECT id FROM muscle_group WHERE name = 'Abs'), 'SECONDARY');
-- Front Delts
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'overhead-barbell-press'), (SELECT id FROM muscle_group WHERE name = 'Front Delts'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'overhead-barbell-press'), (SELECT id FROM muscle_group WHERE name = 'Triceps'), 'SECONDARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'overhead-barbell-press'), (SELECT id FROM muscle_group WHERE name = 'Side Delts'), 'SECONDARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'seated-dumbbell-shoulder-press'), (SELECT id FROM muscle_group WHERE name = 'Front Delts'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'seated-dumbbell-shoulder-press'), (SELECT id FROM muscle_group WHERE name = 'Triceps'), 'SECONDARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'dumbbell-front-raise'), (SELECT id FROM muscle_group WHERE name = 'Front Delts'), 'PRIMARY');
-- Side Delts
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'dumbbell-lateral-raise'), (SELECT id FROM muscle_group WHERE name = 'Side Delts'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'cable-lateral-raise'), (SELECT id FROM muscle_group WHERE name = 'Side Delts'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'upright-row'), (SELECT id FROM muscle_group WHERE name = 'Side Delts'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'upright-row'), (SELECT id FROM muscle_group WHERE name = 'Trapezius'), 'SECONDARY');
-- Rear Delts
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'face-pull'), (SELECT id FROM muscle_group WHERE name = 'Rear Delts'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'face-pull'), (SELECT id FROM muscle_group WHERE name = 'Trapezius'), 'SECONDARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'reverse-pec-deck'), (SELECT id FROM muscle_group WHERE name = 'Rear Delts'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'reverse-pec-deck'), (SELECT id FROM muscle_group WHERE name = 'Upper Back'), 'SECONDARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'bent-over-dumbbell-reverse-fly'), (SELECT id FROM muscle_group WHERE name = 'Rear Delts'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'bent-over-dumbbell-reverse-fly'), (SELECT id FROM muscle_group WHERE name = 'Upper Back'), 'SECONDARY');
-- Biceps
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'barbell-curl'), (SELECT id FROM muscle_group WHERE name = 'Biceps'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'barbell-curl'), (SELECT id FROM muscle_group WHERE name = 'Forearms'), 'SECONDARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'incline-dumbbell-curl'), (SELECT id FROM muscle_group WHERE name = 'Biceps'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'hammer-curl'), (SELECT id FROM muscle_group WHERE name = 'Biceps'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'hammer-curl'), (SELECT id FROM muscle_group WHERE name = 'Forearms'), 'SECONDARY');
-- Triceps
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'close-grip-bench-press'), (SELECT id FROM muscle_group WHERE name = 'Triceps'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'close-grip-bench-press'), (SELECT id FROM muscle_group WHERE name = 'Chest'), 'SECONDARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'triceps-rope-pushdown'), (SELECT id FROM muscle_group WHERE name = 'Triceps'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'overhead-cable-triceps-extension'), (SELECT id FROM muscle_group WHERE name = 'Triceps'), 'PRIMARY');
-- Forearms
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'wrist-curl'), (SELECT id FROM muscle_group WHERE name = 'Forearms'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'reverse-wrist-curl'), (SELECT id FROM muscle_group WHERE name = 'Forearms'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'dead-hang'), (SELECT id FROM muscle_group WHERE name = 'Forearms'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'dead-hang'), (SELECT id FROM muscle_group WHERE name = 'Lats'), 'SECONDARY');
-- Neck
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'neck-curl'), (SELECT id FROM muscle_group WHERE name = 'Neck'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'neck-extension'), (SELECT id FROM muscle_group WHERE name = 'Neck'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'neck-extension'), (SELECT id FROM muscle_group WHERE name = 'Trapezius'), 'SECONDARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'lateral-neck-flexion'), (SELECT id FROM muscle_group WHERE name = 'Neck'), 'PRIMARY');
-- Abs
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'hanging-leg-raise'), (SELECT id FROM muscle_group WHERE name = 'Abs'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'hanging-leg-raise'), (SELECT id FROM muscle_group WHERE name = 'Obliques'), 'SECONDARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'hanging-leg-raise'), (SELECT id FROM muscle_group WHERE name = 'Forearms'), 'SECONDARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'cable-crunch'), (SELECT id FROM muscle_group WHERE name = 'Abs'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'plank'), (SELECT id FROM muscle_group WHERE name = 'Abs'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'plank'), (SELECT id FROM muscle_group WHERE name = 'Obliques'), 'SECONDARY');
-- Obliques
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'russian-twist'), (SELECT id FROM muscle_group WHERE name = 'Obliques'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'russian-twist'), (SELECT id FROM muscle_group WHERE name = 'Abs'), 'SECONDARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'cable-woodchop'), (SELECT id FROM muscle_group WHERE name = 'Obliques'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'cable-woodchop'), (SELECT id FROM muscle_group WHERE name = 'Abs'), 'SECONDARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'side-plank'), (SELECT id FROM muscle_group WHERE name = 'Obliques'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'side-plank'), (SELECT id FROM muscle_group WHERE name = 'Hip Abductors'), 'SECONDARY');
-- Lower Back
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'back-extension'), (SELECT id FROM muscle_group WHERE name = 'Lower Back'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'back-extension'), (SELECT id FROM muscle_group WHERE name = 'Glutes'), 'SECONDARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'good-morning'), (SELECT id FROM muscle_group WHERE name = 'Lower Back'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'good-morning'), (SELECT id FROM muscle_group WHERE name = 'Hamstrings'), 'SECONDARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'conventional-deadlift'), (SELECT id FROM muscle_group WHERE name = 'Lower Back'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'conventional-deadlift'), (SELECT id FROM muscle_group WHERE name = 'Glutes'), 'SECONDARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'conventional-deadlift'), (SELECT id FROM muscle_group WHERE name = 'Hamstrings'), 'SECONDARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'conventional-deadlift'), (SELECT id FROM muscle_group WHERE name = 'Trapezius'), 'SECONDARY');
-- Quadriceps
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'barbell-back-squat'), (SELECT id FROM muscle_group WHERE name = 'Quadriceps'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'barbell-back-squat'), (SELECT id FROM muscle_group WHERE name = 'Glutes'), 'SECONDARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'barbell-back-squat'), (SELECT id FROM muscle_group WHERE name = 'Lower Back'), 'SECONDARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'leg-press'), (SELECT id FROM muscle_group WHERE name = 'Quadriceps'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'leg-press'), (SELECT id FROM muscle_group WHERE name = 'Glutes'), 'SECONDARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'leg-extension'), (SELECT id FROM muscle_group WHERE name = 'Quadriceps'), 'PRIMARY');
-- Hamstrings
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'romanian-deadlift'), (SELECT id FROM muscle_group WHERE name = 'Hamstrings'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'romanian-deadlift'), (SELECT id FROM muscle_group WHERE name = 'Glutes'), 'SECONDARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'romanian-deadlift'), (SELECT id FROM muscle_group WHERE name = 'Lower Back'), 'SECONDARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'lying-leg-curl'), (SELECT id FROM muscle_group WHERE name = 'Hamstrings'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'lying-leg-curl'), (SELECT id FROM muscle_group WHERE name = 'Calves'), 'SECONDARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'seated-leg-curl'), (SELECT id FROM muscle_group WHERE name = 'Hamstrings'), 'PRIMARY');
-- Glutes
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'barbell-hip-thrust'), (SELECT id FROM muscle_group WHERE name = 'Glutes'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'barbell-hip-thrust'), (SELECT id FROM muscle_group WHERE name = 'Hamstrings'), 'SECONDARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'bulgarian-split-squat'), (SELECT id FROM muscle_group WHERE name = 'Glutes'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'bulgarian-split-squat'), (SELECT id FROM muscle_group WHERE name = 'Quadriceps'), 'SECONDARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'cable-glute-kickback'), (SELECT id FROM muscle_group WHERE name = 'Glutes'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'cable-glute-kickback'), (SELECT id FROM muscle_group WHERE name = 'Hamstrings'), 'SECONDARY');
-- Calves
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'standing-calf-raise'), (SELECT id FROM muscle_group WHERE name = 'Calves'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'seated-calf-raise'), (SELECT id FROM muscle_group WHERE name = 'Calves'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'donkey-calf-raise'), (SELECT id FROM muscle_group WHERE name = 'Calves'), 'PRIMARY');
-- Hip Adductors
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'hip-adduction-machine'), (SELECT id FROM muscle_group WHERE name = 'Hip Adductors'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'cable-hip-adduction'), (SELECT id FROM muscle_group WHERE name = 'Hip Adductors'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'copenhagen-plank'), (SELECT id FROM muscle_group WHERE name = 'Hip Adductors'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'copenhagen-plank'), (SELECT id FROM muscle_group WHERE name = 'Obliques'), 'SECONDARY');
-- Hip Abductors
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'hip-abduction-machine'), (SELECT id FROM muscle_group WHERE name = 'Hip Abductors'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'hip-abduction-machine'), (SELECT id FROM muscle_group WHERE name = 'Glutes'), 'SECONDARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'cable-hip-abduction'), (SELECT id FROM muscle_group WHERE name = 'Hip Abductors'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'banded-lateral-walk'), (SELECT id FROM muscle_group WHERE name = 'Hip Abductors'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'banded-lateral-walk'), (SELECT id FROM muscle_group WHERE name = 'Glutes'), 'SECONDARY');
-- Tibialis Anterior
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'tibialis-raise'), (SELECT id FROM muscle_group WHERE name = 'Tibialis Anterior'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'weighted-toe-raise'), (SELECT id FROM muscle_group WHERE name = 'Tibialis Anterior'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'banded-dorsiflexion'), (SELECT id FROM muscle_group WHERE name = 'Tibialis Anterior'), 'PRIMARY');
