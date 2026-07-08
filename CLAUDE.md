# CLAUDE.md — Wiki del progetto (Backend)

> Documento vivo per gli agenti AI. **Consultalo prima di lavorare** e **aggiornalo** quando cambi
> API, sicurezza, struttura o convenzioni. Mantienilo conciso e veritiero.

## Cos'è
Backend REST del sito della **Banda Musicale "Città di Casali del Manco"**. Serve la SPA Angular e la
dashboard admin. **In produzione** dietro reverse proxy Caddy (instrada solo `/api/*` qui).

## Stack
- **Spring Boot 4 / Spring Security 7**, **Java 21**, packaging **war** (Tomcat).
- **PostgreSQL** + Spring Data JPA/Hibernate. **MapStruct** (mapper), **Lombok**.
- **springdoc-openapi** (Swagger) — riservato ad admin, non pubblico.
- Build: `./mvnw -o clean package -DskipTests` (il Dockerfile fa lo stesso).

## Struttura (`org.example.sitopresentazionebandabenew`)
- `controller/` — Auth, Event, Gallery, Message, ActivityLog.
- `service/` + `service/impl/` — logica; `repository/` — JPA repo; `entity/` — User, Event, GalleryPhoto, Message, ActivityLog.
- `dto/requests` + `dto/responses`; `mapper/` (MapStruct); `config/` (Security, Cors, Storage, seeder, Jackson); `exception/` (GlobalExceptionHandler + eccezioni tipizzate).

## ⚠️ Sicurezza (stato attuale — mantenere)
- **CSRF ABILITATO**: `CookieCsrfTokenRepository.withHttpOnlyFalse()` + `SpaCsrfTokenRequestHandler`
  + `CsrfCookieFilter`. Esenti solo `POST /api/auth/login` e `POST /api/messages` (pubblici pre-auth).
- **Autorizzazione per ruolo**: endpoint protetti richiedono `ROLE_ADMIN`. Pubblici: login,
  POST form contatti, GET gallery/eventi pubblici, serving immagini. `/api/auth/**` = authenticated.
- **Attenzione ai matcher**: un `requestMatchers("/path")` copre TUTTI i metodi → non rendere
  pubblica per errore una GET che espone dati (già successo con `GET /api/messages`).
- **Path traversal**: ogni accesso a file passa da `FileStorageServiceImpl.resolveWithin` (containment).
- **Segreti**: mai in chiaro nel repo. Credenziali DB via env del compose / `.env`. Swagger non pubblico.
- Header di sicurezza + cookie `Secure`/`SameSite=Strict` attivi. Vedi `SecurityConfig`.
- **Da fare (backlog)**: rate-limiting sul login; profilo `prod` con `ddl-auto=validate`.

## API (panoramica)
- `POST /api/auth/login` · `GET /api/auth/me` · `POST /api/auth/logout` · profilo/password.
- `GET /api/gallery/public`, `/public/favorites`, serving `/photos/**` (pubblici); CRUD gallery (admin).
- `GET /api/events/public/**` (pubblici); CRUD eventi (admin).
- `POST /api/messages` (form contatti, pubblico); lettura/gestione inbox (admin).
- Upload immagini: multipart, estensioni whitelisted, nomi UUID, ottimizzazione/thumbnail server-side.
  **Qualità thumbnail = 0.82** (`app.storage.thumbnail-jpeg-quality`), non 1.0: a qualità 1.0 le
  thumbnail (max 800px lato lungo) risultavano più pesanti della foto originale (es. 488KB vs
  281KB), lente da caricare senza beneficio visivo reale. 0.82 è lo standard "quasi-lossless" per il
  web. `GalleryInitialSeeder` chiama `regenerateAllThumbnails()` ad ogni avvio: cambiare questo
  valore si applica da solo alle foto già pubblicate al prossimo deploy, senza bisogno di login admin.

## Convenzioni
- Validazione input con `@Valid` + vincoli sui DTO (`@NotBlank`, `@Email`, `@Size`).
- Errori tramite `GlobalExceptionHandler` (nessuno stacktrace verso il client).
- Testi/etichette rivolti all'utente finale stanno nel **frontend** (i18n 4 lingue): il backend
  restituisce dati, non copy localizzata.

## Deploy
Repo separato `Aurelio2809/sito-presentazione-banda-be`. Push su branch **`integrazione`** → runner
CI/CD self-hosted ricostruisce il container. In prod gira col profilo `local` (nessun
`SPRING_PROFILES_ACTIVE`), con credenziali DB sovrascritte dalle env del compose. **Non**
committare/pushare senza richiesta esplicita.

## Agenti (`.claude/agents/`)
- `security-auditor` — audit di sicurezza del backend (read-only, no deploy).
- `wiki-curator` — analizza un commit e aggiorna questo file se una convenzione è cambiata.
  Pensato per essere invocato dopo ogni commit (es. via hook `.githooks/post-commit`, non ancora
  attivato di default: l'attivazione automatica di un agente che fa commit da solo richiede
  un'autorizzazione esplicita dell'utente — vedi `.githooks/post-commit` per i dettagli). Può
  comunque essere richiamato a mano ("aggiorna la wiki").
