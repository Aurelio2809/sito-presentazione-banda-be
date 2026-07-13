package org.example.sitopresentazionebandabenew.repository;

import java.util.List;
import org.example.sitopresentazionebandabenew.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    Page<Message> findByReadFalseOrderByReceivedAtDesc(Pageable pageable);

    Page<Message> findByReadTrueOrderByReceivedAtDesc(Pageable pageable);

    Page<Message> findAllByOrderByReceivedAtDesc(Pageable pageable);

    List<Message> findByReadFalse();

    long countByReadFalse();

    long countByReadTrue();

    @Modifying
    @Query("UPDATE Message m SET m.read = true WHERE m.read = false")
    int markAllAsRead();
}
