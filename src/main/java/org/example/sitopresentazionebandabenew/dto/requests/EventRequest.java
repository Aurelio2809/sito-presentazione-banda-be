package org.example.sitopresentazionebandabenew.dto.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventRequest {

    @NotBlank(message = "Titolo obbligatorio")
    @Size(max = 200, message = "Titolo massimo 200 caratteri")
    private String title;

    @NotNull(message = "Data evento obbligatoria")
    private LocalDate eventDate;

    private LocalTime eventTime;

    @Size(max = 200, message = "Luogo massimo 200 caratteri")
    private String location;

    @Size(max = 200, message = "Città massimo 200 caratteri")
    private String cityLine;

    @Size(max = 500, message = "Descrizione breve massimo 500 caratteri")
    private String shortDescription;

    private String fullDescription;

    private String bannerSrc;

    @NotBlank(message = "Tipo obbligatorio")
    private String type;

    @NotBlank(message = "Stato obbligatorio")
    private String status;

    private String attachmentLabel;

    private String attachmentHref;

    private List<String> tags;
}
