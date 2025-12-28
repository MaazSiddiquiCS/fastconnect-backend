package com.fastconnect.repository;

import com.fastconnect.entity.FacultyPage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FacultyPageRepository extends JpaRepository<FacultyPage, Long> {
    Optional<FacultyPage> findByUserUserId(Long userId);
}