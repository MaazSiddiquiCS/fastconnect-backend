package com.fastconnect.dto;

import com.fastconnect.validation.ValidNUEmail;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConnectionRequestSendDTO {

    @NotNull
    @Email(message = "Must be a valid email format")
    @ValidNUEmail
    private String senderEmail;

    @NotNull
    @Email(message = "Must be a valid email format")
    @ValidNUEmail
    private String receiverEmail;

}
