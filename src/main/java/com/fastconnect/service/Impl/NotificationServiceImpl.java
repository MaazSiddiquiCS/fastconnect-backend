package com.fastconnect.service.Impl;

import com.fastconnect.dto.NotificationResponse;
import com.fastconnect.entity.Notification;
import com.fastconnect.entity.User;
import com.fastconnect.enums.EntityType;
import com.fastconnect.enums.NotificationType;
import com.fastconnect.exception.UserNotFoundException;
import com.fastconnect.mapper.NotificationMapper;
import com.fastconnect.repository.NotificationRepository;
import com.fastconnect.repository.UserRepository;
import com.fastconnect.service.NotificationService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final NotificationMapper notificationMapper;

    @Override
    public void createNotification(Long recipientUserId, String message, NotificationType type, EntityType entityType, Long referenceId) {
        User recipient = userRepository.findById(recipientUserId)
                .orElseThrow(() -> new UserNotFoundException(recipientUserId));

        Notification notification = new Notification();
        notification.setUser(recipient);
        notification.setMessage(message);
        notification.setType(type);
        notification.setNotificationType(entityType); // POST or PROFILE
        notification.setReferenceId(referenceId);     // Post ID
        notification.setRead(false);

        notificationRepository.save(notification);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NotificationResponse> getUserNotifications(Long userId, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        return notificationRepository.findByUserOrderByCreatedAtDesc(user, pageable)
                .map(notificationMapper::toDTO);
    }

    @Override
    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new com.fastconnect.exception.NotificationNotFoundException(notificationId));

        notification.setRead(true);
        notificationRepository.save(notification);
    }

    @Override
    public void markAllAsRead(Long userId) {
        int updatedCount = notificationRepository.markAllAsReadByUserId(userId);

        // Optional: Log the count of records updated
        System.out.println("Marked " + updatedCount + " notifications as read for user " + userId);
    }

}
