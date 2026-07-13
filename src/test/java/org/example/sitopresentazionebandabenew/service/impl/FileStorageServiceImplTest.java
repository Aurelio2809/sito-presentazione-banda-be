package org.example.sitopresentazionebandabenew.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.nio.file.Path;
import java.util.List;
import javax.imageio.ImageIO;
import org.example.sitopresentazionebandabenew.config.StorageProperties;
import org.example.sitopresentazionebandabenew.exception.FileStorageException;
import org.example.sitopresentazionebandabenew.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;

class FileStorageServiceImplTest {

    @TempDir
    Path tempDir;

    private FileStorageServiceImpl service;

    @BeforeEach
    void setUp() {
        StorageProperties properties = new StorageProperties();
        properties.setUploadDir(tempDir.toString());
        properties.setPhotosDir("photos");
        properties.setAllowedExtensions(List.of("jpg", "jpeg", "png"));
        service = new FileStorageServiceImpl(properties);
        service.init();
    }

    @Test
    void storesAnImageAndGeneratesItsThumbnail() throws Exception {
        BufferedImage image = new BufferedImage(40, 20, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        ImageIO.write(image, "png", bytes);
        MockMultipartFile upload = new MockMultipartFile("file", "concerto.png", "image/png", bytes.toByteArray());

        String filename = service.storePhoto(upload);

        assertThat(filename).endsWith(".png");
        assertThat(service.loadPhotoAsResource(filename).exists()).isTrue();
        assertThat(service.loadThumbnailAsResource(filename).exists()).isTrue();
    }

    @Test
    void rejectsDisallowedFileExtensions() {
        MockMultipartFile upload =
                new MockMultipartFile("file", "malware.exe", "application/octet-stream", new byte[] {1});

        assertThatThrownBy(() -> service.storePhoto(upload))
                .isInstanceOf(FileStorageException.class)
                .hasMessageContaining("Estensione file non consentita");
    }

    @Test
    void rejectsPathTraversalWhenLoadingFiles() {
        assertThatThrownBy(() -> service.loadPhotoAsResource("../../etc/passwd"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Percorso file non valido");
    }
}
