package com.hostel.management.service;

import com.hostel.management.dto.request.ComplaintReplyRequest;
import com.hostel.management.dto.request.ComplaintRequest;
import com.hostel.management.dto.response.ComplaintResponse;

import java.util.List;

public interface ComplaintService {
    ComplaintResponse createComplaint(Long studentId, ComplaintRequest request);
    ComplaintResponse getComplaintById(Long id);
    List<ComplaintResponse> getAllComplaints();
    List<ComplaintResponse> getComplaintsByStudent(Long studentId);
    ComplaintResponse updateStatus(Long id, String status);
    ComplaintResponse addReply(Long complaintId, Long userId, ComplaintReplyRequest request);
}
