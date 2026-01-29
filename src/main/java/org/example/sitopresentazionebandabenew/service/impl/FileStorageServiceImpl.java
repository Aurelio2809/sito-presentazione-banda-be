package org.example.sitopresentazionebandabenew.service.impl;

import jakarta.annotation.PostConstruct;
import org.example.sitopresentazionebandabenew.config.StorageProperties;
import org.example.sitopresentazionebandabenew.exception.FileStorageException;
import org.example.sitopresentazionebandabenew.exception.ResourceNotFoundException;
import org.example.sitopresentazionebandabenew.service.FileStorageService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageServiceImpl implements FileStorageService {

    private final Path photosStorageLocation;
    private final StorageProperties storageProperties;

    public FileStorageServiceImpl(StorageProperties storageProperties) {
        this.storageProperties = storageProperties;
        this.photosStorageLocation = Paths.get(storageProperties.getPhotosPath()).toAbsolutePath().normalize();
    }

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(photosStorageLocation);
        } catch (IOException e) {
            throw new FileStorageException("Impossibile creare la directory di storage per le foto", e);
        }
    }

    @Override
    public String storePhoto(MultipartFile file) {
        if (file.isEmpty()) {
            throw new FileStorageException("File vuoto");
        }

        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
        
        // Valida estensione
        String extension = getFileExtension(originalFilename);
        if (!isAllowedExtension(extension)) {
            throw new FileStorageException("Estensione file non consentita: " + extension);
        }

        // Genera nome univoco
        String uniqueFilename = UUID.randomUUID().toString() + "." + extension;

        try {
            // Verifica path traversal
            Path targetLocation = photosStorageLocation.resolve(uniqueFilename);
            if (!targetLocation.getParent().equals(photosStorageLocation)) {
                throw new FileStorageException("Percorso file non valido");
            }

            // Salva il file
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, targetLocation, StandardCopyOption.REPLACE_EXISTING);
            }

            return uniqueFilename;
        } catch (IOException e) {
            throw new FileStorageException("Errore durante il salvataggio del file: " + originalFilename, e);
        }
    }

    @Override
    public Resource loadPhotoAsResource(String filename) {
        try {
            Path filePath = photosStorageLocation.resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            
            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new ResourceNotFoundException("Foto non trovata: " + filename);
            }
        } catch (MalformedURLException e) {
            throw new ResourceNotFoundException("Foto non trovata: " + filename, e);
        }
    }

    @Override
    public void deletePhoto(String filename) {
        try {
            Path filePath = photosStorageLocation.resolve(filename).normalize();
            
            // Verifica path traversal
            if (!filePath.getParent().equals(photosStorageLocation)) {
                throw new FileStorageException("Percorso file non valido");
            }
            
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new FileStorageException("Errore durante l'eliminazione del file: " + filename, e);
        }
    }

    @Override
    public boolean photoExists(String filename) {
        Path filePath = photosStorageLocation.resolve(filename).normalize();
        return Files.exists(filePath);
    }

    @Override
    public String getPhotoUrl(String filename) {
        return "/api/gallery/photos/" + filename;
    }

    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
    }

    private boolean isAllowedExtension(String extension) {
        return storageProperties.getAllowedExtensions().contains(extension.toLowerCase());
    }
}
