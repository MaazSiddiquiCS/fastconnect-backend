package com.fastconnect.entity;

import com.fastconnect.enums.ReactionType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(
        name = "post_reactions",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_user_post_reaction",
                        columnNames = {"user_id", "post_id"}
                )
        }
)
public class Reaction {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "reaction_seq")
    @SequenceGenerator(
            name = "reaction_seq",
            sequenceName = "reaction_sequence",
            allocationSize = 50
    )
    @Column(name = "reaction_id")
    private Long reactionId;

    @Enumerated(EnumType.STRING)
    @Column(name = "reaction_type", nullable = false)
    private ReactionType reactionType;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}