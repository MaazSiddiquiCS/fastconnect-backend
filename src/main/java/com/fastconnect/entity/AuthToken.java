package com.fastconnect.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Check;
import java.time.LocalDateTime;

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
    private Long token_id;

    @NotNull
    @Size(min = 20)
    @Column(unique = true, nullable = false)
    private String refresh_token;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime issued_at;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime expires_at;

    @NotNull
    @Column(nullable = false)
    private Boolean revoked;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id", nullable = false)
    private User user;

    @PrePersist
    public void prePersist() {
        if(this.issued_at == null) {
            this.issued_at =  LocalDateTime.now();
        }
        if(this.revoked == null) {
            this.revoked = false;
        }
    }
}
