package org.example.sitopresentazionebandabenew.config;

import org.example.sitopresentazionebandabenew.dto.requests.GalleryPhotoRequest;
import org.example.sitopresentazionebandabenew.entity.GalleryPhoto;
import org.example.sitopresentazionebandabenew.repository.GalleryPhotoRepository;
import org.example.sitopresentazionebandabenew.service.GalleryPhotoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Configuration
public class GalleryInitialSeeder {

    private static final Logger log = LoggerFactory.getLogger(GalleryInitialSeeder.class);

    @Bean
    public CommandLineRunner seedHighResPhotos(GalleryPhotoRepository repository, GalleryPhotoService service) {
        return args -> {
            try {
                File fotoDir = new File("/app/foto");
                if (fotoDir.exists() && fotoDir.isDirectory()) {
                    File[] files = fotoDir.listFiles((dir, name) -> 
                        name.toLowerCase().endsWith(".jpg") || 
                        name.toLowerCase().endsWith(".png") || 
                        name.toLowerCase().endsWith(".jpeg") ||
                        name.toLowerCase().endsWith(".webp")
                    );
                    
                    if (files != null && files.length > 0) {
                        log.info("[Seeder] Nuove foto trovate in /app/foto. Svuotamento galleria in corso per sostituire i placeholder...");
                        List<GalleryPhoto> existingPhotos = repository.findAll();
                        for (GalleryPhoto p : existingPhotos) {
                            service.delete(p.getId());
                        }

                        for (File file : files) {
                            log.info("[Seeder] Ingestione della foto {} ...", file.getName());
                            
                            GalleryPhotoRequest request = new GalleryPhotoRequest();
                            request.setTitle(file.getName().replace(".JPG", "").replace(".jpg", "").replace("Z72_", "Banda - "));
                            request.setDescription("Foto di alta qualità della banda musicale");
                            request.setPhotoYear(2026);
                            request.setPhotoMonth(3);
                            request.setFavorite(true);

                            MultipartFile multipartFile = new CustomFile(file);
                            service.uploadPhoto(multipartFile, request);
                            
                            // Rinominiamo il file per non processarlo due volte
                            file.renameTo(new File(file.getAbsolutePath() + ".seeded"));
                            log.info("[Seeder] Successo! {} processato e registrato.", file.getName());
                        }
                        log.info("[Seeder] Completato l'inserimento batch delle foto!");
                    } else {
                        log.info("[Seeder] Nessuna nuova immagine '.jpg' da inserire in /app/foto (Tutte processate).");
                    }
                } else {
                    log.info("[Seeder] Cartella /app/foto non montata. Salto il seeding.");
                }
            } catch (Exception e) {
                log.error("[Seeder] Errore critico durante l'inzilizzazione delle foto hi-res", e);
            }
        };
    }

    private static class CustomFile implements MultipartFile {
        private final File file;
        private final byte[] content;

        public CustomFile(File file) throws IOException {
            this.file = file;
            this.content = Files.readAllBytes(file.toPath());
        }

        @Override public String getName() { return file.getName(); }
        @Override public String getOriginalFilename() { return file.getName(); }
        @Override public String getContentType() { return "image/jpeg"; }
        @Override public boolean isEmpty() { return content == null || content.length == 0; }
        @Override public long getSize() { return content.length; }
        @Override public byte[] getBytes() throws IOException { return content; }
        @Override public InputStream getInputStream() throws IOException { return new java.io.ByteArrayInputStream(content); }
        @Override public void transferTo(File dest) throws IOException, IllegalStateException { Files.write(dest.toPath(), content); }
    }
}
