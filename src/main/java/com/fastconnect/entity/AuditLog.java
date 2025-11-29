package com.fastconnect.entity;

import com.fastconnect.enums.ActionType;
import com.fastconnect.enums.EntityType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(
        name = "audit_logs",
        indexes = {
                @Index(name = "idx_audit_user", columnList = "action_user_id"),
                @Index(name = "idx_audit_target", columnList = "entity_type, entity_id")
        }
)
public class AuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "logs_seq")
    @SequenceGenerator(
            name = "logs_seq",
            sequenceName = "logs_sequence",
            allocationSize = 50
    )
    @Column(name = "log_id")
    private Long logId;

    @NotNull(message = "Required action type")
    @Enumerated(EnumType.STRING)
    @Column(name = "action_type", nullable = false,length = 50)
    private ActionType actionType;

    @NotNull(message = "Required entity type")
    @Enumerated(EnumType.STRING)
    @Column(name = "entity_type", nullable = false,length = 50)
    private EntityType entityType;

    @NotNull(message = "Required The ID of the specific entity affected.")
    @Column(name = "entity_id", nullable = false)
    private Long entityId;

    @NotNull
    @Column(name = "action_performed_at", nullable = false,updatable = false)
    private LocalDateTime actionPerformedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "action_user_id")
    private User actionUser;

    @PrePersist
    public void prePersist() {
        if (actionPerformedAt == null) {
            actionPerformedAt = LocalDateTime.now();
        }
    }
}
