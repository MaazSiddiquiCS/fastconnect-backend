package com.fastconnect.entity;

import com.fastconnect.enums.ConnectionRequestStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(
        name = "connection_request",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {
                        "sender_id","receiver_id"
                })
        }

)
public class ConnectionRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "connection_req_seq")
    @SequenceGenerator(
            name = "connection_req_seq",
            sequenceName = "connection_req_sequence",
            allocationSize = 20
    )
    private Long connection_request_id;

    @NotNull(message = "Status is required")
    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private ConnectionRequestStatus status;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime created_at;

    @Column
    private LocalDateTime responded_at;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver;

    @PrePersist
    public void prePersist() {
        if (this.created_at == null) {
            this.created_at = LocalDateTime.now();
        }
    }
}
