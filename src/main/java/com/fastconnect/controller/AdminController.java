package com.fastconnect.controller;

import com.fastconnect.dto.UserResponse;
import com.fastconnect.enums.RoleType;
import com.fastconnect.security.CustomUserDetails;
import com.fastconnect.service.AdminService;
import com.fastconnect.service.Impl.AdminServiceImpl;
import com.fastconnect.service.Impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")

@CrossOrigin(origins = "*")
public class AdminController {
    @Autowired
    private AdminServiceImpl adminService;

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUserById(@PathVariable long userId) {
        adminService.deleteUserById(userId);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/search-by-role-type")
    public ResponseEntity <List<UserResponse>> getUserByRoleType(@RequestParam RoleType roleType, Pageable pageable) {
        List<UserResponse> userResponse=adminService
                .getUsersByRoleType(roleType);
        return ResponseEntity.ok(userResponse);
    }
    @PostMapping("/change-role")
    public ResponseEntity <UserResponse> changeRole(@RequestParam RoleType roleType,
                                                    @RequestParam String email) {
        UserResponse userResponse=adminService.changeUserRole(email, roleType);
        return ResponseEntity.ok(userResponse);
    }
}
