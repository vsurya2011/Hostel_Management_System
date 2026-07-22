# Hostel Management System

A full-stack hostel/dormitory management platform: student records, room
allocation, attendance, leave requests, complaints, payments, visitor
logs, announcements, and an admin/warden dashboard — backed by a
Spring Boot REST API with JWT authentication.

## Backend stack

- **Java 17**, **Spring Boot 3.3**
- Spring Web, Spring Data JPA, Spring Security (JWT), Bean Validation
- **PostgreSQL** + Flyway migrations
- Caffeine cache, ModelMapper, springdoc-openapi (Swagger UI)

## Running locally

### Option A — Docker Compose (recommended)

```bash
cd docker
docker compose up --build
```

This starts Postgres, the backend (port `8080`), and the frontend (port `5173`).

### Option B — Backend only, against a local Postgres

```bash
createdb hostel_management_dev

cd backend
mvn spring-boot:run
# active profile defaults to "dev" (see application.yml)
```

The API is served under `http://localhost:8080/api`.
Swagger UI: `http://localhost:8080/api/swagger-ui.html`.

On first startup, `DataSeeder` creates the four roles (`ADMIN`, `WARDEN`,
`STAFF`, `STUDENT`) and a default admin account:

```
username: admin
password: Admin@123
```

**Change this password immediately in any non-local environment.**

## Configuration

Key environment variables (see `application.yml` / `application-*.yml`):

| Variable | Purpose | Default |
|---|---|---|
| `DB_URL` / `DB_USERNAME` / `DB_PASSWORD` | Postgres connection | dev: local Postgres |
| `JWT_SECRET` | HMAC signing key for JWTs | dev placeholder — **override in prod** |
| `JWT_EXPIRATION` / `JWT_REFRESH_EXPIRATION` | Token lifetimes (ms) | 24h / 7d |
| `SERVER_PORT` | HTTP port | 8080 |
| `FILE_UPLOAD_DIR` | Local file storage path | `./uploads` |

## Project layout

```
backend/    Spring Boot REST API (this is the primary deliverable)
frontend/   React + Vite SPA (scaffolding only)
database/   schema.sql / seed.sql (mirrors backend/.../db/migration)
docker/     docker-compose.yml + Dockerfiles
docs/       API / architecture / deployment notes
postman/    Postman collection for manual API testing
```

## First-time data setup order

The database starts empty apart from roles and the seeded admin. To get
usable data, create things in this order (all via the API / frontend UI,
admin token required for most POSTs):

1. **Register a user** — `POST /auth/register` (for each student/staff you want)
2. **Create a Hostel** — `POST /hostels`
3. **Create a Block** in that hostel — `POST /blocks` (`hostelId`)
4. **Create a Floor** in that block — `POST /floors` (`blockId`)
5. **Create a Student**, linked to a registered user — `POST /students` (`userId`)
6. **Create a Room** on that floor — `POST /rooms` (`floorId`)
7. **Allocate** the student to the room — `POST /rooms/allocate`

The frontend's **Hostels** page (admin/warden only) handles steps 2–4 with
a drill-down UI. **Students** and **Rooms** pages handle the rest.

## API overview

All endpoints are namespaced under `/api`. Authentication is via
`Authorization: Bearer <token>`, obtained from `POST /api/auth/login`.

| Resource | Base path |
|---|---|
| Auth | `/auth` (login, register, refresh) |
| Students | `/students` (create requires an existing `userId` to link to) |
| Hostels | `/hostels` |
| Blocks | `/blocks` (create requires `hostelId`) |
| Floors | `/floors` (create requires `blockId`) |
| Rooms | `/rooms` (create requires `floorId`; incl. `/rooms/allocate`, `/rooms/vacate/{studentId}`) |
| Attendance | `/attendance` |
| Leave requests | `/leaves` |
| Complaints | `/complaints` |
| Payments | `/payments` |
| Visitors | `/visitors` |
| Announcements | `/announcements` |
| Notifications | `/notifications` |
| Dashboard stats | `/dashboard` |
| Reports | `/reports` |
| File upload | `/files` |
| Admin (user management) | `/admin/users` |

See `docs/API.md` and the Postman collection for full request/response
shapes, and `/api/swagger-ui.html` for live, always-current docs generated
from the code.
