package org.example.sitopresentazionebandabenew.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.example.sitopresentazionebandabenew.entity.Event;
import org.example.sitopresentazionebandabenew.entity.Event.EventStatus;
import org.example.sitopresentazionebandabenew.entity.Event.EventType;
import org.example.sitopresentazionebandabenew.repository.EventRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UpcomingEventDataMigrationTest {

    @Mock
    private EventRepository eventRepository;

    private final UpcomingEventDataMigration migration = new UpcomingEventDataMigration();

    @Test
    void publishesTheSantaMarinaConcertWithMapsDirections() throws Exception {
        migration.publishSantaMarinaConcert(eventRepository).run();

        ArgumentCaptor<Event> eventCaptor = ArgumentCaptor.forClass(Event.class);
        verify(eventRepository).save(eventCaptor.capture());
        Event event = eventCaptor.getValue();

        assertThat(event.getTitle()).isEqualTo(UpcomingEventDataMigration.TITLE);
        assertThat(event.getEventDate()).isEqualTo(UpcomingEventDataMigration.EVENT_DATE);
        assertThat(event.getEventTime()).hasToString("20:45");
        assertThat(event.getLocation()).isEqualTo("Chiesa di Santa Marina Vergine");
        assertThat(event.getStatus()).isEqualTo(EventStatus.PUBLISHED);
        assertThat(event.getType()).isEqualTo(EventType.EVENT);
        assertThat(event.getAttachmentLabel()).isEqualTo("Indicazioni stradali");
        assertThat(event.getAttachmentHref()).isEqualTo(UpcomingEventDataMigration.MAPS_URL);
    }

    @Test
    void doesNotCreateADuplicateWhenTheEventAlreadyExists() throws Exception {
        when(eventRepository.existsByTypeAndEventDateAndTitle(
                        EventType.EVENT, UpcomingEventDataMigration.EVENT_DATE, UpcomingEventDataMigration.TITLE))
                .thenReturn(true);

        migration.publishSantaMarinaConcert(eventRepository).run();

        verify(eventRepository, never()).save(any());
    }
}
