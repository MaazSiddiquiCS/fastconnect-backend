package com.fastconnect.service;

import com.fastconnect.entity.User;
import java.util.Date;

public interface IJwtService {
    String generateToken(User user);
    Boolean validateToken(String token, String email);
    String extractSubject(String token);
    Date extractExpiration(String token);
    String extractRole(String token);
    Long extractUserId(String token);

    // Optionally, for refresh token management (or use a separate service)
    String generateRefreshToken(User user);
    Boolean validateRefreshToken(String refreshToken, User user);
}

