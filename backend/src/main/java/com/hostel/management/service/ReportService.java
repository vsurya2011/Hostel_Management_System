package com.hostel.management.service;

import java.time.LocalDate;
import java.util.Map;

public interface ReportService {
    Map<String, Object> generateOccupancyReport();
    Map<String, Object> generatePaymentReport(LocalDate from, LocalDate to);
    Map<String, Object> generateAttendanceReport(LocalDate from, LocalDate to);
}
