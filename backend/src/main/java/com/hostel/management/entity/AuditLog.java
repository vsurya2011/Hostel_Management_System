package com.hostel.management.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "audit_logs")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AuditLog extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private String action;

    private String entityName;

    private Long entityId;

    @Column(length = 2000)
    private String details;

    private String ipAddress;
}
