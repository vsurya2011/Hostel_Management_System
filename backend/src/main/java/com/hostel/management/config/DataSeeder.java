package com.hostel.management.config;

import com.hostel.management.entity.Role;
import com.hostel.management.entity.User;
import com.hostel.management.repository.RoleRepository;
import com.hostel.management.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * Seeds the four default roles (ADMIN, WARDEN, STAFF, STUDENT) and a
 * default admin account on first startup, so the API is usable immediately.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        for (Role.RoleName roleName : Role.RoleName.values()) {
            roleRepository.findByName(roleName)
                    .orElseGet(() -> roleRepository.save(Role.builder().name(roleName).build()));
        }

        if (!userRepository.existsByUsername("admin")) {
            Role adminRole = roleRepository.findByName(Role.RoleName.ADMIN).orElseThrow();
            Set<Role> roles = new HashSet<>();
            roles.add(adminRole);

            User admin = User.builder()
                    .username("admin")
                    .email("admin@hostel.com")
                    .password(passwordEncoder.encode("Admin@123"))
                    .enabled(true)
                    .accountNonLocked(true)
                    .roles(roles)
                    .build();
            userRepository.save(admin);
            log.info("Seeded default admin user (username: admin / password: Admin@123) - change this immediately in production.");
        }
    }
}
