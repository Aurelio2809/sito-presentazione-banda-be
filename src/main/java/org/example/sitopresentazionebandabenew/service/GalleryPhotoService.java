package org.example.sitopresentazionebandabenew.service;

import org.example.sitopresentazionebandabenew.dto.requests.GalleryPhotoRequest;
import org.example.sitopresentazionebandabenew.dto.responses.GalleryPhotoResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface GalleryPhotoService {

    GalleryPhotoResponse uploadPhoto(MultipartFile file, GalleryPhotoRequest metadata);

    GalleryPhotoResponse getById(Long id);

    Page<GalleryPhotoResponse> getAll(Pageable pageable);

    /**
     * Foto pubbliche con ordinamento: "order" = per indice (displayOrder), "date" = per data creazione.
     */
    Page<GalleryPhotoResponse> getPublicPhotos(Pageable pageable, String sortBy);

    List<GalleryPhotoResponse> getFavorites();

    Page<GalleryPhotoResponse> getNonFavorites(Pageable pageable);

    GalleryPhotoResponse update(Long id, GalleryPhotoRequest request);

    GalleryPhotoResponse toggleFavorite(Long id);

    GalleryPhotoResponse updateDisplayOrder(Long id, Integer order);

    void delete(Long id);

    /**
     * Genera le thumbnail per tutte le foto che non le hanno.
     * @return mappa con risultati: "processed", "success", "failed"
     */
    java.util.Map<String, Integer> generateMissingThumbnails();

    /**
     * Rigenera le thumbnail per tutte le foto (elimina le esistenti e rigenera).
     * Utile dopo aver aumentato THUMBNAIL_WIDTH per avere thumbnail più grandi.
     * @return mappa con risultati: "processed", "success", "failed"
     */
    java.util.Map<String, Integer> regenerateAllThumbnails();
}
