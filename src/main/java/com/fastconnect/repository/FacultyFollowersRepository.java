package com.fastconnect.repository;

import com.fastconnect.entity.FacultyFollowers;
import com.fastconnect.entity.FacultyPage;
import com.fastconnect.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FacultyFollowersRepository extends JpaRepository<FacultyFollowers, Long> {
    Optional<FacultyFollowers> findByFacultyPageAndUser(FacultyPage facultyPage, User user);
    long countByFacultyPage(FacultyPage facultyPage);
}