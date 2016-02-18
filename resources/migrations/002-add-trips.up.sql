CREATE TABLE trips (
  id bigint PRIMARY KEY,
  created_at timestamp NOT NULL,
  updated_at timestamp NOT NULL,
  origin_station_id bigint REFERENCES stations(id),
  destination_station_id bigint REFERENCES stations(id),
  price_economy_dollars money,
  price_comfort_dollars money,
  price_premier_dollars money
);

--;;

CREATE SEQUENCE trip_ids START 1;

--;;

CREATE TRIGGER trips_insert
  BEFORE INSERT ON trips
  FOR EACH ROW
  EXECUTE PROCEDURE on_record_insert('trip_ids');

--;;

CREATE TRIGGER trips_update
  BEFORE UPDATE ON trips
  FOR EACH ROW
  EXECUTE PROCEDURE on_record_update();

--;;

CREATE TABLE hops (
  id bigint PRIMARY KEY,
  created_at timestamp NOT NULL,
  updated_at timestamp NOT NULL,
  trip_id bigint REFERENCES trips(id) ON DELETE CASCADE,
  origin_station_name text NOT NULL,
  destination_station_name text NOT NULL,
  departure_time timestamp NOT NULL,
  arrival_time timestamp NOT NULL,
  duration_minutes integer CHECK (duration_minutes > 0)
);

--;;

CREATE SEQUENCE hop_ids START 1;

--;;

CREATE TRIGGER hops_insert
  BEFORE INSERT ON hops
  FOR EACH ROW
  EXECUTE PROCEDURE on_record_insert('hop_ids');

--;;

CREATE TRIGGER hops_update
  BEFORE UPDATE ON hops
  FOR EACH ROW
  EXECUTE PROCEDURE on_record_update();

