package com.fastconnect.service.Impl;

import com.fastconnect.dto.ConnectionRequestActionDTO;
import com.fastconnect.dto.ConnectionRequestDetails;
import com.fastconnect.dto.ConnectionResponse;
import com.fastconnect.entity.Connection;
import com.fastconnect.entity.ConnectionRequest;
import com.fastconnect.entity.User;
import com.fastconnect.enums.ConnectionRequestStatus;
import com.fastconnect.enums.EntityType;
import com.fastconnect.enums.NotificationType;
import com.fastconnect.exception.ConnectionRequestAlreadySent;
import com.fastconnect.exception.ConnectionRequestNotFoundException;
import com.fastconnect.exception.UserEmailNotFoundException;
import com.fastconnect.mapper.ConnectionMapper;
import com.fastconnect.mapper.ConnectionRequestMapper;
import com.fastconnect.repository.ConnectionRepository;
import com.fastconnect.repository.ConnectionRequestRepository;
import com.fastconnect.repository.UserRepository;
import com.fastconnect.service.ConnectionService;
import com.fastconnect.service.NotificationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Transactional
@AllArgsConstructor
public class ConnectionServiceImpl implements ConnectionService {
    private final ConnectionRepository connectionRepository;
    private final ConnectionRequestRepository connectionRequestRepository;
    private final ConnectionRequestMapper  connectionRequestMapper;
    private final UserRepository userRepository;
    private final ConnectionMapper connectionMapper;
    private final NotificationService notificationService;

    private final ObjectMapper objectMapper; // ADD THIS FIELD

    // ⚠️ WARNING: Not suitable for multi-instance production environments!
    private static final ConcurrentHashMap<String, IdempotencyRecord> IDEMPOTENCY_CACHE = new ConcurrentHashMap<>();

    // Simple inner class to hold the map data
    private static class IdempotencyRecord {
        final String status; // Can hold "PROCESSING" or the final JSON result
        final LocalDateTime expiryTime;

        public IdempotencyRecord(String status, LocalDateTime expiryTime) {
            this.status = status;
            this.expiryTime = expiryTime;
        }
    }

    // Helper method to handle cached response retrieval (used by both endpoints)
    private <T> T handleCachedResponse(String idempotencyKey, Class<T> responseClass) {
        IdempotencyRecord existingRecord = IDEMPOTENCY_CACHE.get(idempotencyKey);

        if (existingRecord == null || existingRecord.expiryTime.isBefore(LocalDateTime.now())) {
            // Key expired or disappeared. Force creation of a new lock/record.
            return null;
        }

        if (!"PROCESSING".equals(existingRecord.status)) {
            // Found final successful result.
            try {
                return objectMapper.readValue(existingRecord.status, responseClass);
            } catch (Exception e) {
                throw new RuntimeException("Error deserializing cached response.", e);
            }
        }

        // Key is 'PROCESSING' - duplicate request in progress.
        throw new IllegalStateException("Duplicate request detected and currently processing.");
    }
    @Override
    public ConnectionRequestDetails createConnectionRequest(String senderEmail, String receiverEmail,String idempotencyKey) {
        if (idempotencyKey != null && !idempotencyKey.trim().isEmpty()) {
            ConnectionRequestDetails cachedDetails = handleCachedResponse(idempotencyKey, ConnectionRequestDetails.class);
            if (cachedDetails != null) {
                return cachedDetails;
            }

            // Acquire lock
            IDEMPOTENCY_CACHE.put(idempotencyKey, new IdempotencyRecord("PROCESSING", LocalDateTime.now().plusSeconds(10)));
        }

        try {
            User sender = userRepository.findByEmail(senderEmail)
                    .orElseThrow(() -> new UserEmailNotFoundException(senderEmail));
            User receiver = userRepository.findByEmail(receiverEmail)
                    .orElseThrow(() -> new UserEmailNotFoundException(receiverEmail));

            if (sender.getUserId().equals(receiver.getUserId())) {
                throw new IllegalArgumentException("Cannot send a connection request to yourself.");
            }
            Long minId = Math.min(sender.getUserId(), receiver.getUserId());
            Long maxId = Math.max(sender.getUserId(), receiver.getUserId());
            if (connectionRepository.findByUser1UserIdAndUser2UserId(minId, maxId).isPresent()) {
                throw new ConnectionRequestAlreadySent("Users are already connected.");
            }
            if (connectionRequestRepository.findBetweenUsers(minId, maxId).isPresent()) {
                throw new ConnectionRequestAlreadySent("A pending Connection request already exists.");
            }
            ConnectionRequest connectionRequest = new ConnectionRequest();
            connectionRequest.setCreatedAt(LocalDateTime.now());
            connectionRequest.setSender(sender);
            connectionRequest.setReceiver(receiver);
            connectionRequest.setStatus(ConnectionRequestStatus.PENDING);
            ConnectionRequest savedRequest = connectionRequestRepository.save(connectionRequest);

            notificationService.createNotification(
                    sender.getUserId(),
                    receiver.getProfile().getFullName() + " sent you connection invite",
                    NotificationType.CONNECTION,
                    EntityType.CONNECTION,
                    savedRequest.getConnectionRequestId()
            );
            if (idempotencyKey != null && !idempotencyKey.trim().isEmpty()) {
                String responseJson = objectMapper.writeValueAsString(connectionRequestMapper.toDetailsDTO(savedRequest));
                IDEMPOTENCY_CACHE.put(idempotencyKey, new IdempotencyRecord(responseJson, LocalDateTime.now().plusSeconds(3600)));
            }
            return connectionRequestMapper.toDetailsDTO(savedRequest);
        }
        catch (Exception e) {
            // --- IDEMPOTENCY CLEANUP ---
            if (idempotencyKey != null && !idempotencyKey.trim().isEmpty()) {
                IDEMPOTENCY_CACHE.remove(idempotencyKey);
            }
            throw new RuntimeException("Error creating connection request.", e);
        }

    }

