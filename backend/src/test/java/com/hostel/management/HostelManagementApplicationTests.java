package com.hostel.management;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Smoke test: verifies the Spring application context loads successfully
 * with all beans (security, JPA repositories, services, controllers) wired.
 * Uses the "test" profile against an in-memory H2 database.
 */
@SpringBootTest
@ActiveProfiles("test")
class HostelManagementApplicationTests {

    @Test
    void contextLoads() {
    }
}
