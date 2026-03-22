# Focusr API — Sample Requests & Responses

Base URL: `http://localhost:8080`

All protected endpoints require the header:
```
Authorization: Bearer <token>
```

---

## Auth

### `POST /api/auth/register`

**Request**
```json
{
  "name": "Alice Smith",
  "email": "alice@example.com",
  "password": "secret123"
}
```

**Response — `201 Created`**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhbGljZUBleGFtcGxlLmNvbSIsImlhdCI6MTc0MjYzMDAwMCwiZXhwIjoxNzQyNzE2NDAwfQ.abc123xyz"
}
```

**Error — `409 Conflict`** (email already registered)
```json
{
  "status": 409,
  "error": "Conflict",
  "message": "Email already registered: alice@example.com",
  "timestamp": "2026-03-22T07:00:00Z"
}
```

**Error — `400 Bad Request`** (validation failure)
```json
{
  "status": 400,
  "error": "Bad Request",
  "fieldErrors": {
    "password": "Password must be at least 8 characters",
    "email": "Invalid email format"
  },
  "timestamp": "2026-03-22T07:00:00Z"
}
```

---

### `POST /api/auth/login`

**Request**
```json
{
  "email": "alice@example.com",
  "password": "secret123"
}
```

**Response — `200 OK`**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhbGljZUBleGFtcGxlLmNvbSIsImlhdCI6MTc0MjYzMDAwMCwiZXhwIjoxNzQyNzE2NDAwfQ.abc123xyz"
}
```

**Error — `401 Unauthorized`**
```json
{
  "status": 401,
  "error": "Unauthorized",
  "message": "Invalid email or password",
  "timestamp": "2026-03-22T07:00:00Z"
}
```

---

## Tasks

### `GET /api/tasks`

**Response — `200 OK`**
```json
[
  {
    "id": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
    "title": "Design login screen",
    "status": "IN_PROGRESS",
    "priority": 2,
    "estimatedPomodoros": 4,
    "completedPomodoros": 2,
    "dueDate": "2026-03-25",
    "createdAt": "2026-03-20T08:30:00Z"
  },
  {
    "id": "8a1f2d3e-9b4c-4a5f-b6d7-e8f9a0b1c2d3",
    "title": "Write unit tests",
    "status": "TODO",
    "priority": 1,
    "estimatedPomodoros": 6,
    "completedPomodoros": 0,
    "dueDate": null,
    "createdAt": "2026-03-21T10:00:00Z"
  }
]
```

---

### `POST /api/tasks`

**Request**
```json
{
  "title": "Implement auth service",
  "status": "TODO",
  "priority": 3,
  "estimatedPomodoros": 5,
  "completedPomodoros": 0,
  "dueDate": "2026-03-28"
}
```

> `status` defaults to `TODO` if omitted. `priority` defaults to `1` if omitted.  
> Valid statuses: `TODO`, `IN_PROGRESS`, `DONE`. Valid priorities: `1` (low), `2` (medium), `3` (high).

**Response — `201 Created`**
```json
{
  "id": "c1d2e3f4-a5b6-7890-cdef-123456789abc",
  "title": "Implement auth service",
  "status": "TODO",
  "priority": 3,
  "estimatedPomodoros": 5,
  "completedPomodoros": 0,
  "dueDate": "2026-03-28",
  "createdAt": "2026-03-22T07:05:00Z"
}
```

---

### `PUT /api/tasks/{id}`

**Request**
```json
{
  "title": "Implement auth service (revised)",
  "status": "IN_PROGRESS",
  "priority": 3,
  "estimatedPomodoros": 6,
  "completedPomodoros": 2,
  "dueDate": "2026-03-30"
}
```

**Response — `200 OK`**
```json
{
  "id": "c1d2e3f4-a5b6-7890-cdef-123456789abc",
  "title": "Implement auth service (revised)",
  "status": "IN_PROGRESS",
  "priority": 3,
  "estimatedPomodoros": 6,
  "completedPomodoros": 2,
  "dueDate": "2026-03-30",
  "createdAt": "2026-03-22T07:05:00Z"
}
```

**Error — `404 Not Found`**
```json
{
  "status": 404,
  "error": "Not Found",
  "message": "Task not found: c1d2e3f4-a5b6-7890-cdef-123456789abc",
  "timestamp": "2026-03-22T07:10:00Z"
}
```

---

### `DELETE /api/tasks/{id}`

**Response — `204 No Content`** *(empty body)*

**Error — `404 Not Found`**
```json
{
  "status": 404,
  "error": "Not Found",
  "message": "Task not found: c1d2e3f4-a5b6-7890-cdef-123456789abc",
  "timestamp": "2026-03-22T07:10:00Z"
}
```

---

### `PATCH /api/tasks/{id}/status`

**Request**
```json
{
  "status": "DONE"
}
```

**Response — `200 OK`**
```json
{
  "id": "c1d2e3f4-a5b6-7890-cdef-123456789abc",
  "title": "Implement auth service (revised)",
  "status": "DONE",
  "priority": 3,
  "estimatedPomodoros": 6,
  "completedPomodoros": 6,
  "dueDate": "2026-03-30",
  "createdAt": "2026-03-22T07:05:00Z"
}
```

