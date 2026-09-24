# Deployment

How the production instance at **https://fit.ringelkamp.dev** is set up and deployed.

Status: live since 2026-09-24. Deployment is currently manual (`git pull` + rebuild on the server). CD via GitHub Actions is planned.

Parts of this document are made with AI.

---

## Architecture

```
Browser (HTTPS)
   │
   ▼
Hetzner Cloud Firewall ── allows only TCP 22, 80, 443 (+ ICMP for ping)
   │
   ▼
┌─────────────── VPS (Docker Compose, network: fitness-app_default) ──────────────┐
│                                                                                  │
│  caddy :80/:443  ──►  frontend :3000  ──►  backend :8080  ──►  db :5432          │
│  (published)          (Next.js, BFF)       (Spring Boot)       (PostgreSQL 17)   │
│  TLS termination      not published        not published       not published     │
│                                                                                  │
└──────────────────────────────────────────────────────────────────────────────────┘
```

Only Caddy publishes ports on the host. Frontend, backend and database are reachable exclusively inside the compose network by service name. The browser never talks to the backend directly: the Next.js BFF reads the `httpOnly` auth cookie server-side and forwards the token to Spring as a `Bearer` header.

---

## Infrastructure

| Component           | Choice                                                | Notes                                     |
| ------------------- | ----------------------------------------------------- | ----------------------------------------- |
| Hosting             | Hetzner Cloud, CX23 (2 vCPU x86, 4 GB RAM, 40 GB)     | Shared, cost-optimized line               |
| OS                  | Ubuntu 24.04 LTS                                      | Standard support until 2029               |
| Container runtime   | Docker Engine + Compose plugin                        | Installed from Docker's official apt repo |
| Reverse proxy / TLS | Caddy 2                                               | Automatic HTTPS via Let's Encrypt         |
| Domain / DNS        | `ringelkamp.dev`, registered and hosted at Cloudflare | App lives on the subdomain `fit.`         |

### DNS

| Type | Name  | Target                                  | Proxy                 |
| ---- | ----- | --------------------------------------- | --------------------- |
| A    | `fit` | server IPv4                             | DNS only (grey cloud) |
| AAAA | `fit` | server IPv6 (`::1` of the assigned /64) | DNS only (grey cloud) |

The Cloudflare proxy is deliberately **off**: TLS is terminated by Caddy on the server.

`.dev` is on the browser HSTS preload list, so the domain only works over HTTPS.

### Firewall (Hetzner Cloud Firewall)

| Direction | Protocol | Port | Source        | Purpose                                      |
| --------- | -------- | ---- | ------------- | -------------------------------------------- |
| In        | TCP      | 22   | any IPv4/IPv6 | SSH                                          |
| In        | TCP      | 80   | any IPv4/IPv6 | ACME HTTP-01 challenge + HTTP→HTTPS redirect |
| In        | TCP      | 443  | any IPv4/IPv6 | HTTPS                                        |
| In        | ICMP     | –    | any IPv4/IPv6 | Ping                                         |
| Out       | all      | all  | –             | Updates, image pulls                         |

The cloud firewall sits in front of the VM. This matters because Docker writes its own iptables rules and bypasses host firewalls such as `ufw` for published ports.

---

## Server

- SSH login **by key only** (ed25519). Password and keyboard-interactive auth disabled, root login disabled. Config lives in `/etc/ssh/sshd_config.d/00-hardening.conf`; the `00-` prefix makes it win over cloud-init's `50-cloud-init.conf`, since sshd takes the first value per option.
- Day-to-day work as a **non-root sudo user**, who is also in the `docker` group (effectively root-equivalent; acceptable on a single-user server).
- **Unattended security upgrades** enabled (`apt-daily*.timer`). Reboots are not automatic; check `/var/run/reboot-required`.
- **2 GB swap file** (`/swapfile`, registered in `/etc/fstab`) as a buffer for Maven and Next.js builds on 4 GB RAM.
- The Hetzner web console is the fallback if SSH ever locks out.

---

## Compose layout

Three files, layered:

