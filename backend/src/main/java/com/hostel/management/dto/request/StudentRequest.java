package com.hostel.management.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class StudentRequest {
    // Only required when creating a student (linking a user to a new profile).
    // The edit form doesn't collect this, and updateStudent() never touches it,
    // so it must not be validated with @NotNull here - that's checked manually
    // in StudentServiceImpl.createStudent() instead.
    private Long userId;

    @NotBlank
    private String rollNumber;

    @NotBlank
    private String name;

    private String phone;
    private String department;
    private Integer year;
    private String guardianName;
    private String guardianPhone;
    private String address;
    private LocalDate admissionDate;
}
