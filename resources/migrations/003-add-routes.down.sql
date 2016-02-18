DROP VIEW IF EXISTS routes_pending_fetches;
--;;
ALTER TABLE IF EXISTS trips
  ADD COLUMN origin_station_id bigint NOT NULL,
  ADD COLUMN destination_station_id bigint NOT NULL,
  DROP COLUMN route_id;
--;;
DROP TRIGGER IF EXISTS routes_insert ON routes;
--;;
DROP TRIGGER IF EXISTS routes_update ON routes;
--;;
DROP TABLE IF EXISTS routes CASCADE;
--;;
DROP SEQUENCE IF EXISTS route_ids;
