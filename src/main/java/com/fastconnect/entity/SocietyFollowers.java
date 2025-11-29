package com.fastconnect.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(
        name = "society_followers",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"society_id","user_id"})
        }
)
public class SocietyFollowers {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "society_followers_seq")
    @SequenceGenerator(
            name = "society_followers_seq",
            sequenceName = "society_followers_sequence",
            allocationSize = 50
    )
    @Column(name = "society_follower_id") 
    private Long societyFollowerId;

    @NotNull
    @Column(name = "followed_at", nullable = false) 
    private LocalDateTime followedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "society_id", nullable = false)
    private Society society;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @PrePersist
    public void prePersist() {
        if (this.followedAt == null) {
            this.followedAt = LocalDateTime.now();
        }
    }
}