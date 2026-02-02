package org.example.sitopresentazionebandabenew.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import org.example.sitopresentazionebandabenew.dto.requests.GalleryPhotoRequest;
import org.example.sitopresentazionebandabenew.dto.responses.GalleryPhotoResponse;
import org.example.sitopresentazionebandabenew.service.FileStorageService;
import org.example.sitopresentazionebandabenew.service.GalleryPhotoService;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/gallery")
public class GalleryController {

    private final GalleryPhotoService galleryPhotoService;
    private final FileStorageService fileStorageService;
    private final ObjectMapper objectMapper;

    public GalleryController(GalleryPhotoService galleryPhotoService, FileStorageService fileStorageService, ObjectMapper objectMapper) {
        this.galleryPhotoService = galleryPhotoService;
        this.fileStorageService = fileStorageService;
        this.objectMapper = objectMapper;
    }

    // ==================== ENDPOINT PUBBLICI ====================

    @GetMapping("/public")
    public ResponseEntity<Page<GalleryPhotoResponse>> getPublicPhotos(
            @PageableDefault(size = 12) Pageable pageable,
            @RequestParam(required = false, defaultValue = "order") String sort) {
        return ResponseEntity.ok(galleryPhotoService.getPublicPhotos(pageable, sort));
    }

    @GetMapping("/public/favorites")
    public ResponseEntity<List<GalleryPhotoResponse>> getPublicFavorites() {
        return ResponseEntity.ok(galleryPhotoService.getFavorites());
    }

    @GetMapping("/photos/{filename:.+}")
    public ResponseEntity<Resource> servePhoto(@PathVariable String filename) {
        Resource resource = fileStorageService.loadPhotoAsResource(filename);
        
        String contentType = determineContentType(filename);
        
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CACHE_CONTROL, "max-age=31536000")
                .body(resource);
    }

    @GetMapping("/photos/thumb/{filename:.+}")
    public ResponseEntity<Resource> serveThumbnail(@PathVariable String filename) {
        Resource resource = fileStorageService.loadThumbnailAsResource(filename);
        
        String contentType = determineContentType(filename);
        
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CACHE_CONTROL, "max-age=31536000")
                .body(resource);
    }

    // ==================== ENDPOINT PROTETTI (ADMIN) ====================

    @GetMapping
    public ResponseEntity<Page<GalleryPhotoResponse>> getAllPhotos(
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(galleryPhotoService.getAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<GalleryPhotoResponse> getPhoto(@PathVariable Long id) {
        return ResponseEntity.ok(galleryPhotoService.getById(id));
    }

    @GetMapping("/favorites")
    public ResponseEntity<List<GalleryPhotoResponse>> getFavorites() {
        return ResponseEntity.ok(galleryPhotoService.getFavorites());
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<GalleryPhotoResponse> uploadPhoto(
            @RequestPart("file") MultipartFile file,
            @RequestPart("metadata") MultipartFile metadataPart) throws java.io.IOException {
        String metadataJson = new String(metadataPart.getBytes(), java.nio.charset.StandardCharsets.UTF_8);
        GalleryPhotoRequest metadata = objectMapper.readValue(metadataJson, GalleryPhotoRequest.class);
        GalleryPhotoResponse response = galleryPhotoService.uploadPhoto(file, metadata);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<GalleryPhotoResponse> updatePhoto(
            @PathVariable Long id,
            @Valid @RequestBody GalleryPhotoRequest request) {
        return ResponseEntity.ok(galleryPhotoService.update(id, request));
    }

    @PatchMapping("/{id}/favorite")
    public ResponseEntity<GalleryPhotoResponse> toggleFavorite(@PathVariable Long id) {
        return ResponseEntity.ok(galleryPhotoService.toggleFavorite(id));
    }

    @PatchMapping("/{id}/order")
    public ResponseEntity<GalleryPhotoResponse> updateOrder(
            @PathVariable Long id,
            @RequestParam Integer order) {
        return ResponseEntity.ok(galleryPhotoService.updateDisplayOrder(id, order));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePhoto(@PathVariable Long id) {
        galleryPhotoService.delete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Genera le thumbnail per tutte le foto esistenti che non le hanno.
     * Questo endpoint è utile per migrare foto caricate prima dell'implementazione delle thumbnail.
     */
    @PostMapping("/admin/generate-thumbnails")
    public ResponseEntity<Map<String, Integer>> generateMissingThumbnails() {
        Map<String, Integer> result = galleryPhotoService.generateMissingThumbnails();
        return ResponseEntity.ok(result);
    }

    /**
     * Rigenera le thumbnail per tutte le foto (elimina le esistenti e rigenera con le dimensioni attuali).
     * Utile dopo aver aumentato THUMBNAIL_WIDTH per migliorare la qualità in griglia.
     */
    @PostMapping("/admin/regenerate-thumbnails")
    public ResponseEntity<Map<String, Integer>> regenerateAllThumbnails() {
        Map<String, Integer> result = galleryPhotoService.regenerateAllThumbnails();
        return ResponseEntity.ok(result);
    }

    private String determineContentType(String filename) {
        String extension = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
        return switch (extension) {
            case "jpg", "jpeg" -> "image/jpeg";
            case "png" -> "image/png";
            case "gif" -> "image/gif";
            case "webp" -> "image/webp";
            default -> "application/octet-stream";
        };
    }
}
