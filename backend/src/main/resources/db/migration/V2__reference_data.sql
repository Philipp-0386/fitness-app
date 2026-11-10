-- Reference data: roles, muscle groups and the global exercise catalog.
-- Runs in every environment, production included.

-- roles
INSERT INTO roles (name) VALUES ('USER');
INSERT INTO roles (id, name) VALUES (0, 'ADMIN');

-- muscle_group
-- groups carry the body region, their areas reference them as parent
INSERT INTO muscle_group (name, body_region) VALUES ('Chest', 'UPPER');
INSERT INTO muscle_group (name, body_region) VALUES ('Back', 'UPPER');
INSERT INTO muscle_group (name, body_region) VALUES ('Shoulders', 'UPPER');
INSERT INTO muscle_group (name, body_region) VALUES ('Arms', 'UPPER');
INSERT INTO muscle_group (name, body_region) VALUES ('Neck', 'UPPER');
INSERT INTO muscle_group (name, body_region) VALUES ('Core', 'CORE');
INSERT INTO muscle_group (name, body_region) VALUES ('Legs', 'LOWER');
INSERT INTO muscle_group (parent_id, name) VALUES ((SELECT id FROM muscle_group WHERE name = 'Chest'), 'Upper Chest');
INSERT INTO muscle_group (parent_id, name) VALUES ((SELECT id FROM muscle_group WHERE name = 'Back'), 'Lats');
INSERT INTO muscle_group (parent_id, name) VALUES ((SELECT id FROM muscle_group WHERE name = 'Back'), 'Upper Back');
INSERT INTO muscle_group (parent_id, name) VALUES ((SELECT id FROM muscle_group WHERE name = 'Back'), 'Traps');
INSERT INTO muscle_group (parent_id, name) VALUES ((SELECT id FROM muscle_group WHERE name = 'Back'), 'Lower Back');
INSERT INTO muscle_group (parent_id, name) VALUES ((SELECT id FROM muscle_group WHERE name = 'Shoulders'), 'Front Delts');
INSERT INTO muscle_group (parent_id, name) VALUES ((SELECT id FROM muscle_group WHERE name = 'Shoulders'), 'Side Delts');
INSERT INTO muscle_group (parent_id, name) VALUES ((SELECT id FROM muscle_group WHERE name = 'Shoulders'), 'Rear Delts');
INSERT INTO muscle_group (parent_id, name) VALUES ((SELECT id FROM muscle_group WHERE name = 'Arms'), 'Biceps');
INSERT INTO muscle_group (parent_id, name) VALUES ((SELECT id FROM muscle_group WHERE name = 'Arms'), 'Triceps');
INSERT INTO muscle_group (parent_id, name) VALUES ((SELECT id FROM muscle_group WHERE name = 'Arms'), 'Forearms');
INSERT INTO muscle_group (parent_id, name) VALUES ((SELECT id FROM muscle_group WHERE name = 'Core'), 'Abs');
INSERT INTO muscle_group (parent_id, name) VALUES ((SELECT id FROM muscle_group WHERE name = 'Core'), 'Obliques');
INSERT INTO muscle_group (parent_id, name) VALUES ((SELECT id FROM muscle_group WHERE name = 'Legs'), 'Quadriceps');
INSERT INTO muscle_group (parent_id, name) VALUES ((SELECT id FROM muscle_group WHERE name = 'Legs'), 'Hamstrings');
INSERT INTO muscle_group (parent_id, name) VALUES ((SELECT id FROM muscle_group WHERE name = 'Legs'), 'Glutes');
INSERT INTO muscle_group (parent_id, name) VALUES ((SELECT id FROM muscle_group WHERE name = 'Legs'), 'Calves');
INSERT INTO muscle_group (parent_id, name) VALUES ((SELECT id FROM muscle_group WHERE name = 'Legs'), 'Hip Adductors');
INSERT INTO muscle_group (parent_id, name) VALUES ((SELECT id FROM muscle_group WHERE name = 'Legs'), 'Hip Abductors');
INSERT INTO muscle_group (parent_id, name) VALUES ((SELECT id FROM muscle_group WHERE name = 'Legs'), 'Tibialis Anterior');

