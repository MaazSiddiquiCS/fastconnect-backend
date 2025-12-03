package com.fastconnect.dto;

import com.fastconnect.enums.AccountStatus;
import com.fastconnect.enums.RoleType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long userId;
    private String email;
    private RoleType roleType;
    private AccountStatus accountStatus;
    private ProfileResponse profile;
}
