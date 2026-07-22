package com.hostel.management.service.impl;

import com.hostel.management.dto.request.LeaveRequestDto;
import com.hostel.management.dto.response.LeaveResponse;
import com.hostel.management.entity.LeaveRequest;
import com.hostel.management.entity.Student;
import com.hostel.management.entity.User;
import com.hostel.management.exception.BadRequestException;
import com.hostel.management.exception.ResourceNotFoundException;
import com.hostel.management.mapper.EntityMapper;
import com.hostel.management.repository.LeaveRepository;
import com.hostel.management.repository.StudentRepository;
import com.hostel.management.repository.UserRepository;
import com.hostel.management.service.LeaveService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LeaveServiceImpl implements LeaveService {

    private final LeaveRepository leaveRepository;
    private final StudentRepository studentRepository;
    private final UserRepository userRepository;
    private final EntityMapper entityMapper;

    @Override
    @Transactional
    public LeaveResponse applyLeave(Long studentId, LeaveRequestDto request) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student", "id", studentId));

        if (request.getToDate().isBefore(request.getFromDate())) {
            throw new BadRequestException("Leave end date cannot be before start date");
        }

        LeaveRequest leave = LeaveRequest.builder()
                .student(student)
                .fromDate(request.getFromDate())
                .toDate(request.getToDate())
                .reason(request.getReason())
                .status(LeaveRequest.LeaveStatus.PENDING)
                .build();

        return entityMapper.toLeaveResponse(leaveRepository.save(leave));
    }

    @Override
    @Transactional
    public LeaveResponse approveLeave(Long leaveId, Long approverId) {
        LeaveRequest leave = findLeave(leaveId);
        leave.setStatus(LeaveRequest.LeaveStatus.APPROVED);
        leave.setApprovedBy(resolveUser(approverId));
        return entityMapper.toLeaveResponse(leaveRepository.save(leave));
    }

    @Override
    @Transactional
    public LeaveResponse rejectLeave(Long leaveId, Long approverId, String remarks) {
        LeaveRequest leave = findLeave(leaveId);
        leave.setStatus(LeaveRequest.LeaveStatus.REJECTED);
        leave.setApprovedBy(resolveUser(approverId));
        leave.setRemarks(remarks);
        return entityMapper.toLeaveResponse(leaveRepository.save(leave));
    }

    @Override
    public List<LeaveResponse> getLeavesByStudent(Long studentId) {
        return leaveRepository.findByStudentId(studentId).stream()
                .map(entityMapper::toLeaveResponse).collect(Collectors.toList());
    }

    @Override
    public List<LeaveResponse> getPendingLeaves() {
        return leaveRepository.findByStatus(LeaveRequest.LeaveStatus.PENDING).stream()
                .map(entityMapper::toLeaveResponse).collect(Collectors.toList());
    }

    private LeaveRequest findLeave(Long id) {
        return leaveRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Leave request", "id", id));
    }

    private User resolveUser(Long userId) {
        if (userId == null) return null;
        return userRepository.findById(userId).orElse(null);
    }
}
