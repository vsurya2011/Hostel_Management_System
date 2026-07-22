package com.hostel.management.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HostelRequest {
    @NotBlank
    private String name;
    private String address;
    private String type;
    private Integer totalCapacity;
    private Long wardenId;
}
