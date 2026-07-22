package com.hostel.management.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class RoomResponse {
    private Long id;
    private String roomNumber;
    private Integer capacity;
    private Integer occupied;
    private String roomType;
    private String status;
    private BigDecimal rentAmount;
    private String floorInfo;
}
