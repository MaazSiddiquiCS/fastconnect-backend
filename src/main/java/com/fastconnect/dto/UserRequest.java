package com.fastconnect.dto;

import com.fastconnect.enums.AccountStatus;
import com.fastconnect.enums.RoleType;
// Removed unused JPA imports
import com.fastconnect.validation.ValidNUEmail;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRequest {

    @Email(message = "Must be a valid email format")
    @ValidNUEmail
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;}