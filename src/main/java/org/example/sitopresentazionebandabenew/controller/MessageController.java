package org.example.sitopresentazionebandabenew.controller;

import jakarta.validation.Valid;
import java.util.Map;
import org.example.sitopresentazionebandabenew.dto.requests.MessageRequest;
import org.example.sitopresentazionebandabenew.service.ContactMailService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private final ContactMailService contactMailService;

    public MessageController(ContactMailService contactMailService) {
        this.contactMailService = contactMailService;
    }

    // ==================== ENDPOINT PUBBLICO (Form contatti) ====================

    @PostMapping
    public ResponseEntity<Map<String, String>> sendMessage(@Valid @RequestBody MessageRequest request) {
        contactMailService.send(request);
        return ResponseEntity.accepted().body(Map.of("status", "sent"));
    }
}
