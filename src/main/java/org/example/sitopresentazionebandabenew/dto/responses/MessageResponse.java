package org.example.sitopresentazionebandabenew.dto.responses;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
