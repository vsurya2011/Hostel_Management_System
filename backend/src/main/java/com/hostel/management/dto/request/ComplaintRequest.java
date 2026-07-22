package com.hostel.management.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ComplaintRequest {
    @NotBlank
    private String title;

    private String description;
    private String category;
    private String priority;
}
