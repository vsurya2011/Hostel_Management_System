package com.hostel.management.service.impl;

import com.hostel.management.dto.request.AttendanceRequest;
import com.hostel.management.dto.response.AttendanceResponse;
import com.hostel.management.entity.Attendance;
import com.hostel.management.entity.Student;
import com.hostel.management.entity.User;
import com.hostel.management.exception.ResourceNotFoundException;
import com.hostel.management.mapper.EntityMapper;
import com.hostel.management.repository.AttendanceRepository;
import com.hostel.management.repository.StudentRepository;
import com.hostel.management.repository.UserRepository;
import com.hostel.management.service.AttendanceService;
import com.hostel.management.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttendanceServiceImpl implements AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final StudentRepository studentRepository;
    private final UserRepository userRepository;
    private final EntityMapper entityMapper;

    @Override
    @Transactional
    public AttendanceResponse markAttendance(AttendanceRequest request) {
        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student", "id", request.getStudentId()));

        Attendance attendance = attendanceRepository
                .findByStudentIdAndDate(student.getId(), request.getDate())
                .orElse(Attendance.builder().student(student).date(request.getDate()).build());

        attendance.setStatus(Attendance.AttendanceStatus.valueOf(request.getStatus().toUpperCase()));

        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (currentUserId != null) {
            User marker = userRepository.findById(currentUserId).orElse(null);
            attendance.setMarkedBy(marker);
        }

        return entityMapper.toAttendanceResponse(attendanceRepository.save(attendance));
    }

    @Override
    public List<AttendanceResponse> getAttendanceByStudent(Long studentId) {
        return attendanceRepository.findByStudentId(studentId).stream()
                .map(entityMapper::toAttendanceResponse).collect(Collectors.toList());
    }

    @Override
    public List<AttendanceResponse> getAttendanceByDate(LocalDate date) {
        return attendanceRepository.findByDate(date).stream()
                .map(entityMapper::toAttendanceResponse).collect(Collectors.toList());
    }
}