    @Override
    public void withdrawConnectionRequest(Long requestId, String senderEmail) throws AccessDeniedException {
        ConnectionRequest connectionRequest = connectionRequestRepository.findByConnectionRequestId(requestId)
                .orElseThrow(() -> new ConnectionRequestNotFoundException(requestId));

        if (!connectionRequest.getSender().getEmail().equals(senderEmail)) {
            throw new AccessDeniedException("You are not authorized to withdraw this request.");
        }

        if (connectionRequest.getStatus() != ConnectionRequestStatus.PENDING) {
            throw new IllegalStateException("Only PENDING requests can be withdrawn.");
        }
        connectionRequestRepository.delete(connectionRequest);
    }

    @Override
    public ConnectionRequestDetails respondToConnectionRequest(Long receiverId, ConnectionRequestActionDTO actionDTO) throws AccessDeniedException {
        ConnectionRequest connectionRequest = connectionRequestRepository.findByConnectionRequestId(actionDTO.getConnectionRequestId())
                .orElseThrow(() -> new ConnectionRequestNotFoundException(actionDTO.getConnectionRequestId()));

        if (!connectionRequest.getReceiver().getUserId().equals(receiverId)) {
            throw new AccessDeniedException("You are not authorized to respond to this request.");
        }

        ConnectionRequestStatus desiredStatus = actionDTO.getStatus();
        connectionRequest.setStatus(desiredStatus);
        connectionRequest.setRespondedAt(LocalDateTime.now());

        // Save the connection request first to get an ID if needed for notifications
        connectionRequestRepository.save(connectionRequest);

        User sender = connectionRequest.getSender();
        User receiver = connectionRequest.getReceiver();

        if (desiredStatus == ConnectionRequestStatus.REJECTED) {
            // Clean up pending request if rejected
            cleanupConnectionRequest(sender.getUserId(), receiver.getUserId());
        }

        if (desiredStatus == ConnectionRequestStatus.ACCEPTED) {
            Long minId = Math.min(sender.getUserId(), receiver.getUserId());
            Long maxId = Math.max(sender.getUserId(), receiver.getUserId());

            // Only create connection if it doesn't already exist
            if (!connectionRepository.findByUser1UserIdAndUser2UserId(minId, maxId).isPresent()) {
                User user1 = (sender.getUserId().equals(minId)) ? sender : receiver;
                User user2 = (sender.getUserId().equals(maxId)) ? sender : receiver;

                Connection connection = new Connection();
                connection.setConnectedAt(LocalDateTime.now());
                connection.setUser1(user1);
                connection.setUser2(user2);
                connectionRepository.save(connection);

                // Send notification after saving the connection
                notificationService.createNotification(
                        sender.getUserId(),
                        receiver.getProfile().getFullName() + " accepted your connection invite",
                        NotificationType.CONNECTION,
                        EntityType.CONNECTION,
                        connectionRequest.getConnectionRequestId()
                );
            }
        }

        return connectionRequestMapper.toDetailsDTO(connectionRequest);
    }


