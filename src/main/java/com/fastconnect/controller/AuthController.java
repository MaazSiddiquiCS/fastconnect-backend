package com.fastconnect.controller;

import com.fastconnect.dto.LoginResponse;
import com.fastconnect.dto.RefreshTokenRequest;
import com.fastconnect.dto.UserRequest;
import com.fastconnect.dto.UserResponse;
import com.fastconnect.entity.User;
import com.fastconnect.exception.UserNotFoundException;
import com.fastconnect.service.AuthService;
import com.fastconnect.service.Impl.JwtService;
import com.fastconnect.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {
    @Autowired
    private UserService userService;

    @Autowired
    private AuthService authService;
    @Autowired
    private JwtService jwtService;

    @PostMapping("/register")
    ResponseEntity<UserResponse> register(
            @Valid
            @RequestBody UserRequest userRequest) {
        UserResponse newUser = authService.registerUser(userRequest);
        return ResponseEntity.ok().body(newUser);
    }

    @PostMapping("/login")
    ResponseEntity<LoginResponse> login(
            @Valid
            @RequestBody UserRequest userRequest
    )
    {
        LoginResponse newLogin= authService.loginUser(userRequest);
        return ResponseEntity.ok().body(newLogin);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<LoginResponse> refreshToken(
            @RequestBody RefreshTokenRequest request
    ) {
        LoginResponse response = authService.refreshAccessToken(request.getRefreshToken());
        return ResponseEntity.ok(response);
    }

}
