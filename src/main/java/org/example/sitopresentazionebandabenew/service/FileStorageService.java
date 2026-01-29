package org.example.sitopresentazionebandabenew.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {

    String storePhoto(MultipartFile file);

    Resource loadPhotoAsResource(String filename);

    void deletePhoto(String filename);

    boolean photoExists(String filename);

    String getPhotoUrl(String filename);
}
