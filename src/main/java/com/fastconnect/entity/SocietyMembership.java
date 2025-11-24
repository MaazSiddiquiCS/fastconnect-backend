package com.fastconnect.entity;

import com.fastconnect.enums.SocietyRoles;
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
        name = "society_membership",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"society_id","user_id"})
        },
        indexes = {
                @Index(name = "idx_mem_society_user", columnList = "society_id, user_id")
        }
)
public class SocietyMembership {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "society_membership_seq")
    @SequenceGenerator(
            name = "society_membership_seq",
            sequenceName = "society_membership_sequence",
            allocationSize = 50
    )
    private Long membership_id;

    @NotNull(message = "Role is mandatory")
    @Column(length = 30,nullable = false)
    @Enumerated(EnumType.STRING)
    private SocietyRoles  society_role;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime joined_at;

    @NotNull
    @Column(nullable = false)
    private Boolean active;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "society_id")
    private Society society;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @PrePersist
    public void prePersist() {
        if (this.joined_at == null) this.joined_at = LocalDateTime.now();
        if (this.active == null) this.active = true;
    }
}
