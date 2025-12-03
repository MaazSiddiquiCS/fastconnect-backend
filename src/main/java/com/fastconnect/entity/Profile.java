package com.fastconnect.entity;

import com.fastconnect.enums.Departments;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;


@Getter
@Setter
@NoArgsConstructor
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
    @Column(name = "profile_id") 
    private Long profileId;

    @NotBlank(message = "Name is required")
    @Column(name = "full_name", length = 50, nullable = false) 
    private String fullName;

    @Size(min=8,max=8)
    @NotBlank(message = "Roll number is required")
    @Column(name = "roll_number", unique = true, nullable = false,length = 8) 
    private String rollNumber;

    @NotNull(message = "Required Department")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Departments department;

    @Min(2018)
    @Max(2026)
    @NotNull(message = "Required batch")
    @Column(nullable = false)
    private Integer batch;

    @Column(length = 200)
    private String bio;

    @URL
    @Column(name = "profile_pic", columnDefinition = "TEXT",nullable = true) 
    private String profilePic;

    @URL
    @Column(name = "cover_pic", columnDefinition = "TEXT") 
    private String coverPic;

    @OneToOne
    @JoinColumn(name = "user_id",unique = true,nullable = false)
    private User user;
}