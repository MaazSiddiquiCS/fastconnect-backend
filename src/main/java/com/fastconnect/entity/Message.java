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
        name = "messages",
        indexes = {
                @Index(name = "idx_message_conv", columnList = "conversation_id"),
                @Index(name = "idx_message_sender", columnList = "sender_id")
        }
)
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "message_seq")
    @SequenceGenerator(
            name = "message_seq",
            sequenceName = "message_sequence",
            allocationSize = 50
    )
    private Long message_id;

    @NotBlank(message = "Message content cannot be empty")
    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(nullable = false, updatable = false)
    private LocalDateTime sent_at;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT false")
    private boolean seen = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conversation_id", nullable = false)
    private Conversation conversation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @PrePersist
    protected void onCreate() {
        sent_at = LocalDateTime.now();
    }
}