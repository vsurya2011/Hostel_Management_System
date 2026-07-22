package com.hostel.management.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class DashboardResponse {
    private long totalStudents;
    private long totalRooms;
    private long occupiedRooms;
    private long availableRooms;
    private long pendingComplaints;
    private long pendingLeaveRequests;
    private BigDecimal totalRevenue;
    private long todayVisitors;
}
