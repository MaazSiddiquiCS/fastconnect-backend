package com.fastconnect.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Table(
        name = "faculty_followers",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {
                        "user_id",
                        "faculty_id"
                })
        }
)
public class FacultyFollowers {
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "faculty_followers_seq"
    )
    @SequenceGenerator(
            name = "faculty_followers_seq",
            sequenceName = "faculty_followers_sequence",
            allocationSize = 50
    )
    @Column(name = "faculty_follower_id") 
    private Long facultyFollowerId;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime followedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "faculty_id")
    private FacultyPage facultyPage;

    @PrePersist
    public void prePersist() {
        if (this.followedAt == null) {
            this.followedAt = LocalDateTime.now();
        }
    }
}