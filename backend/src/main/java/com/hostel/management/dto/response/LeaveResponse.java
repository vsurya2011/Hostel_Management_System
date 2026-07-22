package com.hostel.management.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class LeaveResponse {
    private Long id;
    private String studentName;
    private LocalDate fromDate;
    private LocalDate toDate;
    private String reason;
    private String status;
    private String approvedByName;
    private String remarks;
}
