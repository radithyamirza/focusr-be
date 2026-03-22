# focusr-be
Backend repository for Focusr app — a Pomodoro + Task Manager REST API built with Spring Boot 3.x, Spring Security (JWT), Spring Data JPA, and PostgreSQL.

## Quick Start

```bash
# Copy and fill in your environment variables
cp .env.example .env

# Run with Docker (requires a running PostgreSQL instance)
./mvnw spring-boot:run
```

## API Documentation

See [docs/API.md](docs/API.md) for sample requests and responses for every endpoint.

## Endpoints Overview

| Method | Path | Auth | Description |
|--------|------|------|-------------|
| POST | `/api/auth/register` | ✗ | Register a new user, returns JWT |
| POST | `/api/auth/login` | ✗ | Login, returns JWT |
| GET | `/api/tasks` | ✓ | List all tasks for current user |
| POST | `/api/tasks` | ✓ | Create a new task |
| PUT | `/api/tasks/{id}` | ✓ | Update a task |
| DELETE | `/api/tasks/{id}` | ✓ | Delete a task |
| PATCH | `/api/tasks/{id}/status` | ✓ | Update task status only |
| POST | `/api/sessions` | ✓ | Log a completed pomodoro session |
| GET | `/api/sessions` | ✓ | List sessions (supports `?from=&to=` filters) |
| GET | `/api/stats/summary` | ✓ | Today/week pomodoros, streak, total focus minutes |
| GET | `/api/stats/daily` | ✓ | Daily pomodoro counts for last 30 days (heatmap) |

## Environment Variables

| Variable | Default | Description |
|----------|---------|-------------|
| `DATABASE_URL` | `jdbc:postgresql://localhost:5432/focusr` | PostgreSQL JDBC URL |
| `DATABASE_USERNAME` | `focusr` | DB username |
| `DATABASE_PASSWORD` | `focusr` | DB password |
| `JWT_SECRET` | *(built-in dev key)* | HMAC-SHA256 secret — **change in production** |
| `JWT_EXPIRATION_MS` | `86400000` | Token TTL in milliseconds (default: 24 h) |
| `CORS_ALLOWED_ORIGIN` | `http://localhost:3000` | Allowed CORS origin |
| `JPA_DDL_AUTO` | `update` | Hibernate DDL strategy |
