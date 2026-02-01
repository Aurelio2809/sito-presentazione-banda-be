package org.example.sitopresentazionebandabenew.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

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
    private LocalDate photoDate;
    private boolean favorite;
    private Integer displayOrder;
}
