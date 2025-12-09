package com.fastconnect.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostRequest {

    @NotBlank(message = "Content is required")
    private String content;

    @URL(message = "Media URL must be valid")
    private String mediaUrl;
}