package com.hostel.management.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class ComplaintResponse {
    private Long id;
    private String studentName;
    private String title;
    private String description;
    private String category;
    private String priority;
    private String status;
    private LocalDateTime createdAt;
    private List<ComplaintReplyResponse> replies;
}
