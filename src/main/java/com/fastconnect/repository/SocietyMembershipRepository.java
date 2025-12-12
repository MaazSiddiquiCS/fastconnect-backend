package com.fastconnect.repository;

import com.fastconnect.entity.Society;
import com.fastconnect.entity.SocietyMembership;
import com.fastconnect.entity.User;
import com.fastconnect.enums.SocietyRoles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SocietyMembershipRepository extends JpaRepository<SocietyMembership, Long> {

    Optional<SocietyMembership> findBySocietyAndUser(Society society, User user);

    List<SocietyMembership> findBySocietyAndActiveTrue(Society society);

    List<SocietyMembership> findByUserAndActiveTrue(User user);

    List<SocietyMembership> findBySocietyAndSocietyRole(Society society, SocietyRoles societyRole);

    boolean existsBySocietyAndUserAndActiveTrue(Society society, User user);
}