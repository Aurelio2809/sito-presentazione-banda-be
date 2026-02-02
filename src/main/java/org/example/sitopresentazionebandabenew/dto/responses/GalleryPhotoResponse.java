package org.example.sitopresentazionebandabenew.dto.responses;

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
    private Integer photoYear;
    private Integer photoMonth;
    private Integer photoDay;
    private boolean favorite;
    private Integer displayOrder;
}
