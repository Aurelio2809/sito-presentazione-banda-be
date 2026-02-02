package org.example.sitopresentazionebandabenew.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
@ConfigurationProperties(prefix = "app.storage")
public class StorageProperties {

    private String uploadDir = "./uploads";
    private String photosDir = "photos";
    private String maxFileSize = "10MB";
    private List<String> allowedExtensions = List.of("jpg", "jpeg", "png", "gif", "webp");

    /** Qualità JPEG per thumbnail scalate (0.0–1.0). Default 1.0 = massima qualità. */
    private float thumbnailJpegQuality = 1.0f;
    /** Qualità PNG per thumbnail scalate (0.0–1.0). Default 1.0 = massima qualità. */
    private float thumbnailPngQuality = 1.0f;

    public String getPhotosPath() {
        return uploadDir + "/" + photosDir;
    }
}
