package com.hostel.management.service;

import com.hostel.management.dto.response.NotificationResponse;

import java.util.List;

public interface NotificationService {
    NotificationResponse createNotification(Long userId, String title, String message, String type);
    List<NotificationResponse> getNotificationsByUser(Long userId);
    List<NotificationResponse> getUnreadNotifications(Long userId);
    void markAsRead(Long notificationId);
}
