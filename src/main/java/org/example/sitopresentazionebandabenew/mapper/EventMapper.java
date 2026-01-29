package org.example.sitopresentazionebandabenew.mapper;

import org.example.sitopresentazionebandabenew.dto.requests.EventRequest;
import org.example.sitopresentazionebandabenew.dto.responses.EventResponse;
import org.example.sitopresentazionebandabenew.entity.Event;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Mapper(componentModel = "spring")
public interface EventMapper {

    @Mapping(target = "type", expression = "java(event.getType().name())")
    @Mapping(target = "status", expression = "java(event.getStatus().name())")
    @Mapping(target = "tags", source = "tags", qualifiedByName = "tagsToList")
    EventResponse toResponse(Event event);

    List<EventResponse> toResponseList(List<Event> events);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "type", expression = "java(Event.EventType.valueOf(request.getType()))")
    @Mapping(target = "status", expression = "java(Event.EventStatus.valueOf(request.getStatus()))")
    @Mapping(target = "tags", source = "tags", qualifiedByName = "listToTags")
    Event toEntity(EventRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "type", expression = "java(Event.EventType.valueOf(request.getType()))")
    @Mapping(target = "status", expression = "java(Event.EventStatus.valueOf(request.getStatus()))")
    @Mapping(target = "tags", source = "tags", qualifiedByName = "listToTags")
    void updateEntityFromRequest(EventRequest request, @MappingTarget Event event);

    @Named("tagsToList")
    default List<String> tagsToList(String tags) {
        if (tags == null || tags.isBlank()) {
            return Collections.emptyList();
        }
        return Arrays.asList(tags.split(","));
    }

    @Named("listToTags")
    default String listToTags(List<String> tags) {
        if (tags == null || tags.isEmpty()) {
            return null;
        }
        return String.join(",", tags);
    }
}
