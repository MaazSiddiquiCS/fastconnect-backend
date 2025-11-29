package com.fastconnect.entity;

import com.fastconnect.enums.EventRegistrationStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "event_registrations",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"event_id", "user_id"})
        },
        indexes = {
                @Index(name = "idx_reg_event_user", columnList = "event_id, user_id")
        }
)
public class EventRegistration {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "event_reg_seq")
    @SequenceGenerator(
            name = "event_reg_seq",
            sequenceName = "event_reg_sequence",
            allocationSize = 50
    )
    @Column(name = "registration_id") 
    private Long registrationId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventRegistrationStatus status;

    @Column(name = "registered_at", nullable = false, updatable = false) 
    private LocalDateTime registeredAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @PrePersist
    protected void onCreate() {
        // Updated field name
        registeredAt = LocalDateTime.now();
    }
}