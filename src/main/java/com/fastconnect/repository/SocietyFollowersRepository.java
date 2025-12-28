package com.fastconnect.repository;

import com.fastconnect.entity.Society;
import com.fastconnect.entity.SocietyFollowers;
import com.fastconnect.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SocietyFollowersRepository extends JpaRepository<SocietyFollowers, Long> {
    Optional<SocietyFollowers> findBySocietyAndUser(Society society, User user);
    List<SocietyFollowers> findBySociety(Society society);
    List<SocietyFollowers> findByUser(User user);
    long countBySociety(Society society);
    boolean existsBySocietyAndUser(Society society, User user);
}