-- exercise (owner_user_id NULL = standard exercise, set = custom exercise of that user)
-- Global exercises are referenced by slug. A name can repeat across owners, so a lookup by name breaks once users exist.
-- Chest
INSERT INTO exercise (owner_user_id, slug, name, exercise_type, tracking_type, description, instructions) VALUES (NULL, 'barbell-bench-press', 'Barbell Bench Press', 'STRENGTH', 'WEIGHT_REPS', 'Flat barbell press, main horizontal pushing movement.', 'Lie flat, grip slightly wider than shoulders, lower to mid chest, press back up.');
INSERT INTO exercise (owner_user_id, slug, name, exercise_type, tracking_type, description, instructions) VALUES (NULL, 'incline-dumbbell-press', 'Incline Dumbbell Press', 'STRENGTH', 'WEIGHT_REPS', 'Incline press emphasising the upper chest.', 'Set bench to 30 degrees, press dumbbells up without locking harshly.');
INSERT INTO exercise (owner_user_id, slug, name, exercise_type, tracking_type, description, instructions) VALUES (NULL, 'cable-chest-fly', 'Cable Chest Fly', 'STRENGTH', 'WEIGHT_REPS', 'Isolation for the chest with constant cable tension.', 'Slight elbow bend, bring handles together in front of the sternum.');
-- Upper Back
INSERT INTO exercise (owner_user_id, slug, name, exercise_type, tracking_type, description, instructions) VALUES (NULL, 'barbell-row', 'Barbell Row', 'STRENGTH', 'WEIGHT_REPS', 'Bent-over row for mid back thickness.', 'Hinge to about 45 degrees, row the bar to the lower ribs, keep spine neutral.');
INSERT INTO exercise (owner_user_id, slug, name, exercise_type, tracking_type, description, instructions) VALUES (NULL, 'seated-cable-row', 'Seated Cable Row', 'STRENGTH', 'WEIGHT_REPS', 'Horizontal pull on the cable stack.', 'Sit upright, pull the handle to the navel, control the return.');
INSERT INTO exercise (owner_user_id, slug, name, exercise_type, tracking_type, description, instructions) VALUES (NULL, 'chest-supported-dumbbell-row', 'Chest-Supported Dumbbell Row', 'STRENGTH', 'WEIGHT_REPS', 'Row variation without lower back involvement.', 'Chest on an incline bench, row the dumbbells to the lower ribs, squeeze the shoulder blades together.');
INSERT INTO exercise (owner_user_id, slug, name, exercise_type, tracking_type, description, instructions) VALUES (NULL, 'wide-grip-seated-cable-row', 'Wide-Grip Seated Cable Row', 'STRENGTH', 'WEIGHT_REPS', 'Horizontal pull with a wide grip for the upper back and rear delts.', 'Wide bar, pull towards the lower chest with the elbows flared, squeeze the shoulder blades.');
-- Lats
INSERT INTO exercise (owner_user_id, slug, name, exercise_type, tracking_type, description, instructions) VALUES (NULL, 'pull-up', 'Pull-Up', 'STRENGTH', 'BODYWEIGHT_REPS', 'Bodyweight vertical pull.', 'Hang with pronated grip, pull the chest towards the bar, lower fully.');
INSERT INTO exercise (owner_user_id, slug, name, exercise_type, tracking_type, description, instructions) VALUES (NULL, 'lat-pulldown', 'Lat Pulldown', 'STRENGTH', 'WEIGHT_REPS', 'Machine vertical pull, scalable load.', 'Pull the bar to the upper chest, keep the torso nearly upright.');
INSERT INTO exercise (owner_user_id, slug, name, exercise_type, tracking_type, description, instructions) VALUES (NULL, 'straight-arm-pulldown', 'Straight-Arm Pulldown', 'STRENGTH', 'WEIGHT_REPS', 'Lat isolation with extended arms.', 'Keep elbows locked, pull the bar down to the thighs.');
-- Traps
INSERT INTO exercise (owner_user_id, slug, name, exercise_type, tracking_type, description, instructions) VALUES (NULL, 'barbell-shrug', 'Barbell Shrug', 'STRENGTH', 'WEIGHT_REPS', 'Shrug for the upper traps.', 'Shrug straight up, pause briefly, lower under control.');
INSERT INTO exercise (owner_user_id, slug, name, exercise_type, tracking_type, description, instructions) VALUES (NULL, 'dumbbell-shrug', 'Dumbbell Shrug', 'STRENGTH', 'WEIGHT_REPS', 'Shrug with a longer range of motion.', 'Dumbbells at the sides, elevate the shoulders without rolling them.');
INSERT INTO exercise (owner_user_id, slug, name, exercise_type, tracking_type, description, instructions) VALUES (NULL, 'farmers-walk', 'Farmers Walk', 'STRENGTH', 'WEIGHT_DISTANCE', 'Loaded carry for traps, forearms and core.', 'Carry heavy dumbbells for distance, ribs down, shoulders packed.');
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
INSERT INTO exercise (owner_user_id, slug, name, exercise_type, tracking_type, description, instructions) VALUES (NULL, 'machine-preacher-curl', 'Machine Preacher Curl', 'STRENGTH', 'WEIGHT_REPS', 'Biceps curl with the upper arms fixed on a pad.', 'Upper arms flat on the pad, curl up, lower until the arms are almost straight.');
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
INSERT INTO exercise (owner_user_id, slug, name, exercise_type, tracking_type, description, instructions) VALUES (NULL, 'cable-hip-adduction', 'Cable Hip Adduction', 'STRENGTH', 'WEIGHT_REPS', 'Unilateral adduction on the cable.', 'Ankle strap on the leg closest to the stack, pull it across the midline.');
INSERT INTO exercise (owner_user_id, slug, name, exercise_type, tracking_type, description, instructions) VALUES (NULL, 'copenhagen-plank', 'Copenhagen Plank', 'STRENGTH', 'DURATION', 'Isometric adductor hold.', 'Upper leg on the bench, lift the hips, hold in a straight line.');
-- Hip Abductors
INSERT INTO exercise (owner_user_id, slug, name, exercise_type, tracking_type, description, instructions) VALUES (NULL, 'hip-abduction-machine', 'Hip Abduction Machine', 'STRENGTH', 'WEIGHT_REPS', 'Machine work for the abductors.', 'Press the legs apart, hold briefly, return under control.');
INSERT INTO exercise (owner_user_id, slug, name, exercise_type, tracking_type, description, instructions) VALUES (NULL, 'cable-hip-abduction', 'Cable Hip Abduction', 'STRENGTH', 'WEIGHT_REPS', 'Unilateral abduction on the cable.', 'Ankle strap on the leg farthest from the stack, move it outwards, keep the torso still.');
INSERT INTO exercise (owner_user_id, slug, name, exercise_type, tracking_type, description, instructions) VALUES (NULL, 'banded-lateral-walk', 'Banded Lateral Walk', 'STRENGTH', 'BODYWEIGHT_REPS', 'Activation for the gluteus medius.', 'Band above the knees, step sideways in a half squat.');
-- Tibialis Anterior
INSERT INTO exercise (owner_user_id, slug, name, exercise_type, tracking_type, description, instructions) VALUES (NULL, 'tibialis-raise', 'Tibialis Raise', 'STRENGTH', 'BODYWEIGHT_REPS', 'Dorsiflexion against the wall or a machine.', 'Heels on the floor, pull the toes up, lower slowly.');
INSERT INTO exercise (owner_user_id, slug, name, exercise_type, tracking_type, description, instructions) VALUES (NULL, 'weighted-toe-raise', 'Weighted Toe Raise', 'STRENGTH', 'WEIGHT_REPS', 'Loaded dorsiflexion.', 'Weight over the forefoot, raise the toes, pause at the top.');
INSERT INTO exercise (owner_user_id, slug, name, exercise_type, tracking_type, description, instructions) VALUES (NULL, 'banded-dorsiflexion', 'Banded Dorsiflexion', 'STRENGTH', 'BODYWEIGHT_REPS', 'Band work for the tibialis anterior.', 'Band around the forefoot, pull the foot towards the shin.');
-- Appended instead of sorted into the sections above, so the ids of the existing exercises stay stable.
-- Chest
INSERT INTO exercise (owner_user_id, slug, name, exercise_type, tracking_type, description, instructions) VALUES (NULL, 'dumbbell-bench-press', 'Dumbbell Bench Press', 'STRENGTH', 'WEIGHT_REPS', 'Flat dumbbell press with a longer range than the barbell.', 'Lie flat, lower the dumbbells to chest level, press up and slightly together.');
INSERT INTO exercise (owner_user_id, slug, name, exercise_type, tracking_type, description, instructions) VALUES (NULL, 'machine-chest-press', 'Machine Chest Press', 'STRENGTH', 'WEIGHT_REPS', 'Guided horizontal press, easy to take close to failure.', 'Handles at mid chest height, press forward, control the return.');
INSERT INTO exercise (owner_user_id, slug, name, exercise_type, tracking_type, description, instructions) VALUES (NULL, 'pec-deck', 'Pec Deck', 'STRENGTH', 'WEIGHT_REPS', 'Machine fly for the chest.', 'Slight elbow bend, bring the handles together in front of the chest, open under control.');
INSERT INTO exercise (owner_user_id, slug, name, exercise_type, tracking_type, description, instructions) VALUES (NULL, 'push-up', 'Push-Up', 'STRENGTH', 'BODYWEIGHT_REPS', 'Bodyweight horizontal press.', 'Hands slightly wider than shoulders, body in a straight line, lower the chest to the floor, press back up.');
-- Lats
INSERT INTO exercise (owner_user_id, slug, name, exercise_type, tracking_type, description, instructions) VALUES (NULL, 'chin-up', 'Chin-Up', 'STRENGTH', 'BODYWEIGHT_REPS', 'Vertical pull with a supinated grip, more biceps than the pull-up.', 'Hang with the palms facing you, pull the chin over the bar, lower fully.');
INSERT INTO exercise (owner_user_id, slug, name, exercise_type, tracking_type, description, instructions) VALUES (NULL, 'assisted-pull-up', 'Assisted Pull-Up', 'STRENGTH', 'ASSISTED_REPS', 'Pull-up with counterweight support on the machine.', 'Knees on the pad, pronated grip, pull the chest towards the bar, lower fully.');
INSERT INTO exercise (owner_user_id, slug, name, exercise_type, tracking_type, description, instructions) VALUES (NULL, 'one-arm-dumbbell-row', 'One-Arm Dumbbell Row', 'STRENGTH', 'WEIGHT_REPS', 'Unilateral row supported on a bench.', 'Hand and knee on the bench, row the dumbbell towards the hip, keep the torso still.');
-- Biceps
INSERT INTO exercise (owner_user_id, slug, name, exercise_type, tracking_type, description, instructions) VALUES (NULL, 'dumbbell-curl', 'Dumbbell Curl', 'STRENGTH', 'WEIGHT_REPS', 'Basic biceps curl with dumbbells.', 'Elbows at the sides, curl up while turning the palms up, lower under control.');
-- Triceps
INSERT INTO exercise (owner_user_id, slug, name, exercise_type, tracking_type, description, instructions) VALUES (NULL, 'dip', 'Dip', 'STRENGTH', 'BODYWEIGHT_REPS', 'Bodyweight press on parallel bars.', 'Torso fairly upright, lower until the upper arms are about parallel to the floor, press back up.');
INSERT INTO exercise (owner_user_id, slug, name, exercise_type, tracking_type, description, instructions) VALUES (NULL, 'assisted-dip', 'Assisted Dip', 'STRENGTH', 'ASSISTED_REPS', 'Dip with counterweight support on the machine.', 'Knees on the pad, lower until the upper arms are about parallel to the floor, press back up.');
INSERT INTO exercise (owner_user_id, slug, name, exercise_type, tracking_type, description, instructions) VALUES (NULL, 'ez-bar-skull-crusher', 'EZ-Bar Skull Crusher', 'STRENGTH', 'WEIGHT_REPS', 'Lying triceps extension.', 'Lie flat, lower the bar towards the forehead by bending only the elbows, extend back up.');
-- Abs
INSERT INTO exercise (owner_user_id, slug, name, exercise_type, tracking_type, description, instructions) VALUES (NULL, 'crunch', 'Crunch', 'STRENGTH', 'BODYWEIGHT_REPS', 'Basic floor crunch.', 'Lie on the back with the knees bent, curl the shoulders off the floor, lower slowly.');
INSERT INTO exercise (owner_user_id, slug, name, exercise_type, tracking_type, description, instructions) VALUES (NULL, 'ab-wheel-rollout', 'Ab Wheel Rollout', 'STRENGTH', 'BODYWEIGHT_REPS', 'Anti-extension work with the ab wheel.', 'Kneel, roll forward as far as the lower back stays neutral, roll back.');
-- Quadriceps
INSERT INTO exercise (owner_user_id, slug, name, exercise_type, tracking_type, description, instructions) VALUES (NULL, 'hack-squat', 'Hack Squat', 'STRENGTH', 'WEIGHT_REPS', 'Machine squat with back support.', 'Back against the pad, feet shoulder-width, squat deep, press back up.');
INSERT INTO exercise (owner_user_id, slug, name, exercise_type, tracking_type, description, instructions) VALUES (NULL, 'goblet-squat', 'Goblet Squat', 'STRENGTH', 'WEIGHT_REPS', 'Squat with one dumbbell held at the chest.', 'Hold the dumbbell at the chest, squat down between the knees, keep the torso upright.');
INSERT INTO exercise (owner_user_id, slug, name, exercise_type, tracking_type, description, instructions) VALUES (NULL, 'walking-lunge', 'Walking Lunge', 'STRENGTH', 'WEIGHT_REPS', 'Alternating lunges moving forward.', 'Dumbbells at the sides, step forward, lower the back knee towards the floor, step through.');
-- Cardio
INSERT INTO exercise (owner_user_id, slug, name, exercise_type, tracking_type, description, instructions) VALUES (NULL, 'treadmill', 'Treadmill', 'CARDIO', 'DISTANCE_DURATION', 'Walking or running on the treadmill.', 'Set speed and incline, log the distance and time.');
INSERT INTO exercise (owner_user_id, slug, name, exercise_type, tracking_type, description, instructions) VALUES (NULL, 'rowing-machine', 'Rowing Machine', 'CARDIO', 'DISTANCE_DURATION', 'Full body cardio on the rowing ergometer.', 'Drive with the legs first, then lean back and pull the handle to the lower ribs, return in reverse order.');
INSERT INTO exercise (owner_user_id, slug, name, exercise_type, tracking_type, description, instructions) VALUES (NULL, 'stationary-bike', 'Stationary Bike', 'CARDIO', 'DISTANCE_DURATION', 'Low impact cardio on the bike.', 'Saddle at hip height, keep a steady cadence, log the distance and time.');
-- Chest
INSERT INTO exercise (owner_user_id, slug, name, exercise_type, tracking_type, description, instructions) VALUES (NULL, 'incline-barbell-bench-press', 'Incline Barbell Bench Press', 'STRENGTH', 'WEIGHT_REPS', 'Barbell press on an incline for the upper chest.', 'Set the bench to 30 degrees, lower the bar to the upper chest, press back up.');
INSERT INTO exercise (owner_user_id, slug, name, exercise_type, tracking_type, description, instructions) VALUES (NULL, 'incline-machine-press', 'Incline Machine Press', 'STRENGTH', 'WEIGHT_REPS', 'Guided incline press for the upper chest.', 'Handles at upper chest height, press up and forward, control the return.');
INSERT INTO exercise (owner_user_id, slug, name, exercise_type, tracking_type, description, instructions) VALUES (NULL, 'low-to-high-cable-fly', 'Low-to-High Cable Fly', 'STRENGTH', 'WEIGHT_REPS', 'Cable fly with an upward path for the upper chest.', 'Cables from the low pulleys, slight elbow bend, bring the handles together at chin height.');
INSERT INTO exercise (owner_user_id, slug, name, exercise_type, tracking_type, description, instructions) VALUES (NULL, 'dumbbell-fly', 'Dumbbell Fly', 'STRENGTH', 'WEIGHT_REPS', 'Free weight fly with a deep stretch.', 'Lie flat, slight elbow bend, open the arms in a wide arc, bring the dumbbells back together over the chest.');
-- Upper Back
INSERT INTO exercise (owner_user_id, slug, name, exercise_type, tracking_type, description, instructions) VALUES (NULL, 't-bar-row', 'T-Bar Row', 'STRENGTH', 'WEIGHT_REPS', 'Heavy row with a neutral grip.', 'Hinge at the hips, row the handle to the lower chest, keep the spine neutral.');
INSERT INTO exercise (owner_user_id, slug, name, exercise_type, tracking_type, description, instructions) VALUES (NULL, 'machine-row', 'Machine Row', 'STRENGTH', 'WEIGHT_REPS', 'Guided horizontal pull with chest support.', 'Chest against the pad, pull the handles to the lower ribs, control the return.');
-- Lats
INSERT INTO exercise (owner_user_id, slug, name, exercise_type, tracking_type, description, instructions) VALUES (NULL, 'close-grip-lat-pulldown', 'Close-Grip Lat Pulldown', 'STRENGTH', 'WEIGHT_REPS', 'Vertical pull with a narrow neutral grip.', 'V-handle on the cable, pull to the upper chest, keep the torso nearly upright.');
INSERT INTO exercise (owner_user_id, slug, name, exercise_type, tracking_type, description, instructions) VALUES (NULL, 'single-arm-lat-pulldown', 'Single-Arm Lat Pulldown', 'STRENGTH', 'WEIGHT_REPS', 'Unilateral vertical pull.', 'One handle overhead, drive the elbow down to the side, let the lat stretch at the top.');
-- Front Delts
INSERT INTO exercise (owner_user_id, slug, name, exercise_type, tracking_type, description, instructions) VALUES (NULL, 'machine-shoulder-press', 'Machine Shoulder Press', 'STRENGTH', 'WEIGHT_REPS', 'Guided vertical press.', 'Handles at shoulder height, press overhead, lower under control.');
INSERT INTO exercise (owner_user_id, slug, name, exercise_type, tracking_type, description, instructions) VALUES (NULL, 'arnold-press', 'Arnold Press', 'STRENGTH', 'WEIGHT_REPS', 'Dumbbell press with a rotation.', 'Start with the palms facing you, rotate them forward while pressing overhead, reverse on the way down.');
-- Side Delts
INSERT INTO exercise (owner_user_id, slug, name, exercise_type, tracking_type, description, instructions) VALUES (NULL, 'machine-lateral-raise', 'Machine Lateral Raise', 'STRENGTH', 'WEIGHT_REPS', 'Guided isolation for the lateral deltoid.', 'Pads against the upper arms, raise to shoulder height, lower under control.');
-- Rear Delts
INSERT INTO exercise (owner_user_id, slug, name, exercise_type, tracking_type, description, instructions) VALUES (NULL, 'cable-rear-delt-fly', 'Cable Rear Delt Fly', 'STRENGTH', 'WEIGHT_REPS', 'Reverse fly with constant cable tension.', 'Cross the cables at shoulder height, open the arms in a wide arc with slightly bent elbows.');
-- Biceps
INSERT INTO exercise (owner_user_id, slug, name, exercise_type, tracking_type, description, instructions) VALUES (NULL, 'ez-bar-curl', 'EZ-Bar Curl', 'STRENGTH', 'WEIGHT_REPS', 'Biceps curl with an angled grip that is easier on the wrists.', 'Elbows at the sides, curl up, lower under control.');
INSERT INTO exercise (owner_user_id, slug, name, exercise_type, tracking_type, description, instructions) VALUES (NULL, 'cable-curl', 'Cable Curl', 'STRENGTH', 'WEIGHT_REPS', 'Biceps curl with constant cable tension.', 'Bar on the low pulley, elbows at the sides, curl up, lower under control.');
INSERT INTO exercise (owner_user_id, slug, name, exercise_type, tracking_type, description, instructions) VALUES (NULL, 'ez-bar-preacher-curl', 'EZ-Bar Preacher Curl', 'STRENGTH', 'WEIGHT_REPS', 'Free weight curl with the upper arms fixed on a pad.', 'Upper arms flat on the pad, curl up, lower until the arms are almost straight.');
-- Triceps
INSERT INTO exercise (owner_user_id, slug, name, exercise_type, tracking_type, description, instructions) VALUES (NULL, 'triceps-straight-bar-pushdown', 'Triceps Straight-Bar Pushdown', 'STRENGTH', 'WEIGHT_REPS', 'Cable pushdown with a straight bar.', 'Overhand grip, elbows fixed at the sides, push down to lockout.');
INSERT INTO exercise (owner_user_id, slug, name, exercise_type, tracking_type, description, instructions) VALUES (NULL, 'triceps-v-bar-pushdown', 'Triceps V-Bar Pushdown', 'STRENGTH', 'WEIGHT_REPS', 'Cable pushdown with an angled V-bar.', 'Elbows fixed at the sides, push down to lockout, control the return.');
INSERT INTO exercise (owner_user_id, slug, name, exercise_type, tracking_type, description, instructions) VALUES (NULL, 'single-arm-cable-pushdown', 'Single-Arm Cable Pushdown', 'STRENGTH', 'WEIGHT_REPS', 'Unilateral cable pushdown.', 'One handle, elbow fixed at the side, push down to lockout.');
INSERT INTO exercise (owner_user_id, slug, name, exercise_type, tracking_type, description, instructions) VALUES (NULL, 'overhead-dumbbell-triceps-extension', 'Overhead Dumbbell Triceps Extension', 'STRENGTH', 'WEIGHT_REPS', 'Triceps work in the stretched position with one dumbbell.', 'Hold the dumbbell overhead with both hands, lower behind the head, extend the elbows.');
-- Abs
INSERT INTO exercise (owner_user_id, slug, name, exercise_type, tracking_type, description, instructions) VALUES (NULL, 'machine-crunch', 'Machine Crunch', 'STRENGTH', 'WEIGHT_REPS', 'Loadable crunch on the machine.', 'Set the pads at chest height, flex the spine down, return slowly.');
INSERT INTO exercise (owner_user_id, slug, name, exercise_type, tracking_type, description, instructions) VALUES (NULL, 'hanging-knee-raise', 'Hanging Knee Raise', 'STRENGTH', 'BODYWEIGHT_REPS', 'Easier variation of the hanging leg raise.', 'Hang from the bar, pull the knees towards the chest, lower without swinging.');
INSERT INTO exercise (owner_user_id, slug, name, exercise_type, tracking_type, description, instructions) VALUES (NULL, 'decline-sit-up', 'Decline Sit-Up', 'STRENGTH', 'BODYWEIGHT_REPS', 'Sit-up on a decline bench.', 'Feet secured, curl the torso up towards the knees, lower slowly.');
-- Quadriceps
INSERT INTO exercise (owner_user_id, slug, name, exercise_type, tracking_type, description, instructions) VALUES (NULL, 'front-squat', 'Front Squat', 'STRENGTH', 'WEIGHT_REPS', 'Squat with the bar on the front of the shoulders.', 'Bar on the front delts, elbows high, squat deep with an upright torso.');
INSERT INTO exercise (owner_user_id, slug, name, exercise_type, tracking_type, description, instructions) VALUES (NULL, 'smith-machine-squat', 'Smith Machine Squat', 'STRENGTH', 'WEIGHT_REPS', 'Squat on a guided bar.', 'Bar on the upper back, feet slightly in front, squat deep, press back up.');
INSERT INTO exercise (owner_user_id, slug, name, exercise_type, tracking_type, description, instructions) VALUES (NULL, 'reverse-lunge', 'Reverse Lunge', 'STRENGTH', 'WEIGHT_REPS', 'Lunge stepping backwards, easier on the knees.', 'Dumbbells at the sides, step back, lower the back knee towards the floor, return to standing.');
INSERT INTO exercise (owner_user_id, slug, name, exercise_type, tracking_type, description, instructions) VALUES (NULL, 'step-up', 'Step-Up', 'STRENGTH', 'WEIGHT_REPS', 'Unilateral step onto a box.', 'Dumbbells at the sides, step onto the box, drive through the front foot, lower slowly.');
-- Hamstrings
INSERT INTO exercise (owner_user_id, slug, name, exercise_type, tracking_type, description, instructions) VALUES (NULL, 'dumbbell-romanian-deadlift', 'Dumbbell Romanian Deadlift', 'STRENGTH', 'WEIGHT_REPS', 'Hip hinge with dumbbells.', 'Push the hips back, dumbbells close to the legs, stop at the end of the stretch.');
INSERT INTO exercise (owner_user_id, slug, name, exercise_type, tracking_type, description, instructions) VALUES (NULL, 'sumo-deadlift', 'Sumo Deadlift', 'STRENGTH', 'WEIGHT_REPS', 'Wide stance pull from the floor.', 'Wide stance, grip inside the knees, brace, push the floor apart, lock out the hips.');
INSERT INTO exercise (owner_user_id, slug, name, exercise_type, tracking_type, description, instructions) VALUES (NULL, 'nordic-curl', 'Nordic Curl', 'STRENGTH', 'BODYWEIGHT_REPS', 'Eccentric knee flexion with bodyweight.', 'Ankles secured, lower the torso forward as slowly as possible, push back up with the hands.');
-- Glutes
INSERT INTO exercise (owner_user_id, slug, name, exercise_type, tracking_type, description, instructions) VALUES (NULL, 'glute-bridge', 'Glute Bridge', 'STRENGTH', 'BODYWEIGHT_REPS', 'Hip extension from the floor.', 'Lie on the back with the knees bent, drive the hips up, squeeze at the top.');
-- Calves
INSERT INTO exercise (owner_user_id, slug, name, exercise_type, tracking_type, description, instructions) VALUES (NULL, 'leg-press-calf-raise', 'Leg Press Calf Raise', 'STRENGTH', 'WEIGHT_REPS', 'Calf raise on the leg press.', 'Balls of the feet on the platform edge, knees nearly straight, push through the toes, full stretch at the bottom.');
-- Cardio
INSERT INTO exercise (owner_user_id, slug, name, exercise_type, tracking_type, description, instructions) VALUES (NULL, 'elliptical', 'Elliptical', 'CARDIO', 'DISTANCE_DURATION', 'Low impact cardio on the elliptical trainer.', 'Keep a steady pace, log the distance and time.');
INSERT INTO exercise (owner_user_id, slug, name, exercise_type, tracking_type, description, instructions) VALUES (NULL, 'stair-climber', 'Stair Climber', 'CARDIO', 'DISTANCE_DURATION', 'Cardio on the stair machine.', 'Upright posture, light hand contact, log the distance and time.');
INSERT INTO exercise (owner_user_id, slug, name, exercise_type, tracking_type, description, instructions) VALUES (NULL, 'zone-2-treadmill', 'Zone 2 Treadmill', 'CARDIO', 'DISTANCE_DURATION', 'Steady low intensity cardio on the treadmill.', 'Pick a pace where you can still hold a conversation, log the distance and time.');

