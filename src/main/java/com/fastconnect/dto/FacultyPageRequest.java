package com.fastconnect.dto;

import com.fastconnect.enums.Departments;
import com.fastconnect.enums.FacultyDesignation;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class FacultyPageRequest {
    @NotBlank(message = "Full Name is required")
    private String fullName;

    private String facultyProfilePic;
    private String facultyCoverPic;
    private String facultyBio;

    @NotNull(message = "Designation is required")
    private FacultyDesignation facultyDesignation;

    private Departments department;
}