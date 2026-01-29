package org.example.sitopresentazionebandabenew.controller;

import jakarta.validation.Valid;
import org.example.sitopresentazionebandabenew.dto.requests.MessageRequest;
import org.example.sitopresentazionebandabenew.dto.responses.MessageResponse;
import org.example.sitopresentazionebandabenew.service.MessageService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    // ==================== ENDPOINT PUBBLICO (Form contatti) ====================

    @PostMapping
    public ResponseEntity<MessageResponse> sendMessage(@Valid @RequestBody MessageRequest request) {
        return ResponseEntity.ok(messageService.create(request));
    }

    // ==================== ENDPOINT PROTETTI (ADMIN) ====================

    @GetMapping
    public ResponseEntity<Page<MessageResponse>> getAllMessages(
            @RequestParam(required = false) String filter,
            @PageableDefault(size = 20) Pageable pageable) {
        
        if ("unread".equalsIgnoreCase(filter)) {
            return ResponseEntity.ok(messageService.getUnread(pageable));
        } else if ("read".equalsIgnoreCase(filter)) {
            return ResponseEntity.ok(messageService.getRead(pageable));
        }
        return ResponseEntity.ok(messageService.getAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MessageResponse> getMessage(@PathVariable Long id) {
        return ResponseEntity.ok(messageService.getById(id));
    }

    @GetMapping("/unread/count")
    public ResponseEntity<Map<String, Long>> getUnreadCount() {
        return ResponseEntity.ok(Map.of("count", messageService.countUnread()));
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<MessageResponse> markAsRead(@PathVariable Long id) {
        return ResponseEntity.ok(messageService.markAsRead(id));
    }

    @PatchMapping("/read-all")
    public ResponseEntity<Map<String, Integer>> markAllAsRead() {
        int count = messageService.markAllAsRead();
        return ResponseEntity.ok(Map.of("markedAsRead", count));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMessage(@PathVariable Long id) {
        messageService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
