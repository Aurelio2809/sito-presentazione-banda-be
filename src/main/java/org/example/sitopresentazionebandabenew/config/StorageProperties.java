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

    public String getPhotosPath() {
        return uploadDir + "/" + photosDir;
    }
}
