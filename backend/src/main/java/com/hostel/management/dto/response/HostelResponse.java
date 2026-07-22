package com.hostel.management.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HostelResponse {
    private Long id;
    private String name;
    private String address;
    private String type;
    private Integer totalCapacity;
    private Long wardenId;
    private String wardenName;
    private int blockCount;
}
