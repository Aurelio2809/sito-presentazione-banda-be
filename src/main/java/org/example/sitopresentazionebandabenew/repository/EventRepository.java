package org.example.sitopresentazionebandabenew.repository;

import org.example.sitopresentazionebandabenew.entity.Event;
import org.example.sitopresentazionebandabenew.entity.Event.EventStatus;
import org.example.sitopresentazionebandabenew.entity.Event.EventType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    Page<Event> findByType(EventType type, Pageable pageable);

    Page<Event> findByStatus(EventStatus status, Pageable pageable);

    Page<Event> findByTypeAndStatus(EventType type, EventStatus status, Pageable pageable);

    @Query("SELECT e FROM Event e WHERE e.type = :type AND e.status = 'PUBLISHED' AND e.eventDate >= :date ORDER BY e.eventDate ASC")
    List<Event> findUpcomingPublished(@Param("type") EventType type, @Param("date") LocalDate date);

    @Query("SELECT e FROM Event e WHERE e.type = :type AND e.status = 'PUBLISHED' AND e.eventDate < :date ORDER BY e.eventDate DESC")
    Page<Event> findPastPublished(@Param("type") EventType type, @Param("date") LocalDate date, Pageable pageable);
    
    @Query("SELECT e FROM Event e WHERE e.type = :type AND e.status = 'PUBLISHED' AND e.eventDate < :date ORDER BY e.eventDate DESC")
    List<Event> findPastPublishedList(@Param("type") EventType type, @Param("date") LocalDate date);

    @Query("SELECT e FROM Event e WHERE e.status = 'PUBLISHED' ORDER BY e.eventDate DESC")
    Page<Event> findAllPublished(Pageable pageable);

    long countByStatus(EventStatus status);

    long countByTypeAndStatus(EventType type, EventStatus status);
}
