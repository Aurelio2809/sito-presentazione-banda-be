# Infrastruttura VPS

Questa cartella conserva una copia priva di segreti delle configurazioni operative
della VPS. I file `.env`, le password, le chiavi private e la chiave simmetrica dei
backup non devono essere aggiunti al repository.

## Sicurezza e aggiornamenti

- I servizi Docker usano reti private e `no-new-privileges`.
- Kafka usa l'immagine `apache/kafka:4.3.1` e un volume persistente montato nella
  directory dati corretta.
- n8n usa un'immagine derivata dalla release ufficiale `2.31.6`, con `tar`
  aggiornato alla versione `7.5.19`.
- `scan-silaware-containers` esegue una scansione Trivy settimanale e conserva i
  report per 90 giorni in `/var/log/silaware-security`.

## Backup

`backup-silaware` crea dump consistenti dei database, archivia i volumi
applicativi, include le configurazioni di ripristino e cifra tutto con
AES-256-CBC e PBKDF2.

`prepare-silaware-offsite-backup`:

1. crea e verifica un nuovo backup;
2. prepara una copia temporanea leggibile dal runner;
3. cifra la chiave del backup con la chiave pubblica SSH del Mac tramite `age`;
4. genera `SHA256SUMS`.

Il workflow off-site è conservato come modello non attivo finché non viene
copiato nel branch predefinito come `.github/workflows/offsite-backup.yml`.

Per decifrare la chiave su Mac:

```bash
age -d -i ~/.ssh/vps_hostinger silaware-backup-key.age
```

Per verificare un artifact:

```bash
sha256sum -c SHA256SUMS
```

I file di questa cartella vanno installati sulla VPS con proprietario `root` e
permessi coerenti con il tipo di file: `750` per gli script, `600` per i compose
contenenti riferimenti a variabili sensibili e `644` per le unità systemd.