    @Override
    @Transactional(readOnly = true)
    public Page<ConnectionRequestDetails> getPendingReceivedRequests(Long receiverId, Pageable pageable) {
        Page<ConnectionRequest> requestsPage = connectionRequestRepository.findAllByReceiverUserIdAndStatus(
                receiverId,
                ConnectionRequestStatus.PENDING,
                pageable
        );
        return requestsPage.map(connectionRequestMapper::toDetailsDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ConnectionRequestDetails> getSentRequests(Long senderId, Pageable pageable) {
        Page<ConnectionRequest> requestsPage = connectionRequestRepository.findAllBySenderUserIdAndStatus(
                senderId,
                ConnectionRequestStatus.PENDING,
                pageable
        );
        return requestsPage.map(connectionRequestMapper::toDetailsDTO);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    @Transactional(readOnly = true)
    public Optional<ConnectionRequestDetails> findConnectionRequestBetweenUsers(Long userAId, Long userBId) {
        return connectionRequestRepository.findBetweenUsers(userAId, userBId)
                .map(connectionRequestMapper::toDetailsDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ConnectionResponse> getEstablishedConnections(Long authenticatedUserId, Pageable pageable) {
        Page<Connection> connectionsPage = connectionRepository.findByUser1UserIdOrUser2UserId(
                authenticatedUserId,
                authenticatedUserId,
                pageable
        );

        return connectionsPage.map(connection -> {
            User connectedUser = connection.getUser1().getUserId().equals(authenticatedUserId)
                    ? connection.getUser2()
                    : connection.getUser1();

            return connectionMapper.mapWithConnectedUser(connection, connectedUser);
        });
    }
    @Override
    public void deleteConnection(Long currentUserId, Long targetUserId) {
        if (currentUserId.equals(targetUserId)) {
            throw new IllegalArgumentException("Cannot disconnect from yourself.");
        }
        Long minId = Math.min(currentUserId, targetUserId);
        Long maxId = Math.max(currentUserId, targetUserId);
        cleanupConnectionRequest(minId, maxId);
        connectionRepository.deleteByUser1UserIdAndUser2UserId(minId, maxId);
    }

    @Override
    public Page<ConnectionRequestDetails> getRequestsByStatus(ConnectionRequestStatus status, Pageable pageable) {
        Page<ConnectionRequest> requestsPage = connectionRequestRepository.findAllByStatus(status, pageable);
        return requestsPage.map(connectionRequestMapper::toDetailsDTO);
    }

    @Override
    public Page<ConnectionRequestDetails> getAllRequestsByTwoStatuses(Long userId, ConnectionRequestStatus status1, ConnectionRequestStatus status2, Pageable pageable) {
        Page<ConnectionRequest> requestsPage = connectionRequestRepository.findAllByReceiverUserIdAndStatusOrSenderUserIdAndStatus(
                userId, status1, userId, status2, pageable);
        return requestsPage.map(connectionRequestMapper::toDetailsDTO);
    }

    @Override
    public Page<ConnectionRequestDetails> getReceivedRequestsByStatusSorted(Long receiverId, ConnectionRequestStatus status, Pageable pageable) {
        Page<ConnectionRequest> requestsPage = connectionRequestRepository.findAllByReceiverUserIdAndStatusOrderByCreatedAtDesc(
                receiverId, status, pageable);
        return requestsPage.map(connectionRequestMapper::toDetailsDTO);
    }

    @Override
    public Page<ConnectionRequestDetails> getAllRequestsSortedByCreation(Pageable pageable) {
        Page<ConnectionRequest> requestsPage = connectionRequestRepository.findAllByOrderByCreatedAtDesc(pageable);
        return requestsPage.map(connectionRequestMapper::toDetailsDTO);
    }

    @Override
    public Page<ConnectionRequestDetails> getAllRequestsSortedByResponse(Pageable pageable) {
        Page<ConnectionRequest> requestsPage = connectionRequestRepository.findAllByOrderByRespondedAtDesc(pageable);
        return requestsPage.map(connectionRequestMapper::toDetailsDTO);
    }

    // Inside ConnectionServiceImpl.java
    private void cleanupConnectionRequest(Long userAId, Long userBId) {
        Optional<ConnectionRequest> optionalRequest = connectionRequestRepository.findBetweenUsers(userAId, userBId);

        optionalRequest.ifPresent(connectionRequestRepository::delete);
    }
}
