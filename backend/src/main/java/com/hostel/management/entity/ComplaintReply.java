package com.hostel.management.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "complaint_replies")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ComplaintReply extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "complaint_id", nullable = false)
    private Complaint complaint;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "replied_by", nullable = false)
    private User repliedBy;

    @Column(length = 2000, nullable = false)
    private String message;
}
