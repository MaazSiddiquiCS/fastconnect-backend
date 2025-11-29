package com.fastconnect.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Check;
import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(
        name = "auth_tokens",
        indexes = {
                @Index(name = "idx_token_user", columnList = "user_id")
        }
)
@Check(name = "chk_expiry", constraints = "expires_at>issued_at")
public class AuthToken {
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "auth_token_seq"
    )
    @SequenceGenerator(
            name = "auth_token_seq",
            sequenceName = "auth_token_sequence",
            allocationSize = 50
    )
    @Column(name = "token_id") 
    private Long tokenId;

    @NotNull
    @Size(min = 20)
    @Column(name = "refresh_token", unique = true, nullable = false) 
    private String refreshToken;

    @NotNull
    @Column(name = "issued_at", nullable = false) 
    private LocalDateTime issuedAt;

    @NotNull
    @Column(name = "expires_at", nullable = false) 
    private LocalDateTime expiresAt;

    @NotNull
    @Column(name = "revoked", nullable = false) 
    private Boolean revoked;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id", nullable = false)
    private User user;

    @PrePersist
    public void prePersist() {
        if(this.issuedAt == null) {
            this.issuedAt =  LocalDateTime.now();
        }
        if(this.revoked == null) {
            this.revoked = false;
        }
    }
}