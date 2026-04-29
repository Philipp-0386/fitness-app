# CLAUDE.md

## Environment variables

Defined in `db/.env` (see `.env.example`):
- `ORACLE_PASSWORD` — password for the Oracle `system` user
- `APP_USER` / `APP_USER_PASSWORD` — credentials for `fitness_user` (used by the backend)

## Schema

Oracle DB, no migration framework — schema is applied manually via scripts in `src/`.

- `src/admin-init.sql` — grants privileges to `fitness_user`; run once as `system`
- `src/main.sql` — creates tables and inserts seed data; run as `fitness_user`

**Tables:**
- `ROLES` — id, name (seeded with `USER` and `ADMIN`)
- `USERDATA` — id, username, email, password_hashed, first_name, last_name, date_of_birth, role_id (FK → ROLES), created_at

Hibernate DDL mode is `validate` — schema must exist before the backend starts.
