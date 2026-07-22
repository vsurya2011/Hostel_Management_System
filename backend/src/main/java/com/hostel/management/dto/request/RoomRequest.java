package com.hostel.management.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class RoomRequest {
    @NotBlank
    private String roomNumber;

    @NotNull
    private Long floorId;

    private Integer capacity;
    private String roomType;
    private BigDecimal rentAmount;
}
