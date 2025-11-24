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
    private Long society_follower_id;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime followed_at;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "society_id", nullable = false)
    private Society society;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @PrePersist
    public void prePersist() {
        if (this.followed_at == null) {
            this.followed_at = LocalDateTime.now();
        }
    }
}
