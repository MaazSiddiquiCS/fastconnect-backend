package com.fastconnect.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Check;

import java.sql.Timestamp;
import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "auth_tokens")
@Check(name = "chk_expiry", constraints = "expires_in>issued_at")
public class AuthToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long token_id;

    @NotNull
    @Size(min = 20)
    @Column(unique = true, nullable = false)
    private String refresh_token;

    @NotNull
    @Column(nullable = false)
    private Timestamp issued_at;

    @NotNull
    @Column(nullable = false)
    private Timestamp expires_in;

    @NotNull
    @Column(nullable = false)
    private Boolean revoked;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id", nullable = false)
    private User user;

    @PrePersist
    public void prePersist() {
        if(this.issued_at == null) {
            this.issued_at = Timestamp.from(Instant.now());
        }
        if(this.revoked == null) {
            this.revoked = false;
        }
    }
}
