package com.fastconnect.controller;

import com.fastconnect.dto.ConnectionRequestActionDTO;
import com.fastconnect.dto.ConnectionRequestDetails;
import com.fastconnect.dto.ConnectionRequestSendDTO;
import com.fastconnect.dto.ConnectionResponse;
import com.fastconnect.enums.ConnectionRequestStatus;
import com.fastconnect.security.CustomUserDetails;
import com.fastconnect.service.ConnectionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public ResponseEntity<ConnectionRequestDetails> sendConnectionRequest(@Valid
                                                                              @RequestBody ConnectionRequestSendDTO connectionRequestSendDTO,
                                                                          @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        ConnectionRequestDetails details = connectionService.createConnectionRequest(
                customUserDetails.getUsername(),
                connectionRequestSendDTO.getReceiverEmail()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(details);
    }

    @DeleteMapping("/withdraw-request/{requestId}")
    public ResponseEntity<Void> withdrawConnectionRequest(
            @PathVariable Long requestId,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) throws AccessDeniedException {
        connectionService.withdrawConnectionRequest(requestId, customUserDetails.getUsername());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/respond-request")
    public ResponseEntity<ConnectionRequestDetails> respondToConnectionRequest(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @Valid @RequestBody ConnectionRequestActionDTO actionDTO
    ) throws AccessDeniedException {
        ConnectionRequestDetails details = connectionService.respondToConnectionRequest(
                customUserDetails.getUserId(),
                actionDTO
        );
        return ResponseEntity.ok(details);
    }

    @GetMapping("/requests/received")
    public ResponseEntity<Page<ConnectionRequestDetails>> getPendingReceivedRequests(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            Pageable pageable
    ) {
        Page<ConnectionRequestDetails> page = connectionService.getPendingReceivedRequests(customUserDetails.getUserId(), pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/requests/sent")
    public ResponseEntity<Page<ConnectionRequestDetails>> getSentRequests(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            Pageable pageable
    ) {
        Page<ConnectionRequestDetails> page = connectionService.getSentRequests(customUserDetails.getUserId(), pageable);
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

    @GetMapping("/established")
    public ResponseEntity<Page<ConnectionResponse>> getEstablishedConnections(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            Pageable pageable
    ) {
        Page<ConnectionResponse> page = connectionService.getEstablishedConnections(customUserDetails.getUserId(), pageable);
        return ResponseEntity.ok(page);
    }

    @DeleteMapping("/disconnect/{targetUserId}")
    public ResponseEntity<Void> deleteConnection(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable Long targetUserId
    ) {
        connectionService.deleteConnection(customUserDetails.getUserId(), targetUserId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/all/status/{status}")
    public ResponseEntity<Page<ConnectionRequestDetails>> getRequestsByStatus(
            @PathVariable ConnectionRequestStatus status,
            Pageable pageable
    ) {
        Page<ConnectionRequestDetails> page = connectionService.getRequestsByStatus(status, pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/all/user/status/{status1}/or/{status2}")
    public ResponseEntity<Page<ConnectionRequestDetails>> getAllRequestsByTwoStatuses(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable ConnectionRequestStatus status1,
            @PathVariable ConnectionRequestStatus status2,
            Pageable pageable
    ) {
        Page<ConnectionRequestDetails> page = connectionService.getAllRequestsByTwoStatuses(customUserDetails.getUserId(), status1, status2, pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/received/status/{status}/sorted")
    public ResponseEntity<Page<ConnectionRequestDetails>> getReceivedRequestsByStatusSorted(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable ConnectionRequestStatus status,
            Pageable pageable
    ) {
        Page<ConnectionRequestDetails> page = connectionService.getReceivedRequestsByStatusSorted(customUserDetails.getUserId(), status, pageable);
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