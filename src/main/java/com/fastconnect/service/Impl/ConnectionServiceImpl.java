package com.fastconnect.service.Impl;

import com.fastconnect.dto.ConnectionRequestActionDTO;
import com.fastconnect.dto.ConnectionRequestDetails;
import com.fastconnect.dto.ConnectionResponse;
import com.fastconnect.entity.Connection;
import com.fastconnect.entity.ConnectionRequest;
import com.fastconnect.entity.User;
import com.fastconnect.enums.ConnectionRequestStatus;
import com.fastconnect.exception.ConnectionRequestAlreadySent;
import com.fastconnect.exception.ConnectionRequestNotFoundException;
import com.fastconnect.exception.UserEmailNotFoundException;
import com.fastconnect.mapper.ConnectionMapper;
import com.fastconnect.mapper.ConnectionRequestMapper;
import com.fastconnect.repository.ConnectionRepository;
import com.fastconnect.repository.ConnectionRequestRepository;
import com.fastconnect.repository.UserRepository;
import com.fastconnect.service.ConnectionService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional
@AllArgsConstructor
public class ConnectionServiceImpl implements ConnectionService {
    private final ConnectionRepository connectionRepository;
    private final ConnectionRequestRepository connectionRequestRepository;
    private final ConnectionRequestMapper  connectionRequestMapper;
    private final UserRepository userRepository;
    private final ConnectionMapper connectionMapper;


    @Override
    public ConnectionRequestDetails createConnectionRequest(String senderEmail, String receiverEmail) {
        User sender=userRepository.findByEmail(senderEmail)
                .orElseThrow(() -> new UserEmailNotFoundException(senderEmail));
        User receiver =userRepository.findByEmail(receiverEmail)
                .orElseThrow(()->new UserEmailNotFoundException(receiverEmail));

        if (sender.getUserId().equals(receiver.getUserId())) {
            throw new IllegalArgumentException("Cannot send a connection request to yourself.");
        }
        Long minId = Math.min(sender.getUserId(), receiver.getUserId());
        Long maxId = Math.max(sender.getUserId(), receiver.getUserId());
        if (connectionRepository.findByUser1UserIdAndUser2UserId(minId, maxId).isPresent() ) {
            throw new ConnectionRequestAlreadySent("Users are already connected.");
        }
        if (connectionRequestRepository.findBetweenUsers(minId, maxId).isPresent()) {
            throw new ConnectionRequestAlreadySent("A pending Connection request already exists.");
        }
        ConnectionRequest connectionRequest= new ConnectionRequest();
        connectionRequest.setCreatedAt(LocalDateTime.now());
        connectionRequest.setSender(sender);
        connectionRequest.setReceiver(receiver);
        connectionRequest.setStatus(ConnectionRequestStatus.PENDING);
        return connectionRequestMapper.toDetailsDTO(connectionRequestRepository.save(connectionRequest));
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
        if (desiredStatus == ConnectionRequestStatus.REJECTED) {
            cleanupConnectionRequest(connectionRequest.getSender().getUserId(), receiverId);
        }
        if (desiredStatus == ConnectionRequestStatus.ACCEPTED) {

            User sender = connectionRequest.getSender();
            User receiver = connectionRequest.getReceiver();

            Long minId = Math.min(sender.getUserId(), receiver.getUserId());
            Long maxId = Math.max(sender.getUserId(), receiver.getUserId());

            if (!connectionRepository.findByUser1UserIdAndUser2UserId(minId, maxId).isPresent()) {

                User user1 = (sender.getUserId().equals(minId)) ? sender : receiver;
                User user2 = (sender.getUserId().equals(maxId)) ? sender : receiver;

                Connection connection = new Connection();
                connection.setConnectedAt(LocalDateTime.now());
                connection.setUser1(user1);
                connection.setUser2(user2);
                connectionRepository.save(connection);
            }

            connectionRequest.setStatus(desiredStatus);
            connectionRequest.setRespondedAt(LocalDateTime.now());

            return connectionRequestMapper.toDetailsDTO(connectionRequestRepository.save(connectionRequest));
        }
        return null;
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