| File                    | Loaded when                                                     | Purpose                                                                                |
| ----------------------- | --------------------------------------------------------------- | -------------------------------------------------------------------------------------- |
| `compose.yaml`          | always                                                          | Base definition of `db`, `backend`, `frontend`. **No published ports.**                |
| `compose.override.yaml` | automatically, when no `-f` / `COMPOSE_FILE` is set (local dev) | Publishes 5432 / 8080 / 3000 for IntelliJ and `npm run dev`                            |
| `compose.prod.yaml`     | explicitly, on the server                                       | Sets `SPRING_PROFILES_ACTIVE=prod`, requires the seed hashes, adds the `caddy` service |

Ports are intentionally absent from the base file: Compose **appends** list fields like `ports` when merging, so an overlay could never remove them.

On the server, the root `.env` selects the production stack:

```
COMPOSE_FILE=compose.yaml:compose.prod.yaml
COMPOSE_PROFILES=full
```

Setting `COMPOSE_FILE` also prevents `compose.override.yaml` from being merged, so the dev ports stay closed. With these two lines, a plain `docker compose up -d --build` starts the full production stack.

Verification:

```bash
docker compose config | grep -A1 published   # must show only 80 and 443
docker compose config --services             # db, backend, frontend, caddy
```

### Volumes

