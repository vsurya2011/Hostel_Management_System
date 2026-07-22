package com.hostel.management.controller;

import com.hostel.management.dto.request.AnnouncementRequest;
import com.hostel.management.dto.response.AnnouncementResponse;
import com.hostel.management.service.AnnouncementService;
import com.hostel.management.util.ApiResponse;
import com.hostel.management.util.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/announcements")
@RequiredArgsConstructor
public class AnnouncementController {

    private final AnnouncementService announcementService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'WARDEN')")
    public ResponseEntity<ApiResponse<AnnouncementResponse>> create(@Valid @RequestBody AnnouncementRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Announcement posted", announcementService.createAnnouncement(userId, request)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<AnnouncementResponse>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success(announcementService.getAllAnnouncements()));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'WARDEN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        announcementService.deleteAnnouncement(id);
        return ResponseEntity.ok(ApiResponse.success("Announcement deleted", null));
    }
}
