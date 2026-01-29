package org.example.sitopresentazionebandabenew.service.impl;

import org.example.sitopresentazionebandabenew.dto.requests.EventRequest;
import org.example.sitopresentazionebandabenew.dto.responses.EventResponse;
import org.example.sitopresentazionebandabenew.entity.ActivityLog.ActionType;
import org.example.sitopresentazionebandabenew.entity.ActivityLog.TargetType;
import org.example.sitopresentazionebandabenew.entity.Event;
import org.example.sitopresentazionebandabenew.entity.Event.EventStatus;
import org.example.sitopresentazionebandabenew.entity.Event.EventType;
import org.example.sitopresentazionebandabenew.entity.User;
import org.example.sitopresentazionebandabenew.exception.ResourceNotFoundException;
import org.example.sitopresentazionebandabenew.mapper.EventMapper;
import org.example.sitopresentazionebandabenew.repository.EventRepository;
import org.example.sitopresentazionebandabenew.service.ActivityLogService;
import org.example.sitopresentazionebandabenew.service.EventService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final ActivityLogService activityLogService;

    public EventServiceImpl(EventRepository eventRepository, EventMapper eventMapper, ActivityLogService activityLogService) {
        this.eventRepository = eventRepository;
        this.eventMapper = eventMapper;
        this.activityLogService = activityLogService;
    }

    @Override
    public EventResponse create(EventRequest request) {
        Event event = eventMapper.toEntity(request);
        event.setCreatedBy(getCurrentUser());
        
        Event savedEvent = eventRepository.save(event);

        TargetType targetType = savedEvent.getType() == EventType.EVENT ? TargetType.EVENT : TargetType.ANNOUNCEMENT;
        activityLogService.log(ActionType.CREATE, targetType, savedEvent.getId(), savedEvent.getTitle());

        return eventMapper.toResponse(savedEvent);
    }

    @Override
    @Transactional(readOnly = true)
    public EventResponse getById(Long id) {
        Event event = findEventOrThrow(id);
        return eventMapper.toResponse(event);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EventResponse> getAll(Pageable pageable) {
        return eventRepository.findAll(pageable).map(eventMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EventResponse> getByType(EventType type, Pageable pageable) {
        return eventRepository.findByType(type, pageable).map(eventMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EventResponse> getByStatus(EventStatus status, Pageable pageable) {
        return eventRepository.findByStatus(status, pageable).map(eventMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EventResponse> getByTypeAndStatus(EventType type, EventStatus status, Pageable pageable) {
        return eventRepository.findByTypeAndStatus(type, status, pageable).map(eventMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventResponse> getUpcomingEvents() {
        return eventMapper.toResponseList(
                eventRepository.findUpcomingPublished(EventType.EVENT, LocalDate.now())
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventResponse> getUpcomingAnnouncements() {
        return eventMapper.toResponseList(
                eventRepository.findUpcomingPublished(EventType.ANNOUNCEMENT, LocalDate.now())
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EventResponse> getPastEvents(Pageable pageable) {
        return eventRepository.findPastPublished(EventType.EVENT, LocalDate.now(), pageable)
                .map(eventMapper::toResponse);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<EventResponse> getPastEventsList() {
        return eventMapper.toResponseList(
                eventRepository.findPastPublishedList(EventType.EVENT, LocalDate.now())
        );
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<EventResponse> getPublicAll(EventType type, Pageable pageable) {
        if (type != null) {
            return eventRepository.findByTypeAndStatus(type, EventStatus.PUBLISHED, pageable)
                    .map(eventMapper::toResponse);
        }
        return eventRepository.findByStatus(EventStatus.PUBLISHED, pageable)
                .map(eventMapper::toResponse);
    }

    @Override
    public EventResponse update(Long id, EventRequest request) {
        Event event = findEventOrThrow(id);
        eventMapper.updateEntityFromRequest(request, event);
        
        Event updatedEvent = eventRepository.save(event);

        TargetType targetType = updatedEvent.getType() == EventType.EVENT ? TargetType.EVENT : TargetType.ANNOUNCEMENT;
        activityLogService.log(ActionType.UPDATE, targetType, updatedEvent.getId(), updatedEvent.getTitle());

        return eventMapper.toResponse(updatedEvent);
    }

    @Override
    public EventResponse publish(Long id) {
        Event event = findEventOrThrow(id);
        event.setStatus(EventStatus.PUBLISHED);
        
        Event updatedEvent = eventRepository.save(event);

        TargetType targetType = updatedEvent.getType() == EventType.EVENT ? TargetType.EVENT : TargetType.ANNOUNCEMENT;
        activityLogService.log(ActionType.PUBLISH, targetType, updatedEvent.getId(), updatedEvent.getTitle());

        return eventMapper.toResponse(updatedEvent);
    }

    @Override
    public EventResponse unpublish(Long id) {
        Event event = findEventOrThrow(id);
        event.setStatus(EventStatus.DRAFT);
        
        Event updatedEvent = eventRepository.save(event);

        TargetType targetType = updatedEvent.getType() == EventType.EVENT ? TargetType.EVENT : TargetType.ANNOUNCEMENT;
        activityLogService.log(ActionType.UNPUBLISH, targetType, updatedEvent.getId(), updatedEvent.getTitle());

        return eventMapper.toResponse(updatedEvent);
    }

    @Override
    public void delete(Long id) {
        Event event = findEventOrThrow(id);
        String title = event.getTitle();
        TargetType targetType = event.getType() == EventType.EVENT ? TargetType.EVENT : TargetType.ANNOUNCEMENT;

        eventRepository.delete(event);

        activityLogService.log(ActionType.DELETE, targetType, id, title);
    }

    private Event findEventOrThrow(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Evento", "id", id));
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            return (User) authentication.getPrincipal();
        }
        return null;
    }
}