-- exercise_musclegroup
-- Chest
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'barbell-bench-press'), (SELECT id FROM muscle_group WHERE name = 'Chest'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'barbell-bench-press'), (SELECT id FROM muscle_group WHERE name = 'Triceps'), 'SECONDARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'barbell-bench-press'), (SELECT id FROM muscle_group WHERE name = 'Front Delts'), 'SECONDARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'incline-dumbbell-press'), (SELECT id FROM muscle_group WHERE name = 'Upper Chest'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'incline-dumbbell-press'), (SELECT id FROM muscle_group WHERE name = 'Front Delts'), 'SECONDARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'incline-dumbbell-press'), (SELECT id FROM muscle_group WHERE name = 'Triceps'), 'SECONDARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'cable-chest-fly'), (SELECT id FROM muscle_group WHERE name = 'Chest'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'cable-chest-fly'), (SELECT id FROM muscle_group WHERE name = 'Front Delts'), 'SECONDARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'dumbbell-bench-press'), (SELECT id FROM muscle_group WHERE name = 'Chest'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'dumbbell-bench-press'), (SELECT id FROM muscle_group WHERE name = 'Triceps'), 'SECONDARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'dumbbell-bench-press'), (SELECT id FROM muscle_group WHERE name = 'Front Delts'), 'SECONDARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'machine-chest-press'), (SELECT id FROM muscle_group WHERE name = 'Chest'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'machine-chest-press'), (SELECT id FROM muscle_group WHERE name = 'Triceps'), 'SECONDARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'machine-chest-press'), (SELECT id FROM muscle_group WHERE name = 'Front Delts'), 'SECONDARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'pec-deck'), (SELECT id FROM muscle_group WHERE name = 'Chest'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'pec-deck'), (SELECT id FROM muscle_group WHERE name = 'Front Delts'), 'SECONDARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'push-up'), (SELECT id FROM muscle_group WHERE name = 'Chest'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'push-up'), (SELECT id FROM muscle_group WHERE name = 'Triceps'), 'SECONDARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'push-up'), (SELECT id FROM muscle_group WHERE name = 'Front Delts'), 'SECONDARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'incline-barbell-bench-press'), (SELECT id FROM muscle_group WHERE name = 'Upper Chest'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'incline-barbell-bench-press'), (SELECT id FROM muscle_group WHERE name = 'Front Delts'), 'SECONDARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'incline-barbell-bench-press'), (SELECT id FROM muscle_group WHERE name = 'Triceps'), 'SECONDARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'incline-machine-press'), (SELECT id FROM muscle_group WHERE name = 'Upper Chest'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'incline-machine-press'), (SELECT id FROM muscle_group WHERE name = 'Front Delts'), 'SECONDARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'incline-machine-press'), (SELECT id FROM muscle_group WHERE name = 'Triceps'), 'SECONDARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'low-to-high-cable-fly'), (SELECT id FROM muscle_group WHERE name = 'Upper Chest'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'low-to-high-cable-fly'), (SELECT id FROM muscle_group WHERE name = 'Front Delts'), 'SECONDARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'dumbbell-fly'), (SELECT id FROM muscle_group WHERE name = 'Chest'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'dumbbell-fly'), (SELECT id FROM muscle_group WHERE name = 'Front Delts'), 'SECONDARY');
-- Upper Back
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'barbell-row'), (SELECT id FROM muscle_group WHERE name = 'Upper Back'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'barbell-row'), (SELECT id FROM muscle_group WHERE name = 'Lats'), 'SECONDARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'barbell-row'), (SELECT id FROM muscle_group WHERE name = 'Biceps'), 'SECONDARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'barbell-row'), (SELECT id FROM muscle_group WHERE name = 'Lower Back'), 'SECONDARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'seated-cable-row'), (SELECT id FROM muscle_group WHERE name = 'Upper Back'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'seated-cable-row'), (SELECT id FROM muscle_group WHERE name = 'Lats'), 'SECONDARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'seated-cable-row'), (SELECT id FROM muscle_group WHERE name = 'Biceps'), 'SECONDARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'chest-supported-dumbbell-row'), (SELECT id FROM muscle_group WHERE name = 'Upper Back'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'chest-supported-dumbbell-row'), (SELECT id FROM muscle_group WHERE name = 'Rear Delts'), 'SECONDARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'chest-supported-dumbbell-row'), (SELECT id FROM muscle_group WHERE name = 'Biceps'), 'SECONDARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'wide-grip-seated-cable-row'), (SELECT id FROM muscle_group WHERE name = 'Upper Back'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'wide-grip-seated-cable-row'), (SELECT id FROM muscle_group WHERE name = 'Biceps'), 'SECONDARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'wide-grip-seated-cable-row'), (SELECT id FROM muscle_group WHERE name = 'Rear Delts'), 'SECONDARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 't-bar-row'), (SELECT id FROM muscle_group WHERE name = 'Upper Back'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 't-bar-row'), (SELECT id FROM muscle_group WHERE name = 'Lats'), 'SECONDARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 't-bar-row'), (SELECT id FROM muscle_group WHERE name = 'Biceps'), 'SECONDARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 't-bar-row'), (SELECT id FROM muscle_group WHERE name = 'Lower Back'), 'SECONDARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'machine-row'), (SELECT id FROM muscle_group WHERE name = 'Upper Back'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'machine-row'), (SELECT id FROM muscle_group WHERE name = 'Lats'), 'SECONDARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'machine-row'), (SELECT id FROM muscle_group WHERE name = 'Biceps'), 'SECONDARY');
-- Lats
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'pull-up'), (SELECT id FROM muscle_group WHERE name = 'Lats'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'pull-up'), (SELECT id FROM muscle_group WHERE name = 'Biceps'), 'SECONDARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'pull-up'), (SELECT id FROM muscle_group WHERE name = 'Upper Back'), 'SECONDARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'lat-pulldown'), (SELECT id FROM muscle_group WHERE name = 'Lats'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'lat-pulldown'), (SELECT id FROM muscle_group WHERE name = 'Biceps'), 'SECONDARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'lat-pulldown'), (SELECT id FROM muscle_group WHERE name = 'Upper Back'), 'SECONDARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'straight-arm-pulldown'), (SELECT id FROM muscle_group WHERE name = 'Lats'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'straight-arm-pulldown'), (SELECT id FROM muscle_group WHERE name = 'Triceps'), 'SECONDARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'chin-up'), (SELECT id FROM muscle_group WHERE name = 'Lats'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'chin-up'), (SELECT id FROM muscle_group WHERE name = 'Biceps'), 'SECONDARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'chin-up'), (SELECT id FROM muscle_group WHERE name = 'Upper Back'), 'SECONDARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'assisted-pull-up'), (SELECT id FROM muscle_group WHERE name = 'Lats'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'assisted-pull-up'), (SELECT id FROM muscle_group WHERE name = 'Biceps'), 'SECONDARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'assisted-pull-up'), (SELECT id FROM muscle_group WHERE name = 'Upper Back'), 'SECONDARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'one-arm-dumbbell-row'), (SELECT id FROM muscle_group WHERE name = 'Lats'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'one-arm-dumbbell-row'), (SELECT id FROM muscle_group WHERE name = 'Upper Back'), 'SECONDARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'one-arm-dumbbell-row'), (SELECT id FROM muscle_group WHERE name = 'Biceps'), 'SECONDARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'close-grip-lat-pulldown'), (SELECT id FROM muscle_group WHERE name = 'Lats'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'close-grip-lat-pulldown'), (SELECT id FROM muscle_group WHERE name = 'Biceps'), 'SECONDARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'close-grip-lat-pulldown'), (SELECT id FROM muscle_group WHERE name = 'Upper Back'), 'SECONDARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'single-arm-lat-pulldown'), (SELECT id FROM muscle_group WHERE name = 'Lats'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'single-arm-lat-pulldown'), (SELECT id FROM muscle_group WHERE name = 'Biceps'), 'SECONDARY');
-- Traps
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'barbell-shrug'), (SELECT id FROM muscle_group WHERE name = 'Traps'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'barbell-shrug'), (SELECT id FROM muscle_group WHERE name = 'Forearms'), 'SECONDARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'dumbbell-shrug'), (SELECT id FROM muscle_group WHERE name = 'Traps'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'dumbbell-shrug'), (SELECT id FROM muscle_group WHERE name = 'Forearms'), 'SECONDARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'farmers-walk'), (SELECT id FROM muscle_group WHERE name = 'Traps'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'farmers-walk'), (SELECT id FROM muscle_group WHERE name = 'Forearms'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'farmers-walk'), (SELECT id FROM muscle_group WHERE name = 'Abs'), 'SECONDARY');
-- Front Delts
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'overhead-barbell-press'), (SELECT id FROM muscle_group WHERE name = 'Front Delts'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'overhead-barbell-press'), (SELECT id FROM muscle_group WHERE name = 'Triceps'), 'SECONDARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'overhead-barbell-press'), (SELECT id FROM muscle_group WHERE name = 'Side Delts'), 'SECONDARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'seated-dumbbell-shoulder-press'), (SELECT id FROM muscle_group WHERE name = 'Front Delts'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'seated-dumbbell-shoulder-press'), (SELECT id FROM muscle_group WHERE name = 'Triceps'), 'SECONDARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'seated-dumbbell-shoulder-press'), (SELECT id FROM muscle_group WHERE name = 'Side Delts'), 'SECONDARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'dumbbell-front-raise'), (SELECT id FROM muscle_group WHERE name = 'Front Delts'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'machine-shoulder-press'), (SELECT id FROM muscle_group WHERE name = 'Front Delts'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'machine-shoulder-press'), (SELECT id FROM muscle_group WHERE name = 'Triceps'), 'SECONDARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'machine-shoulder-press'), (SELECT id FROM muscle_group WHERE name = 'Side Delts'), 'SECONDARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'arnold-press'), (SELECT id FROM muscle_group WHERE name = 'Front Delts'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'arnold-press'), (SELECT id FROM muscle_group WHERE name = 'Triceps'), 'SECONDARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'arnold-press'), (SELECT id FROM muscle_group WHERE name = 'Side Delts'), 'SECONDARY');
-- Side Delts
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'dumbbell-lateral-raise'), (SELECT id FROM muscle_group WHERE name = 'Side Delts'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'cable-lateral-raise'), (SELECT id FROM muscle_group WHERE name = 'Side Delts'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'upright-row'), (SELECT id FROM muscle_group WHERE name = 'Side Delts'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'upright-row'), (SELECT id FROM muscle_group WHERE name = 'Traps'), 'SECONDARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'machine-lateral-raise'), (SELECT id FROM muscle_group WHERE name = 'Side Delts'), 'PRIMARY');
-- Rear Delts
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'face-pull'), (SELECT id FROM muscle_group WHERE name = 'Rear Delts'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'face-pull'), (SELECT id FROM muscle_group WHERE name = 'Upper Back'), 'SECONDARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'reverse-pec-deck'), (SELECT id FROM muscle_group WHERE name = 'Rear Delts'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'reverse-pec-deck'), (SELECT id FROM muscle_group WHERE name = 'Upper Back'), 'SECONDARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'bent-over-dumbbell-reverse-fly'), (SELECT id FROM muscle_group WHERE name = 'Rear Delts'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'bent-over-dumbbell-reverse-fly'), (SELECT id FROM muscle_group WHERE name = 'Upper Back'), 'SECONDARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'cable-rear-delt-fly'), (SELECT id FROM muscle_group WHERE name = 'Rear Delts'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'cable-rear-delt-fly'), (SELECT id FROM muscle_group WHERE name = 'Upper Back'), 'SECONDARY');
-- Biceps
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'barbell-curl'), (SELECT id FROM muscle_group WHERE name = 'Biceps'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'barbell-curl'), (SELECT id FROM muscle_group WHERE name = 'Forearms'), 'SECONDARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'incline-dumbbell-curl'), (SELECT id FROM muscle_group WHERE name = 'Biceps'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'hammer-curl'), (SELECT id FROM muscle_group WHERE name = 'Biceps'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'hammer-curl'), (SELECT id FROM muscle_group WHERE name = 'Forearms'), 'SECONDARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'machine-preacher-curl'), (SELECT id FROM muscle_group WHERE name = 'Biceps'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'dumbbell-curl'), (SELECT id FROM muscle_group WHERE name = 'Biceps'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'dumbbell-curl'), (SELECT id FROM muscle_group WHERE name = 'Forearms'), 'SECONDARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'ez-bar-curl'), (SELECT id FROM muscle_group WHERE name = 'Biceps'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'ez-bar-curl'), (SELECT id FROM muscle_group WHERE name = 'Forearms'), 'SECONDARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'cable-curl'), (SELECT id FROM muscle_group WHERE name = 'Biceps'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'ez-bar-preacher-curl'), (SELECT id FROM muscle_group WHERE name = 'Biceps'), 'PRIMARY');
-- Triceps
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'close-grip-bench-press'), (SELECT id FROM muscle_group WHERE name = 'Triceps'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'close-grip-bench-press'), (SELECT id FROM muscle_group WHERE name = 'Chest'), 'SECONDARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'close-grip-bench-press'), (SELECT id FROM muscle_group WHERE name = 'Front Delts'), 'SECONDARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'triceps-rope-pushdown'), (SELECT id FROM muscle_group WHERE name = 'Triceps'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'overhead-cable-triceps-extension'), (SELECT id FROM muscle_group WHERE name = 'Triceps'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'dip'), (SELECT id FROM muscle_group WHERE name = 'Triceps'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'dip'), (SELECT id FROM muscle_group WHERE name = 'Chest'), 'SECONDARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'dip'), (SELECT id FROM muscle_group WHERE name = 'Front Delts'), 'SECONDARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'assisted-dip'), (SELECT id FROM muscle_group WHERE name = 'Triceps'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'assisted-dip'), (SELECT id FROM muscle_group WHERE name = 'Chest'), 'SECONDARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'assisted-dip'), (SELECT id FROM muscle_group WHERE name = 'Front Delts'), 'SECONDARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'ez-bar-skull-crusher'), (SELECT id FROM muscle_group WHERE name = 'Triceps'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'triceps-straight-bar-pushdown'), (SELECT id FROM muscle_group WHERE name = 'Triceps'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'triceps-v-bar-pushdown'), (SELECT id FROM muscle_group WHERE name = 'Triceps'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'single-arm-cable-pushdown'), (SELECT id FROM muscle_group WHERE name = 'Triceps'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'overhead-dumbbell-triceps-extension'), (SELECT id FROM muscle_group WHERE name = 'Triceps'), 'PRIMARY');
-- Forearms
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'wrist-curl'), (SELECT id FROM muscle_group WHERE name = 'Forearms'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'reverse-wrist-curl'), (SELECT id FROM muscle_group WHERE name = 'Forearms'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'dead-hang'), (SELECT id FROM muscle_group WHERE name = 'Forearms'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'dead-hang'), (SELECT id FROM muscle_group WHERE name = 'Lats'), 'SECONDARY');
-- Neck
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'neck-curl'), (SELECT id FROM muscle_group WHERE name = 'Neck'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'neck-extension'), (SELECT id FROM muscle_group WHERE name = 'Neck'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'neck-extension'), (SELECT id FROM muscle_group WHERE name = 'Traps'), 'SECONDARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'lateral-neck-flexion'), (SELECT id FROM muscle_group WHERE name = 'Neck'), 'PRIMARY');
-- Abs
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'hanging-leg-raise'), (SELECT id FROM muscle_group WHERE name = 'Abs'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'hanging-leg-raise'), (SELECT id FROM muscle_group WHERE name = 'Obliques'), 'SECONDARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'hanging-leg-raise'), (SELECT id FROM muscle_group WHERE name = 'Forearms'), 'SECONDARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'cable-crunch'), (SELECT id FROM muscle_group WHERE name = 'Abs'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'plank'), (SELECT id FROM muscle_group WHERE name = 'Abs'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'plank'), (SELECT id FROM muscle_group WHERE name = 'Obliques'), 'SECONDARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'crunch'), (SELECT id FROM muscle_group WHERE name = 'Abs'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'ab-wheel-rollout'), (SELECT id FROM muscle_group WHERE name = 'Abs'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'ab-wheel-rollout'), (SELECT id FROM muscle_group WHERE name = 'Lats'), 'SECONDARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'machine-crunch'), (SELECT id FROM muscle_group WHERE name = 'Abs'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'hanging-knee-raise'), (SELECT id FROM muscle_group WHERE name = 'Abs'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'hanging-knee-raise'), (SELECT id FROM muscle_group WHERE name = 'Forearms'), 'SECONDARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'decline-sit-up'), (SELECT id FROM muscle_group WHERE name = 'Abs'), 'PRIMARY');
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
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'good-morning'), (SELECT id FROM muscle_group WHERE name = 'Hamstrings'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'good-morning'), (SELECT id FROM muscle_group WHERE name = 'Glutes'), 'SECONDARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'conventional-deadlift'), (SELECT id FROM muscle_group WHERE name = 'Lower Back'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'conventional-deadlift'), (SELECT id FROM muscle_group WHERE name = 'Glutes'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'conventional-deadlift'), (SELECT id FROM muscle_group WHERE name = 'Hamstrings'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'conventional-deadlift'), (SELECT id FROM muscle_group WHERE name = 'Traps'), 'SECONDARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'conventional-deadlift'), (SELECT id FROM muscle_group WHERE name = 'Quadriceps'), 'SECONDARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'conventional-deadlift'), (SELECT id FROM muscle_group WHERE name = 'Forearms'), 'SECONDARY');
-- Quadriceps
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'barbell-back-squat'), (SELECT id FROM muscle_group WHERE name = 'Quadriceps'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'barbell-back-squat'), (SELECT id FROM muscle_group WHERE name = 'Glutes'), 'SECONDARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'barbell-back-squat'), (SELECT id FROM muscle_group WHERE name = 'Lower Back'), 'SECONDARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'leg-press'), (SELECT id FROM muscle_group WHERE name = 'Quadriceps'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'leg-press'), (SELECT id FROM muscle_group WHERE name = 'Glutes'), 'SECONDARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'leg-extension'), (SELECT id FROM muscle_group WHERE name = 'Quadriceps'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'hack-squat'), (SELECT id FROM muscle_group WHERE name = 'Quadriceps'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'hack-squat'), (SELECT id FROM muscle_group WHERE name = 'Glutes'), 'SECONDARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'goblet-squat'), (SELECT id FROM muscle_group WHERE name = 'Quadriceps'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'goblet-squat'), (SELECT id FROM muscle_group WHERE name = 'Glutes'), 'SECONDARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'walking-lunge'), (SELECT id FROM muscle_group WHERE name = 'Quadriceps'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'walking-lunge'), (SELECT id FROM muscle_group WHERE name = 'Glutes'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'front-squat'), (SELECT id FROM muscle_group WHERE name = 'Quadriceps'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'front-squat'), (SELECT id FROM muscle_group WHERE name = 'Glutes'), 'SECONDARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'front-squat'), (SELECT id FROM muscle_group WHERE name = 'Upper Back'), 'SECONDARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'smith-machine-squat'), (SELECT id FROM muscle_group WHERE name = 'Quadriceps'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'smith-machine-squat'), (SELECT id FROM muscle_group WHERE name = 'Glutes'), 'SECONDARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'reverse-lunge'), (SELECT id FROM muscle_group WHERE name = 'Quadriceps'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'reverse-lunge'), (SELECT id FROM muscle_group WHERE name = 'Glutes'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'step-up'), (SELECT id FROM muscle_group WHERE name = 'Quadriceps'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'step-up'), (SELECT id FROM muscle_group WHERE name = 'Glutes'), 'PRIMARY');
-- Hamstrings
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'romanian-deadlift'), (SELECT id FROM muscle_group WHERE name = 'Hamstrings'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'romanian-deadlift'), (SELECT id FROM muscle_group WHERE name = 'Glutes'), 'SECONDARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'romanian-deadlift'), (SELECT id FROM muscle_group WHERE name = 'Lower Back'), 'SECONDARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'lying-leg-curl'), (SELECT id FROM muscle_group WHERE name = 'Hamstrings'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'lying-leg-curl'), (SELECT id FROM muscle_group WHERE name = 'Calves'), 'SECONDARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'seated-leg-curl'), (SELECT id FROM muscle_group WHERE name = 'Hamstrings'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'seated-leg-curl'), (SELECT id FROM muscle_group WHERE name = 'Calves'), 'SECONDARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'dumbbell-romanian-deadlift'), (SELECT id FROM muscle_group WHERE name = 'Hamstrings'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'dumbbell-romanian-deadlift'), (SELECT id FROM muscle_group WHERE name = 'Glutes'), 'SECONDARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'dumbbell-romanian-deadlift'), (SELECT id FROM muscle_group WHERE name = 'Lower Back'), 'SECONDARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'sumo-deadlift'), (SELECT id FROM muscle_group WHERE name = 'Glutes'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'sumo-deadlift'), (SELECT id FROM muscle_group WHERE name = 'Hamstrings'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'sumo-deadlift'), (SELECT id FROM muscle_group WHERE name = 'Quadriceps'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'sumo-deadlift'), (SELECT id FROM muscle_group WHERE name = 'Lower Back'), 'SECONDARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'sumo-deadlift'), (SELECT id FROM muscle_group WHERE name = 'Traps'), 'SECONDARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'sumo-deadlift'), (SELECT id FROM muscle_group WHERE name = 'Forearms'), 'SECONDARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'nordic-curl'), (SELECT id FROM muscle_group WHERE name = 'Hamstrings'), 'PRIMARY');
-- Glutes
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'barbell-hip-thrust'), (SELECT id FROM muscle_group WHERE name = 'Glutes'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'barbell-hip-thrust'), (SELECT id FROM muscle_group WHERE name = 'Hamstrings'), 'SECONDARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'bulgarian-split-squat'), (SELECT id FROM muscle_group WHERE name = 'Glutes'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'bulgarian-split-squat'), (SELECT id FROM muscle_group WHERE name = 'Quadriceps'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'cable-glute-kickback'), (SELECT id FROM muscle_group WHERE name = 'Glutes'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'cable-glute-kickback'), (SELECT id FROM muscle_group WHERE name = 'Hamstrings'), 'SECONDARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'glute-bridge'), (SELECT id FROM muscle_group WHERE name = 'Glutes'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'glute-bridge'), (SELECT id FROM muscle_group WHERE name = 'Hamstrings'), 'SECONDARY');
-- Calves
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'standing-calf-raise'), (SELECT id FROM muscle_group WHERE name = 'Calves'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'seated-calf-raise'), (SELECT id FROM muscle_group WHERE name = 'Calves'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'donkey-calf-raise'), (SELECT id FROM muscle_group WHERE name = 'Calves'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'leg-press-calf-raise'), (SELECT id FROM muscle_group WHERE name = 'Calves'), 'PRIMARY');
-- Hip Adductors
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'hip-adduction-machine'), (SELECT id FROM muscle_group WHERE name = 'Hip Adductors'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'cable-hip-adduction'), (SELECT id FROM muscle_group WHERE name = 'Hip Adductors'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'copenhagen-plank'), (SELECT id FROM muscle_group WHERE name = 'Hip Adductors'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'copenhagen-plank'), (SELECT id FROM muscle_group WHERE name = 'Obliques'), 'SECONDARY');
-- Hip Abductors
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'hip-abduction-machine'), (SELECT id FROM muscle_group WHERE name = 'Hip Abductors'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'hip-abduction-machine'), (SELECT id FROM muscle_group WHERE name = 'Glutes'), 'SECONDARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'cable-hip-abduction'), (SELECT id FROM muscle_group WHERE name = 'Hip Abductors'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'cable-hip-abduction'), (SELECT id FROM muscle_group WHERE name = 'Glutes'), 'SECONDARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'banded-lateral-walk'), (SELECT id FROM muscle_group WHERE name = 'Hip Abductors'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'banded-lateral-walk'), (SELECT id FROM muscle_group WHERE name = 'Glutes'), 'SECONDARY');
-- Tibialis Anterior
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'tibialis-raise'), (SELECT id FROM muscle_group WHERE name = 'Tibialis Anterior'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'weighted-toe-raise'), (SELECT id FROM muscle_group WHERE name = 'Tibialis Anterior'), 'PRIMARY');
INSERT INTO exercise_musclegroup (exercise_id, muscle_group_id, role) VALUES ((SELECT id FROM exercise WHERE slug = 'banded-dorsiflexion'), (SELECT id FROM muscle_group WHERE name = 'Tibialis Anterior'), 'PRIMARY');

