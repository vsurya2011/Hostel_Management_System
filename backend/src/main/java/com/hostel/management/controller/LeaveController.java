package com.hostel.management.controller;

import com.hostel.management.dto.request.LeaveRequestDto;
import com.hostel.management.dto.response.LeaveResponse;
import com.hostel.management.service.LeaveService;
import com.hostel.management.util.ApiResponse;
import com.hostel.management.util.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/leaves")
@RequiredArgsConstructor
public class LeaveController {

    private final LeaveService leaveService;

    @PostMapping("/student/{studentId}")
    public ResponseEntity<ApiResponse<LeaveResponse>> apply(@PathVariable Long studentId, @Valid @RequestBody LeaveRequestDto request) {
        return ResponseEntity.ok(ApiResponse.success("Leave request submitted", leaveService.applyLeave(studentId, request)));
    }

    @PutMapping("/{id}/approve")
    @PreAuthorize("hasAnyRole('ADMIN', 'WARDEN')")
    public ResponseEntity<ApiResponse<LeaveResponse>> approve(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Leave approved", leaveService.approveLeave(id, SecurityUtils.getCurrentUserId())));
    }

    @PutMapping("/{id}/reject")
    @PreAuthorize("hasAnyRole('ADMIN', 'WARDEN')")
    public ResponseEntity<ApiResponse<LeaveResponse>> reject(@PathVariable Long id, @RequestParam(required = false) String remarks) {
        return ResponseEntity.ok(ApiResponse.success("Leave rejected", leaveService.rejectLeave(id, SecurityUtils.getCurrentUserId(), remarks)));
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<ApiResponse<List<LeaveResponse>>> byStudent(@PathVariable Long studentId) {
        return ResponseEntity.ok(ApiResponse.success(leaveService.getLeavesByStudent(studentId)));
    }

    @GetMapping("/pending")
    @PreAuthorize("hasAnyRole('ADMIN', 'WARDEN')")
    public ResponseEntity<ApiResponse<List<LeaveResponse>>> pending() {
        return ResponseEntity.ok(ApiResponse.success(leaveService.getPendingLeaves()));
    }
}
