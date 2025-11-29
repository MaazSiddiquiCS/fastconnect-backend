package com.fastconnect.entity;

import com.fastconnect.enums.EntityType;
import com.fastconnect.enums.FlagReason;
import com.fastconnect.enums.FlagStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(
        name = "flags",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {
                                "flagged_by",
                                "target_type",
                                "target_id"
                        }
                )
        },
        indexes = {
                @Index(name = "idx_flag_target", columnList = "target_type, target_id"),
                @Index(name = "idx_flag_flaggedby", columnList = "flagged_by")
        }
)
public class Flag {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "flag_seq")
    @SequenceGenerator(
            name = "flag_seq",
            sequenceName = "flag_sequence",
            allocationSize = 50
    )
    @Column(name = "flag_id") 
    private Long flagId;

    @NotNull(message = "Required target type")
    @Enumerated(EnumType.STRING)
    @Column(name = "target_type", nullable = false,length = 50) 
    private EntityType targetType;

    @NotNull(message = "Required The ID of the specific entity flagged.")
    @Column(name = "target_id", nullable = false) 
    private Long targetId;

    @NotNull(message = "Required reason for flag")
    @Enumerated(EnumType.STRING)
    @Column(name = "flag_reason", nullable = false,length = 50) 
    private FlagReason flagReason;

    @NotNull(message = "Required status")
    @Enumerated(EnumType.STRING)
    @Column(name = "flag_status", nullable = false,length = 50) 
    private FlagStatus flagStatus;

    @NotNull
    @Column(name = "created_at", nullable = false,updatable = false) 
    private LocalDateTime createdAt;

    @Column(name = "resolved_at") 
    private LocalDateTime resolvedAt;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flagged_by")
    private User flaggedBy;

    @OneToMany(mappedBy = "flag", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE,orphanRemoval = true)
    private Set<FlagReportLink> flagReportLinks;

    @PrePersist
    public void prePersist() {
        if(this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }
}