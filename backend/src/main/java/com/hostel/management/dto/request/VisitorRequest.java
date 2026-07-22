package com.hostel.management.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VisitorRequest {
    @NotNull
    private Long studentId;

    @NotBlank
    private String visitorName;

    private String relation;
    private String phone;
    private String purpose;
}
