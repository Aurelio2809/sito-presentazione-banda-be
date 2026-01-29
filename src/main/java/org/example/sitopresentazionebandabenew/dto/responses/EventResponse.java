package org.example.sitopresentazionebandabenew.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventResponse {

    private Long id;
    private String title;
    private LocalDate eventDate;
    private LocalTime eventTime;
    private String location;
    private String cityLine;
    private String shortDescription;
    private String fullDescription;
    private String bannerSrc;
    private String type;
    private String status;
    private String attachmentLabel;
    private String attachmentHref;
    private List<String> tags;
}
