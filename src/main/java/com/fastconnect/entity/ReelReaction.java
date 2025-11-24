package com.fastconnect.entity;

import com.fastconnect.enums.ReactionType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(
        name = "reel_reactions",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"reel_id", "user_id"})
        }
)
public class ReelReaction {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "reel_reaction_seq")
    @SequenceGenerator(
            name = "reel_reaction_seq",
            sequenceName = "reel_reaction_sequence",
            allocationSize = 50
    )
    private Long reel_reaction_id;

    @Enumerated(EnumType.STRING)
    @Column(name = "reaction_type", nullable = false)
    private ReactionType reactionType;

    @Column(nullable = false, updatable = false)
    private LocalDateTime created_at;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reel_id", nullable = false)
    private Reel reel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @PrePersist
    protected void onCreate() {
        created_at = LocalDateTime.now();
    }
}