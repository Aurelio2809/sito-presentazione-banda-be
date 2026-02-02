package org.example.sitopresentazionebandabenew.dto.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GalleryPhotoResponse {

    private Long id;
    private String src;
    private String thumbnailSrc;
    private String title;
    private String description;
    private String location;

    @JsonProperty("photoYear")
    private Integer photoYear;

    @JsonProperty("photoMonth")
    private Integer photoMonth;

    @JsonProperty("photoDay")
    private Integer photoDay;

    private boolean favorite;
    private Integer displayOrder;
}
