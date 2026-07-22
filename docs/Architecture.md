# Architecture

**Layers:** `controller` → `service` (interface + `impl`) → `repository` (Spring Data JPA) → PostgreSQL.

- **Security**: stateless JWT auth. `JwtAuthenticationFilter` validates the
  bearer token on every request and populates the `SecurityContext`;
  `@PreAuthorize` on controller methods enforces role checks
  (`ADMIN` / `WARDEN` / `STAFF` / `STUDENT`).
- **Entities** (`entity/`) all extend `BaseEntity` for `id` + audited
  `createdAt`/`updatedAt`. Core aggregates: `User` (auth identity) with a
  1:1 `Student` or `Staff` profile; `Hostel` → `Block` → `Floor` → `Room`;
  `RoomAllocation` links a `Student` to a `Room`.
- **DTOs**: requests are validated with Bean Validation; responses are
  hand-mapped from entities via `EntityMapper` to avoid leaking JPA proxies
  / lazy-loading issues and to shape nested data (e.g. a student's current
  room number) cleanly.
- **Caching**: Caffeine-backed `@Cacheable`/`@CacheEvict` on read-heavy,
  write-light data (student list, dashboard stats).
- **Migrations**: Flyway (`db/migration/V1__init_schema.sql`) owns the
  production schema; `ddl-auto=validate` in prod ensures the JPA model and
  the migration never silently diverge. Dev uses `ddl-auto=update` for
  fast iteration.
