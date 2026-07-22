# Deployment

1. Provision a PostgreSQL instance; set `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`.
2. Set a strong, random `JWT_SECRET` (256-bit+, base64-encoded) — do **not**
   reuse the development default in `application.yml`.
3. Run with `SPRING_PROFILES_ACTIVE=prod`. Flyway applies
   `db/migration/V1__init_schema.sql` automatically on boot; `ddl-auto=validate`
   will fail fast if the schema and entities disagree.
4. Build & run via Docker:
   ```bash
   cd docker && docker compose up --build -d
   ```
   or build the jar directly: `cd backend && mvn package -DskipTests` →
   `java -jar target/hostel-management.jar`.
5. Change the seeded default admin password (`admin` / `Admin@123`)
   immediately after first login in any shared environment.
