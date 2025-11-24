package com.fastconnect.entity;

import com.fastconnect.enums.ActionType;
import com.fastconnect.enums.EntityType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
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
    private Long log_id;

    @NotNull(message = "Required action type")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false,length = 50)
    private ActionType action_type;

    @NotNull(message = "Required entity type")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false,length = 50)
    private EntityType entity_type;

    @NotNull(message = "Required The ID of the specific entity affected.")
    @Column(nullable = false)
    private Long entity_id;

    @NotNull
    @Column(nullable = false,updatable = false)
    private LocalDateTime action_performed_at;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "action_user_id")
    private User actionUser;

    @PrePersist
    public void prePersist() {
        if (action_performed_at == null) {
            action_performed_at = LocalDateTime.now();
        }
    }
}
