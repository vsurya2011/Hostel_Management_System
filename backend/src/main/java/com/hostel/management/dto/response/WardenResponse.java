package com.hostel.management.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WardenResponse {
    private Long staffId;
    private Long userId;
    private String name;
    private String username;
    private String email;
    private String phone;
    private boolean enabled;
    private Long assignedHostelId;
    private String assignedHostelName;
}
