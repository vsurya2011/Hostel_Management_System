package com.hostel.management.service.impl;

import com.hostel.management.dto.response.NotificationResponse;
import com.hostel.management.entity.Notification;
import com.hostel.management.entity.User;
import com.hostel.management.exception.ResourceNotFoundException;
import com.hostel.management.mapper.EntityMapper;
import com.hostel.management.repository.NotificationRepository;
import com.hostel.management.repository.UserRepository;
import com.hostel.management.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final EntityMapper entityMapper;

    @Override
    @Transactional
    public NotificationResponse createNotification(Long userId, String title, String message, String type) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        Notification notification = Notification.builder()
                .user(user)
                .title(title)
                .message(message)
                .type(type != null ? Notification.NotificationType.valueOf(type.toUpperCase()) : Notification.NotificationType.INFO)
                .isRead(false)
                .build();

        return entityMapper.toNotificationResponse(notificationRepository.save(notification));
    }

    @Override
    public List<NotificationResponse> getNotificationsByUser(Long userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(entityMapper::toNotificationResponse).collect(Collectors.toList());
    }

    @Override
    public List<NotificationResponse> getUnreadNotifications(Long userId) {
        return notificationRepository.findByUserIdAndIsReadFalse(userId).stream()
                .map(entityMapper::toNotificationResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification", "id", notificationId));
        notification.setRead(true);
        notificationRepository.save(notification);
    }
}
