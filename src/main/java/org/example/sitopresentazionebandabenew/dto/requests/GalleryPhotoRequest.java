package org.example.sitopresentazionebandabenew.dto.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GalleryPhotoRequest {

    // src viene generato automaticamente durante l'upload
    private String src;

    @NotBlank(message = "Titolo obbligatorio")
    @Size(max = 200, message = "Titolo massimo 200 caratteri")
    private String title;

    @Size(max = 500, message = "Descrizione massimo 500 caratteri")
    private String description;

    @Size(max = 200, message = "Luogo massimo 200 caratteri")
    private String location;

    private Integer photoYear;
    private Integer photoMonth;
    private Integer photoDay;

    private boolean favorite;

    private Integer displayOrder;
}
