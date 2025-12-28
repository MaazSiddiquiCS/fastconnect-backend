package com.fastconnect.dto;

import com.fastconnect.enums.Departments;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfileResponse {
    private Long profileId;
    private String fullName;
    private String rollNumber;
    private Departments department;
    private Integer batch;
    private String bio;
    private String profilePic;
    private String coverPic;
    private Long userId;
}