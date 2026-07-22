package com.hostel.management.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FloorResponse {
    private Long id;
    private Integer floorNumber;
    private Long blockId;
    private String blockName;
    private Long hostelId;
    private String hostelName;
    private int roomCount;
}
