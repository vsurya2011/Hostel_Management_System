package com.hostel.management.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class StudentResponse {
    private Long id;
    private Long userId;
    private String rollNumber;
    private String name;
    private String email;
    private String phone;
    private String department;
    private Integer year;
    private String guardianName;
    private String guardianPhone;
    private String address;
    private LocalDate admissionDate;
    private String status;
    private String roomNumber;
}
