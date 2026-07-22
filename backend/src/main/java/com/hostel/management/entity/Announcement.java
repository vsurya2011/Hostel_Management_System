package com.hostel.management.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "announcements")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Announcement extends BaseEntity {

    @Column(nullable = false)
    private String title;

    @Column(length = 3000)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "posted_by", nullable = false)
    private User postedBy;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private TargetAudience targetAudience = TargetAudience.ALL;

    private LocalDate expiryDate;

    public enum TargetAudience {
        ALL, STUDENTS, STAFF, WARDENS
    }
}
