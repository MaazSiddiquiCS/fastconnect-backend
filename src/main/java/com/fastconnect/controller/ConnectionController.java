package com.fastconnect.controller;

import com.fastconnect.dto.ConnectionRequestActionDTO;
import com.fastconnect.dto.ConnectionRequestDetails;
import com.fastconnect.dto.ConnectionRequestSendDTO;
import com.fastconnect.dto.ConnectionResponse;
import com.fastconnect.enums.ConnectionRequestStatus;
import com.fastconnect.security.CustomUserDetails;
import com.fastconnect.service.ConnectionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "User Connection Management", description = "Endpoints for sending, managing, and viewing user connection requests and established connections.")
public class ConnectionController {

    @Autowired
    private ConnectionService connectionService;


    @Operation(
            summary = "Send a connection request to another user.",
            description = "Initiates a connection request from the authenticated user (sender) to the user specified by receiverEmail.",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Connection request successfully sent."
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Receiver user not found or user is blocked."
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Conflict: Request already exists or connection is already established."
                    )
            }
    )
    @PostMapping("/send-request")
    public ResponseEntity<ConnectionRequestDetails> sendConnectionRequest(@Valid
                                                                              @RequestBody ConnectionRequestSendDTO connectionRequestSendDTO,
                                                                          @AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                                          @Parameter(
                                                                                  name = "Idempotency-Key",
                                                                                  description = "Unique key to ensure the request is processed only once.",
                                                                                  in = io.swagger.v3.oas.annotations.enums.ParameterIn.HEADER,
                                                                                  required = false
                                                                          )
                                                                              @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey) {
        ConnectionRequestDetails details = connectionService.createConnectionRequest(
                customUserDetails.getUsername(),
                connectionRequestSendDTO.getReceiverEmail(),
                idempotencyKey
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(details);
    }


    @Operation(
            summary = "Withdraw a pending connection request.",
            description = "Allows the sender to cancel a connection request that has not yet been accepted or rejected.",
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Request successfully withdrawn (No Content)."
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Request not found."
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden: User is not the sender of this request."
                    )
            }
    )
    @DeleteMapping("/withdraw-request/{requestId}")
    public ResponseEntity<Void> withdrawConnectionRequest(
            @PathVariable Long requestId,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) throws AccessDeniedException {
        connectionService.withdrawConnectionRequest(requestId, customUserDetails.getUsername());
        return ResponseEntity.noContent().build();
    }


    @Operation(
            summary = "Accept or Reject a connection request.",
            description = "Allows the receiver to respond to a pending connection request using the actionDTO (status change to ACCEPTED or REJECTED).",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Request successfully processed."
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Request not found."
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden: User is not the receiver of this request."
                    )
            }
    )
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

    @Operation(
            summary = "Get all pending received connection requests.",
            description = "Retrieves a paginated list of requests addressed to the authenticated user that are currently in the PENDING status.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved received requests."
                    )
            }
    )
    @GetMapping("/requests/received")
    public ResponseEntity<Page<ConnectionRequestDetails>> getPendingReceivedRequests(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            Pageable pageable
    ) {
        Page<ConnectionRequestDetails> page = connectionService.getPendingReceivedRequests(customUserDetails.getUserId(), pageable);
        return ResponseEntity.ok(page);
    }


    @Operation(
            summary = "Get all sent connection requests.",
            description = "Retrieves a paginated list of all requests initiated by the authenticated user, regardless of status.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved sent requests."
                    )
            }
    )
    @GetMapping("/requests/sent")
    public ResponseEntity<Page<ConnectionRequestDetails>> getSentRequests(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            Pageable pageable
    ) {
        Page<ConnectionRequestDetails> page = connectionService.getSentRequests(customUserDetails.getUserId(), pageable);
        return ResponseEntity.ok(page);
    }

    @Operation(
            summary = "Find a connection request between two specific users.",
            description = "Retrieves the details of any existing connection request (regardless of status or sender/receiver role) between userAId and userBId. Requires authentication/Admin role depending on security config.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Request details found."
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "No connection request found between the two users."
                    )
            }
    )
    @GetMapping("/requests/between/{userAId}/{userBId}")
    public ResponseEntity<ConnectionRequestDetails> findConnectionRequestBetweenUsers(
            @PathVariable Long userAId,
            @PathVariable Long userBId
    ) {
        Optional<ConnectionRequestDetails> details = connectionService.findConnectionRequestBetweenUsers(userAId, userBId);
        return details.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }


    @Operation(
            summary = "Get all established connections.",
            description = "Retrieves a paginated list of all users who have an ACCEPTED connection with the authenticated user.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved established connections."
                    )
            }
    )
    @GetMapping("/established")
    public ResponseEntity<Page<ConnectionResponse>> getEstablishedConnections(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            Pageable pageable
    ) {
        Page<ConnectionResponse> page = connectionService.getEstablishedConnections(customUserDetails.getUserId(), pageable);
        return ResponseEntity.ok(page);
    }


    @Operation(
            summary = "Disconnect/unfriend from a user.",
            description = "Deletes the established connection record between the authenticated user and the targetUserId.",
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Connection successfully deleted (No Content)."
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Connection was not established."
                    )
            }
    )
    @DeleteMapping("/disconnect/{targetUserId}")
    public ResponseEntity<Void> deleteConnection(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable Long targetUserId
    ) {
        connectionService.deleteConnection(customUserDetails.getUserId(), targetUserId);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Get all connection requests by status (Global).",
            description = "Retrieves a paginated list of ALL requests in the system matching the specified status. Requires ADMIN role.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved requests by status."
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden: Requires ADMIN role."
                    )
            }
    )
    @GetMapping("/all/status/{status}")
    public ResponseEntity<Page<ConnectionRequestDetails>> getRequestsByStatus(
            @PathVariable ConnectionRequestStatus status,
            Pageable pageable
    ) {
        Page<ConnectionRequestDetails> page = connectionService.getRequestsByStatus(status, pageable);
        return ResponseEntity.ok(page);
    }

    @Operation(
            summary = "Get all requests related to the user by two statuses.",
            description = "Retrieves a paginated list of requests where the authenticated user is either the sender or receiver, matching either status1 OR status2.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved user's requests."
                    )
            }
    )
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


    @Operation(
            summary = "Get received requests by status, sorted.",
            description = "Retrieves a paginated list of requests received by the authenticated user, filtered by status and sorted by creation date or response date.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved sorted received requests."
                    )
            }
    )
    @GetMapping("/received/status/{status}/sorted")
    public ResponseEntity<Page<ConnectionRequestDetails>> getReceivedRequestsByStatusSorted(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable ConnectionRequestStatus status,
            Pageable pageable
    ) {
        Page<ConnectionRequestDetails> page = connectionService.getReceivedRequestsByStatusSorted(customUserDetails.getUserId(), status, pageable);
        return ResponseEntity.ok(page);
    }


    @Operation(
            summary = "Get all connection requests sorted by creation date (Global).",
            description = "Retrieves ALL requests in the system, sorted by the date they were created. Requires ADMIN role.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved sorted global requests."
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden: Requires ADMIN role."
                    )
            }
    )
    @GetMapping("/all/sorted/created")
    public ResponseEntity<Page<ConnectionRequestDetails>> getAllRequestsSortedByCreation(Pageable pageable) {
        Page<ConnectionRequestDetails> page = connectionService.getAllRequestsSortedByCreation(pageable);
        return ResponseEntity.ok(page);
    }

    @Operation(
            summary = "Get all connection requests sorted by response date (Global).",
            description = "Retrieves ALL requests in the system that have a response date (ACCEPTED/REJECTED), sorted by that date. Requires ADMIN role.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved sorted global requests."
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden: Requires ADMIN role."
                    )
            }
    )
    @GetMapping("/all/sorted/responded")
    public ResponseEntity<Page<ConnectionRequestDetails>> getAllRequestsSortedByResponse(Pageable pageable) {
        Page<ConnectionRequestDetails> page = connectionService.getAllRequestsSortedByResponse(pageable);
        return ResponseEntity.ok(page);
    }
}