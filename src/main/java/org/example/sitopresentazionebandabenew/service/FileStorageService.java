package org.example.sitopresentazionebandabenew.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {

    String storePhoto(MultipartFile file);

    Resource loadPhotoAsResource(String filename);

    Resource loadThumbnailAsResource(String filename);

    void deletePhoto(String filename);

    boolean photoExists(String filename);

    String getPhotoUrl(String filename);

    String getThumbnailUrl(String filename);

    /**
     * Genera una thumbnail per un file esistente.
     * @param filename il nome del file originale
     * @return true se la thumbnail è stata generata con successo, false altrimenti
     */
    boolean generateThumbnailForExistingFile(String filename);
}
