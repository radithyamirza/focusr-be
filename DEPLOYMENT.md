# Deployment Guide — Focusr Backend

This document describes how to connect **Supabase** (PostgreSQL), **Render** (hosting), and **GitHub Actions** (CI/CD) from scratch.

---

## Table of Contents

1. [GitHub Secrets](#1-github-secrets)
2. [Supabase Setup](#2-supabase-setup)
3. [Render Setup](#3-render-setup)
4. [GitHub Actions Workflows](#4-github-actions-workflows)
5. [Branch Strategy](#5-branch-strategy)

---

## 1. GitHub Secrets

Add the following secrets in **GitHub → Repository → Settings → Secrets and variables → Actions → New repository secret**:

| Secret name | Description |
|---|---|
| `RENDER_DEPLOY_HOOK_URL` | Deploy hook URL from Render dashboard → Settings → Deploy Hook |
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
5. Add these values as GitHub Secrets (`DATABASE_URL`, `DATABASE_USERNAME`, `DATABASE_PASSWORD`) and as Render environment variables (see section 3).

---

## 3. Render Setup

### Create a Web Service

1. Go to [render.com](https://render.com) and create a new **Web Service**.
2. Connect it to your GitHub repository (`main` branch = production).
3. Set the following:
   - **Runtime**: Docker **or** use the native Java build
   - **Build command**: `mvn package -DskipTests`
   - **Start command**: `java -jar target/focusr-be-0.0.1-SNAPSHOT.jar`
   - **Health check path**: `/actuator/health`

### Environment Variables (Render Dashboard → Environment)

| Variable | Value |
|---|---|
| `DATABASE_URL` | `jdbc:postgresql://<host>:5432/postgres` |
| `DATABASE_USERNAME` | Supabase DB username |
| `DATABASE_PASSWORD` | Supabase DB password |
| `JWT_SECRET` | Your JWT signing secret (min 32 chars) |
| `SPRING_PROFILES_ACTIVE` | `default` |
| `PORT` | `8080` *(Render sets this automatically)* |

### Deploy Hook

1. In the Render dashboard, go to **Settings → Deploy Hook**.
2. Copy the URL and add it to GitHub Secrets as `RENDER_DEPLOY_HOOK_URL`.

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
| Trigger deploy | POSTs to the Render deploy hook URL |

---

## 5. Branch Strategy

| Branch | Environment | Trigger |
|---|---|---|
| `main` | Production (Render) | Auto-deploy via GitHub Actions on push |
| `develop` | Staging | CI runs on every push; to deploy to staging, create a second Render Web Service pointing to the `develop` branch and add a separate `RENDER_STAGING_DEPLOY_HOOK_URL` secret. Then duplicate `deploy.yml`, change the branch filter to `develop`, and use the staging secret. |

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
