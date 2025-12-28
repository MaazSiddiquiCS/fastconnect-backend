package com.fastconnect.dto;

import com.fastconnect.enums.EntityType;
import com.fastconnect.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {
    private Long notificationId;
    private EntityType notificationType;
    private Long referenceId;
    private NotificationType type;
    private String message;
    private boolean isRead;
    private LocalDateTime createdAt;
}