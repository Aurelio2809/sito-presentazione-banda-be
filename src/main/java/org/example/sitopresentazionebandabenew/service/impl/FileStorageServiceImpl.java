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

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Iterator;
import java.util.UUID;

@Service
public class FileStorageServiceImpl implements FileStorageService {

    /** Larghezza thumbnail: sufficiente per griglia e mosaic senza upscale (≈ 1 colonna × 2 per retina). */
    private static final int THUMBNAIL_WIDTH = 1024;
    
    private final Path photosStorageLocation;
    private final Path thumbnailsStorageLocation;
    private final StorageProperties storageProperties;

    public FileStorageServiceImpl(StorageProperties storageProperties) {
        this.storageProperties = storageProperties;
        this.photosStorageLocation = Paths.get(storageProperties.getPhotosPath()).toAbsolutePath().normalize();
        this.thumbnailsStorageLocation = photosStorageLocation.resolve("thumbnails").toAbsolutePath().normalize();
    }

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(photosStorageLocation);
            Files.createDirectories(thumbnailsStorageLocation);
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

            // Salva il file originale
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, targetLocation, StandardCopyOption.REPLACE_EXISTING);
            }

            // Genera thumbnail
            generateThumbnail(targetLocation, uniqueFilename, extension);

            return uniqueFilename;
        } catch (IOException e) {
            throw new FileStorageException("Errore durante il salvataggio del file: " + originalFilename, e);
        }
    }

    private void generateThumbnail(Path originalPath, String filename, String extension) {
        try {
            BufferedImage originalImage = ImageIO.read(originalPath.toFile());
            if (originalImage == null) {
                return; // Non è possibile leggere l'immagine
            }

            int originalWidth = originalImage.getWidth();
            int originalHeight = originalImage.getHeight();

            // Calcola le dimensioni mantenendo l'aspect ratio
            int thumbnailHeight;
            int thumbnailWidth;
            
            if (originalWidth <= THUMBNAIL_WIDTH) {
                // L'immagine è già più piccola della thumbnail, copia l'originale
                thumbnailWidth = originalWidth;
                thumbnailHeight = originalHeight;
            } else {
                thumbnailWidth = THUMBNAIL_WIDTH;
                thumbnailHeight = (int) ((double) originalHeight / originalWidth * THUMBNAIL_WIDTH);
            }

            // Crea la thumbnail
            BufferedImage thumbnail = new BufferedImage(thumbnailWidth, thumbnailHeight, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = thumbnail.createGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.drawImage(originalImage, 0, 0, thumbnailWidth, thumbnailHeight, null);
            g2d.dispose();

            // Salva la thumbnail (JPEG con qualità esplicita per nitidezza in griglia)
            Path thumbnailPath = thumbnailsStorageLocation.resolve(filename);
            if ("png".equalsIgnoreCase(extension)) {
                ImageIO.write(thumbnail, "png", thumbnailPath.toFile());
            } else {
                writeJpegWithQuality(thumbnail, thumbnailPath.toFile(), 0.88f);
            }
        } catch (IOException e) {
            // Log error but don't fail the upload
            System.err.println("Errore nella generazione della thumbnail: " + e.getMessage());
        }
    }

    private static void writeJpegWithQuality(BufferedImage image, java.io.File outputFile, float quality) throws IOException {
        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpg");
        if (!writers.hasNext()) {
            ImageIO.write(image, "jpg", outputFile);
            return;
        }
        ImageWriter writer = writers.next();
        try (ImageOutputStream ios = ImageIO.createImageOutputStream(outputFile)) {
            writer.setOutput(ios);
            ImageWriteParam param = writer.getDefaultWriteParam();
            if (param.canWriteCompressed()) {
                param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                param.setCompressionQuality(quality);
            }
            writer.write(null, new IIOImage(image, null, null), param);
        } finally {
            writer.dispose();
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
    public Resource loadThumbnailAsResource(String filename) {
        try {
            Path filePath = thumbnailsStorageLocation.resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            
            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                // Fallback all'immagine originale se la thumbnail non esiste
                return loadPhotoAsResource(filename);
            }
        } catch (MalformedURLException e) {
            // Fallback all'immagine originale
            return loadPhotoAsResource(filename);
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
            
            // Elimina anche la thumbnail
            Path thumbnailPath = thumbnailsStorageLocation.resolve(filename).normalize();
            Files.deleteIfExists(thumbnailPath);
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

    @Override
    public String getThumbnailUrl(String filename) {
        return "/api/gallery/photos/thumb/" + filename;
    }

    @Override
    public boolean generateThumbnailForExistingFile(String filename) {
        try {
            Path originalPath = photosStorageLocation.resolve(filename).normalize();
            
            if (!Files.exists(originalPath)) {
                System.err.println("File originale non trovato: " + filename);
                return false;
            }

            // Verifica se la thumbnail esiste già
            Path thumbnailPath = thumbnailsStorageLocation.resolve(filename);
            if (Files.exists(thumbnailPath)) {
                return true; // Thumbnail già esistente
            }

            String extension = getFileExtension(filename);
            generateThumbnail(originalPath, filename, extension);
            
            // Verifica che la thumbnail sia stata creata
            return Files.exists(thumbnailPath);
        } catch (Exception e) {
            System.err.println("Errore nella generazione thumbnail per " + filename + ": " + e.getMessage());
            return false;
        }
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
