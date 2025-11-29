package com.fastconnect.entity;

import com.fastconnect.enums.Departments;
import com.fastconnect.enums.FacultyDesignation;
import com.fastconnect.validation.DepartmentRequired;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;

@Getter
@Setter
@Entity
@Table(name = "faculty_page")
@DepartmentRequired
public class FacultyPage {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "faculty_page_seq")
    @SequenceGenerator(
            name = "faculty_page_seq",
            sequenceName = "faculty_page_sequence",
            allocationSize = 50
    )
    @Column(name = "faculty_id") 
    private Long facultyId;

    @NotNull(message = "Name is requires")
    @Column(name = "full_name", length = 100, nullable = false) 
    private String fullName;

    @URL
    @Column(name = "faculty_profile_pic", columnDefinition = "TEXT") 
    private String facultyProfilePic;

    @URL
    @Column(name = "faculty_cover_pic", columnDefinition = "TEXT") 
    private String facultyCoverPic;

    @Size(max = 500)
    @Column(name = "faculty_bio", columnDefinition = "TEXT", length = 500) 
    private String facultyBio;


    @NotNull(message = "Designation is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "faculty_designation", nullable = false) 
    private FacultyDesignation facultyDesignation;

    @Enumerated(EnumType.STRING)
    @Column
    private Departments department;

    @Column(name = "is_verified", nullable = false) 
    private Boolean isVerified;

    @OneToOne
    @JoinColumn(name="user_id", nullable = false,unique = true)
    private User user;

    @PrePersist
    public void prePersist() {
        if(this.isVerified == null) {
            this.isVerified = false;
        }
    }
}