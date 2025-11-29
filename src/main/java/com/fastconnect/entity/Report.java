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
    @Column(name = "report_id") 
    private Long reportId;

    @NotNull(message = "Required content type")
    @Enumerated(EnumType.STRING)
    @Column(name = "reported_item_type", nullable = false,length = 50) 
    private EntityType reportedItemType;

    @NotNull(message = "Required The ID of the specific entity reported")
    @Column(name = "reported_item_id", nullable = false) 
    private Long reportedItemId;

    @Enumerated(EnumType.STRING)
    @Column(name = "moderation_action", length = 50) 
    private ModerationAction moderationAction;

    @Size(min = 20, max = 500)
    @Column(name = "action_details", columnDefinition = "TEXT",length = 500) 
    private String actionDetails;

    @NotNull
    @Column(name = "moderation_time", nullable = false,updatable = false) 
    private LocalDateTime moderationTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "moderator_id", nullable = false) 
    private User moderator;

    @OneToMany(mappedBy = "report", fetch = FetchType.LAZY,
            cascade = CascadeType.REMOVE,orphanRemoval = true
    )
    private Set<FlagReportLink> flagReportLinks;

    @PrePersist
    public void prePersist() {
        if (this.moderationTime == null) {
            this.moderationTime = LocalDateTime.now();
        }
    }
}