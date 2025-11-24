package com.fastconnect.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Check;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(
        name = "connections",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {
                        "user1_id","user2_id"
                })
        },
        indexes = {
                @Index(name = "idx_connection_users", columnList = "user1_id, user2_id")
        }
)
@Check(name = "chk_connection_order",constraints = "user1_id<user2_id")
public class Connection {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "connection_seq")
    @SequenceGenerator(
            name = "connection_seq",
            sequenceName = "connection_sequence",
            allocationSize = 50
    )
    private Long connection_id;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime connected_at;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user1_id", nullable = false)
    private User user1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user2_id",nullable = false)
    private User user2;

    @PrePersist
    public void prePersist() {
        if (this.connected_at == null) {
            this.connected_at = LocalDateTime.now();
        }
    }
}
