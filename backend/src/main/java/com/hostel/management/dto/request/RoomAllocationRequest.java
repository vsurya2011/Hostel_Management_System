package com.hostel.management.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoomAllocationRequest {
    @NotNull
    private Long studentId;

    @NotNull
    private Long roomId;
}
