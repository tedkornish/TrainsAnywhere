CREATE TABLE routes (
  id bigint PRIMARY KEY,
  created_at timestamp NOT NULL,
  updated_at timestamp NOT NULL,

  -- These three columns form the unique identity of the route
  origin_station_id bigint REFERENCES stations(id),
  destination_station_id bigint REFERENCES stations(id),
  date date,

  -- This "fetching" (which includes being queued) or "waiting"
  fetch_status text NOT NULL DEFAULT 'waiting',
  -- if this is NULL, never been fetched
  last_fetch_queued_at timestamp, 

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
  ADD COLUMN route_id bigint REFERENCES routes(id) ON DELETE CASCADE;

--;;

CREATE VIEW routes_pending_fetches AS
  WITH
    t1 AS (
      SELECT *, 0 AS ord
      FROM routes
      WHERE fetch_status = 'waiting'
      AND date < current_date + '7 days'::interval
      AND (
        last_fetch_queued_at IS NULL OR
        last_fetch_queued_at < current_date - '6 hours'::interval
      )
    ),
    t2 AS (
      SELECT *, 1 AS ord
      FROM routes
      WHERE fetch_status = 'waiting'
      AND date < current_date + '31 days'::interval
      AND date > current_date + '7 days'::interval
      AND (
        last_fetch_queued_at IS NULL OR
        last_fetch_queued_at < current_date - '1 day'::interval
      )
    ),
    t3 AS (
      SELECT *, 2 AS ord
      FROM routes
      WHERE fetch_status = 'waiting'
      AND date < current_date + '120 days'::interval
      AND date > current_date + '31 days'::interval
      AND (
        last_fetch_queued_at IS NULL OR
        last_fetch_queued_at < current_date - '7 days'::interval
      )
    ),
    combined_routes AS (
      SELECT *
      FROM t1
      UNION (SELECT * FROM t2)
      UNION (SELECT * FROM t3)
    ),
    origins AS (
      SELECT *
      FROM stations
    ),
    destinations AS (
      SELECT *
      FROM stations
    )
  SELECT
    combined_routes.id,
    origin_station_id,
    destination_station_id,
    date,
    origins.name origin_station_name,
    destinations.name destination_station_name
  FROM combined_routes
  JOIN origins ON combined_routes.origin_station_id = origins.id
  JOIN destinations ON combined_routes.destination_station_id = destinations.id
  ORDER BY ord, date, origin_station_id, destination_station_id ASC NULLS FIRST;
