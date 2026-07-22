# HostelSpace — Frontend

A real React + Vite frontend for the Spring Boot Hostel Management System API, replacing
the earlier mock-data prototype. Every page in this app calls the live backend — nothing
here is simulated.

## Stack

- React 18 + Vite 5
- Tailwind CSS
- Axios (with JWT attach + automatic refresh-token retry)
- Recharts (dashboard charts)
- lucide-react (icons)

## Getting started (local dev)

```bash
cd frontend
cp .env.example .env      # defaults to http://localhost:8080/api
npm install
npm run dev                # http://localhost:5173
```

Make sure the Spring Boot backend is running first (`cd ../backend && mvn spring-boot:run`,
or via Docker — see below). Default seeded login: **admin / Admin@123** (see
`backend/.../DataSeeder.java`).

## Production build

```bash
npm run build     # outputs to dist/
npm run preview   # serve the production build locally
```

This matches `docker/frontend.Dockerfile` exactly: `npm install` → `npm run build` →
serve `dist/` with nginx.

## Docker

`docker/docker-compose.yml` now includes a `frontend` service alongside `postgres` and
`backend`:

```bash
cd docker
docker compose up --build
```

- Backend: http://localhost:8080/api
- Frontend: http://localhost:3000
- The frontend image bakes in `VITE_API_BASE_URL` at **build time** (Vite env vars are
  compiled into the JS bundle, not read at container runtime) — see the `args:` block
  in `docker-compose.yml` and the `ARG`/`ENV` lines added to `frontend.Dockerfile`. If
  you deploy the backend somewhere other than `localhost:8080`, update that build arg.

## How it's wired to the API

- `src/lib/apiClient.js` — Axios instance. Attaches `Authorization: Bearer <token>` to
  every request, unwraps the backend's `{ success, message, data, timestamp }` envelope,
  and transparently retries a request once via `POST /auth/refresh` on a 401 (queuing
  concurrent requests so only one refresh call fires).
- `src/lib/services.js` — one thin service per controller (`studentsService`,
  `roomsService`, `attendanceService`, `complaintsService`, `paymentsService`,
  `dashboardService`), each mapped 1:1 to the real endpoint paths and DTOs.
- `src/context/AuthContext.jsx` — real login/register/logout against `/auth/*`, persists
  tokens in `localStorage`, restores session on reload, decodes the JWT (see
  `src/lib/jwt.js`) to read the `roles` claim the backend embeds — there's no separate
  "who am I" endpoint, so the frontend decodes the token instead.

## Role-based navigation

Roles come straight from the JWT (`ADMIN`, `WARDEN`, `STAFF`, `STUDENT`, matching
`Role.RoleName`). The sidebar and default landing page adapt per role:

| Role | Sees | Default view |
|---|---|---|
| ADMIN | Everything | Dashboard |
| WARDEN | Everything | Dashboard |
| STAFF | Students, Rooms, Attendance, Complaints | Students |
| STUDENT | Rooms (read), Complaints, Payments | Complaints |

## Known limitations (backend gaps, not frontend bugs)

1. ~~No `/students/me` endpoint.~~ **Fixed** — `GET /students/me` now resolves the
   Student record for the logged-in user server-side via the JWT (see
   `StudentController#getMyProfile`, using `StudentRepository.findByUserId`).
   `src/hooks/useMyStudent.js` calls this directly instead of guessing.
2. ~~No floor-listing endpoint.~~ **Fixed** — `GET /floors` (and `GET /blocks`) now
   exist. The "Add Room" form uses a real Floor dropdown, and the new **Hostels**
   page lets you create the Hostel → Block → Floor hierarchy from the UI.
3. **`JwtTokenResponse` doesn't return the user's role/name directly** — only
   `userId`/`username`. The frontend decodes the JWT payload itself to get `roles`
   (this is fine and expected — that's literally why the backend puts them in the token
   claims), but it does mean the display name shown in the sidebar is just the username,
   not a "full name" field, since that's what's available in the token/response.

Item 3 doesn't block day-to-day use — it's just a spot where a future backend
field would let the frontend show a nicer display name.

