package com.fastconnect.entity;

import com.fastconnect.enums.AccountStatus;
import com.fastconnect.enums.RoleType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(
        name = "users"
//        ,indexes = {
//        @Index(name="idx_accountStatus",columnList = "account_status"),
//    @Index(name = "idx_roleType",columnList = "role_type")}
)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long user_id;

    @Email
    @NotBlank
    @Column(unique = true, nullable = false)
    private String email;

    @NotBlank
    @Column(nullable = false)
    private String password;

    @NotBlank
    @Column(unique = true, nullable = false)
    private String roll_number;

    @Enumerated(EnumType.STRING)
    @Column(name = "account_status", nullable = false)
    private AccountStatus account_status=AccountStatus.ACTIVE;

    @Enumerated(EnumType.STRING)
    @Column(name = "role_type", nullable = false)
    private RoleType role_type=RoleType.STUDENT;

    @OneToOne(mappedBy = "user",cascade = CascadeType.ALL)
    private Profile profile;
}
