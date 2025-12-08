package com.fastconnect.service;

import com.fastconnect.dto.NotificationResponse;
import com.fastconnect.enums.EntityType;
import com.fastconnect.enums.NotificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NotificationService {

    void createNotification(Long recipientUserId, String message, NotificationType type, EntityType entityType, Long referenceId);

    // Fetch
    Page<NotificationResponse> getUserNotifications(Long userId, Pageable pageable);
    void markAsRead(Long notificationId);
    void markAllAsRead(Long userId);
}