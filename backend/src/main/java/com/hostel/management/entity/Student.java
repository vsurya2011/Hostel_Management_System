package com.hostel.management.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "students")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Student extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false, unique = true)
    private String rollNumber;

    @Column(nullable = false)
    private String name;

    private String phone;

    private String department;

    private Integer year;

    private String guardianName;

    private String guardianPhone;

    private String address;

    private LocalDate admissionDate;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private StudentStatus status = StudentStatus.ACTIVE;

    public enum StudentStatus {
        ACTIVE, INACTIVE, GRADUATED, SUSPENDED
    }
}
