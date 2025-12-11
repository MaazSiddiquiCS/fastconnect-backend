package com.fastconnect.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SocietyFollowersResponse {

    private Long societyFollowerId;
    private Long societyId;
    private String societyName;
    private Long userId;
    private String userEmail;
    private LocalDateTime followedAt;
}