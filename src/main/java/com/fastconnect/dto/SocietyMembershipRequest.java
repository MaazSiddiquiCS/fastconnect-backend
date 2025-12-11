package com.fastconnect.dto;

import com.fastconnect.enums.SocietyRoles;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SocietyMembershipRequest {

    @NotNull(message = "Society ID is required")
    private Long societyId;

    // This is optional; mainly used for ADMINs to set initial roles or change existing ones.
    private SocietyRoles societyRole;
}