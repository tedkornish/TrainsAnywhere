CREATE TABLE routes (
  id bigint PRIMARY KEY,
  created_at timestamp NOT NULL,
  updated_at timestamp NOT NULL,

  -- These three columns form the unique identity of the route
  origin_station_id bigint REFERENCES stations(id),
  destination_station_id bigint REFERENCES stations(id),
  date date,

  -- This can be "fetched", "fetching" (which includes being queued) or "waiting"
  fetch_status text NOT NULL DEFAULT 'waiting',
  -- if this is NULL, never been fetched
  last_fetched_at timestamp, 

  UNIQUE (origin_station_id, destination_station_id, date),
  CHECK (origin_station_id <> destination_station_id)
);

--;;

CREATE SEQUENCE route_ids START 1;

--;;

CREATE TRIGGER routes_insert
  BEFORE INSERT ON routes
  FOR EACH ROW
  EXECUTE PROCEDURE on_record_insert('route_ids');

--;;

CREATE TRIGGER routes_update
  BEFORE UPDATE ON routes
  FOR EACH ROW
  EXECUTE PROCEDURE on_record_update();

--;;

ALTER TABLE trips
  DROP COLUMN origin_station_id,
  DROP COLUMN destination_station_id,
  ADD COLUMN route_id bigint REFERENCES routes(id) ON DELETE CASCADE
