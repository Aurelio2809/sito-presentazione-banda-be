package org.example.sitopresentazionebandabenew.config;

import org.example.sitopresentazionebandabenew.entity.User;
import org.example.sitopresentazionebandabenew.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;

/**
 * Migrazione di sicurezza: neutralizza la credenziale di default "test"/"test" (debole, nota) e
 * mantiene un elenco di account amministratore nominali. Le password sono bcrypt gia' calcolate
 * offline con lo stesso {@link org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder}
 * usato dall'applicazione: qui non compare mai testo in chiaro.
 *
 * <p>L'utente "test" viene DISABILITATO (non eliminato): {@code activity_logs.user_id} ha una FK
 * non-nullable verso "users", quindi cancellarlo romperebbe lo storico attività già registrato.
 * Disabilitarlo (e invalidarne la password) impedisce comunque per sempre il login, senza perdere
 * l'integrità referenziale dello storico.
 *
 * <p>Idempotente per-utente: la disabilitazione di "test" agisce una sola volta (si ferma appena
 * lo trova già disabilitato); ogni {@code createIfMissing} è a sua volta un no-op se l'utente
 * esiste già. Aggiungere un nuovo admin in futuro = aggiungere una nuova riga qui: al prossimo
 * deploy verrà creato, senza toccare gli utenti già esistenti o le password già cambiate a mano.
 */
@Configuration
public class AdminUsersSecurityMigration {

    private static final Logger log = LoggerFactory.getLogger(AdminUsersSecurityMigration.class);

    @Bean
    public CommandLineRunner migrateDefaultAdminUsers(UserRepository userRepository) {
        return args -> {
            boolean testStillActive = userRepository.findByUsername("test")
                    .map(User::isEnabled)
                    .orElse(false);
            if (testStillActive) {
                log.info("[Migrazione sicurezza] Disabilitazione utente di default 'test'...");
                userRepository.findByUsername("test").ifPresent(testUser -> {
                    testUser.setEnabled(false);
                    // Password invalidata in difesa aggiuntiva (enabled=false blocca già il login).
                    testUser.setPassword("$2a$10$fsZTXDhTUO9xz1wRvjoza.qjoqdSEXtGw6DXPN33YrcxrhtSqlWvy");
                    userRepository.save(testUser);
                });
            }

            createIfMissing(userRepository, "aurelio99", "aurelio99@bandamusicale.local",
                    "$2a$10$us54aO2Bhf1UTMySlnr.N.kSw6G.3wcMzTQjDTrmXSwwCFlAtNJ9y");
            createIfMissing(userRepository, "gianmarco99", "gianmarco99@bandamusicale.local",
                    "$2a$10$L1JclilWK0BCL3t1OyePZOcjPJOOniNS0hwM.s5i83o.OLPVI3ocy");
            createIfMissing(userRepository, "pietrosib", "pietrosib@bandamusicale.local",
                    "$2a$10$5UE2DmjmGrOTY9s1pUfh6.rydRX0Ghi16WnLBNzTyKoRimSdORM6K");
            createIfMissing(userRepository, "mario99", "mario99@bandamusicale.local",
                    "$2a$10$jPJpWPtHJu6w79VPSQom9OHzTzdOPee4aTwC2h7kMC3vMXO8.oHr.");
        };
    }

    private void createIfMissing(UserRepository repo, String username, String email, String bcryptHash) {
        if (repo.existsByUsername(username)) {
            return;
        }
        LocalDateTime now = LocalDateTime.now();
        User user = User.builder()
                .username(username)
                .email(email)
                .password(bcryptHash)
                .role(User.Role.ADMIN)
                .enabled(true)
                .createdAt(now)
                .updatedAt(now)
                .build();
        repo.save(user);
    }
}
