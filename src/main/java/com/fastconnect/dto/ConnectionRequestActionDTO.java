package com.fastconnect.dto;

import com.fastconnect.enums.ConnectionRequestStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConnectionRequestActionDTO {
    @NotNull
    private Long connectionRequestId;

    private ConnectionRequestStatus status;
}
