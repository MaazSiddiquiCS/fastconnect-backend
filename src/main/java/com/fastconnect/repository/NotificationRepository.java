package com.fastconnect.repository;

import com.fastconnect.entity.Notification;
import com.fastconnect.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    Page<Notification> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);
    long countByUserAndIsReadFalse(User user);
    @Modifying // Indicates that the query is a write operation
    @Query("UPDATE Notification n SET n.isRead = TRUE WHERE n.user.userId = :userId AND n.isRead = FALSE")
    int markAllAsReadByUserId(@Param("userId") Long userId);
}