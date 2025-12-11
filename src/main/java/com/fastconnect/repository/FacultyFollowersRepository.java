package com.fastconnect.repository;

import com.fastconnect.entity.FacultyFollowers;
import com.fastconnect.entity.FacultyPage;
import com.fastconnect.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FacultyFollowersRepository extends JpaRepository<FacultyFollowers, Long> {

    boolean existsByUserAndFacultyPage(User user, FacultyPage facultyPage);

    List<FacultyFollowers> findAllByFacultyPage(FacultyPage facultyPage);

    Long countByFacultyPage(FacultyPage facultyPage);
}