-- preset routines (user_id NULL, referenced by slug). Users get a copy, programs and workouts never reference a preset.
INSERT INTO routine (user_id, slug, name, description) VALUES (NULL, 'push', 'Push', 'Upper chest, chest isolation, triceps and side delts.');
INSERT INTO routine (user_id, slug, name, description) VALUES (NULL, 'pull', 'Pull', 'Lats, upper back and biceps.');
INSERT INTO routine (user_id, slug, name, description) VALUES (NULL, 'legs', 'Legs', 'Machine based legs, calves and lower back.');
INSERT INTO routine (user_id, slug, name, description) VALUES (NULL, 'upper', 'Upper', 'Chest, lats, upper back, triceps and biceps.');
INSERT INTO routine (user_id, slug, name, description) VALUES (NULL, 'lower', 'Lower', 'Machine based legs, calves, abs and lower back.');

INSERT INTO routine_exercise (routine_id, exercise_id, order_index, target_sets, target_reps_min, target_reps_max) VALUES ((SELECT id FROM routine WHERE slug = 'push'), (SELECT id FROM exercise WHERE slug = 'incline-dumbbell-press'), 0, 3, 8, 12);
INSERT INTO routine_exercise (routine_id, exercise_id, order_index, target_sets, target_reps_min, target_reps_max) VALUES ((SELECT id FROM routine WHERE slug = 'push'), (SELECT id FROM exercise WHERE slug = 'cable-chest-fly'), 1, 3, 10, 15);
INSERT INTO routine_exercise (routine_id, exercise_id, order_index, target_sets, target_reps_min, target_reps_max) VALUES ((SELECT id FROM routine WHERE slug = 'push'), (SELECT id FROM exercise WHERE slug = 'triceps-rope-pushdown'), 2, 3, 10, 15);
INSERT INTO routine_exercise (routine_id, exercise_id, order_index, target_sets, target_reps_min, target_reps_max) VALUES ((SELECT id FROM routine WHERE slug = 'push'), (SELECT id FROM exercise WHERE slug = 'dumbbell-lateral-raise'), 3, 3, 12, 15);

