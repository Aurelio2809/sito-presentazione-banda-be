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
public class ActivityLogResponse {

    private Long id;
    private String username;
    private String action;
    private String targetName;
    private String targetType;
    private Long targetId;
    private String details;
    private LocalDateTime timestamp;
}
