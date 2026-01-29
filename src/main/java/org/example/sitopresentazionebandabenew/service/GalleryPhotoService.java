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

    List<GalleryPhotoResponse> getFavorites();

    Page<GalleryPhotoResponse> getNonFavorites(Pageable pageable);

    GalleryPhotoResponse update(Long id, GalleryPhotoRequest request);

    GalleryPhotoResponse toggleFavorite(Long id);

    GalleryPhotoResponse updateDisplayOrder(Long id, Integer order);

    void delete(Long id);
}
