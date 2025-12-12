package com.fastconnect.repository;

import com.fastconnect.entity.Society;
import com.fastconnect.enums.SocietyCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SocietyRepository extends JpaRepository<Society, Long> {

    boolean existsBySocietyName(String societyName);

    Optional<Society> findBySocietyName(String societyName);

    Page<Society> findByCategory(SocietyCategory category, Pageable pageable);

    Page<Society> findBySocietyNameContainingIgnoreCase(String name, Pageable pageable);

}