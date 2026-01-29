package org.example.sitopresentazionebandabenew.service;

import org.example.sitopresentazionebandabenew.dto.requests.EventRequest;
import org.example.sitopresentazionebandabenew.dto.responses.EventResponse;
import org.example.sitopresentazionebandabenew.entity.Event.EventStatus;
import org.example.sitopresentazionebandabenew.entity.Event.EventType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface EventService {

    EventResponse create(EventRequest request);

    EventResponse getById(Long id);

    Page<EventResponse> getAll(Pageable pageable);

    Page<EventResponse> getByType(EventType type, Pageable pageable);

    Page<EventResponse> getByStatus(EventStatus status, Pageable pageable);

    Page<EventResponse> getByTypeAndStatus(EventType type, EventStatus status, Pageable pageable);

    List<EventResponse> getUpcomingEvents();

    List<EventResponse> getUpcomingAnnouncements();

    Page<EventResponse> getPastEvents(Pageable pageable);
    
    List<EventResponse> getPastEventsList();
    
    Page<EventResponse> getPublicAll(EventType type, Pageable pageable);

    EventResponse update(Long id, EventRequest request);

    EventResponse publish(Long id);

    EventResponse unpublish(Long id);

    void delete(Long id);
}
