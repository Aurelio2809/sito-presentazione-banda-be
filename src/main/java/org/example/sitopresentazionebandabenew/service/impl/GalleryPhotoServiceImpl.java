package org.example.sitopresentazionebandabenew.service.impl;

import org.example.sitopresentazionebandabenew.dto.requests.GalleryPhotoRequest;
import org.example.sitopresentazionebandabenew.dto.responses.GalleryPhotoResponse;
import org.example.sitopresentazionebandabenew.entity.ActivityLog.ActionType;
import org.example.sitopresentazionebandabenew.entity.ActivityLog.TargetType;
import org.example.sitopresentazionebandabenew.entity.GalleryPhoto;
import org.example.sitopresentazionebandabenew.entity.User;
import org.example.sitopresentazionebandabenew.exception.ResourceNotFoundException;
import org.example.sitopresentazionebandabenew.mapper.GalleryPhotoMapper;
import org.example.sitopresentazionebandabenew.repository.GalleryPhotoRepository;
import org.example.sitopresentazionebandabenew.service.ActivityLogService;
import org.example.sitopresentazionebandabenew.service.FileStorageService;
import org.example.sitopresentazionebandabenew.service.GalleryPhotoService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@Transactional
public class GalleryPhotoServiceImpl implements GalleryPhotoService {

    private final GalleryPhotoRepository photoRepository;
    private final GalleryPhotoMapper photoMapper;
    private final FileStorageService fileStorageService;
    private final ActivityLogService activityLogService;

    public GalleryPhotoServiceImpl(
            GalleryPhotoRepository photoRepository,
            GalleryPhotoMapper photoMapper,
            FileStorageService fileStorageService,
            ActivityLogService activityLogService) {
        this.photoRepository = photoRepository;
        this.photoMapper = photoMapper;
        this.fileStorageService = fileStorageService;
        this.activityLogService = activityLogService;
    }

    @Override
    public GalleryPhotoResponse uploadPhoto(MultipartFile file, GalleryPhotoRequest metadata) {
        // Salva il file
        String filename = fileStorageService.storePhoto(file);
        String photoUrl = fileStorageService.getPhotoUrl(filename);

        // Crea l'entity
        GalleryPhoto photo = photoMapper.toEntity(metadata);
        photo.setSrc(photoUrl);
        photo.setCreatedBy(getCurrentUser());

        // Se è preferito, calcola l'ordine
        if (photo.isFavorite() && photo.getDisplayOrder() == null) {
            Integer maxOrder = photoRepository.findMaxDisplayOrder();
            photo.setDisplayOrder(maxOrder != null ? maxOrder + 1 : 1);
        }

        GalleryPhoto savedPhoto = photoRepository.save(photo);

        // Log attività
        activityLogService.log(
                ActionType.UPLOAD,
                TargetType.PHOTO,
                savedPhoto.getId(),
                savedPhoto.getTitle(),
                "File: " + filename
        );

        return photoMapper.toResponse(savedPhoto);
    }

    @Override
    @Transactional(readOnly = true)
    public GalleryPhotoResponse getById(Long id) {
        GalleryPhoto photo = findPhotoOrThrow(id);
        return photoMapper.toResponse(photo);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<GalleryPhotoResponse> getAll(Pageable pageable) {
        return photoRepository.findAllOrderByCreatedAtDesc(pageable)
                .map(photoMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GalleryPhotoResponse> getFavorites() {
        return photoMapper.toResponseList(
                photoRepository.findByFavoriteTrueOrderByDisplayOrderAsc()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Page<GalleryPhotoResponse> getNonFavorites(Pageable pageable) {
        return photoRepository.findByFavoriteFalse(pageable)
                .map(photoMapper::toResponse);
    }

    @Override
    public GalleryPhotoResponse update(Long id, GalleryPhotoRequest request) {
        GalleryPhoto photo = findPhotoOrThrow(id);
        
        // Aggiorna solo i metadati, non il file
        String currentSrc = photo.getSrc();
        photoMapper.updateEntityFromRequest(request, photo);
        photo.setSrc(currentSrc); // Mantieni il src originale

        GalleryPhoto updatedPhoto = photoRepository.save(photo);

        activityLogService.log(
                ActionType.UPDATE,
                TargetType.PHOTO,
                updatedPhoto.getId(),
                updatedPhoto.getTitle()
        );

        return photoMapper.toResponse(updatedPhoto);
    }

    @Override
    public GalleryPhotoResponse toggleFavorite(Long id) {
        GalleryPhoto photo = findPhotoOrThrow(id);
        photo.setFavorite(!photo.isFavorite());

        if (photo.isFavorite()) {
            // Assegna ordine
            Integer maxOrder = photoRepository.findMaxDisplayOrder();
            photo.setDisplayOrder(maxOrder != null ? maxOrder + 1 : 1);
        } else {
            // Rimuovi ordine
            photo.setDisplayOrder(null);
        }

        GalleryPhoto updatedPhoto = photoRepository.save(photo);

        activityLogService.log(
                ActionType.UPDATE,
                TargetType.PHOTO,
                updatedPhoto.getId(),
                updatedPhoto.getTitle(),
                photo.isFavorite() ? "Aggiunto ai preferiti" : "Rimosso dai preferiti"
        );

        return photoMapper.toResponse(updatedPhoto);
    }

    @Override
    public GalleryPhotoResponse updateDisplayOrder(Long id, Integer order) {
        GalleryPhoto photo = findPhotoOrThrow(id);
        photo.setDisplayOrder(order);
        
        GalleryPhoto updatedPhoto = photoRepository.save(photo);
        return photoMapper.toResponse(updatedPhoto);
    }

    @Override
    public void delete(Long id) {
        GalleryPhoto photo = findPhotoOrThrow(id);
        String title = photo.getTitle();

        // Estrai il filename dal src
        String src = photo.getSrc();
        if (src != null && src.contains("/")) {
            String filename = src.substring(src.lastIndexOf("/") + 1);
            fileStorageService.deletePhoto(filename);
        }

        photoRepository.delete(photo);

        activityLogService.log(
                ActionType.DELETE,
                TargetType.PHOTO,
                id,
                title
        );
    }

    private GalleryPhoto findPhotoOrThrow(Long id) {
        return photoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Foto", "id", id));
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            return (User) authentication.getPrincipal();
        }
        return null;
    }
}
