package org.example.sitopresentazionebandabenew.dto.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageRequest {

    @NotBlank(message = "Nome obbligatorio")
    @Size(max = 100, message = "Nome massimo 100 caratteri")
    private String senderName;

    @NotBlank(message = "Email obbligatoria")
    @Email(message = "Email non valida")
    @Size(max = 100, message = "Email massimo 100 caratteri")
    private String senderEmail;

    @NotBlank(message = "Oggetto obbligatorio")
    @Size(max = 200, message = "Oggetto massimo 200 caratteri")
    private String subject;

    @NotBlank(message = "Messaggio obbligatorio")
    private String content;
}
