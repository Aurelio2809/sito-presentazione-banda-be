package org.example.sitopresentazionebandabenew.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageResponse {

    private Long id;
    private String senderName;
    private String senderEmail;
    private String subject;
    private String content;
    private boolean read;
    private LocalDateTime receivedAt;
    private LocalDateTime readAt;
}
