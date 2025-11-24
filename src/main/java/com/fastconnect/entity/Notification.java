package com.fastconnect.entity;

import com.fastconnect.enums.EntityType;
import com.fastconnect.enums.NotificationType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "notification_seq")
    @SequenceGenerator(
            name = "notification_seq",
            sequenceName = "notification_sequence",
            allocationSize = 50
    )
    private Long notification_id;

    @NotNull(message = "Required entity type whose notification is created")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false,length = 50)
    private EntityType notification_type;

    @Column(nullable = false)
    private Long reference_id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;

    @Column(nullable = false)
    private String message;

    @Column(name = "is_read", nullable = false, columnDefinition = "BOOLEAN DEFAULT false")
    private boolean isRead;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}