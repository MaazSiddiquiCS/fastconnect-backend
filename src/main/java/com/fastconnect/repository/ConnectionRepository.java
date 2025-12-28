package com.fastconnect.repository;

import com.fastconnect.entity.Connection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface ConnectionRepository extends JpaRepository<Connection, Long> {

    Optional<Connection> findByUser1UserIdAndUser2UserId(Long user1Id, Long user2Id);
    Page<Connection> findByUser1UserIdOrUser2UserId(Long user1Id, Long user2Id, Pageable pageable);
    Page<Connection> findByUser1UserId(Long user1Id, Pageable pageable);
    // --- Retrieval (Finding a User's Network)

    // --- Deletion (Disconnecting) ---
    void deleteByUser1UserIdAndUser2UserId(Long user1Id, Long user2Id);
}
