package org.example.sitopresentazionebandabenew.mapper;

import org.example.sitopresentazionebandabenew.dto.responses.ActivityLogResponse;
import org.example.sitopresentazionebandabenew.entity.ActivityLog;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ActivityLogMapper {

    @Mapping(target = "username", source = "user.username")
    @Mapping(target = "action", expression = "java(activityLog.getAction().name())")
    @Mapping(target = "targetType", expression = "java(activityLog.getTargetType().name())")
    ActivityLogResponse toResponse(ActivityLog activityLog);

    List<ActivityLogResponse> toResponseList(List<ActivityLog> activityLogs);
}
