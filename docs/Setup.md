# Local Setup

Prerequisites: JDK 17, Maven 3.9+, PostgreSQL 14+ (or Docker).

```bash
git clone <repo-url>
cd HostelManagementSystem

# 1. Database
createdb hostel_management_dev

# 2. Backend
cd backend
mvn spring-boot:run
```

API available at `http://localhost:8080/api`, Swagger UI at
`http://localhost:8080/api/swagger-ui.html`.

Default admin credentials are seeded automatically — see the main README.
