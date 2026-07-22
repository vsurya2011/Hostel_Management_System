package com.hostel.management.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class LeaveRequestDto {
    @NotNull
    private LocalDate fromDate;

    @NotNull
    private LocalDate toDate;

    private String reason;
}
