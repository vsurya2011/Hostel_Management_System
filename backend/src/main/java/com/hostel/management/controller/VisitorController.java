package com.hostel.management.controller;

import com.hostel.management.dto.request.VisitorRequest;
import com.hostel.management.dto.response.VisitorResponse;
import com.hostel.management.service.VisitorService;
import com.hostel.management.util.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/visitors")
@RequiredArgsConstructor
public class VisitorController {

    private final VisitorService visitorService;

    @PostMapping("/check-in")
    public ResponseEntity<ApiResponse<VisitorResponse>> checkIn(@Valid @RequestBody VisitorRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Visitor checked in", visitorService.checkIn(request)));
    }

    @PutMapping("/{id}/check-out")
    public ResponseEntity<ApiResponse<VisitorResponse>> checkOut(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Visitor checked out", visitorService.checkOut(id)));
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<ApiResponse<List<VisitorResponse>>> byStudent(@PathVariable Long studentId) {
        return ResponseEntity.ok(ApiResponse.success(visitorService.getVisitorsByStudent(studentId)));
    }

    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<VisitorResponse>>> active() {
        return ResponseEntity.ok(ApiResponse.success(visitorService.getActiveVisitors()));
    }
}
