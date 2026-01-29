package org.example.sitopresentazionebandabenew.dto.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordRequest {
    
    @NotBlank(message = "La password attuale è obbligatoria")
    private String currentPassword;
    
    @NotBlank(message = "La nuova password è obbligatoria")
    @Size(min = 6, message = "La nuova password deve avere almeno 6 caratteri")
    private String newPassword;
}
