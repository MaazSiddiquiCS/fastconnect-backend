package com.fastconnect.dto;

import com.fastconnect.enums.Departments;
import com.fastconnect.enums.FacultyDesignation;
import lombok.Data;

@Data
public class FacultyPageResponse {
    private Long facultyId;
    private String fullName;
    private String facultyProfilePic;
    private String facultyCoverPic;
    private String facultyBio;
    private FacultyDesignation facultyDesignation;
    private Departments department;
    private Boolean isVerified;
    private Long userId;
}