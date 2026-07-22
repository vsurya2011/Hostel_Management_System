package com.hostel.management.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class AnnouncementRequest {
    @NotBlank
    private String title;

    private String content;
    private String targetAudience;
    private LocalDate expiryDate;
}
