package com.fastconnect.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConnectionResponse {
    private Long connectionId;
    private LocalDateTime connectedAt;
    private Long userId;
    private String userFullName;
    private String userProfilePic;
}
