package org.example.sitopresentazionebandabenew.controller;

import jakarta.validation.Valid;
import org.example.sitopresentazionebandabenew.dto.requests.EventRequest;
import org.example.sitopresentazionebandabenew.dto.responses.EventResponse;
import org.example.sitopresentazionebandabenew.entity.Event.EventStatus;
import org.example.sitopresentazionebandabenew.entity.Event.EventType;
import org.example.sitopresentazionebandabenew.service.EventService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    // ==================== ENDPOINT PUBBLICI ====================

    @GetMapping("/public/upcoming")
    public ResponseEntity<List<EventResponse>> getUpcomingEvents() {
        return ResponseEntity.ok(eventService.getUpcomingEvents());
    }

    @GetMapping("/public/announcements")
    public ResponseEntity<List<EventResponse>> getUpcomingAnnouncements() {
        return ResponseEntity.ok(eventService.getUpcomingAnnouncements());
    }

    @GetMapping("/public/past")
    public ResponseEntity<Page<EventResponse>> getPastEvents(
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(eventService.getPastEvents(pageable));
    }

    @GetMapping("/public/{id}")
    public ResponseEntity<EventResponse> getPublicEvent(@PathVariable Long id) {
        return ResponseEntity.ok(eventService.getById(id));
    }

    // ==================== ENDPOINT PROTETTI (ADMIN) ====================

    @GetMapping
    public ResponseEntity<Page<EventResponse>> getAllEvents(
            @RequestParam(required = false) EventType type,
            @RequestParam(required = false) EventStatus status,
            @PageableDefault(size = 20) Pageable pageable) {
        
        if (type != null && status != null) {
            return ResponseEntity.ok(eventService.getByTypeAndStatus(type, status, pageable));
        } else if (type != null) {
            return ResponseEntity.ok(eventService.getByType(type, pageable));
        } else if (status != null) {
            return ResponseEntity.ok(eventService.getByStatus(status, pageable));
        }
        return ResponseEntity.ok(eventService.getAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventResponse> getEvent(@PathVariable Long id) {
        return ResponseEntity.ok(eventService.getById(id));
    }

    @PostMapping
    public ResponseEntity<EventResponse> createEvent(@Valid @RequestBody EventRequest request) {
        return ResponseEntity.ok(eventService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EventResponse> updateEvent(
            @PathVariable Long id,
            @Valid @RequestBody EventRequest request) {
        return ResponseEntity.ok(eventService.update(id, request));
    }

    @PatchMapping("/{id}/publish")
    public ResponseEntity<EventResponse> publishEvent(@PathVariable Long id) {
        return ResponseEntity.ok(eventService.publish(id));
    }

    @PatchMapping("/{id}/unpublish")
    public ResponseEntity<EventResponse> unpublishEvent(@PathVariable Long id) {
        return ResponseEntity.ok(eventService.unpublish(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        eventService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
