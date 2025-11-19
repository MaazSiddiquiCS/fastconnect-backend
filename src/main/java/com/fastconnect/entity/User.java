package com.fastconnect.entity;

import com.fastconnect.enums.AccountStatus;
import com.fastconnect.enums.RoleType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;

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
    @NotBlank(message = "Email is required")
    @Column(unique = true, nullable = false)
    private String email;

    @NotBlank(message = "Password is required")
    @Column(nullable = false)
    private String password;

    @NotBlank(message = "Roll number is required")
    @Column(unique = true, nullable = false)
    private String roll_number;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "account_status", nullable = false)
    private AccountStatus account_status=AccountStatus.ACTIVE;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "role_type", nullable = false)
    private RoleType role_type=RoleType.STUDENT;

    @OneToOne(mappedBy = "user",cascade = CascadeType.ALL)
    private Profile profile;

    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL)
    private List<SocietyMembership> societyMemberships;
}
