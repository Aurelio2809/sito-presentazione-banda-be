package org.example.sitopresentazionebandabenew.config;

import java.time.LocalDate;
import java.time.LocalTime;
import org.example.sitopresentazionebandabenew.entity.Event;
import org.example.sitopresentazionebandabenew.entity.Event.EventStatus;
import org.example.sitopresentazionebandabenew.entity.Event.EventType;
import org.example.sitopresentazionebandabenew.repository.EventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Publishes one-off editorial content that must be present after deployment. */
@Configuration
public class UpcomingEventDataMigration {

    static final String TITLE = "Concerto a Casole";
    static final LocalDate EVENT_DATE = LocalDate.of(2026, 8, 28);
    static final String MAPS_URL =
            "https://www.google.com/maps/dir/?api=1&destination=Chiesa+di+Santa+Marina+Vergine%2C+Via+Duomo%2C+Casole+Bruzio%2C+Casali+del+Manco+CS";

    private static final Logger log = LoggerFactory.getLogger(UpcomingEventDataMigration.class);

    @Bean
    public CommandLineRunner publishSantaMarinaConcert(EventRepository eventRepository) {
        return args -> {
            if (eventRepository.existsByTypeAndEventDateAndTitle(EventType.EVENT, EVENT_DATE, TITLE)) {
                return;
            }

            Event event = Event.builder()
                    .title(TITLE)
                    .eventDate(EVENT_DATE)
                    .eventTime(LocalTime.of(20, 45))
                    .location("Chiesa di Santa Marina Vergine")
                    .cityLine("Via Duomo, Casole Bruzio · Casali del Manco (CS)")
                    .shortDescription(
                            "Concerto della Banda Musicale di Casali del Manco nella Chiesa di Santa Marina Vergine.")
                    .fullDescription(
                            "Vi aspettiamo venerdì 28 agosto alle ore 20:45 presso la Chiesa di Santa Marina Vergine, a Casole Bruzio.")
                    .type(EventType.EVENT)
                    .status(EventStatus.PUBLISHED)
                    .attachmentLabel("Indicazioni stradali")
                    .attachmentHref(MAPS_URL)
                    .tags("Concerto,Casole Bruzio")
                    .build();

            eventRepository.save(event);
            log.info("Pubblicato evento '{}' del {}", TITLE, EVENT_DATE);
        };
    }
}
