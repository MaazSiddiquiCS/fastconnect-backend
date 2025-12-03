package com.fastconnect.repository;

import com.fastconnect.entity.AuthToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface AuthTokenRepository extends JpaRepository<AuthToken, Long> {

    Optional<AuthToken> findByRefreshToken(String refreshToken);
    void deleteByRefreshToken(String refreshToken);
}
