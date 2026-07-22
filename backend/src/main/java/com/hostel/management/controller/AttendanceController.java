package com.hostel.management.controller;

import com.hostel.management.dto.request.AttendanceRequest;
import com.hostel.management.dto.response.AttendanceResponse;
import com.hostel.management.service.AttendanceService;
import com.hostel.management.util.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'WARDEN', 'STAFF')")
    public ResponseEntity<ApiResponse<AttendanceResponse>> mark(@Valid @RequestBody AttendanceRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Attendance marked", attendanceService.markAttendance(request)));
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<ApiResponse<List<AttendanceResponse>>> byStudent(@PathVariable Long studentId) {
        return ResponseEntity.ok(ApiResponse.success(attendanceService.getAttendanceByStudent(studentId)));
    }

    @GetMapping("/date")
    public ResponseEntity<ApiResponse<List<AttendanceResponse>>> byDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(ApiResponse.success(attendanceService.getAttendanceByDate(date)));
    }
}
