package com.hostel.management.controller;

import com.hostel.management.dto.response.NotificationResponse;
import com.hostel.management.service.NotificationService;
import com.hostel.management.util.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> byUser(@PathVariable Long userId) {
        return ResponseEntity.ok(ApiResponse.success(notificationService.getNotificationsByUser(userId)));
    }

    @GetMapping("/user/{userId}/unread")
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> unread(@PathVariable Long userId) {
        return ResponseEntity.ok(ApiResponse.success(notificationService.getUnreadNotifications(userId)));
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<ApiResponse<Void>> markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok(ApiResponse.success("Notification marked as read", null));
    }
}
