package com.hostel.management.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "rooms")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Room extends BaseEntity {

    @Column(nullable = false)
    private String roomNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "floor_id", nullable = false)
    private Floor floor;

    private Integer capacity;

    @Builder.Default
    private Integer occupied = 0;

    @Enumerated(EnumType.STRING)
    private RoomType roomType;

    private BigDecimal rentAmount;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private RoomStatus status = RoomStatus.AVAILABLE;

    public enum RoomType {
        SINGLE, DOUBLE, TRIPLE, DORMITORY
    }

    public enum RoomStatus {
        AVAILABLE, FULL, MAINTENANCE
    }
}
