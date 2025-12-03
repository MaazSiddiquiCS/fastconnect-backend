package com.fastconnect.dto;

import com.fastconnect.enums.RoleType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {
    private String accessToken;   // JWT
    private String refreshToken;  // stored in AuthToken table
    private Long userId;
    private String email;
    private RoleType role;        // optional: user role info
}