INSERT INTO routine_exercise (routine_id, exercise_id, order_index, target_sets, target_reps_min, target_reps_max) VALUES ((SELECT id FROM routine WHERE slug = 'pull'), (SELECT id FROM exercise WHERE slug = 'lat-pulldown'), 0, 3, 8, 12);
INSERT INTO routine_exercise (routine_id, exercise_id, order_index, target_sets, target_reps_min, target_reps_max) VALUES ((SELECT id FROM routine WHERE slug = 'pull'), (SELECT id FROM exercise WHERE slug = 'wide-grip-seated-cable-row'), 1, 3, 8, 12);
INSERT INTO routine_exercise (routine_id, exercise_id, order_index, target_sets, target_reps_min, target_reps_max) VALUES ((SELECT id FROM routine WHERE slug = 'pull'), (SELECT id FROM exercise WHERE slug = 'machine-preacher-curl'), 2, 3, 8, 12);
INSERT INTO routine_exercise (routine_id, exercise_id, order_index, target_sets, target_reps_min, target_reps_max) VALUES ((SELECT id FROM routine WHERE slug = 'pull'), (SELECT id FROM exercise WHERE slug = 'hammer-curl'), 3, 3, 8, 12);

INSERT INTO routine_exercise (routine_id, exercise_id, order_index, target_sets, target_reps_min, target_reps_max) VALUES ((SELECT id FROM routine WHERE slug = 'legs'), (SELECT id FROM exercise WHERE slug = 'leg-extension'), 0, 3, 10, 15);
INSERT INTO routine_exercise (routine_id, exercise_id, order_index, target_sets, target_reps_min, target_reps_max) VALUES ((SELECT id FROM routine WHERE slug = 'legs'), (SELECT id FROM exercise WHERE slug = 'seated-leg-curl'), 1, 3, 10, 15);
INSERT INTO routine_exercise (routine_id, exercise_id, order_index, target_sets, target_reps_min, target_reps_max) VALUES ((SELECT id FROM routine WHERE slug = 'legs'), (SELECT id FROM exercise WHERE slug = 'leg-press'), 2, 3, 8, 12);
INSERT INTO routine_exercise (routine_id, exercise_id, order_index, target_sets, target_reps_min, target_reps_max) VALUES ((SELECT id FROM routine WHERE slug = 'legs'), (SELECT id FROM exercise WHERE slug = 'standing-calf-raise'), 3, 3, 10, 15);
INSERT INTO routine_exercise (routine_id, exercise_id, order_index, target_sets, target_reps_min, target_reps_max) VALUES ((SELECT id FROM routine WHERE slug = 'legs'), (SELECT id FROM exercise WHERE slug = 'back-extension'), 4, 3, 10, 15);

