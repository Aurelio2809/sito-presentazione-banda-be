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

## Modulo contatti e privacy

Le richieste inviate dal form pubblico non vengono salvate nel database: il backend le inoltra
via SMTP alla casella dell'associazione. La gestione Messaggi e i relativi endpoint sono stati
rimossi. L'eventuale archivio storico preesistente deve essere eliminato con un'operazione
amministrativa separata e autorizzata.

Variabili d'ambiente obbligatorie in produzione:

- `MAIL_USERNAME`: account SMTP mittente;
- `MAIL_PASSWORD`: password per applicazioni o credenziale SMTP, mai da committare;
- `CONTACT_MAIL_TO`: destinatario, predefinito a `bandamusicalecasalidelmanco@gmail.com`;
- `MAIL_HOST` e `MAIL_PORT`: facoltative, predefinite a `smtp.gmail.com:587` con STARTTLS.

Attività operative:

- proteggere la casella con autenticazione a due fattori e accessi nominativi;
- eliminare i messaggi entro 12 mesi dalla chiusura della richiesta, salvo obblighi legali;
- non abilitare log del body delle richieste;
- preferire un servizio email professionale con accordo sul trattamento dei dati.
