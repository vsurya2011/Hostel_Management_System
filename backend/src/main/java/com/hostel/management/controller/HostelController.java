package com.hostel.management.controller;

import com.hostel.management.dto.request.HostelRequest;
import com.hostel.management.dto.response.HostelResponse;
import com.hostel.management.service.HostelService;
import com.hostel.management.util.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/hostels")
@RequiredArgsConstructor
public class HostelController {

    private final HostelService hostelService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<HostelResponse>> create(@Valid @RequestBody HostelRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Hostel created", hostelService.createHostel(request)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<HostelResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(hostelService.getHostelById(id)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<HostelResponse>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success(hostelService.getAllHostels()));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<HostelResponse>> update(@PathVariable Long id, @Valid @RequestBody HostelRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Hostel updated", hostelService.updateHostel(id, request)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        hostelService.deleteHostel(id);
        return ResponseEntity.ok(ApiResponse.success("Hostel deleted", null));
    }

    // Only admins assign wardens to hostels — this happens after the admin
    // has already permitted (approved) the warden's account.
    @PutMapping("/{id}/assign-warden/{staffId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<HostelResponse>> assignWarden(@PathVariable Long id, @PathVariable Long staffId) {
        return ResponseEntity.ok(ApiResponse.success("Warden assigned", hostelService.assignWarden(id, staffId)));
    }

    @DeleteMapping("/{id}/assign-warden")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<HostelResponse>> unassignWarden(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Warden unassigned", hostelService.unassignWarden(id)));
    }
}
