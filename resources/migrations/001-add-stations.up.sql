CREATE FUNCTION on_record_insert() RETURNS trigger AS $$
  DECLARE
    id_sequence VARCHAR;
  BEGIN
    SELECT TG_ARGV[0] INTO id_sequence;
    NEW.id         := nextval(id_sequence);
    NEW.created_at := now() AT TIME ZONE 'UTC';
    NEW.updated_at := now() AT TIME ZONE 'UTC';
    RETURN NEW;
  END;
$$ LANGUAGE plpgsql;

--;;

CREATE FUNCTION on_record_update() RETURNS trigger AS $$
  BEGIN
    NEW.id         := OLD.id;
    NEW.created_at := OLD.created_at;
    NEW.updated_at := now() AT TIME ZONE 'UTC';
    RETURN NEW;
  END;
$$ LANGUAGE plpgsql;

--;;

CREATE TABLE stations (
  id bigint PRIMARY KEY,
  created_at timestamp NOT NULL,
  updated_at timestamp NOT NULL,
  station_id bigint NOT NULL,
  country_name text NOT NULL,
  name text NOT NULL,
  country_code text NOT NULL,
  native_name text NOT NULL,
  longitude double precision NOT NULL,
  latitude double precision NOT NULL
);

--;;

CREATE SEQUENCE station_ids START 1;

--;;

CREATE TRIGGER stations_insert
  BEFORE INSERT ON stations
  FOR EACH ROW
  EXECUTE PROCEDURE on_record_insert('station_ids');

--;;

CREATE TRIGGER stations_update
  BEFORE UPDATE ON stations
  FOR EACH ROW
  EXECUTE PROCEDURE on_record_update();

