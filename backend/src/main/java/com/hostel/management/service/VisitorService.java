package com.hostel.management.service;

import com.hostel.management.dto.request.VisitorRequest;
import com.hostel.management.dto.response.VisitorResponse;

import java.util.List;

public interface VisitorService {
    VisitorResponse checkIn(VisitorRequest request);
    VisitorResponse checkOut(Long visitorId);
    List<VisitorResponse> getVisitorsByStudent(Long studentId);
    List<VisitorResponse> getActiveVisitors();
}
