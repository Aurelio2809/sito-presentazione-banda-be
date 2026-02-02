package org.example.sitopresentazionebandabenew.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "gallery_photos")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GalleryPhoto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String src;

    @Column
    private String thumbnailSrc;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(length = 500)
    private String description;

    @Column(length = 200)
    private String location;

    @Column(name = "photo_year")
    private Integer photoYear;

    @Column(name = "photo_month")
    private Integer photoMonth;

    @Column(name = "photo_day")
    private Integer photoDay;

    @Column(nullable = false)
    private boolean favorite;

    private Integer displayOrder;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
