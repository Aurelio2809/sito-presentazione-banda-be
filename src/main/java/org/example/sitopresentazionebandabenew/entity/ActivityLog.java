package org.example.sitopresentazionebandabenew.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "activity_logs", indexes = {
    @Index(name = "idx_activity_timestamp", columnList = "timestamp"),
    @Index(name = "idx_activity_target_type", columnList = "targetType"),
    @Index(name = "idx_activity_user", columnList = "user_id")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ActionType action;

    @Column(nullable = false, length = 200)
    private String targetName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TargetType targetType;

    private Long targetId;

    @Column(length = 500)
    private String details;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @PrePersist
    protected void onCreate() {
        if (timestamp == null) {
            timestamp = LocalDateTime.now();
        }
    }

    public enum ActionType {
        CREATE,
        UPDATE,
        DELETE,
        PUBLISH,
        UNPUBLISH,
        ARCHIVE,
        READ,
        UPLOAD,
        LOGIN,
        LOGOUT
    }

    public enum TargetType {
        PHOTO,
        EVENT,
        ANNOUNCEMENT,
        MESSAGE,
        USER,
        SETTINGS
    }
}
