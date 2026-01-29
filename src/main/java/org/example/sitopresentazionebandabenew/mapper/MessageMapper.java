package org.example.sitopresentazionebandabenew.mapper;

import org.example.sitopresentazionebandabenew.dto.requests.MessageRequest;
import org.example.sitopresentazionebandabenew.dto.responses.MessageResponse;
import org.example.sitopresentazionebandabenew.entity.Message;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MessageMapper {

    MessageResponse toResponse(Message message);

    List<MessageResponse> toResponseList(List<Message> messages);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "read", ignore = true)
    @Mapping(target = "receivedAt", ignore = true)
    @Mapping(target = "readAt", ignore = true)
    @Mapping(target = "readBy", ignore = true)
    Message toEntity(MessageRequest request);
}
