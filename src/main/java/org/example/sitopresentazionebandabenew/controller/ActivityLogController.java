package org.example.sitopresentazionebandabenew.controller;

import org.example.sitopresentazionebandabenew.dto.responses.ActivityLogResponse;
import org.example.sitopresentazionebandabenew.entity.ActivityLog.TargetType;
import org.example.sitopresentazionebandabenew.service.ActivityLogService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/activity-log")
public class ActivityLogController {

    private final ActivityLogService activityLogService;

    public ActivityLogController(ActivityLogService activityLogService) {
        this.activityLogService = activityLogService;
    }

    @GetMapping
    public ResponseEntity<Page<ActivityLogResponse>> getAllActivity(
            @RequestParam(required = false) TargetType targetType,
            @PageableDefault(size = 50) Pageable pageable) {
        
        if (targetType != null) {
            return ResponseEntity.ok(activityLogService.getByTargetType(targetType, pageable));
        }
        return ResponseEntity.ok(activityLogService.getAll(pageable));
    }

    @GetMapping("/recent")
    public ResponseEntity<List<ActivityLogResponse>> getRecentActivity(
            @RequestParam(defaultValue = "24") int hours) {
        return ResponseEntity.ok(activityLogService.getRecentActivity(hours));
    }

    @GetMapping("/target/{targetType}/{targetId}")
    public ResponseEntity<List<ActivityLogResponse>> getActivityForTarget(
            @PathVariable TargetType targetType,
            @PathVariable Long targetId) {
        return ResponseEntity.ok(activityLogService.getActivityForTarget(targetType, targetId));
    }
}
