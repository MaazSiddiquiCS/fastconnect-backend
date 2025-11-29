package com.fastconnect.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(
        name = "reel_comments",
        indexes = {
                @Index(name = "idx_reel_comment_reel", columnList = "reel_id"),
                @Index(name = "idx_reel_comment_user", columnList = "user_id")
        }
)
public class ReelComment {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "reel_comment_seq")
    @SequenceGenerator(
            name = "reel_comment_seq",
            sequenceName = "reel_comment_sequence",
            allocationSize = 50
    )
    @Column(name = "reel_comment_id")
    private Long reelCommentId;

    @NotBlank(message = "Comment cannot be empty")
    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reel_id", nullable = false)
    private Reel reel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}