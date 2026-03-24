# Deployment Guide — Focusr Backend

This document describes how to connect **Supabase** (PostgreSQL), **Fly.io** (hosting), and **GitHub Actions** (CI/CD) from scratch.

---

## Table of Contents

1. [GitHub Secrets](#1-github-secrets)
2. [Supabase Setup](#2-supabase-setup)
3. [Fly.io Setup](#3-flyio-setup)
4. [GitHub Actions Workflows](#4-github-actions-workflows)
5. [Branch Strategy](#5-branch-strategy)

---

## 1. GitHub Secrets

Add the following secrets in **GitHub → Repository → Settings → Secrets and variables → Actions → New repository secret**:

| Secret name | Description |
|---|---|
| `FLY_API_TOKEN` | API token from Fly.io dashboard → Account → Access Tokens |
| `DATABASE_URL` | Supabase JDBC URL: `jdbc:postgresql://<host>:5432/postgres` |
| `DATABASE_USERNAME` | Supabase database username (e.g. `postgres`) |
| `DATABASE_PASSWORD` | Supabase database password |
| `JWT_SECRET` | Secret key for JWT signing — must be at least 32 characters |

---

## 2. Supabase Setup

1. Create a free project at [supabase.com](https://supabase.com).
2. Go to **Project Settings → Database**.
3. Copy the connection details:
   - **Host**: `db.<project-ref>.supabase.co`
   - **Port**: `5432`
   - **Database name**: `postgres`
   - **User**: `postgres`
   - **Password**: the password you set when creating the project
4. Build the JDBC URL:
   ```
   jdbc:postgresql://db.<project-ref>.supabase.co:5432/postgres
   ```
5. Add these values as GitHub Secrets (`DATABASE_URL`, `DATABASE_USERNAME`, `DATABASE_PASSWORD`) and as Fly.io secrets (see section 3).

---

## 3. Fly.io Setup

### Install flyctl

```bash
curl -L https://fly.io/install.sh | sh
```

### Create and configure the app

1. Log in to Fly.io:
   ```bash
   flyctl auth login
   ```
2. From the repository root, launch the app (first time only):
   ```bash
   flyctl launch --no-deploy
   ```
   When prompted, choose a unique app name (or accept the generated one) and select a region close to your users. Fly.io will detect the `Dockerfile` automatically.
3. The `fly.toml` file in the repository already contains a baseline configuration. Verify that `app` matches the name you chose in the previous step and update `primary_region` if needed.

### Set secrets on Fly.io

Set the required environment secrets using `flyctl secrets set`:

```bash
flyctl secrets set \
  DATABASE_URL="jdbc:postgresql://<host>:5432/postgres" \
  DATABASE_USERNAME="postgres" \
  DATABASE_PASSWORD="<your-db-password>" \
  JWT_SECRET="<your-jwt-secret-min-32-chars>"
```

> **Note:** `PORT` and `SPRING_PROFILES_ACTIVE` are already defined as plain environment variables in `fly.toml` and do not need to be set as secrets.

### Deploy manually (optional)

```bash
flyctl deploy
```

### Get a Fly.io API token for GitHub Actions

1. Go to [fly.io/user/personal_access_tokens](https://fly.io/user/personal_access_tokens) to create a new personal access token, **or** run:
   ```bash
   flyctl tokens create deploy -x 999999h
   ```
2. Copy the token value.
3. Add it to GitHub Secrets as `FLY_API_TOKEN` (see section 1).

---

## 4. GitHub Actions Workflows

### CI — `.github/workflows/ci.yml`

Triggered on every **push** and **pull request** to any branch.

| Step | Description |
|---|---|
| Checkout | Fetches the source code |
| Set up Java 21 | Installs Temurin JDK 21 |
| Cache Maven | Caches `~/.m2` to speed up builds |
| Run tests | `mvn test -Dspring.profiles.active=test` — uses H2 in-memory DB, no external dependency |
| Build JAR | `mvn package -DskipTests` |
| Upload artifact | Uploads the JAR for debugging (retained for 7 days) |

### Deploy — `.github/workflows/deploy.yml`

Triggered on every **push to `main`**.

| Step | Description |
|---|---|
| CI job | Runs the full CI pipeline first via `workflow_call` |
| Set up flyctl | Installs the Fly.io CLI using the official `superfly/flyctl-actions/setup-flyctl` action |
| Deploy to Fly.io | Runs `flyctl deploy --remote-only` using the `FLY_API_TOKEN` secret |

---

## 5. Branch Strategy

| Branch | Environment | Trigger |
|---|---|---|
| `main` | Production (Fly.io) | Auto-deploy via GitHub Actions on push |
| `develop` | Staging | CI runs on every push; to deploy to staging, create a second Fly.io app (e.g. `focusr-be-staging`), add a `FLY_API_TOKEN_STAGING` secret, then duplicate `deploy.yml`, change the branch filter to `develop`, and point `flyctl deploy` to the staging app using `--app focusr-be-staging`. |

---

## Local Development

```bash
# Copy and fill in your environment variables
cp .env.example .env

# Run the application locally (requires a running PostgreSQL instance)
./mvnw spring-boot:run
```

Tests run against an H2 in-memory database and do not require a running PostgreSQL instance:

```bash
./mvnw test
```
