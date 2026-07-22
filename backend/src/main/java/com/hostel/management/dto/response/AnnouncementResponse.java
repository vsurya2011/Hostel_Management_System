package com.hostel.management.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class AnnouncementResponse {
    private Long id;
    private String title;
    private String content;
    private String postedByName;
    private String targetAudience;
    private LocalDateTime createdAt;
    private LocalDate expiryDate;
}
