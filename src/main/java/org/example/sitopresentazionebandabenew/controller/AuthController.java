package org.example.sitopresentazionebandabenew.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.example.sitopresentazionebandabenew.dto.requests.LoginRequest;
import org.example.sitopresentazionebandabenew.dto.responses.UserResponse;
import org.example.sitopresentazionebandabenew.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<UserResponse> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest) {
        
        UserResponse user = authService.login(request);
        
        // Crea la sessione HTTP
        HttpSession session = httpRequest.getSession(true);
        session.setAttribute("user", user);
        
        return ResponseEntity.ok(user);
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser() {
        return ResponseEntity.ok(authService.getCurrentUser());
    }
}
