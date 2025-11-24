package com.fastconnect.entity;

import com.fastconnect.enums.EntityType;
import com.fastconnect.enums.ModerationAction;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "reports")
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "report_seq")
    @SequenceGenerator(
            name = "report_seq",
            sequenceName = "report_sequence",
            allocationSize = 50
    )
    private Long report_id;

    @NotNull(message = "Required content type")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false,length = 50)
    private EntityType reported_item_type;

    @NotNull(message = "Required The ID of the specific entity reported")
    @Column(nullable = false)
    private Long reported_item_id;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private ModerationAction moderation_action;

    @Size(min = 20, max = 500)
    @Column(columnDefinition = "TEXT",length = 500)
    private String action_details;

    @NotNull
    @Column(nullable = false,updatable = false)
    private LocalDateTime moderation_time;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "moderator_id", nullable = false)
    private User moderator;

    @OneToMany(mappedBy = "report", fetch = FetchType.LAZY,
            cascade = CascadeType.REMOVE,orphanRemoval = true
    )
    private Set<FlagReportLink> flag_report_links;

    @PrePersist
    public void prePersist() {
        if (this.moderation_time == null) {
            this.moderation_time = LocalDateTime.now();
        }
    }
}
