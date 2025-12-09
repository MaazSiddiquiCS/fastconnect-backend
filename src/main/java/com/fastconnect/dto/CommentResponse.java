package com.fastconnect.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponse {
    private Long commentId;
    private String content;
    private LocalDateTime createdAt;

    private Long userId;
    private String fullName;
    private String profilePic;
}