package org.example.sitopresentazionebandabenew.service;

import java.util.List;
import org.example.sitopresentazionebandabenew.dto.responses.ActivityLogResponse;
import org.example.sitopresentazionebandabenew.entity.ActivityLog.ActionType;
import org.example.sitopresentazionebandabenew.entity.ActivityLog.TargetType;
import org.example.sitopresentazionebandabenew.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ActivityLogService {

    void log(User user, ActionType action, TargetType targetType, Long targetId, String targetName, String details);

    void log(ActionType action, TargetType targetType, Long targetId, String targetName);

    void log(ActionType action, TargetType targetType, Long targetId, String targetName, String details);

    Page<ActivityLogResponse> getAll(Pageable pageable);

    Page<ActivityLogResponse> getByTargetType(TargetType targetType, Pageable pageable);

    List<ActivityLogResponse> getRecentActivity(int hours);

    List<ActivityLogResponse> getActivityForTarget(TargetType targetType, Long targetId);
}
