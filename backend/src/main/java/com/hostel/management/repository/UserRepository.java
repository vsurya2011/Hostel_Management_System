package com.hostel.management.repository;

import com.hostel.management.entity.Role;
import com.hostel.management.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    // Used to fan out "new warden pending approval" notifications to every admin.
    List<User> findByRoles_Name(Role.RoleName roleName);

    // Wardens who registered but haven't been permitted access by an admin yet.
    List<User> findByRoles_NameAndEnabledFalse(Role.RoleName roleName);
}