---

## Sessions

### `POST /api/sessions`

Log a completed pomodoro session.

**Request** (work session linked to a task)
```json
{
  "taskId": "c1d2e3f4-a5b6-7890-cdef-123456789abc",
  "type": "WORK",
  "durationMinutes": 25,
  "startedAt": "2026-03-22T07:00:00Z",
  "endedAt": "2026-03-22T07:25:00Z"
}
```

> `taskId` is optional. Valid types: `WORK`, `SHORT_BREAK`, `LONG_BREAK`.

**Request** (break session, no task)
```json
{
  "type": "SHORT_BREAK",
  "durationMinutes": 5,
  "startedAt": "2026-03-22T07:25:00Z",
  "endedAt": "2026-03-22T07:30:00Z"
}
```

**Response — `201 Created`**
```json
{
  "id": "d4e5f6a7-b8c9-0123-def4-567890abcdef",
  "taskId": "c1d2e3f4-a5b6-7890-cdef-123456789abc",
  "type": "WORK",
  "durationMinutes": 25,
  "startedAt": "2026-03-22T07:00:00Z",
  "endedAt": "2026-03-22T07:25:00Z"
}
```

---

### `GET /api/sessions`

Retrieve all sessions. Optionally filter by date range using ISO-8601 timestamps.

**Without filters**
```
GET /api/sessions
```

**With filters**
```
GET /api/sessions?from=2026-03-01T00:00:00Z&to=2026-03-22T23:59:59Z
```

**Response — `200 OK`**
```json
[
  {
    "id": "d4e5f6a7-b8c9-0123-def4-567890abcdef",
    "taskId": "c1d2e3f4-a5b6-7890-cdef-123456789abc",
    "type": "WORK",
    "durationMinutes": 25,
    "startedAt": "2026-03-22T07:00:00Z",
    "endedAt": "2026-03-22T07:25:00Z"
  },
  {
    "id": "e5f6a7b8-c9d0-1234-ef56-7890abcdef01",
    "taskId": null,
    "type": "SHORT_BREAK",
    "durationMinutes": 5,
    "startedAt": "2026-03-22T07:25:00Z",
    "endedAt": "2026-03-22T07:30:00Z"
  },
  {
    "id": "f6a7b8c9-d0e1-2345-fa67-890abcdef012",
    "taskId": "8a1f2d3e-9b4c-4a5f-b6d7-e8f9a0b1c2d3",
    "type": "WORK",
    "durationMinutes": 25,
    "startedAt": "2026-03-22T06:30:00Z",
    "endedAt": "2026-03-22T06:55:00Z"
  }
]
```

---

## Stats

### `GET /api/stats/summary`

**Response — `200 OK`**
```json
{
  "todayPomodoros": 4,
  "weekPomodoros": 18,
  "currentStreak": 5,
  "totalFocusMinutes": 2750
}
```

| Field | Description |
|-------|-------------|
| `todayPomodoros` | Number of `WORK` sessions today (UTC) |
| `weekPomodoros` | Number of `WORK` sessions this week (Monday–now, UTC) |
| `currentStreak` | Consecutive days with at least 1 `WORK` session (max 365) |
| `totalFocusMinutes` | Sum of all `WORK` session durations in minutes |

---

### `GET /api/stats/daily`

Returns pomodoro counts for the last 30 days (for heatmap rendering).

**Response — `200 OK`**
```json
[
  { "date": "2026-02-21", "pomodoroCount": 0 },
  { "date": "2026-02-22", "pomodoroCount": 3 },
  { "date": "2026-02-23", "pomodoroCount": 5 },
  { "date": "2026-02-24", "pomodoroCount": 0 },
  { "date": "2026-02-25", "pomodoroCount": 4 },
  { "date": "2026-02-26", "pomodoroCount": 6 },
  { "date": "2026-02-27", "pomodoroCount": 2 },
  "...",
  { "date": "2026-03-21", "pomodoroCount": 4 },
  { "date": "2026-03-22", "pomodoroCount": 4 }
]
```

Always returns exactly 30 entries (oldest → newest). Days with no sessions have `pomodoroCount: 0`.

---

## Common Error Responses

### `401 Unauthorized` — missing or invalid JWT

```json
{
  "status": 401,
  "error": "Unauthorized",
  "message": "Invalid email or password",
  "timestamp": "2026-03-22T07:00:00Z"
}
```

### `400 Bad Request` — validation failure

```json
{
  "status": 400,
  "error": "Bad Request",
  "fieldErrors": {
    "title": "Title is required",
    "priority": "Priority must be between 1 and 3"
  },
  "timestamp": "2026-03-22T07:00:00Z"
}
```

### `404 Not Found`

```json
{
  "status": 404,
  "error": "Not Found",
  "message": "Task not found: c1d2e3f4-a5b6-7890-cdef-123456789abc",
  "timestamp": "2026-03-22T07:00:00Z"
}
```

### `500 Internal Server Error`

```json
{
  "status": 500,
  "error": "Internal Server Error",
  "message": "An unexpected error occurred",
  "timestamp": "2026-03-22T07:00:00Z"
}
```
