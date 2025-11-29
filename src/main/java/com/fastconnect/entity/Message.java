package com.fastconnect.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
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
    @Column(name = "message_id") 
    private Long messageId;

    @NotBlank(message = "Message content cannot be empty")
    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(name = "sent_at", nullable = false, updatable = false) 
    private LocalDateTime sentAt;

    @Column(name = "seen", nullable = false, columnDefinition = "BOOLEAN DEFAULT false") 
    private boolean seen = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conversation_id", nullable = false)
    private Conversation conversation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @PrePersist
    protected void onCreate() {

        sentAt = LocalDateTime.now();
    }
}