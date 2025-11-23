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
    private Long flag_id;

    @NotNull(message = "Required target type")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false,length = 50)
    private EntityType target_type;

    @NotNull(message = "Required The ID of the specific entity flagged.")
    @Column(nullable = false)
    private Long target_id;

    @NotNull(message = "Required reason for flag")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false,length = 50)
    private FlagReason flag_reason;

    @NotNull(message = "Required status")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false,length = 50)
    private FlagStatus flag_status;

    @NotNull
    @Column(nullable = false,updatable = false)
    private LocalDateTime created_at;

    @Column
    private LocalDateTime resolved_at;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flagged_by")
    private User flaggedBy;

    @OneToMany(mappedBy = "flag", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private Set<FlagReportLink> flag_report_links;

    @PrePersist
    public void prePersist() {
        if(this.created_at==null) {
            this.created_at = LocalDateTime.now();
        }
    }
}