INSERT INTO routine_exercise (routine_id, exercise_id, order_index, target_sets, target_reps_min, target_reps_max) VALUES ((SELECT id FROM routine WHERE slug = 'upper'), (SELECT id FROM exercise WHERE slug = 'cable-chest-fly'), 0, 3, 10, 15);
INSERT INTO routine_exercise (routine_id, exercise_id, order_index, target_sets, target_reps_min, target_reps_max) VALUES ((SELECT id FROM routine WHERE slug = 'upper'), (SELECT id FROM exercise WHERE slug = 'machine-chest-press'), 1, 3, 8, 12);
INSERT INTO routine_exercise (routine_id, exercise_id, order_index, target_sets, target_reps_min, target_reps_max) VALUES ((SELECT id FROM routine WHERE slug = 'upper'), (SELECT id FROM exercise WHERE slug = 'lat-pulldown'), 2, 3, 8, 12);
INSERT INTO routine_exercise (routine_id, exercise_id, order_index, target_sets, target_reps_min, target_reps_max) VALUES ((SELECT id FROM routine WHERE slug = 'upper'), (SELECT id FROM exercise WHERE slug = 'wide-grip-seated-cable-row'), 3, 3, 8, 12);
INSERT INTO routine_exercise (routine_id, exercise_id, order_index, target_sets, target_reps_min, target_reps_max) VALUES ((SELECT id FROM routine WHERE slug = 'upper'), (SELECT id FROM exercise WHERE slug = 'triceps-rope-pushdown'), 4, 3, 10, 15);
INSERT INTO routine_exercise (routine_id, exercise_id, order_index, target_sets, target_reps_min, target_reps_max) VALUES ((SELECT id FROM routine WHERE slug = 'upper'), (SELECT id FROM exercise WHERE slug = 'machine-preacher-curl'), 5, 3, 8, 12);

