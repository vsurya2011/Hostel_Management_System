package com.hostel.management.service.impl;

import com.hostel.management.entity.Attendance;
import com.hostel.management.entity.Payment;
import com.hostel.management.entity.Room;
import com.hostel.management.repository.AttendanceRepository;
import com.hostel.management.repository.PaymentRepository;
import com.hostel.management.repository.RoomRepository;
import com.hostel.management.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final RoomRepository roomRepository;
    private final PaymentRepository paymentRepository;
    private final AttendanceRepository attendanceRepository;

    @Override
    public Map<String, Object> generateOccupancyReport() {
        List<Room> rooms = roomRepository.findAll();
        long total = rooms.size();
        long full = rooms.stream().filter(r -> r.getStatus() == Room.RoomStatus.FULL).count();
        long available = rooms.stream().filter(r -> r.getStatus() == Room.RoomStatus.AVAILABLE).count();
        long maintenance = rooms.stream().filter(r -> r.getStatus() == Room.RoomStatus.MAINTENANCE).count();

        Map<String, Object> report = new HashMap<>();
        report.put("totalRooms", total);
        report.put("fullRooms", full);
        report.put("availableRooms", available);
        report.put("maintenanceRooms", maintenance);
        report.put("occupancyRate", total == 0 ? 0 : (double) full / total * 100);
        return report;
    }

    @Override
    public Map<String, Object> generatePaymentReport(LocalDate from, LocalDate to) {
        List<Payment> payments = paymentRepository.findAll().stream()
                .filter(p -> p.getPaymentDate() != null
                        && !p.getPaymentDate().isBefore(from)
                        && !p.getPaymentDate().isAfter(to))
                .collect(Collectors.toList());

        BigDecimal total = payments.stream()
                .filter(p -> p.getStatus() == Payment.PaymentStatus.SUCCESS)
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, Object> report = new HashMap<>();
        report.put("from", from);
        report.put("to", to);
        report.put("totalPayments", payments.size());
        report.put("totalCollected", total);
        return report;
    }

    @Override
    public Map<String, Object> generateAttendanceReport(LocalDate from, LocalDate to) {
        List<Attendance> attendanceList = attendanceRepository.findAll().stream()
                .filter(a -> a.getDate() != null && !a.getDate().isBefore(from) && !a.getDate().isAfter(to))
                .collect(Collectors.toList());

        long present = attendanceList.stream().filter(a -> a.getStatus() == Attendance.AttendanceStatus.PRESENT).count();
        long absent = attendanceList.stream().filter(a -> a.getStatus() == Attendance.AttendanceStatus.ABSENT).count();

        Map<String, Object> report = new HashMap<>();
        report.put("from", from);
        report.put("to", to);
        report.put("totalRecords", attendanceList.size());
        report.put("present", present);
        report.put("absent", absent);
        return report;
    }
}