| Volume         | Content                         | Loss means                                                      |
| -------------- | ------------------------------- | --------------------------------------------------------------- |
| `pgdata`       | PostgreSQL data                 | **All user data gone**                                          |
| `caddy_data`   | TLS certificates + ACME account | New certificate on next start (Let's Encrypt rate limits apply) |
| `caddy_config` | Caddy autosaved config          | Nothing critical                                                |
| `backend-logs` | Spring log file                 | Log history only                                                |

---

## Caddy

`Caddyfile` (repo root, mounted read-only into the container):

```
fit.ringelkamp.dev {
    encode zstd gzip
    reverse_proxy frontend:3000
}
```

- A real domain as site address enables Automatic HTTPS: certificate issuance via ACME HTTP-01, renewal (ARI-guided, well before the 90-day expiry) and HTTP→HTTPS redirect.
- Caddy adds `X-Forwarded-For` and `X-Forwarded-Proto: https`.
- Caddy → frontend traffic is plain HTTP, but never leaves the host's Docker network.
- The admin API (`:2019`) is not published.
- HTTP/3 is not in use (`443/udp` is neither published nor allowed in the firewall).

After editing the Caddyfile, use `docker compose restart caddy` instead of `caddy reload`: the file is a single-file bind mount, and editors that replace the inode on save would leave the container reading the old version.

**Sign-up is currently blocked at the proxy** until a privacy policy is online (see [Known gaps](#known-gaps--todo)):

```
@signup path /signup /signup/* /api/signup
respond @signup "Registrierung ist aktuell geschlossen." 403
```

Because the backend is only reachable through the Next.js BFF, blocking these two frontend paths closes registration completely.

---

## Backend configuration

### Spring profiles and Flyway

| Profile                   | Flyway locations          | Extra                                                           |
| ------------------------- | ------------------------- | --------------------------------------------------------------- |
| (base) `application.yaml` | `db/migration`            | Schema and reference data                                       |
| `dev`                     | `db/migration`, `db/dev`  | Test users (seed at `V9001`), `out-of-order: true`, SQL logging |
| `prod`                    | `db/migration`, `db/prod` | Repeatable seed `R__prod_seed_users.sql` for two accounts       |

- The prod seed is idempotent (`ON CONFLICT DO NOTHING`) and repeatable, so prod keeps a strictly increasing version history without `out-of-order`.
- Password hashes for the seeded accounts are **not in the repo**. They come from `SEED_ADMIN_PASSWORD_HASH` and `SEED_USER_PASSWORD_HASH` as Flyway placeholders. `compose.prod.yaml` uses `${VAR:?}` so the stack refuses to start without them.
- Changing a hash later does not change an existing account (insert skipped on conflict).
- **A database created with the `dev` profile cannot be switched to `prod`**: `V9001` is recorded in `flyway_schema_history` but missing from the prod locations, so Flyway validation fails.

### Auth cookies

Set by the Next.js BFF in `app/api/login/route.ts`:

- `httpOnly: true`, `sameSite: 'lax'`, `path: '/'`
- `secure: process.env.NODE_ENV === 'production'` — the frontend runtime image sets `NODE_ENV=production`, so cookies are `Secure` in the container and plain in `npm run dev`.

### Environment variables (server `.env`)

Values are never committed. The server copy has mode `600`.

| Variable                                              | Purpose                                                                        |
| ----------------------------------------------------- | ------------------------------------------------------------------------------ |
| `POSTGRES_DB`, `POSTGRES_USER`, `POSTGRES_PASSWORD`   | Database; only applied when `pgdata` is first created                          |
| `JWT_SECRET`                                          | HS512 signing key, ≥ 64 bytes (generated with `openssl rand -hex 64`)          |
| `API_DOCS_ENABLED`                                    | `false` in prod (Swagger UI / `/v3/api-docs` off)                              |
| `SPRING_PROFILES_ACTIVE`                              | `prod` (also forced by `compose.prod.yaml`)                                    |
| `SEED_ADMIN_PASSWORD_HASH`, `SEED_USER_PASSWORD_HASH` | bcrypt hashes, **single-quoted** so Compose does not interpolate the `$` signs |
| `COMPOSE_FILE`, `COMPOSE_PROFILES`                    | Select the prod overlay and the `full` profile                                 |

Generating a bcrypt hash (leading space keeps it out of shell history):

```bash
 docker run --rm httpd:alpine htpasswd -nbBC 10 "" 'PASSWORD' | tr -d ':\n'; echo
```

`docker compose config` prints `$` as `$$`; that is escaping in the output only.

---

## Deploying

### Regular update

```bash
cd ~/fitness-app
git pull
docker compose up -d --build
docker compose ps
docker image prune -f
```

Changes are always made locally, committed and pushed. The server only pulls; nothing is edited there permanently.

### Fresh setup on a new server

1. Create the VM with SSH key, attach the firewall above.
2. Harden SSH, create the sudo user, add swap, install Docker (official repo).
3. `git clone` the repo, `cp .env.example .env`, fill in secrets and the `COMPOSE_*` lines, `chmod 600 .env`.
4. Point the `A` / `AAAA` records of `fit` to the new IPs (DNS only).
5. `docker compose config | grep -A1 published` → only 80 and 443.
6. `docker compose up -d --build`, then `docker compose logs caddy` until `certificate obtained successfully`.

### Rollback

- Code: `git checkout <previous-commit>` + `docker compose up -d --build`.
- Flyway migrations are forward-only: rolling back the code does not roll back the schema. Migrations should stay backward compatible
- Whole server: snapshot.

---

## Known gaps / TODO

| Item                                                                            | Why it matters                                                                                                                                      |
| ------------------------------------------------------------------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Backups** (Hetzner backups + `pg_dump` cron, copy off-server)                 | `pgdata` is the only copy of user data                                                                                                              |
| **Docker log rotation** (`/etc/docker/daemon.json`: `local` driver, `max-size`) | json-file logs grow without limit by default                                                                                                        |
| **Privacy policy** (+ decision on Impressum)                                    | Required before sign-up is reopened and other people's data is stored                                                                               |
| **Token refresh endpoint**                                                      | Access token expires after 15 min, users are logged out mid-session                                                                                 |
| **Rate limiting on login**                                                      | Unlimited password guessing is currently possible                                                                                                   |
| **Real client IPs**                                                             | Connections forwarded by `docker-proxy` (e.g. IPv6) arrive at Caddy with the Docker gateway IP (`172.18.0.1`); needed before IP-based rate limiting |
| **Uptime monitoring**                                                           | Outages are currently noticed only by using the app                                                                                                 |
| **Regular image updates** (`docker compose pull` + `build --pull`)              | `unattended-upgrades` covers the OS, not container images                                                                                           |
| **CD via GitHub Actions**                                                       | Build images in CI, push to GHCR tagged with the commit SHA, deploy via SSH with a forced-command deploy key                                        |
| **Feature flag for sign-up**                                                    | Replace the Caddy block with a backend setting                                                                                                      |
