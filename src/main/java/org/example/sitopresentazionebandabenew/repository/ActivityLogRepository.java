package org.example.sitopresentazionebandabenew.repository;

import org.example.sitopresentazionebandabenew.entity.ActivityLog;
import org.example.sitopresentazionebandabenew.entity.ActivityLog.ActionType;
import org.example.sitopresentazionebandabenew.entity.ActivityLog.TargetType;
import org.example.sitopresentazionebandabenew.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {

    Page<ActivityLog> findByOrderByTimestampDesc(Pageable pageable);

    Page<ActivityLog> findByTargetTypeOrderByTimestampDesc(TargetType targetType, Pageable pageable);

    Page<ActivityLog> findByUserOrderByTimestampDesc(User user, Pageable pageable);

    Page<ActivityLog> findByActionOrderByTimestampDesc(ActionType action, Pageable pageable);

    @Query("SELECT a FROM ActivityLog a WHERE a.timestamp >= :since ORDER BY a.timestamp DESC")
    List<ActivityLog> findRecentActivity(@Param("since") LocalDateTime since);

    @Query("SELECT a FROM ActivityLog a WHERE a.targetType = :type AND a.targetId = :id ORDER BY a.timestamp DESC")
    List<ActivityLog> findByTarget(@Param("type") TargetType type, @Param("id") Long id);

    long countByTimestampAfter(LocalDateTime timestamp);
}
