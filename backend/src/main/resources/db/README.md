# Database migrations

Flyway owns the schema. It runs on application startup, before Hibernate's
`ddl-auto: validate` checks the entities against the result. There is no reset
script anymore.

## Layout

| Location     | Runs in         | Contents                              |
| ------------ | --------------- | ------------------------------------- |
| `migration/` | everywhere      | schema and reference data             |
| `dev/`       | dev and CI only | test users and the data owned by them |

`dev/` is only picked up when the `dev` profile is active (see
`application-dev.yaml`). The default in `application.yaml` lists `migration/`
alone. Forgetting -> ideally prod safe.

## Rules

- **Never edit a migration that has run somewhere.** Flyway stores a checksum per
  file and refuses to start when one changes. Correct a mistake with a new
  migration.
- **Keep migrations additive** where possible - adding a nullable column or a new
  table lets an older image keep running. A dropped or renamed column makes a
  rollback to the previous image impossible.
- **Version ranges:** real migrations count up from `V1`. Files under `dev/`
  start at `V9001`, because both locations share one history table and one
  version sequence — a `V3` in each would abort with "found more than one
  migration with version 3".

  The reserved range has one consequence: the dev seed is always applied
  _before_ any real migration added later. A development database that already
  ran `V9001` would fail validation as soon as `V3` appears, so the dev profile
  sets `spring.flyway.out-of-order: true`. Production never loads `dev/`, its
  versions are strictly increasing, and it does not get that flag.

## Local reset

```
docker compose down -v && docker compose --profile full up -d --build
```

Dropping the `pgdata` volume is the reset. The next startup migrates an empty
database from scratch.
