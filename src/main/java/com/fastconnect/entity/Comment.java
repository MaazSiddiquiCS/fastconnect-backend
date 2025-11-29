package com.fastconnect.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

// 👇 Modern Date Import
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(
        name = "comments",
        indexes = {
                @Index(name = "idx_comment_post", columnList = "post_id"),
                @Index(name = "idx_comment_user", columnList = "user_id")
        }
)
public class Comment {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "comments_seq"
    )
    @SequenceGenerator(
            name = "comments_seq",
            sequenceName = "comment_sequence",
            allocationSize = 50
    )
    @Column(name = "comment_id") 
    private Long commentId;

    @NotBlank(message = "Comment cannot be blank")
    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;


    @Column(name = "created_at", nullable = false, updatable = false) 
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false) 
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false) 
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false) 
    private User user;

    @PrePersist
    protected void onCreate() {
        
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        
        updatedAt = LocalDateTime.now();
    }
}