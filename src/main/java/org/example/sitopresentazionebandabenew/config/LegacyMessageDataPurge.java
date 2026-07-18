package org.example.sitopresentazionebandabenew.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/** Elimina definitivamente i dati del precedente modulo contatti, non più persistente. */
@Component
public class LegacyMessageDataPurge implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(LegacyMessageDataPurge.class);
    private final JdbcTemplate jdbcTemplate;

    public LegacyMessageDataPurge(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(ApplicationArguments args) {
        int removedLogs = jdbcTemplate.update("DELETE FROM activity_logs WHERE target_type = 'MESSAGE'");
        jdbcTemplate.execute("DROP TABLE IF EXISTS messages CASCADE");
        log.info("Bonifica privacy completata; rimossi {} log collegati al vecchio modulo contatti", removedLogs);
    }
}
