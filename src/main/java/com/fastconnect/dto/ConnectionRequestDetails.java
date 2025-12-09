package com.fastconnect.dto;

import com.fastconnect.enums.ConnectionRequestStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConnectionRequestDetails {
    private Long connectionRequestId;
    private ConnectionRequestStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime respondedAt;
    private Long senderId;
    private String senderFullName;
    private Long receiverId;
    private String receiverFullName;
}
