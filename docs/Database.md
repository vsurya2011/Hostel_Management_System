# Database

PostgreSQL. Schema lives in `database/schema.sql` (mirrored under
`backend/src/main/resources/db/migration/V1__init_schema.sql` for Flyway).

Core tables: `users`, `roles`, `user_roles`, `students`, `staff`,
`hostels`, `blocks`, `floors`, `rooms`, `room_allocations`, `attendance`,
`leave_requests`, `complaints`, `complaint_replies`, `payments`,
`visitors`, `announcements`, `notifications`, `audit_logs`.

See `database/seed.sql` for sample data (a demo hostel/block/floor/room).
