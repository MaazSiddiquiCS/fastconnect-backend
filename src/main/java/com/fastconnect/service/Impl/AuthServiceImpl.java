package com.fastconnect.service.Impl;

import com.fastconnect.dto.LoginResponse;
import com.fastconnect.dto.UserRequest;
import com.fastconnect.dto.UserResponse;
import com.fastconnect.entity.AuthToken;
import com.fastconnect.entity.User;
import com.fastconnect.enums.AccountStatus;
import com.fastconnect.enums.RoleType;
import com.fastconnect.exception.EmailAlreadyExistsException;
import com.fastconnect.exception.UserEmailNotFoundException;
import com.fastconnect.mapper.UserMapper;
import com.fastconnect.repository.AuthTokenRepository;
import com.fastconnect.repository.UserRepository;
import com.fastconnect.service.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final PasswordEncoder passwordEncoder;
    private final UserServiceImpl userService;
    private final AuthTokenRepository authTokenRepository;
    private final JwtService jwtService;
    private final UserMapper userMapper;

    @Override
    public UserResponse registerUser(UserRequest userRequest) {
        boolean existsByEmail=userService.existsByEmail(userRequest.getEmail());
        if(existsByEmail){
            throw new EmailAlreadyExistsException(userRequest.getEmail());
        }
        User user= userMapper.toEntity(userRequest);
        user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        user.setAccountStatus(AccountStatus.ACTIVE);
        user.setRoleType(RoleType.STUDENT);

        User registeredUser= userService.saveUserEntity(user);
        return userMapper.toDTO(registeredUser);
    }

    @Override
    public LoginResponse loginUser(UserRequest userRequest) {
        User user= userService.getUserEntityByEmail(userRequest.getEmail());
        if(!passwordEncoder.matches(userRequest.getPassword(),user.getPassword())){
            throw new BadCredentialsException("Wrong Password");
        }
        String accessToken = jwtService.generateToken(user);
        String refreshTokenString = jwtService.generateRefreshToken(user);
        createAuthToken(refreshTokenString,user);
        return new LoginResponse(
                accessToken,
                refreshTokenString,
                user.getUserId(),
                user.getEmail(),
                user.getRoleType()
        );
    }

    public void createAuthToken(String refreshTokenString, User user) {
        AuthToken token = new AuthToken();
        token.setRefreshToken(refreshTokenString);
        token.setUser(user);

        Date expirationDate = jwtService.extractExpiration(refreshTokenString);
        token.setExpiresAt(expirationDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());

        authTokenRepository.save(token);
    }

    @Override
    public LoginResponse refreshAccessToken(String refreshTokenString) {


        AuthToken authToken = authTokenRepository.findByRefreshToken(refreshTokenString)
                .orElseThrow(() -> new RuntimeException("Refresh Token not found"));

        if (authToken.getRevoked() || authToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BadCredentialsException("Refresh Token expired/revoked");
        }

        String email = jwtService.extractSubject(refreshTokenString);
        User user = userService.getUserEntityByEmail(email);

        if (!jwtService.validateRefreshToken(refreshTokenString, user)) {
            throw new BadCredentialsException("Invalid Refresh Token");
        }

        String newAccessToken = jwtService.generateToken(user);
        String newRefreshToken = jwtService.generateRefreshToken(user);

        authToken.setRevoked(true);
        authTokenRepository.save(authToken);

        createAuthToken(newRefreshToken, user);

        return new LoginResponse(
                newAccessToken,
                newRefreshToken,
                user.getUserId(),
                user.getEmail(),
                user.getRoleType()
        );
    }

}
