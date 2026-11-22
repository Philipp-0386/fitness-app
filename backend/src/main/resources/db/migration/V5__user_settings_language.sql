-- Language of the UI and of the translated catalog. Existing users get the default.
ALTER TABLE user_settings ADD COLUMN language VARCHAR(8) DEFAULT 'EN' NOT NULL
    CONSTRAINT ck_user_settings_language CHECK (language IN ('EN', 'DE'));
