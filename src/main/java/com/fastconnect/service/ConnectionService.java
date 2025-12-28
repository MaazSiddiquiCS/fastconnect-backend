package com.fastconnect.service;

import com.fastconnect.dto.ConnectionRequestActionDTO;
import com.fastconnect.dto.ConnectionRequestDetails;
import com.fastconnect.dto.ConnectionResponse;
import com.fastconnect.enums.ConnectionRequestStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Optional;

public interface ConnectionService {

    // --- 1. Request Creation and Withdrawal ---
    ConnectionRequestDetails createConnectionRequest(String senderEmail, String receiverEmail,String idempotencyKey);

    void withdrawConnectionRequest(Long requestId, String senderEmail) throws AccessDeniedException;

    // --- 2. Response and Status Update ---

     ConnectionRequestDetails respondToConnectionRequest(Long receiverId, ConnectionRequestActionDTO actionDTO) throws AccessDeniedException;

    // --- 3. Retrieval and Listing ---

    Page<ConnectionRequestDetails> getPendingReceivedRequests(Long receiverId, Pageable pageable);


    Page<ConnectionRequestDetails> getSentRequests(Long senderId, Pageable pageable);

    Optional<ConnectionRequestDetails> findConnectionRequestBetweenUsers(Long userAId, Long userBId);

    // ConnectionService
    Page<ConnectionResponse> getEstablishedConnections(Long authenticatedUserId, Pageable pageable);
    // ConnectionService Interface
    void deleteConnection(Long userIdA, Long userIdB);

    Page<ConnectionRequestDetails> getRequestsByStatus(ConnectionRequestStatus status, Pageable pageable);
    Page<ConnectionRequestDetails> getAllRequestsByTwoStatuses(Long userId, ConnectionRequestStatus status1, ConnectionRequestStatus status2, Pageable pageable);
    Page<ConnectionRequestDetails> getReceivedRequestsByStatusSorted(Long receiverId, ConnectionRequestStatus status, Pageable pageable);
    Page<ConnectionRequestDetails> getAllRequestsSortedByCreation(Pageable pageable);
    Page<ConnectionRequestDetails> getAllRequestsSortedByResponse(Pageable pageable);
}