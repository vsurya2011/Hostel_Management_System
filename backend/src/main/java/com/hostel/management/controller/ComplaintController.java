package com.hostel.management.controller;

import com.hostel.management.dto.request.ComplaintReplyRequest;
import com.hostel.management.dto.request.ComplaintRequest;
import com.hostel.management.dto.response.ComplaintResponse;
import com.hostel.management.service.ComplaintService;
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
@RequestMapping("/complaints")
@RequiredArgsConstructor
public class ComplaintController {

    private final ComplaintService complaintService;

    @PostMapping("/student/{studentId}")
    public ResponseEntity<ApiResponse<ComplaintResponse>> create(@PathVariable Long studentId, @Valid @RequestBody ComplaintRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Complaint filed", complaintService.createComplaint(studentId, request)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ComplaintResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(complaintService.getComplaintById(id)));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'WARDEN', 'STAFF')")
    public ResponseEntity<ApiResponse<List<ComplaintResponse>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success(complaintService.getAllComplaints()));
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<ApiResponse<List<ComplaintResponse>>> byStudent(@PathVariable Long studentId) {
        return ResponseEntity.ok(ApiResponse.success(complaintService.getComplaintsByStudent(studentId)));
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'WARDEN', 'STAFF')")
    public ResponseEntity<ApiResponse<ComplaintResponse>> updateStatus(@PathVariable Long id, @RequestParam String status) {
        return ResponseEntity.ok(ApiResponse.success("Status updated", complaintService.updateStatus(id, status)));
    }

    @PostMapping("/{id}/reply")
    public ResponseEntity<ApiResponse<ComplaintResponse>> reply(@PathVariable Long id, @Valid @RequestBody ComplaintReplyRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(ApiResponse.success("Reply added", complaintService.addReply(id, userId, request)));
    }
}
