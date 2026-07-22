package com.hostel.management.controller;

import com.hostel.management.dto.request.StudentRequest;
import com.hostel.management.dto.response.StudentResponse;
import com.hostel.management.security.UserPrincipal;
import com.hostel.management.service.StudentService;
import com.hostel.management.util.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'WARDEN')")
    public ResponseEntity<ApiResponse<StudentResponse>> create(@Valid @RequestBody StudentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Student created", studentService.createStudent(request)));
    }

    // Resolves the Student profile linked to whoever is currently logged in.
    // Lets the frontend stop guessing (matching email/roll number against username).
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<StudentResponse>> getMyProfile(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(ApiResponse.success(studentService.getStudentByUserId(principal.getId())));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<StudentResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(studentService.getStudentById(id)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<StudentResponse>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success(studentService.getAllStudents()));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'WARDEN')")
    public ResponseEntity<ApiResponse<StudentResponse>> update(@PathVariable Long id, @Valid @RequestBody StudentRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Student updated", studentService.updateStudent(id, request)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return ResponseEntity.ok(ApiResponse.success("Student deleted", null));
    }
}
