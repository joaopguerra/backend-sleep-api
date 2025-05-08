ALTER TABLE sleep
    ADD COLUMN is_deleted BOOLEAN DEFAULT FALSE;

UPDATE sleep
SET is_deleted = FALSE
WHERE is_deleted IS NULL;