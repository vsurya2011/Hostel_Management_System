package com.hostel.management.service.impl;

import com.hostel.management.dto.request.LoginRequest;
import com.hostel.management.dto.request.RefreshTokenRequest;
import com.hostel.management.dto.request.RegisterRequest;
import com.hostel.management.entity.Role;
import com.hostel.management.entity.Staff;
import com.hostel.management.entity.User;
import com.hostel.management.exception.BadRequestException;
import com.hostel.management.exception.DuplicateResourceException;
import com.hostel.management.repository.RoleRepository;
import com.hostel.management.repository.StaffRepository;
import com.hostel.management.repository.UserRepository;
import com.hostel.management.security.JwtProvider;
import com.hostel.management.security.JwtTokenResponse;
import com.hostel.management.security.UserPrincipal;
import com.hostel.management.service.AuthService;
import com.hostel.management.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final StaffRepository staffRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final NotificationService notificationService;

    @Override
    public JwtTokenResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsernameOrEmail(), request.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();

        String accessToken = jwtProvider.generateToken(principal);
        String refreshToken = jwtProvider.generateRefreshToken(principal);

        return JwtTokenResponse.builder()
                .tokenType("Bearer")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userId(principal.getId())
                .username(principal.getUsername())
                .build();
    }

    @Override
    @Transactional
    public JwtTokenResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException("Username is already taken");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email is already in use");
        }

        Role.RoleName roleName = request.getRole() != null
                ? Role.RoleName.valueOf(request.getRole().toUpperCase())
                : Role.RoleName.STUDENT;

        Role role = roleRepository.findByName(roleName)
                .orElseGet(() -> roleRepository.save(Role.builder().name(roleName).build()));

        Set<Role> roles = new HashSet<>();
        roles.add(role);

        // Wardens don't get instant access: the account is created disabled and
        // every admin is notified. A warden can only log in — and only then gets
        // to use attendance/room/complaint features — once an admin permits
        // (enables) the account. Everyone else keeps immediate access.
        boolean isWardenSignup = roleName == Role.RoleName.WARDEN;

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .enabled(!isWardenSignup)
                .accountNonLocked(true)
                .roles(roles)
                .build();

        User saved = userRepository.save(user);

        if (isWardenSignup) {
            // Give the warden a Staff profile up front so an admin can immediately
            // assign them to a hostel as soon as the account is approved.
            Staff staff = Staff.builder()
                    .user(saved)
                    .name(request.getFullName())
                    .designation("Warden")
                    .build();
            staffRepository.save(staff);

            notifyAdminsOfPendingWarden(saved);

            return JwtTokenResponse.builder()
                    .tokenType(null)
                    .accessToken(null)
                    .refreshToken(null)
                    .userId(saved.getId())
                    .username(saved.getUsername())
                    .pendingApproval(true)
                    .message("Your warden account has been created and is awaiting admin approval. "
                            + "You'll be able to sign in once an admin permits your account.")
                    .build();
        }

        UserPrincipal principal = UserPrincipal.create(saved);

        return JwtTokenResponse.builder()
                .tokenType("Bearer")
                .accessToken(jwtProvider.generateToken(principal))
                .refreshToken(jwtProvider.generateRefreshToken(principal))
                .userId(saved.getId())
                .username(saved.getUsername())
                .build();
    }

    private void notifyAdminsOfPendingWarden(User warden) {
        List<User> admins = userRepository.findByRoles_Name(Role.RoleName.ADMIN);
        String title = "New warden registration";
        String message = "Warden \"" + warden.getUsername() + "\" (" + warden.getEmail()
                + ") registered and is awaiting your approval before they can access "
                + "attendance, room, and complaint features.";
        for (User admin : admins) {
            try {
                notificationService.createNotification(admin.getId(), title, message, "ALERT");
            } catch (Exception ex) {
                // A failed notification shouldn't block registration itself.
                log.warn("Failed to notify admin {} about pending warden {}", admin.getId(), warden.getId(), ex);
            }
        }
    }

    @Override
    public JwtTokenResponse refreshToken(RefreshTokenRequest request) {
        String token = request.getRefreshToken();
        if (!jwtProvider.validateToken(token)) {
            throw new BadRequestException("Invalid or expired refresh token");
        }
        Long userId = jwtProvider.getUserIdFromToken(token);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException("User not found for this token"));
        UserPrincipal principal = UserPrincipal.create(user);

        return JwtTokenResponse.builder()
                .tokenType("Bearer")
                .accessToken(jwtProvider.generateToken(principal))
                .refreshToken(jwtProvider.generateRefreshToken(principal))
                .userId(user.getId())
                .username(user.getUsername())
                .build();
    }
}
