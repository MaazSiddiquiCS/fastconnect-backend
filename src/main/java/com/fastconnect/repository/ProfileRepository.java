package com.fastconnect.repository;

import com.fastconnect.entity.Profile;
import com.fastconnect.entity.User;
import com.fastconnect.enums.Departments;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProfileRepository extends JpaRepository<Profile, Long> {

    //Optional<Profile> findByProfileId(Long id);

    Optional<Profile> findByFullName(String fullName);

    Optional<Profile> findByRollNumber(String rollNumber);

    Page<Profile> findByDepartment(Departments department, Pageable pageable);

    List<Profile> findByBatch(Integer batch);

    boolean existsByUser_UserId(Long userId);

    boolean existsByFullName(String fullName);

    boolean existsByRollNumber(String rollNumber);

    Optional<Profile> findByUser_UserId(Long id);

    Page<Profile> findByFullNameContaining(String fullName, Pageable pageable);

    Page<Profile> findByRollNumberContaining(String rollNumber, Pageable pageable);
}
