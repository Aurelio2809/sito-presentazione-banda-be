-- Migrazione opzionale: da photo_date (DATE) a photo_year, photo_month, photo_day.
-- Eseguire solo se la tabella gallery_photos ha già la colonna photo_date e dati da migrare.
-- Con ddl-auto=update, Hibernate aggiunge le nuove colonne; questa migrazione copia i dati e rimuove photo_date.

-- Aggiungi colonne se non esistono (Hibernate le crea con ddl-auto=update, ma per sicurezza)
ALTER TABLE gallery_photos ADD COLUMN IF NOT EXISTS photo_year INTEGER;
ALTER TABLE gallery_photos ADD COLUMN IF NOT EXISTS photo_month INTEGER;
ALTER TABLE gallery_photos ADD COLUMN IF NOT EXISTS photo_day INTEGER;

-- Copia dati da photo_date alle nuove colonne (solo se photo_date esiste e ha valore)
DO $$
BEGIN
  IF EXISTS (
    SELECT 1 FROM information_schema.columns
    WHERE table_name = 'gallery_photos' AND column_name = 'photo_date'
  ) THEN
    UPDATE gallery_photos
    SET
      photo_year = EXTRACT(YEAR FROM photo_date)::INTEGER,
      photo_month = EXTRACT(MONTH FROM photo_date)::INTEGER,
      photo_day = EXTRACT(DAY FROM photo_date)::INTEGER
    WHERE photo_date IS NOT NULL;
    ALTER TABLE gallery_photos DROP COLUMN photo_date;
  END IF;
END $$;
