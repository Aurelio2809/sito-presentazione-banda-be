package org.example.sitopresentazionebandabenew.config;

import org.example.sitopresentazionebandabenew.service.GalleryPhotoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GallerySeedDatesRunner {

    private static final Logger log = LoggerFactory.getLogger(GallerySeedDatesRunner.class);

    @Bean
    public CommandLineRunner seedDatesForPhotosWithoutDate(GalleryPhotoService galleryPhotoService) {
        return args -> {
            int updated = galleryPhotoService.setDatesFromCreatedAtForPhotosWithoutDate();
            if (updated > 0) {
                log.info("Galleria: impostate date (da createdAt) su {} foto che non avevano data", updated);
            }
        };
    }
}
