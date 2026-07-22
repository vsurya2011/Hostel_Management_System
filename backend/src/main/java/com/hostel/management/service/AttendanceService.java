package com.hostel.management.service;

import com.hostel.management.dto.request.AttendanceRequest;
import com.hostel.management.dto.response.AttendanceResponse;

import java.time.LocalDate;
import java.util.List;

public interface AttendanceService {
    AttendanceResponse markAttendance(AttendanceRequest request);
    List<AttendanceResponse> getAttendanceByStudent(Long studentId);
    List<AttendanceResponse> getAttendanceByDate(LocalDate date);
}
