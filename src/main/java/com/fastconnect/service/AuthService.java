package com.fastconnect.service;

import com.fastconnect.dto.LoginResponse;
import com.fastconnect.dto.UserRequest;
import com.fastconnect.dto.UserResponse;
import com.fastconnect.entity.User;

public interface AuthService {

    UserResponse registerUser(UserRequest userRequest);
    LoginResponse loginUser(UserRequest userRequest);
    void createAuthToken(String refreshTokenString, User user);
    LoginResponse refreshAccessToken(String refreshTokenString);
    void logoutUser(String refreshToken);
}
