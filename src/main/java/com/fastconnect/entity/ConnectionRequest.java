package com.fastconnect.entity;

import com.fastconnect.enums.ConnectionRequestStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(
        name = "connection_requests",
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
            allocationSize = 50
    )
    @Column(name = "connection_request_id") 
    private Long connectionRequestId;

    @NotNull(message = "Status is required")
    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private ConnectionRequestStatus status;

    @NotNull
    @Column(name = "created_at", nullable = false) 
    private LocalDateTime createdAt;

    @Column(name = "responded_at") 
    private LocalDateTime respondedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver;

    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }
}