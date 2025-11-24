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
    private Long faculty_id;

    @NotNull(message = "Name is requires")
    @Column(length = 100, nullable = false)
    private String full_name;

    @URL
    @Column(columnDefinition = "TEXT")
    private String faculty_profile_pic;

    @URL
    @Column(columnDefinition = "TEXT")
    private String faculty_cover_pic;

    @Size(max = 500)
    @Column(columnDefinition = "TEXT", length = 500)
    private String faculty_bio;


    @NotNull(message = "Designation is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FacultyDesignation faculty_designation;

    @Enumerated(EnumType.STRING)
    @Column
    private Departments department;

    @Column(nullable = false)
    private Boolean is_verified;

    @OneToOne
    @JoinColumn(name="user_id", nullable = false,unique = true)
    private User user;

    @PrePersist
    public void prePersist() {
        if(this.is_verified == null) {
            this.is_verified = false;
        }
    }
}
