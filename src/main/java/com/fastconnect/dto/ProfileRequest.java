package com.fastconnect.dto;

import com.fastconnect.enums.Departments;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfileRequest {

    @NotBlank(message = "Full name is required")
    private String fullName;

    @Size(min = 8, max = 8, message = "Roll number must be 8 characters")
    @NotBlank(message = "Roll number is required")
    private String rollNumber;

    @NotNull(message = "Department is required")
    private Departments department;

    @Min(value = 2018, message = "Batch must be >= 2018")
    @Max(value = 2026, message = "Batch must be <= 2026")
    @NotNull(message = "Batch is required")
    private Integer batch;

    @Size(max = 200, message = "Bio must be at most 200 characters")
    private String bio;

    @URL(message = "Profile picture must be a valid URL")
    private String profilePic;

    @URL(message = "Cover picture must be a valid URL")
    private String coverPic;
}
