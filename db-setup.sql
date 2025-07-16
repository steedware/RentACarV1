-- Włączenie rozszerzenia PostGIS
CREATE EXTENSION IF NOT EXISTS postgis;

-- Sprawdzenie, czy rozszerzenie zostało poprawnie zainstalowane
SELECT postgis_full_version();
