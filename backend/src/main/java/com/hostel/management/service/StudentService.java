package com.hostel.management.service;

import com.hostel.management.dto.request.StudentRequest;
import com.hostel.management.dto.response.StudentResponse;

import java.util.List;

public interface StudentService {
    StudentResponse createStudent(StudentRequest request);
    StudentResponse getStudentById(Long id);
    StudentResponse getStudentByUserId(Long userId);
    List<StudentResponse> getAllStudents();
    StudentResponse updateStudent(Long id, StudentRequest request);
    void deleteStudent(Long id);
}
