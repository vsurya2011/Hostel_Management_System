package com.hostel.management.service;

import com.hostel.management.dto.request.LeaveRequestDto;
import com.hostel.management.dto.response.LeaveResponse;

import java.util.List;

public interface LeaveService {
    LeaveResponse applyLeave(Long studentId, LeaveRequestDto request);
    LeaveResponse approveLeave(Long leaveId, Long approverId);
    LeaveResponse rejectLeave(Long leaveId, Long approverId, String remarks);
    List<LeaveResponse> getLeavesByStudent(Long studentId);
    List<LeaveResponse> getPendingLeaves();
}
