package com.hostel.management.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BlockResponse {
    private Long id;
    private String name;
    private Long hostelId;
    private String hostelName;
    private int floorCount;
}
