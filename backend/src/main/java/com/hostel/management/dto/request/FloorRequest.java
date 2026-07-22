package com.hostel.management.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FloorRequest {
    @NotNull
    private Integer floorNumber;

    @NotNull
    private Long blockId;
}
