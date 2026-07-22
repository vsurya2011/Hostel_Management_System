package com.hostel.management.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ComplaintReplyResponse {
    private Long id;
    private String repliedByName;
    private String message;
    private LocalDateTime createdAt;
}
