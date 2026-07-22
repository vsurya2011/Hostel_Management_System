package com.hostel.management.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class UserResponse {
    private Long id;
    private String username;
    private String email;
    private boolean enabled;
    private Set<String> roles;
    private boolean hasStudentProfile;
}
