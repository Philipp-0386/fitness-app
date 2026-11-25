-- Translations of the global catalog and the presets, one row per locale.
-- The base rows stay English and are the fallback. Only global rows (slug set) are translated, rows owned by users never.

CREATE TABLE muscle_group_translation (
    muscle_group_id BIGINT NOT NULL,
    locale VARCHAR(8) NOT NULL,
    name VARCHAR(64) NOT NULL,
    CONSTRAINT pk_muscle_group_translation PRIMARY KEY (muscle_group_id, locale),
    CONSTRAINT uq_muscle_group_translation_name UNIQUE (locale, name),
    CONSTRAINT fk_muscle_group_translation_muscle_group FOREIGN KEY (muscle_group_id) REFERENCES muscle_group(id) ON DELETE CASCADE
);

CREATE TABLE exercise_translation (
    exercise_id BIGINT NOT NULL,
    locale VARCHAR(8) NOT NULL,
    name VARCHAR(128) NOT NULL,
    description VARCHAR(1000),
    instructions TEXT,
    CONSTRAINT pk_exercise_translation PRIMARY KEY (exercise_id, locale),
    CONSTRAINT uq_exercise_translation_name UNIQUE (locale, name),
    CONSTRAINT fk_exercise_translation_exercise FOREIGN KEY (exercise_id) REFERENCES exercise(id) ON DELETE CASCADE
);

CREATE TABLE routine_translation (
    routine_id BIGINT NOT NULL,
    locale VARCHAR(8) NOT NULL,
    name VARCHAR(128) NOT NULL,
    description VARCHAR(1000),
    CONSTRAINT pk_routine_translation PRIMARY KEY (routine_id, locale),
    CONSTRAINT fk_routine_translation_routine FOREIGN KEY (routine_id) REFERENCES routine(id) ON DELETE CASCADE
);

CREATE TABLE program_translation (
    program_id BIGINT NOT NULL,
    locale VARCHAR(8) NOT NULL,
    name VARCHAR(128) NOT NULL,
    description VARCHAR(1000),
    CONSTRAINT pk_program_translation PRIMARY KEY (program_id, locale),
    CONSTRAINT fk_program_translation_program FOREIGN KEY (program_id) REFERENCES program(id) ON DELETE CASCADE
);
