package com.hostel.management.controller;

import com.hostel.management.dto.response.WardenResponse;
import com.hostel.management.entity.Hostel;
import com.hostel.management.entity.Role;
import com.hostel.management.entity.Staff;
import com.hostel.management.entity.User;
import com.hostel.management.exception.BadRequestException;
import com.hostel.management.exception.ResourceNotFoundException;
import com.hostel.management.repository.HostelRepository;
import com.hostel.management.repository.StaffRepository;
import com.hostel.management.repository.UserRepository;
import com.hostel.management.service.NotificationService;
import com.hostel.management.util.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Warden lifecycle: a warden self-registers (see AuthServiceImpl), which
 * creates a disabled User + linked Staff profile and notifies every admin.
 * An admin then approves (permits) or rejects the request here. Only once
 * approved can the warden log in and use attendance/room/complaint features
 * (enforced by Spring Security's "enabled" account check). Assigning the
 * approved warden to a hostel is a separate, later admin action handled by
 * HostelController#assignWarden.
 */
@RestController
@RequestMapping("/warden")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'WARDEN')")
public class WardenController {

    private final StaffRepository staffRepository;
    private final UserRepository userRepository;
    private final HostelRepository hostelRepository;
    private final NotificationService notificationService;

    // All wardens whose accounts have already been approved (enabled).
    @GetMapping("/list")
    public ResponseEntity<ApiResponse<List<WardenResponse>>> listWardens() {
        List<Staff> wardenStaff = staffRepository.findAll().stream()
                .filter(s -> s.getUser() != null && hasWardenRole(s.getUser()) && s.getUser().isEnabled())
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(wardenStaff.stream().map(this::toResponse).collect(Collectors.toList())));
    }

    // Warden registrations awaiting admin approval.
    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<WardenResponse>>> listPending() {
        List<User> pendingUsers = userRepository.findByRoles_NameAndEnabledFalse(Role.RoleName.WARDEN);
        List<WardenResponse> response = pendingUsers.stream()
                .map(u -> staffRepository.findByUserId(u.getId()).orElse(null))
                .filter(s -> s != null)
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // Admin "permits" a warden: enables the account so they can log in and
    // immediately get attendance/room/complaint access via their WARDEN role.
    @PutMapping("/{userId}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public ResponseEntity<ApiResponse<Void>> approve(@PathVariable Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        if (!hasWardenRole(user)) {
            throw new BadRequestException("User " + userId + " is not a warden registration");
        }
        user.setEnabled(true);
        userRepository.save(user);
        notificationService.createNotification(
                user.getId(),
                "Account approved",
                "Your warden account has been approved. You can now sign in and access attendance, "
                        + "room, and complaint features. An admin will assign you to a hostel shortly.",
                "INFO");
        return ResponseEntity.ok(ApiResponse.success("Warden approved", null));
    }

    // Admin rejects a pending warden registration outright, removing it so
    // the person can re-register if this was a mistake.
    @DeleteMapping("/{userId}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public ResponseEntity<ApiResponse<Void>> reject(@PathVariable Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        if (!hasWardenRole(user)) {
            throw new BadRequestException("User " + userId + " is not a warden registration");
        }
        if (user.isEnabled()) {
            throw new BadRequestException("This warden is already approved — disable them from Admin > Users instead");
        }
        staffRepository.findByUserId(userId).ifPresent(staffRepository::delete);
        userRepository.delete(user);
        return ResponseEntity.ok(ApiResponse.success("Warden registration rejected", null));
    }

    private boolean hasWardenRole(User user) {
        return user.getRoles().stream().anyMatch(r -> r.getName() == Role.RoleName.WARDEN);
    }

    private WardenResponse toResponse(Staff staff) {
        WardenResponse dto = new WardenResponse();
        dto.setStaffId(staff.getId());
        dto.setName(staff.getName());
        dto.setPhone(staff.getPhone());
        User user = staff.getUser();
        if (user != null) {
            dto.setUserId(user.getId());
            dto.setUsername(user.getUsername());
            dto.setEmail(user.getEmail());
            dto.setEnabled(user.isEnabled());
        }
        hostelRepository.findByWardenId(staff.getId()).ifPresent(h -> {
            dto.setAssignedHostelId(h.getId());
            dto.setAssignedHostelName(h.getName());
        });
        return dto;
    }
}
