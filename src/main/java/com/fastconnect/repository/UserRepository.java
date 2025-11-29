package com.fastconnect.repository;

import com.fastconnect.entity.User;
import com.fastconnect.enums.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {

    //Optional<User> findByProfile_FullName(String fullName);

    Optional<User> findByEmail(String email);

    boolean existsByUserId(Long userId);

    //boolean existsByProfile_FullName(String fullName);

    boolean existsByEmail(String email);

    Optional<User> findByUserId(Long userId);

    List<User> findUserByRoleType(RoleType roleType);

    boolean existsByRoleType(RoleType roleType);
}
