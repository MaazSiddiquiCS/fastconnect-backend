package com.fastconnect.service;

import com.fastconnect.dto.UserResponse;
import com.fastconnect.enums.RoleType;

import java.util.List;

public interface AdminService {
    void deleteUserById(Long id);
    List<UserResponse> getUsersByRoleType(RoleType roleType);
    UserResponse changeUserRole(String email, RoleType newRole);
}
