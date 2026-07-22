package com.hostel.management.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "visitors")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Visitor extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Column(nullable = false)
    private String visitorName;

    private String relation;

    private String phone;

    private String purpose;

    private LocalDateTime checkInTime;

    private LocalDateTime checkOutTime;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private VisitorStatus status = VisitorStatus.CHECKED_IN;

    public enum VisitorStatus {
        CHECKED_IN, CHECKED_OUT
    }
}
