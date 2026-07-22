package com.hostel.management.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "hostels")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Hostel extends BaseEntity {

    @Column(nullable = false)
    private String name;

    private String address;

    @Enumerated(EnumType.STRING)
    private HostelType type;

    private Integer totalCapacity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warden_id")
    private Staff warden;

    @OneToMany(mappedBy = "hostel", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Block> blocks = new ArrayList<>();

    public enum HostelType {
        BOYS, GIRLS, CO_ED
    }
}
