package com.hostel.management.controller;

import com.hostel.management.dto.response.UserResponse;
import com.hostel.management.entity.Role;
import com.hostel.management.entity.User;
import com.hostel.management.repository.StudentRepository;
import com.hostel.management.repository.UserRepository;
import com.hostel.management.util.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class AdminController {

    private final UserRepository userRepository;
    private final StudentRepository studentRepository;

    // Readable by ADMIN or WARDEN (both can create Students and need to pick a user to link).
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'WARDEN')")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers() {
        List<UserResponse> users = userRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(users));
    }

    @PutMapping("/{id}/enable")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> enableUser(@PathVariable Long id) {
        userRepository.findById(id).ifPresent(u -> {
            u.setEnabled(true);
            userRepository.save(u);
        });
        return ResponseEntity.ok(ApiResponse.success("User enabled", null));
    }

    @PutMapping("/{id}/disable")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> disableUser(@PathVariable Long id) {
        userRepository.findById(id).ifPresent(u -> {
            u.setEnabled(false);
            userRepository.save(u);
        });
        return ResponseEntity.ok(ApiResponse.success("User disabled", null));
    }

    // Never expose the password hash to the frontend — map to a clean DTO instead.
    private UserResponse toResponse(User user) {
        UserResponse dto = new UserResponse();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setEnabled(user.isEnabled());
        dto.setRoles(user.getRoles().stream().map(Role::getName).map(Enum::name).collect(Collectors.toSet()));
        dto.setHasStudentProfile(studentRepository.existsByUserId(user.getId()));
        return dto;
    }
}
