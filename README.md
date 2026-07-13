# Backend — Banda Musicale Città di Casali del Manco

Il backend richiede Java 21. Per eseguire in locale lo stesso quality gate usato dalla CI:

```bash
./mvnw verify
```

Il comando compila l'applicazione, esegue test unitari e test HTTP di integrazione su un database H2 effimero, genera il WAR e verifica la formattazione Java con Spotless.

Comandi mirati:

- `./mvnw test` esegue i test.
- `./mvnw spotless:check` verifica la formattazione.
- `./mvnw spotless:apply` formatta i sorgenti Java e rimuove gli import inutilizzati.

GitHub Actions esegue il quality gate su pull request e push verso `integrazione`. Il deploy viene avviato solo dopo il suo completamento con successo.
