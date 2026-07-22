# API Reference (summary)

Base URL: `http://localhost:8080/api`
Auth: `Authorization: Bearer <accessToken>` (obtained from `/auth/login`)

For the full, always-current schema (request/response bodies, validation
rules), see Swagger UI at `/swagger-ui.html` — it's generated directly from
the controller/DTO annotations, so it can't drift from the code.

## Auth
- `POST /auth/register` — create an account (`username`, `email`, `password`, `fullName`, optional `role`)
- `POST /auth/login` — `{ usernameOrEmail, password }` → JWT access + refresh token
- `POST /auth/refresh` — `{ refreshToken }` → new token pair

## Students
- `POST /students` (ADMIN, WARDEN) · `GET /students` · `GET /students/{id}` · `PUT /students/{id}` (ADMIN, WARDEN) · `DELETE /students/{id}` (ADMIN)

## Hostels / Rooms
- `POST|GET|PUT|DELETE /hostels[/{id}]` (writes: ADMIN)
- `POST|GET|PUT|DELETE /rooms[/{id}]`, `GET /rooms/available`
- `POST /rooms/allocate` — `{ studentId, roomId }`
- `POST /rooms/vacate/{studentId}`

## Attendance
- `POST /attendance` — `{ studentId, date, status }`
- `GET /attendance/student/{studentId}`, `GET /attendance/date?date=YYYY-MM-DD`

## Leave requests
- `POST /leaves/student/{studentId}` — `{ fromDate, toDate, reason }`
- `PUT /leaves/{id}/approve` / `PUT /leaves/{id}/reject?remarks=...` (ADMIN, WARDEN)
- `GET /leaves/student/{studentId}`, `GET /leaves/pending` (ADMIN, WARDEN)

## Complaints
- `POST /complaints/student/{studentId}` — `{ title, description, category, priority }`
- `GET /complaints/{id}`, `GET /complaints` (staff), `GET /complaints/student/{studentId}`
- `PUT /complaints/{id}/status?status=...`, `POST /complaints/{id}/reply`

## Payments
- `POST /payments` — `{ studentId, amount, paymentType, paymentMethod }`
- `GET /payments/{id}`, `GET /payments/student/{studentId}`, `GET /payments` (ADMIN, WARDEN)
- `PUT /payments/{id}/status?status=...&transactionId=...`

## Visitors
- `POST /visitors/check-in`, `PUT /visitors/{id}/check-out`
- `GET /visitors/student/{studentId}`, `GET /visitors/active`

## Announcements / Notifications
- `POST /announcements` (ADMIN, WARDEN), `GET /announcements`, `DELETE /announcements/{id}`
- `GET /notifications/user/{userId}`, `GET /notifications/user/{userId}/unread`, `PUT /notifications/{id}/read`

## Dashboard / Reports (ADMIN, WARDEN)
- `GET /dashboard`
- `GET /reports/occupancy`, `GET /reports/payments?from=...&to=...`, `GET /reports/attendance?from=...&to=...`

## Files
- `POST /files/upload` (multipart `file`), `DELETE /files/{fileName}`
