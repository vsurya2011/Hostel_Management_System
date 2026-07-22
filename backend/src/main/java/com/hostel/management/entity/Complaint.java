package com.hostel.management.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "complaints")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Complaint extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Column(nullable = false)
    private String title;

    @Column(length = 2000)
    private String description;

    @Enumerated(EnumType.STRING)
    private ComplaintCategory category;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ComplaintPriority priority = ComplaintPriority.MEDIUM;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ComplaintStatus status = ComplaintStatus.OPEN;

    @OneToMany(mappedBy = "complaint", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ComplaintReply> replies = new ArrayList<>();

    public enum ComplaintCategory {
        ELECTRICAL, PLUMBING, FURNITURE, CLEANLINESS, FOOD, INTERNET, SECURITY, OTHER
    }

    public enum ComplaintPriority {
        LOW, MEDIUM, HIGH, URGENT
    }

    public enum ComplaintStatus {
        OPEN, IN_PROGRESS, RESOLVED, CLOSED, REOPENED
    }
}
