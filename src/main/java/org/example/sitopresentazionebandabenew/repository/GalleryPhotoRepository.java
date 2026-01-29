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

    List<GalleryPhoto> findByFavoriteTrue();

    List<GalleryPhoto> findByFavoriteTrueOrderByDisplayOrderAsc();

    Page<GalleryPhoto> findByFavoriteFalse(Pageable pageable);

    @Query("SELECT p FROM GalleryPhoto p ORDER BY p.createdAt DESC")
    Page<GalleryPhoto> findAllOrderByCreatedAtDesc(Pageable pageable);

    @Query("SELECT MAX(p.displayOrder) FROM GalleryPhoto p WHERE p.favorite = true")
    Integer findMaxDisplayOrder();
}
