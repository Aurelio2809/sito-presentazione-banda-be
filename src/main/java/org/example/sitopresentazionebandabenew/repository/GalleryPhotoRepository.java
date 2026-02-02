package org.example.sitopresentazionebandabenew.repository;

import org.example.sitopresentazionebandabenew.entity.GalleryPhoto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GalleryPhotoRepository extends JpaRepository<GalleryPhoto, Long> {

    long countByFavoriteTrue();

    List<GalleryPhoto> findByFavoriteTrue();

    List<GalleryPhoto> findByFavoriteTrueOrderByDisplayOrderAsc();

    Page<GalleryPhoto> findByFavoriteFalse(Pageable pageable);

    @Query("SELECT p FROM GalleryPhoto p ORDER BY p.createdAt DESC")
    Page<GalleryPhoto> findAllOrderByCreatedAtDesc(Pageable pageable);

    /** Ordine per indice (displayOrder), nulli in coda, poi per data creazione. */
    @Query("SELECT p FROM GalleryPhoto p ORDER BY COALESCE(p.displayOrder, 999999) ASC, p.createdAt DESC")
    Page<GalleryPhoto> findAllOrderByDisplayOrderAsc(Pageable pageable);

    @Query("SELECT MAX(p.displayOrder) FROM GalleryPhoto p WHERE p.favorite = true")
    Integer findMaxDisplayOrder();

    @Query("SELECT p FROM GalleryPhoto p WHERE p.thumbnailSrc IS NULL")
    List<GalleryPhoto> findPhotosWithoutThumbnail();
}