INSERT INTO routine_exercise (routine_id, exercise_id, order_index, target_sets, target_reps_min, target_reps_max) VALUES ((SELECT id FROM routine WHERE slug = 'lower'), (SELECT id FROM exercise WHERE slug = 'seated-leg-curl'), 0, 3, 10, 15);
INSERT INTO routine_exercise (routine_id, exercise_id, order_index, target_sets, target_reps_min, target_reps_max) VALUES ((SELECT id FROM routine WHERE slug = 'lower'), (SELECT id FROM exercise WHERE slug = 'leg-extension'), 1, 3, 10, 15);
INSERT INTO routine_exercise (routine_id, exercise_id, order_index, target_sets, target_reps_min, target_reps_max) VALUES ((SELECT id FROM routine WHERE slug = 'lower'), (SELECT id FROM exercise WHERE slug = 'leg-press'), 2, 3, 8, 12);
INSERT INTO routine_exercise (routine_id, exercise_id, order_index, target_sets, target_reps_min, target_reps_max) VALUES ((SELECT id FROM routine WHERE slug = 'lower'), (SELECT id FROM exercise WHERE slug = 'standing-calf-raise'), 3, 3, 10, 15);
INSERT INTO routine_exercise (routine_id, exercise_id, order_index, target_sets, target_reps_min, target_reps_max) VALUES ((SELECT id FROM routine WHERE slug = 'lower'), (SELECT id FROM exercise WHERE slug = 'cable-crunch'), 4, 3, 10, 15);
INSERT INTO routine_exercise (routine_id, exercise_id, order_index, target_sets, target_reps_min, target_reps_max) VALUES ((SELECT id FROM routine WHERE slug = 'lower'), (SELECT id FROM exercise WHERE slug = 'back-extension'), 5, 3, 10, 15);

