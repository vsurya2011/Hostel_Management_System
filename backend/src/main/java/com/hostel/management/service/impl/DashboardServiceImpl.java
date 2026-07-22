package com.hostel.management.service.impl;

import com.hostel.management.dto.response.DashboardResponse;
import com.hostel.management.entity.Complaint;
import com.hostel.management.entity.LeaveRequest;
import com.hostel.management.entity.Payment;
import com.hostel.management.entity.Room;
import com.hostel.management.entity.Visitor;
import com.hostel.management.repository.*;
import com.hostel.management.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final StudentRepository studentRepository;
    private final RoomRepository roomRepository;
    private final ComplaintRepository complaintRepository;
    private final LeaveRepository leaveRepository;
    private final PaymentRepository paymentRepository;
    private final VisitorRepository visitorRepository;

    @Override
    @Cacheable("dashboard")
    public DashboardResponse getDashboardStats() {
        DashboardResponse stats = new DashboardResponse();
        stats.setTotalStudents(studentRepository.count());

        long totalRooms = roomRepository.count();
        long occupiedRooms = roomRepository.findByStatus(Room.RoomStatus.FULL).size();
        stats.setTotalRooms(totalRooms);
        stats.setOccupiedRooms(occupiedRooms);
        stats.setAvailableRooms(totalRooms - occupiedRooms);

        stats.setPendingComplaints(complaintRepository.findByStatus(Complaint.ComplaintStatus.OPEN).size());
        stats.setPendingLeaveRequests(leaveRepository.findByStatus(LeaveRequest.LeaveStatus.PENDING).size());

        BigDecimal totalRevenue = paymentRepository.findByStatus(Payment.PaymentStatus.SUCCESS).stream()
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        stats.setTotalRevenue(totalRevenue);

        stats.setTodayVisitors(visitorRepository.findByStatus(Visitor.VisitorStatus.CHECKED_IN).stream()
                .filter(v -> v.getCheckInTime() != null && v.getCheckInTime().toLocalDate().isEqual(LocalDate.now()))
                .count());

        return stats;
    }
}
