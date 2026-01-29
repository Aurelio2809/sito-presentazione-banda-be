package org.example.sitopresentazionebandabenew.service.impl;

import org.example.sitopresentazionebandabenew.dto.responses.ActivityLogResponse;
import org.example.sitopresentazionebandabenew.entity.ActivityLog;
import org.example.sitopresentazionebandabenew.entity.ActivityLog.ActionType;
import org.example.sitopresentazionebandabenew.entity.ActivityLog.TargetType;
import org.example.sitopresentazionebandabenew.entity.User;
import org.example.sitopresentazionebandabenew.mapper.ActivityLogMapper;
import org.example.sitopresentazionebandabenew.repository.ActivityLogRepository;
import org.example.sitopresentazionebandabenew.service.ActivityLogService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class ActivityLogServiceImpl implements ActivityLogService {

    private final ActivityLogRepository activityLogRepository;
    private final ActivityLogMapper activityLogMapper;

    public ActivityLogServiceImpl(ActivityLogRepository activityLogRepository, ActivityLogMapper activityLogMapper) {
        this.activityLogRepository = activityLogRepository;
        this.activityLogMapper = activityLogMapper;
    }

    @Override
    public void log(User user, ActionType action, TargetType targetType, Long targetId, String targetName, String details) {
        ActivityLog log = ActivityLog.builder()
                .user(user)
                .action(action)
                .targetType(targetType)
                .targetId(targetId)
                .targetName(targetName)
                .details(details)
                .timestamp(LocalDateTime.now())
                .build();
        activityLogRepository.save(log);
    }

    @Override
    public void log(ActionType action, TargetType targetType, Long targetId, String targetName) {
        log(getCurrentUser(), action, targetType, targetId, targetName, null);
    }

    @Override
    public void log(ActionType action, TargetType targetType, Long targetId, String targetName, String details) {
        log(getCurrentUser(), action, targetType, targetId, targetName, details);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ActivityLogResponse> getAll(Pageable pageable) {
        return activityLogRepository.findByOrderByTimestampDesc(pageable)
                .map(activityLogMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ActivityLogResponse> getByTargetType(TargetType targetType, Pageable pageable) {
        return activityLogRepository.findByTargetTypeOrderByTimestampDesc(targetType, pageable)
                .map(activityLogMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ActivityLogResponse> getRecentActivity(int hours) {
        LocalDateTime since = LocalDateTime.now().minusHours(hours);
        return activityLogMapper.toResponseList(activityLogRepository.findRecentActivity(since));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ActivityLogResponse> getActivityForTarget(TargetType targetType, Long targetId) {
        return activityLogMapper.toResponseList(activityLogRepository.findByTarget(targetType, targetId));
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            return (User) authentication.getPrincipal();
        }
        return null;
    }
}
