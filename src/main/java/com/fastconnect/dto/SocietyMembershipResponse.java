package com.fastconnect.dto;

import com.fastconnect.enums.SocietyRoles;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SocietyMembershipResponse {

    private Long membershipId;
    private Long societyId;
    private String societyName; // Helpful for user's profile view
    private Long userId;
    private SocietyRoles societyRole;
    private LocalDateTime joinedAt;
    private Boolean active;
}