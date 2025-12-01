package com.fastconnect.repository;

import com.fastconnect.entity.User;
import com.fastconnect.enums.AccountStatus;
import com.fastconnect.enums.RoleType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {

    Optional<User> findByEmail(String email);

    boolean existsByUserId(Long userId);

    boolean existsByEmail(String email);

    Optional<User> findByUserId(Long userId);

    List<User> findByRoleType(RoleType roleType);

    boolean existsByRoleType(RoleType roleType);

    Page<User> findByAccountStatus(AccountStatus accountStatus, Pageable pageable);
}
