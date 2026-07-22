package com.hostel.management.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class AttendanceResponse {
    private Long id;
    private String studentName;
    private LocalDate date;
    private String status;
    private String markedByName;
}
