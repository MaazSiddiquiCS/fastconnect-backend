package com.fastconnect.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "reels")
public class Reel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reel_id;

    @NotBlank(message = "Video URL is required")
    @URL
    @Column(name = "video_url", nullable = false)
    private String videoUrl;

    @Column(columnDefinition = "TEXT")
    private String caption;

    // Duration in seconds (e.g., 60.50)
    @Column(nullable = false)
    private Double duration;

    @Column(nullable = false, columnDefinition = "INT DEFAULT 0")
    private Integer views = 0;

    @Column(name = "likes_count", nullable = false, columnDefinition = "INT DEFAULT 0")
    private Integer likesCount = 0;

    @Column(name = "is_public", nullable = false, columnDefinition = "BOOLEAN DEFAULT true")
    private boolean isPublic = true;

    @Column(nullable = false, updatable = false)
    private LocalDateTime created_at;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "reel", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ReelComment> comments = new HashSet<>();

    @OneToMany(mappedBy = "reel", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ReelReaction> reactions = new HashSet<>();

    @PrePersist
    protected void onCreate() {
        created_at = LocalDateTime.now();
        if (views == null) views = 0;
        if (likesCount == null) likesCount = 0;
    }
}