-- preset programs over the preset routines. Taking one copies the program and each of its routines.
INSERT INTO program (user_id, slug, name, description) VALUES (NULL, 'push-pull-legs', 'Push Pull Legs', 'Three training days in a row, then a rest day.');
INSERT INTO program_day (user_id, program_id, routine_id, order_index) VALUES (NULL, (SELECT id FROM program WHERE slug = 'push-pull-legs'), (SELECT id FROM routine WHERE slug = 'push'), 0);
INSERT INTO program_day (user_id, program_id, routine_id, order_index) VALUES (NULL, (SELECT id FROM program WHERE slug = 'push-pull-legs'), (SELECT id FROM routine WHERE slug = 'pull'), 1);
INSERT INTO program_day (user_id, program_id, routine_id, order_index) VALUES (NULL, (SELECT id FROM program WHERE slug = 'push-pull-legs'), (SELECT id FROM routine WHERE slug = 'legs'), 2);
INSERT INTO program_day (user_id, program_id, routine_id, order_index) VALUES (NULL, (SELECT id FROM program WHERE slug = 'push-pull-legs'), NULL, 3);

INSERT INTO program (user_id, slug, name, description) VALUES (NULL, 'upper-lower', 'Upper Lower', 'Upper body, lower body, then a rest day.');
INSERT INTO program_day (user_id, program_id, routine_id, order_index) VALUES (NULL, (SELECT id FROM program WHERE slug = 'upper-lower'), (SELECT id FROM routine WHERE slug = 'upper'), 0);
INSERT INTO program_day (user_id, program_id, routine_id, order_index) VALUES (NULL, (SELECT id FROM program WHERE slug = 'upper-lower'), (SELECT id FROM routine WHERE slug = 'lower'), 1);
INSERT INTO program_day (user_id, program_id, routine_id, order_index) VALUES (NULL, (SELECT id FROM program WHERE slug = 'upper-lower'), NULL, 2);
