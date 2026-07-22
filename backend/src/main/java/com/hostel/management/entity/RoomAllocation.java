package com.hostel.management.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "room_allocations")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RoomAllocation extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    private LocalDate allocatedDate;

    private LocalDate vacatedDate;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private AllocationStatus status = AllocationStatus.ACTIVE;

    public enum AllocationStatus {
        ACTIVE, VACATED, TRANSFERRED
    }
}
