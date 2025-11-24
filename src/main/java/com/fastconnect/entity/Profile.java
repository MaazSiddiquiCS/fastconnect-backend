package com.fastconnect.entity;

import com.fastconnect.enums.Departments;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;


@Getter
@Setter
@Entity
@Table(name = "profile")
public class Profile {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "profile_seq")
    @SequenceGenerator(
            name = "profile_seq",
            sequenceName = "profile_sequence",
            allocationSize = 50
    )
    private Long profile_id;

    @NotBlank(message = "Name is required")
    @Column(length = 50, nullable = false)
    private String full_name;

    @Size(min=8,max=8)
    @NotBlank(message = "Roll number is required")
    @Column(unique = true, nullable = false,length = 8)
    private String roll_number;

    @NotNull(message = "Required Department")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Departments department=Departments.CS;

    @Min(2018)
    @Max(2026)
    @NotNull(message = "Required batch")
    @Column(nullable = false)
    private Integer batch;

    @Column(length = 200)
    private String bio;

    @URL
    @Column(columnDefinition = "TEXT",nullable = true)
    private String profile_pic;

    @URL
    @Column(columnDefinition = "TEXT")
    private String cover_pic;

    @OneToOne
    @JoinColumn(name = "user_id",unique = true,nullable = false)
    private User user;
}
