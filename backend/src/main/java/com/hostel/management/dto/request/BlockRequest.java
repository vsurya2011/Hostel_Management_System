package com.hostel.management.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BlockRequest {
    @NotBlank
    private String name;

    @NotNull
    private Long hostelId;
}
