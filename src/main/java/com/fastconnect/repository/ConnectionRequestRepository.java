package com.fastconnect.repository;

import com.fastconnect.entity.ConnectionRequest;
import com.fastconnect.enums.ConnectionRequestStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ConnectionRequestRepository extends JpaRepository<ConnectionRequest,Integer> {

    // --- Existence and Uniqueness Checks ---
    Optional<ConnectionRequest> findBySenderUserIdAndReceiverUserId(Long senderId, Long receiverId);
    @Query("SELECT cr FROM ConnectionRequest cr " +
            "WHERE (cr.sender.userId = :userAId AND cr.receiver.userId = :userBId " +
            "OR cr.sender.userId = :userBId AND cr.receiver.userId = :userAId) " )
    Optional<ConnectionRequest> findBetweenUsers(@Param("userAId") Long userAId, @Param("userBId") Long userBId);
    boolean existsBySenderUserIdAndReceiverUserId(Long senderId, Long receiverId);

    // --- Filtering by Status and Receiver/Sender (for UI lists) ---
    Page<ConnectionRequest> findAllByReceiverUserIdAndStatus(Long receiverId, ConnectionRequestStatus status,
                                                             Pageable pageable);
    Page<ConnectionRequest> findAllBySenderUserIdAndStatus(Long senderId, ConnectionRequestStatus status,
                                                           Pageable pageable);
    Page<ConnectionRequest> findAllByStatus(ConnectionRequestStatus status,
                                            Pageable pageable);

    // --- Finding All Connections (where status is ACCEPTED) ---
    Page<ConnectionRequest> findAllByReceiverUserIdAndStatusOrSenderUserIdAndStatus(Long receiverId, ConnectionRequestStatus receiverStatus, Long senderId, ConnectionRequestStatus senderStatus,
                                                                                    Pageable pageable);

    // --- Ordering ---
    Page<ConnectionRequest> findAllByReceiverUserIdAndStatusOrderByCreatedAtDesc(Long receiverId, ConnectionRequestStatus status,
                                                                                 Pageable pageable);
    Page<ConnectionRequest> findAllByOrderByCreatedAtDesc(Pageable pageable);
    Page<ConnectionRequest> findAllByOrderByRespondedAtDesc(Pageable pageable);


    Optional<ConnectionRequest> findByConnectionRequestId(Long connectionRequestId);
}
