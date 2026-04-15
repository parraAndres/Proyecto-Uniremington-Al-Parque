DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.tables
        WHERE table_schema = current_schema()
          AND table_name = 'sincronizacion_lotes'
    ) THEN
        -- Consulta con filtro por dispositivo + estado + rango de fecha.
        EXECUTE 'CREATE INDEX IF NOT EXISTS idx_sync_lotes_disp_estado_fecha ON sincronizacion_lotes (dispositivo_id, estado_lote, fecha_actualizacion DESC)';

        -- Consulta con filtro por dispositivo + rango de fecha (sin estado).
        EXECUTE 'CREATE INDEX IF NOT EXISTS idx_sync_lotes_disp_fecha ON sincronizacion_lotes (dispositivo_id, fecha_actualizacion DESC)';
    END IF;
END $$;
