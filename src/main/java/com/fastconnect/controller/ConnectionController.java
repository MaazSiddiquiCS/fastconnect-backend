package com.fastconnect.controller;

import com.fastconnect.dto.ConnectionRequestActionDTO;
import com.fastconnect.dto.ConnectionRequestDetails;
import com.fastconnect.dto.ConnectionRequestSendDTO;
import com.fastconnect.dto.ConnectionResponse;
import com.fastconnect.enums.ConnectionRequestStatus;
import com.fastconnect.service.ConnectionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.Optional;

@RestController
@RequestMapping("/api/connections")
@CrossOrigin(origins = "*")
public class ConnectionController {

    @Autowired
    private ConnectionService connectionService;

    @PostMapping("/send-request")
    public ResponseEntity<ConnectionRequestDetails> sendConnectionRequest(@Valid @RequestBody ConnectionRequestSendDTO connectionRequestSendDTO) {
        ConnectionRequestDetails details = connectionService.createConnectionRequest(
                connectionRequestSendDTO.getSenderEmail(),
                connectionRequestSendDTO.getReceiverEmail()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(details);
    }

    @DeleteMapping("/withdraw-request/{requestId}")
    public ResponseEntity<Void> withdrawConnectionRequest(
            @PathVariable Long requestId,
            @RequestParam String senderEmail
    ) throws AccessDeniedException {
        connectionService.withdrawConnectionRequest(requestId, senderEmail);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/respond-request/{receiverId}")
    public ResponseEntity<ConnectionRequestDetails> respondToConnectionRequest(
            @PathVariable Long receiverId,
            @Valid @RequestBody ConnectionRequestActionDTO actionDTO
    ) throws AccessDeniedException {
        ConnectionRequestDetails details = connectionService.respondToConnectionRequest(
                receiverId,
                actionDTO
        );
        return ResponseEntity.ok(details);
    }

    @GetMapping("/requests/received/{receiverId}")
    public ResponseEntity<Page<ConnectionRequestDetails>> getPendingReceivedRequests(
            @PathVariable Long receiverId,
            Pageable pageable
    ) {
        Page<ConnectionRequestDetails> page = connectionService.getPendingReceivedRequests(receiverId, pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/requests/sent/{senderId}")
    public ResponseEntity<Page<ConnectionRequestDetails>> getSentRequests(
            @PathVariable Long senderId,
            Pageable pageable
    ) {
        Page<ConnectionRequestDetails> page = connectionService.getSentRequests(senderId, pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/requests/between/{userAId}/{userBId}")
    public ResponseEntity<ConnectionRequestDetails> findConnectionRequestBetweenUsers(
            @PathVariable Long userAId,
            @PathVariable Long userBId
    ) {
        Optional<ConnectionRequestDetails> details = connectionService.findConnectionRequestBetweenUsers(userAId, userBId);
        return details.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/established/{userId}")
    public ResponseEntity<Page<ConnectionResponse>> getEstablishedConnections(
            @PathVariable("userId") Long authenticatedUserId,
            Pageable pageable
    ) {
        Page<ConnectionResponse> page = connectionService.getEstablishedConnections(authenticatedUserId, pageable);
        return ResponseEntity.ok(page);
    }

    @DeleteMapping("/disconnect/{currentUserId}/{targetUserId}")
    public ResponseEntity<Void> deleteConnection(
            @PathVariable Long currentUserId,
            @PathVariable Long targetUserId
    ) {
        connectionService.deleteConnection(currentUserId, targetUserId);
        return ResponseEntity.noContent().build();
    }
    // Inside ConnectionController.java

    @GetMapping("/all/status/{status}")
    public ResponseEntity<Page<ConnectionRequestDetails>> getRequestsByStatus(
            @PathVariable ConnectionRequestStatus status,
            Pageable pageable
    ) {
        Page<ConnectionRequestDetails> page = connectionService.getRequestsByStatus(status, pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/all/user/{userId}/status/{status1}/or/{status2}")
    public ResponseEntity<Page<ConnectionRequestDetails>> getAllRequestsByTwoStatuses(
            @PathVariable Long userId,
            @PathVariable ConnectionRequestStatus status1,
            @PathVariable ConnectionRequestStatus status2,
            Pageable pageable
    ) {
        Page<ConnectionRequestDetails> page = connectionService.getAllRequestsByTwoStatuses(userId, status1, status2, pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/received/{receiverId}/status/{status}/sorted")
    public ResponseEntity<Page<ConnectionRequestDetails>> getReceivedRequestsByStatusSorted(
            @PathVariable Long receiverId,
            @PathVariable ConnectionRequestStatus status,
            Pageable pageable
    ) {
        Page<ConnectionRequestDetails> page = connectionService.getReceivedRequestsByStatusSorted(receiverId, status, pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/all/sorted/created")
    public ResponseEntity<Page<ConnectionRequestDetails>> getAllRequestsSortedByCreation(Pageable pageable) {
        Page<ConnectionRequestDetails> page = connectionService.getAllRequestsSortedByCreation(pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/all/sorted/responded")
    public ResponseEntity<Page<ConnectionRequestDetails>> getAllRequestsSortedByResponse(Pageable pageable) {
        Page<ConnectionRequestDetails> page = connectionService.getAllRequestsSortedByResponse(pageable);
        return ResponseEntity.ok(page);
    }
}