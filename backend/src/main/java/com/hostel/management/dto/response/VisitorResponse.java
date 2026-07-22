package com.hostel.management.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class VisitorResponse {
    private Long id;
    private String studentName;
    private String visitorName;
    private String relation;
    private String phone;
    private String purpose;
    private LocalDateTime checkInTime;
    private LocalDateTime checkOutTime;
    private String status;
}
